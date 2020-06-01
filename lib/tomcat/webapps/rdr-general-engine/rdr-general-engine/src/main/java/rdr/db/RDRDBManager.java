package rdr.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import rdr.apps.Main;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.logger.Logger;
import rdr.model.Attribute;
import rdr.model.AttributeFactory;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.mysql.MysqlConnection;
import rdr.mysql.MysqlDBManager;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Operator;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleLoader;
import rdr.rules.RuleSet;
import rdr.sqlite.SqliteDBManager;
import rdr.utils.DateUtil;
import rdr.utils.RDRConfig;
import rdr.utils.RDRConstants;
import rdr.utils.StringUtil;
import rdr.utils.Utility;

/**
*
* @author Kim Woo-Cheol (ucciri@gmail.com)
*/
public abstract class RDRDBManager 
{
	protected static Connection c;
	private static RDRDBManager instance;
	
	protected static int dbIndex; //default(0), secondary(1)
	
	public static final String SQLITE  = "sqlite";
	public static final String MYSQL   = "mysql";
	public static final String MARIADB = "mariadb";
	public static final String MSSQL   = "mssql";
	
	public RDRDBManager()
	{
		;
	}
	
	public static RDRDBManager getInstance()
	{
		return RDRDBManager.getInstance(0);
	}
	
	public static RDRDBManager getSecondaryInstance()
	{
		return RDRDBManager.getInstance(1);
	}
	
	private static RDRDBManager getInstance(int pIndex)
	{
		if (instance == null || c == null || dbIndex != pIndex)
		{
			if (RDRDBManager.connectDataBase(pIndex) == false)
			{
				Logger.error("connectDataBase Failed");
			}
		}
		
		try
		{
			//일정시간이상 idle 상태이면  DBMS 가 connection 을 끊으므로 확인하고 다시 연결한다.
			if (RDRConfig.getDBType().equals(RDRDBManager.SQLITE) == false)
			{
				if (c != null && c.isValid(60) == false)
				{
					Logger.info("Connection missing, time out, try reconnect");
					
					if (RDRDBManager.connectDataBase(pIndex) == false)
					{
						Logger.error("try reconnect, connectDataBase Failed");
					}
				}
			}
		}
		catch (Exception e)
		{
			Logger.error(e.getClass().getName() + ":" + e.getMessage(), e);
		}
		
		return instance;
	}
	
	public static boolean connectDataBase(int pIndex)
	{
		try
		{
			if (c != null)
				c.close();
		}
		catch (Exception e)
		{
			Logger.error(e.getClass().getName() + ":" + e.getMessage(), e);
		}
		
		dbIndex = pIndex;

		if (RDRConfig.getDBType().equals(RDRDBManager.SQLITE))
		{
			instance = new SqliteDBManager();
		}
		else if (RDRConfig.getDBType().equals(RDRDBManager.MYSQL) ||
				 RDRConfig.getDBType().equals(RDRDBManager.MARIADB) ||
				 RDRConfig.getDBType().equals(RDRDBManager.MSSQL))
		{
			instance = new MysqlDBManager();
		}
		else
		{
			Logger.error("DB Type is not allowed : " + RDRConfig.getDBType());
			return false;
		}
		
		return instance.setConnection();
	}
	
	public static void closeDataBase()
	{
		try
		{
			if (c != null)
			{
				c.close();
				c = null;
			}
		}
		catch (Exception e)
		{
			Logger.error(e.getClass().getName() + ":" + e.getMessage(), e);
		}
	}
	
	public boolean setConnection()
	{
		System.out.println("It's abstract");
		return false;
	}
	
	public Connection getConnection()
	{
		return this.c;
	}
	
	public void setAutoCommit(boolean flag)
	{
		try
		{
			c.setAutoCommit(flag);
		}
		catch ( Exception e ) 
		{
			Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
		}
	}
	
	public void doCommit(boolean flag)
	{
		if (flag) this.commit();
		else this.rollback();
	}
	
	public void commit()
	{
		try
		{
			c.commit();
		}
		catch ( Exception e ) 
		{
			Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
		}
	}
	
	public void rollback()
	{
		try
		{
			c.rollback();
		}
		catch ( Exception e ) 
		{
			Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
		}
	}
	
	public void initialize()
	{
		/** gui용으로 기존 DBManager.initialise내용임 */
		createRDRTables();
		
		RuleLoader.insertRule(Rule.ROOT_RULE_ID, Main.KB.getRootRule(), Conclusion.DEFAULT_CONCLUSION_ID);
        RuleLoader.insertRuleConclusions(0, Main.KB.getRootRule().getConclusion());
	}
	
	public synchronized void executeQueryFile(String sqlFn, String[] bindStr)
	{
		executeQuery(new SqlReader(sqlFn).get(), bindStr);
	}
	
	/** select sql file 실행
	 * 
	 * @param sqlFn
	 * @param bindObj
	 * @return list of csv string
	 */
	public ArrayList<String[]> executeSelectQueryFile(String sqlFn, Object[] bindObj)
	{
		return executeSelectQuery(new SqlReader(sqlFn).get(), bindObj);
	}

	/** select sql 실행
	 * 
	 * @param sql
	 * @param bindObj
	 * @return list of csv string
	 */
    public ArrayList<String[]> executeSelectQuery(String sql, Object[] bindObj)
    {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        
        ArrayList<String[]> list = new ArrayList<String[]>();
        
        try {
            pstmt = c.prepareStatement(sql);
            if (bindObj != null)
            {
            	for (int i = 0; i < bindObj.length; i++)
            	{
            		Object tObj = bindObj[i];

            		if (tObj == null)
        			{
        				pstmt.setNull(i+1, Types.VARCHAR);
        			}
            		else if ( tObj instanceof String )
            		{
            			String bindStr = tObj.toString();
            			pstmt.setString(i+1, bindStr);
            		}
            		else if ( tObj instanceof Integer)
            		{
            			int val = ((Integer)tObj).intValue();
            			pstmt.setInt(i+1, val);
            		}
            		else if ( tObj instanceof Double)
            		{
            			double val = ((Double)tObj).doubleValue();
            			pstmt.setDouble(i+1, val);
            		}
            		else if ( tObj instanceof java.util.Date)
            		{
            			java.sql.Date tSqlDate = DateUtil.convertToSqlDate((java.util.Date)tObj);
            			pstmt.setDate(i+1, tSqlDate);
            		}
            	}
            }

            rs = pstmt.executeQuery();
            while (rs.next())
            {
            	list.add(Utility.convertResultSetToArray(rs));
            }
            return list;
        }
        catch ( Exception e )
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e);
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        
        return list;
    }
            
    /** select sql file 실행 (조회되는 건수는 1건이어야 하고 복수인 경우는 첫번째 정보를 반환한다)
	 * 
	 * @param sqlFn
	 * @param bindObj
	 * @return LinkedHashMap (key=컬럼명, value=컬럼값)
	 */
	public LinkedHashMap<String, String> executeSelectQueryFileToMap(String sqlFn, Object[] bindObj)
	{
		return executeSelectQueryToMap(new SqlReader(sqlFn).get(), bindObj);
	}

	/** select sql 실행 (조회되는 건수는 1건이어야 하고 복수인 경우는 첫번째 정보를 반환한다)
	 * 
	 * @param sql
	 * @param bindObj
	 * @return LinkedHashMap (key=컬럼명, value=컬럼값)
	 */
    public LinkedHashMap<String, String> executeSelectQueryToMap(String sql, Object[] bindObj)
    {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        
        try {
            pstmt = c.prepareStatement(sql);
            if (bindObj != null)
            {
            	for (int i = 0; i < bindObj.length; i++)
            	{
            		Object tObj = bindObj[i];

            		if (tObj == null)
        			{
        				pstmt.setNull(i+1, Types.VARCHAR);
        			}
            		else if ( tObj instanceof String )
            		{
            			String bindStr = tObj.toString();
            			pstmt.setString(i+1, bindStr);
            		}
            		else if ( tObj instanceof Integer)
            		{
            			int val = ((Integer)tObj).intValue();
            			pstmt.setInt(i+1, val);
            		}
            		else if ( tObj instanceof Double)
            		{
            			double val = ((Double)tObj).doubleValue();
            			pstmt.setDouble(i+1, val);
            		}
            		else if ( tObj instanceof java.util.Date)
            		{
            			java.sql.Date tSqlDate = DateUtil.convertToSqlDate((java.util.Date)tObj);
            			pstmt.setDate(i+1, tSqlDate);
            		}
            	}
            }

            rs = pstmt.executeQuery();
            while (rs.next())
            {
            	return Utility.convertResultSetToMap(rs);
            }
        }
        catch ( Exception e )
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        
        return new LinkedHashMap<String, String>();
    }

	public boolean executeQueryFile(String sqlFn, Object[] bindObj)
	{
		return executeQuery(new SqlReader(sqlFn).get(), bindObj);
	}

	public boolean executeQuery(String sql, Object[] bindObj)
	{
		PreparedStatement pstmt = null;
        try 
        {
            pstmt = c.prepareStatement(sql);
            if (bindObj != null)
            {
            	for (int i = 0; i < bindObj.length; i++)
            	{
            		Object tObj = bindObj[i];

            		if (tObj == null)
        			{
        				pstmt.setNull(i+1, Types.VARCHAR);
        			}
            		else if ( tObj instanceof String )
            		{
            			String bindStr = tObj.toString();
            			pstmt.setString(i+1, bindStr);
            		}
            		else if ( tObj instanceof Integer)
            		{
            			int val = ((Integer)tObj).intValue();
            			pstmt.setInt(i+1, val);
            		}
            		else if ( tObj instanceof Double)
            		{
            			double val = ((Double)tObj).doubleValue();
            			pstmt.setDouble(i+1, val);
            		}
            		else if ( tObj instanceof java.util.Date)
            		{
            			//시간 관리 못함
            			//java.sql.Date tSqlDate = SGDateUtils.convertToSqlDate((java.util.Date)tObj);
            			//pstmt.setDate(i+1, tSqlDate);

            			java.util.Date tUtilDate = (java.util.Date)tObj;
            			String sDate = DateUtil.convert(tUtilDate);
            			pstmt.setString(i+1,  sDate);
            		}
            	}
            }

            pstmt.execute();

            //SGLogger.info("Query (" + sql + ") executed successfully");
            return true;
        }
        catch ( Exception e )
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
	}

    public synchronized int insertQueryString(String sql)
    {
    	int inserted_id = 0;
        
        Statement stmt = null;
        ResultSet rs = null;
        try 
        {
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = stmt.getGeneratedKeys();
            if (rs.next())
            {
                inserted_id=rs.getInt(1);
            }

            Logger.info("Query (" + sql + ") executed successfully");
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
		}
        
        return inserted_id;
      }
    
    
    public int insertQuery(String tableName, String[] attributes, String[] values, boolean lastId)
    {
        int inserted_id = 0;
        
        Statement stmt = null;
        int attributeAmount = attributes.length;
        String attrSql = "";
        String valSql = "";    
        for(int i=0; i<attributeAmount; i++) {
            if(i==0){
                attrSql += " `" + attributes[i] + "`";
                valSql += " '" + values[i] + "'";
            } else {
                attrSql += ", `" + attributes[i] + "`";
                valSql += ", '" + values[i] + "'";
            }
        }
        try {
            stmt = c.createStatement();
            String sql = "INSERT INTO `" + tableName + "` " 
                       + " ( " + attrSql + ") "
                       + "VALUES ( " + valSql + ") ";      
            if(lastId){
                stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()){
                    inserted_id=rs.getInt(1);
                }
                rs.close();
            } else {
                stmt.executeUpdate(sql);
            }

            Logger.info("Query (" + sql + ") executed successfully");
            
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
        }
        finally
		{
        	if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
		}
        
        return inserted_id;
      }
    
    public synchronized void deleteQuery(String tableName, String columnName, int value)
    {
        Statement stmt = null;
       
        try {
            stmt = c.createStatement();
            String sql = "DELETE FROM `" + tableName + "` " 
                       + " WHERE `" + columnName 
                       + "` = " + value; 
            
            stmt.executeUpdate(sql);

            Logger.info("Query (" + sql + ") executed successfully");
            
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
        }
        finally
		{
        	if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
		}
    }
	
    public boolean createRDRTables()
    {
    	Statement stmt = null;
    	
        try {
            stmt = c.createStatement();
            String sqlFn = "createAllTables.sql";
            stmt.executeUpdate(new SqlReader(sqlFn).get());
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
		}
        
        Logger.info("All Tables are created successfully");
        return true;
    }
    
    public boolean insertDomainDetails(String domainName, String domainDesc,String domainReasoner)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("insertDomainDetail.sql").get());
	        pstmt.setString(1, domainName);
	        pstmt.setString(2, domainDesc);
	        pstmt.setString(3, domainReasoner);
	        pstmt.setString(4, Main.userid);
	        
	        pstmt.execute();
	        
	        String logStr = String.format("name[%s] desc[%s] reasoner[%s]", 
                                          domainName, domainDesc, domainReasoner);
	        Logger.info("@insert domainDetail : " + logStr);

	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteAllDomainData(String domainName)
    {
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
    	/** delete sequence is importance */
    	boolean flag = true;
    	flag &= this.deleteRuleCornerstoneInferenceResult(domainName);
    	flag &= this.deleteCornerstoneCase(domainName);
    	flag &= this.deleteRuleCornerstones(domainName);
    	flag &= this.deleteRuleConditions(domainName);
    	flag &= this.deleteRuleStructure(domainName);
    	flag &= this.deleteRuleConclusion(domainName);
    	flag &= this.deleteCategoricalValue(domainName);
    	flag &= this.deleteCaseStructure(domainName);
    	flag &= this.deleteDomainDetail(domainName);
    	
	    RDRDBManager.getInstance().doCommit(flag);
	    RDRDBManager.getInstance().setAutoCommit(true);
	    return flag;
    }
    
    public boolean deleteCaseStructure(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteCaseStructure.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteCaseStructureByAttribute(String domainName, String attrName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteCaseStructureByName.sql").get());
	    	pstmt.setString(1, domainName);
	    	pstmt.setString(2, attrName);
	        
	        pstmt.execute();
	        
	        String logStr = String.format("domain[%s] attr[%s]", domainName, attrName);
	        Logger.info("@delete caseStructureByAttribute : " + logStr);
	        
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteCategoricalValue(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteCategoricalValue.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteCategoricalValueByAttribute(String domainName, String attrName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteCategoricalValueByName.sql").get());
	    	pstmt.setString(1, domainName);
	    	pstmt.setString(2, domainName);
	    	pstmt.setString(3, attrName);
	        
	        pstmt.execute();
	        
	        String logStr = String.format("domain[%s] attr[%s]", domainName, attrName);
	        Logger.info("@delete categoricalValueByAttribute : " + logStr);
	        
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteCornerstoneCase(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteCornerstoneCase.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteDomainDetail(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteDomainDetail.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteRuleConclusion(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteRuleConclusion.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteRuleConditions(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteRuleConditions.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteRuleCornerstoneInferenceResult(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteRuleCornerstoneInferenceResult.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteRuleCornerstones(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteRuleCornerstones.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean deleteRuleStructure(String domainName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("deleteRuleStructure.sql").get());
	    	pstmt.setString(1, domainName);
	        
	        pstmt.execute();
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
	
    public boolean insertAttribute(IAttribute attr)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	int attribute_id = attr.getAttributeId();
	    	int value_type_id = attr.getValueType().getTypeCode();
	    	String attribute_name = attr.getName();
	    	String attribute_desc = attr.getDescription();
	    	
	    	pstmt = c.prepareStatement(new SqlReader("insertCaseStructure.sql").get());
	        pstmt.setString(1, Main.domainName);
	        pstmt.setInt(2, attribute_id);
	        pstmt.setInt(3, value_type_id);
	        pstmt.setString(4, attribute_name);
	        pstmt.setString(5,  attribute_desc);
	        pstmt.setString(6,  Main.userid);
	        
	        pstmt.execute();
	        
	        String logStr = String.format("domain[%s] id[%d] valueTypeId[%d] attrName[%s] attrDesc[%s]", 
	        		                      Main.domainName, attribute_id, value_type_id, attribute_name, attribute_desc);
	        Logger.info("@insert attribute : " + logStr);
	        
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean updateAttributeName(String domainName, String attrName, String newAttrName)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("updateAttributeName.sql").get());
	        pstmt.setString(1, newAttrName);
	        pstmt.setString(2,  Main.userid);
	        pstmt.setString(3, domainName);
	        pstmt.setString(4, attrName);
	        
	        pstmt.execute();
	        
	        String logStr = String.format("domain[%s] attrName[%s] newAttrName[%s]", 
                                          domainName, attrName, newAttrName);
	        Logger.info("@update attribute : " + logStr);

	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean updateAttributeDesc(String domainName, String attrName, String attrDesc)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("updateAttributeDesc.sql").get());
	        pstmt.setString(1, attrDesc);
	        pstmt.setString(2,  Main.userid);
	        pstmt.setString(3, domainName);
	        pstmt.setString(4, attrName);
	        
	        pstmt.execute();
	        	        String logStr = String.format("domain[%s] attrName[%s] attrDesc[%s]", 
                                          domainName, attrName, attrDesc);
	        Logger.info("@update attribute description : " + logStr);

	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean insertCategoricalValue(int attributeId, ArrayList<String> catValues)
    {
    	PreparedStatement pstmt = null;
    	
	    try
	    {
	    	pstmt = c.prepareStatement(new SqlReader("insertCategoricalValue.sql").get());
	    	
	    	String logStr = String.format("domain[%s] attrId[%d] categoricalValue : ",
                                           Main.domainName, attributeId);
	    	
	    	for(int i=0; i< catValues.size(); i++) 
	    	{
	    		pstmt.setString(1, Main.domainName);
		        pstmt.setInt(2, attributeId);
		        pstmt.setString(3, catValues.get(i));
		        pstmt.setString(4,  Main.userid);
		        pstmt.execute();
		        
		        logStr += "[";
		        logStr += catValues.get(i);
		        logStr += "]";
	    	}
	        
	        Logger.info("@insert categoricalValue : " + logStr);
	        return true;
	    }
	    catch (Exception e)
	    {
	    	Logger.error(e.getClass().getName() + ": " + e.getMessage(), e);
	    	return false;
	    }
	    finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }

    
    /**
     * insert attribute (cornerstone_case) to db
     * 
     * @param attr
     */
    public boolean insertAttributeToCornerStoneCaseWithNullValue(IAttribute attr)
    {
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        try 
        {
            pstmt1 = c.prepareStatement(new SqlReader("selectCornerstoneCaseID.sql").get());
            pstmt1.setString(1,  Main.domainName);
            rs = pstmt1.executeQuery();
            
            pstmt2 = c.prepareStatement(new SqlReader("insertCornerstoneCase.sql").get());
            
            while ( rs.next() ) {
                
                int caseId = rs.getInt(1);
                int attributeId = attr.getAttributeId();
                String caseValue = RDRConfig.getRepNullValueString();
                
                pstmt2.setString(1,  Main.domainName);
                pstmt2.setInt(2, caseId);
                pstmt2.setInt(3, attributeId);
                pstmt2.setString(4, caseValue);
                pstmt2.setString(5,  Main.userid);

                pstmt2.execute();
            }

            return true;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt1 != null) try { pstmt1.close(); } catch(SQLException ex) {}
        	if (pstmt2 != null) try { pstmt2.close(); } catch(SQLException ex) {}
		}
    }
    
    public int getNewCornerstoneCaseId()
    {
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        try 
        {
            pstmt1 = c.prepareStatement(new SqlReader("selectCornerstoneCaseID.sql").get());
            pstmt1.setString(1,  Main.domainName);
            rs = pstmt1.executeQuery();

            int maxId = 0;
            while ( rs.next() ) 
            {
                int caseId = rs.getInt(1);
                maxId = Math.max(maxId, caseId);
            }
 
            return maxId+1;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return -1;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt1 != null) try { pstmt1.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean insertRuleConclusion(int conclusionId, int valueTypeId, String conclusionName)
    {
        PreparedStatement pstmt = null;
        
        try 
        {
        	pstmt = c.prepareStatement(new SqlReader("insertRuleConclusion.sql").get());
        	pstmt.setString(1,  Main.domainName);
            pstmt.setInt(2, conclusionId);
            pstmt.setInt(3, valueTypeId);
            pstmt.setString(4, conclusionName);
            pstmt.setString(5,  Main.userid);
                
            pstmt.execute();
            
            String logStr = String.format("domain[%s] id[%d] valueTypeId[%d] name[%s]", 
            		                      Main.domainName, conclusionId, valueTypeId, conclusionName);
            Logger.info("@insert ruleConclusion : " + logStr);
            
            return true;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean updateRuleConclusion(int conclusionId, String conclusionName)
    {
        PreparedStatement pstmt = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("updateRuleConclusion.sql").get());
            pstmt.setString(1, conclusionName);
            pstmt.setString(2,  Main.userid);
            pstmt.setString(3,  Main.domainName);
            pstmt.setInt(4, conclusionId);
                
            pstmt.execute();
            
            String logStr = String.format("domain[%s] id[%d] name[%s]", 
                                          Main.domainName, conclusionId, conclusionName);
            Logger.info("@update ruleConclusion : " + logStr);
            
            return true;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean insertRuleStructure(int ruleId, int parentId, int conclusionId)
    {
        PreparedStatement pstmt = null;
        
        try 
        {
            pstmt = c.prepareStatement(new SqlReader("insertRuleStructure.sql").get());
            pstmt.setString(1, Main.domainName);
            pstmt.setInt(2, ruleId);
            pstmt.setInt(3, parentId);
            pstmt.setInt(4, conclusionId);
            pstmt.setString(5,  Main.userid);    
            pstmt.execute();
            
            String logStr = String.format("domain[%s] ruleId[%d] parentId[%d] conclusionId[%d]", 
                    						Main.domainName, ruleId, parentId, conclusionId);
            Logger.info("@insert ruleStructure : " + logStr);

            return true;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    
    public boolean insertRuleCondition(int ruleId, int attrId, int operId, String conditionVal)
    {
        PreparedStatement pstmt = null;
        
        try 
        {
            pstmt = c.prepareStatement(new SqlReader("insertRuleCondition.sql").get());
            pstmt.setString(1,  Main.domainName);
            pstmt.setInt(2, ruleId);
            pstmt.setInt(3, attrId);
            pstmt.setInt(4, operId);
            pstmt.setString(5, conditionVal);
            pstmt.setString(6,  Main.userid);
                
            pstmt.execute();
            
            String logStr = String.format("domain[%s] ruleId[%d] attrId[%d] opId[%d] condVal[%s]", 
					                      Main.domainName, ruleId, attrId, operId, conditionVal);
            Logger.info("@insert ruleCondition : " + logStr);

            return true;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    
    public boolean insertRuleCornerstone(int ruleId, int caseId)
    {
        PreparedStatement pstmt = null;
        
        try 
        {
            pstmt = c.prepareStatement(new SqlReader("insertRuleCornerstone.sql").get());
            pstmt.setString(1,  Main.domainName);
            pstmt.setInt(2, ruleId);
            pstmt.setInt(3, caseId);
            pstmt.setString(4,  Main.userid);
                
            pstmt.execute();
            
            String logStr = String.format("domain[%s] ruleId[%d] caseId[%d]", 
					                       Main.domainName, ruleId, caseId);
            Logger.info("@insert ruleCornerstone : " + logStr);

            return true;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public boolean insertCornerstoneValue(CornerstoneCase cornerstoneCase)
    {
        PreparedStatement pstmt = null;
        
        CaseStructure caseStructure = cornerstoneCase.getCaseStructure();
        Set structures = caseStructure.getBase().entrySet();

        try 
        {            
            // Get an iterator
            Iterator attrIterator = structures.iterator();
            
            //Logger.info("adding new cornerstone case values.");

            int cnt = 0;
            while (attrIterator.hasNext()) 
            {
                Map.Entry me3 = (Map.Entry) attrIterator.next();
                IAttribute aAttr = (IAttribute) me3.getValue();    
                Value aValue = cornerstoneCase.getValue(aAttr);
                String aValueStr = "";
                
                if ( aValue == null ) continue;       //case에 없는 attribute 는 저장하지 않음
                if ( aValue.isNullValue()) continue;  //null value (ARFF의 ?)는 저장하지 않음

                aValueStr = aValue.toString();
                
                pstmt = c.prepareStatement(new SqlReader("insertCornerstoneCase.sql").get());
                pstmt.setString(1,  Main.domainName);
                pstmt.setInt(2, cornerstoneCase.getCaseId());
                pstmt.setInt(3, aAttr.getAttributeId());
                pstmt.setString(4, aValueStr);
                pstmt.setString(5, Main.userid);

                pstmt.execute();
                cnt++;
            }
            
            Logger.info("Total " + cnt + " attribute values are added.");
            Logger.info("@insert cornerstoneCase : " + cornerstoneCase.toString());
            
            return true;
        } 
        catch ( Exception e )
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    
    public boolean insertRuleCornerstoneInferenceResult(int caseId, int ruleId)
    {
        PreparedStatement pstmt = null;
        
        try 
        {
            pstmt = c.prepareStatement(new SqlReader("insertRuleCornerstoneInferenceResult.sql").get());
            pstmt.setString(1, Main.domainName);
            pstmt.setInt(2, caseId);
            pstmt.setInt(3, ruleId);
            pstmt.setString(4, Main.userid);
                
            pstmt.execute();
            
            return true;
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        finally
		{
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    }
    
    public ArrayList<HashMap<String,String>> getDomainDetails(String pDomainName)
    {
    	ArrayList<HashMap<String, String>> domainList = new ArrayList<>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectDomainDetail.sql").get());
            pstmt.setString(1, (pDomainName == null ? "%" : pDomainName));
            
            rs = pstmt.executeQuery();
          
            while ( rs.next() ) 
            {
            	HashMap<String, String> domainDetails = new HashMap<>();
            	
                String domainName = rs.getString(1);
                String domainDesc = rs.getString(2);
                String domainReasoner = rs.getString(3);
                domainDetails.put("domainName", domainName);
                domainDetails.put("domainDesc", domainDesc);
                domainDetails.put("domainReasoner", domainReasoner);
                
                Logger.info("domain : " + domainName + ", " + domainDesc + ", " + domainReasoner);
                domainList.add(domainDetails);
            }
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        
        return domainList;
    }
    
    public ArrayList<String> getDomainNames()
    {
    	ArrayList<String> domains = new ArrayList<String>();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectDomainDetail.sql").get());
            pstmt.setString(1, "%");
            rs = pstmt.executeQuery();
            
            while ( rs.next() ) {
                String domainName = rs.getString(1);
                domains.add(domainName);
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        return domains;
    }
    
    public CaseStructure getCaseStructure()
    {
        CaseStructure caseStructure = new CaseStructure();
       
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectCaseStructure.sql").get());
            pstmt.setString(1, Main.domainName);
            
            rs = pstmt.executeQuery();
            Logger.info("Case structure loading...");
            
            int cnt = 0;
            int dupCnt = 0;
            while ( rs.next() ) {
                int attributeId = rs.getInt(2);
                int valueTypeId = rs.getInt(3);
                String attrName = rs.getString(4);
                String attrDesc = rs.getString(5);
                
                IAttribute attr = AttributeFactory.createAttribute(valueTypeId);
                attr.setAttributeId(attributeId);
                attr.setAttributeType(Attribute.CASE_TYPE);
                attr.setName(attrName);
                attr.setDescription(attrDesc);
                attr.setValueType(new ValueType(valueTypeId));
                
                if(attr.isThisType("CATEGORICAL")){
                    pstmt2 = c.prepareStatement(new SqlReader("selectCategoricalValue.sql").get());
                    pstmt2.setString(1, Main.domainName);
                    pstmt2.setInt(2, attributeId);
                    
                    rs2 = pstmt2.executeQuery();
                    
                    //adding null value -> blocked, 20180222
                    //attr.addCategoricalValue("");
                    
                    while (rs2.next()){
                        String catVal = rs2.getString(4);
                        attr.addCategoricalValue(catVal);
                    }
                }

                if ( !caseStructure.addAttribute(attr) )
                {
                	dupCnt++;
                	Logger.warn("CaseStructure load from DB, attr duplicated, id(" + attributeId + ", " + attr.toString());
                }
                
                cnt++;
            }
            
            Logger.info("CaseStructure Load from DB, cnt : " + cnt + ", dup cnt : " + dupCnt);
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (rs2 != null) try { rs2.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
        	if (pstmt2 != null) try { pstmt2.close(); } catch(SQLException ex) {}
		}
        return caseStructure;
    }
    
    /** 사용중이거나 체크에러시 true 반환, 사용중이 아니면 false 반환
     * 
     * @param domainName
     * @param attrName
     * @return
     */
    public boolean isAttributeUsedCondition(String domainName, String attrName)
    {
    	ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            pstmt = c.prepareStatement(new SqlReader("selectAttributeUsedConditions.sql").get());
            pstmt.setString(1, domainName);
            pstmt.setString(2,  domainName);
            pstmt.setString(3,  attrName);
            
            rs = pstmt.executeQuery();
            
            while ( rs.next() ) 
            {
                int count = rs.getInt(1);
                return (count > 0);
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return true;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        
        return true;
    }
    
    /** 사용중이거나 체크에러시 true 반환, 사용중이 아니면 false 반환
     * 
     * @param domainName
     * @param attrName
     * @return
     */
    public boolean isAttributeUsedCornerstone(String domainName, String attrName)
    {
    	ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            pstmt = c.prepareStatement(new SqlReader("selectAttributeUsedCornerstones.sql").get());
            pstmt.setString(1, domainName);
            pstmt.setString(2,  domainName);
            pstmt.setString(3,  attrName);
            
            rs = pstmt.executeQuery();
            
            while ( rs.next() ) 
            {
                int count = rs.getInt(1);
                return (count > 0);
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return true;
        } 
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        
        return true;
    }
    
    public RuleSet getRuleStructureSet(HashMap<Integer, ConditionSet> conditionHashMap, ConclusionSet conclusionSet) 
    {
        RuleSet kb = new RuleSet();
        
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        
        try 
        {
            pstmt = c.prepareStatement(new SqlReader("selectRuleStructure.sql").get());
            pstmt.setString(1, Main.domainName);
            
            rs = pstmt.executeQuery();
            Logger.info("Rule loading...");
            
            //adding rule id 0 as mysql does not provice auto_increment with id 0
            Conclusion rootConclusion = conclusionSet.getConclusionById(0);
            Rule rootRule = new Rule();
            rootRule.setRuleId(0);
            rootRule.setConclusion(rootConclusion);
            kb.addRule(rootRule);
            kb.setRootRule(rootRule);   
            
            while ( rs.next() ) 
            {
                Rule aRule = new Rule();
                
                int ruleId = rs.getInt(2);
                int parentId = rs.getInt(3);                
                int conclusionId = rs.getInt(4);
                java.util.Date creationDate 
                	= (rs.getTimestamp(5) == null ? null : new java.util.Date(rs.getTimestamp(5).getTime()));
                java.util.Date updateDate 
                	= (rs.getTimestamp(6) == null ? null : new java.util.Date(rs.getTimestamp(5).getTime()));   
                
                if (ruleId==0)
                {
                    if(conclusionId==0)
                    {
                        Conclusion aConclusion = conclusionSet.getConclusionById(conclusionId);
                        aRule.setRuleId(ruleId);
                        aRule.setConclusion(aConclusion);
                    } 
                    else 
                    {
                        aRule = RuleBuilder.buildRootRule();
                    }

                    aRule.setCreationDate(creationDate);
                    aRule.setUpdateDate(updateDate);
                    
                    kb.addRule(aRule);
                    kb.setRootRule(aRule);      
                    
                } 
                else 
                {     
                    Conclusion aConclusion = new Conclusion();
                    if(conclusionId == 0)
                    {
                        //root conclusion
                        Value rootValue = new Value(ValueType.TEXT, "");
                        aConclusion = new Conclusion(rootValue);
                        aConclusion.setConclusionId(conclusionId);
                    } 
                    else if (conclusionId < 0)
                    {
                    	aConclusion.setConclusionId(Conclusion.NULL_CONCLUSION_ID);
                    }
                    else 
                    {
                        aConclusion = conclusionSet.getConclusionById(conclusionId);
                    }
                    aRule.setRuleId(ruleId);
                    aRule.setConclusion(aConclusion);
                    aRule.setParent(kb.getRuleById(parentId));                    
                    kb.getRuleById(parentId).addChildRule(aRule);
                    
                    if (conditionHashMap.containsKey(new Integer(ruleId)))
                    {
                    	aRule.setConditionSet(conditionHashMap.get(ruleId));
                    }
                    
                    aRule.setCreationDate(creationDate);
                    aRule.setUpdateDate(updateDate);
                    
                    kb.addRule(aRule);
                }
                //Logger.info("Rule loaded: " + aRule.toString());
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        return kb;
    }
    
    public HashMap<Integer, ArrayList<Integer>> getCornerstoneCaseIdsHashMap() 
    {
        HashMap<Integer, ArrayList<Integer>> caseIdHashMap = new HashMap<>();

        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectRuleCornerstone.sql").get());
            pstmt.setString(1, Main.domainName);
            
            rs = pstmt.executeQuery();
            while(rs.next()) {
                int ruleId = rs.getInt(2);
                int caseId = rs.getInt(3);           
                if(caseIdHashMap.containsKey(ruleId)){
                    caseIdHashMap.get(ruleId).add(caseId);
                } else {
                    ArrayList<Integer> caseIdList = new ArrayList();
                    caseIdList.add(caseId);
                    caseIdHashMap.put(ruleId, caseIdList);
                }
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        } 
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        return caseIdHashMap;
    }
    
    public CornerstoneCaseSet getCornerstoneCaseSet(CaseStructure caseStructure) 
    {
        CornerstoneCaseSet cornerstoneCaseSet = new CornerstoneCaseSet();
        
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectCornerstoneCase.sql").get());
            pstmt.setString(1, Main.domainName);
            
            rs = pstmt.executeQuery();
            while(rs.next()) 
            {
                int caseId = rs.getInt(2); 
                int attrId = rs.getInt(3); 
                String caseVal = rs.getString(4);

                /** missing value (null data)는 db 저장에서 제외되므로 loading되지 않는 항목은 null 처리한다.
                 *  저장된 데이터가 missing value string 인 경우 null 처리 한다.
                 */
                CornerstoneCase currentCornerstoneCase;
                if(!cornerstoneCaseSet.isCaseIdExist(caseId))
                {
                    currentCornerstoneCase = new CornerstoneCase(caseStructure);
                    currentCornerstoneCase.setCaseId(caseId);
                    currentCornerstoneCase.initNullValue();
                    
                    IAttribute attr = caseStructure.getAttributeByAttrId(attrId);
                    
                    if ( attr == null)
                    {
                    	Logger.error("CornerstoneCase loading, attr by attrid not found, attrId : " + attrId);
                    	continue;
                    }
                    
                    if (RDRConfig.isNullValueString(caseVal) ||
                    	(attr.getValueType().getTypeCode() == ValueType.CONTINUOUS && 
                         StringUtil.isNumeric(caseVal) == false))
                    {
                        Value value = new Value(ValueType.NULL_TYPE);
                        currentCornerstoneCase.setValue(attr.getName(), value);
                    } 
                    else 
                    {
                        Value value =new Value(attr.getValueType(), caseVal);
                        currentCornerstoneCase.setValue(attr.getName(), value);
                    }
                    
                    //ucciri@gmail.com 
                    //여기서 이함수를 쓰면 안됨, case의 value가 아직 모두 setting되기 전이기 때문
                    //cornerstoneCaseSet.addCornerstoneCase(null, currentCornerstoneCase);
                    cornerstoneCaseSet.addCornerstoneCase(currentCornerstoneCase);
                } 
                else 
                {
                    currentCornerstoneCase = cornerstoneCaseSet.getCornerstoneCaseById(caseId);
                    IAttribute attr = caseStructure.getAttributeByAttrId(attrId);
                    
                    if ( attr == null)
                    {
                    	Logger.error("CornerstoneCase loading, attr by attrid not found, attrId : " + attrId);
                    	continue;
                    }

                    if (RDRConfig.isNullValueString(caseVal) ||
                        (attr.getValueType().getTypeCode() == ValueType.CONTINUOUS && 
                         StringUtil.isNumeric(caseVal) == false))
                    {
                        Value value =new Value(ValueType.NULL_TYPE);
                        currentCornerstoneCase.setValue(attr.getName(), value);
                    } 
                    else 
                    {
                        Value value =new Value(attr.getValueType(), caseVal);
                        currentCornerstoneCase.setValue(attr.getName(), value);
                    }
                }
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        return cornerstoneCaseSet;
    }
    
    
    public HashMap<Integer, ArrayList<Integer>> getCornerstoneCaseInferenceResultHashMap() 
    {
        HashMap<Integer, ArrayList<Integer>> inferenceResultHashMap = new HashMap<>();
       
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectRuleCornerstoneInferenceResult.sql").get());
            pstmt.setString(1, Main.domainName);
            
            rs = pstmt.executeQuery();
            while(rs.next()) {
                int caseId = rs.getInt(2);
                int ruleId = rs.getInt(3);           
                if(inferenceResultHashMap.containsKey(caseId)){
                    inferenceResultHashMap.get(caseId).add(ruleId);
                } else {
                    ArrayList<Integer> ruleIdList = new ArrayList();
                    ruleIdList.add(ruleId);
                    inferenceResultHashMap.put(caseId, ruleIdList);
                }
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        return inferenceResultHashMap;
    }
    
    public HashMap<Integer, ConditionSet> getConditionHashMap() 
    {
        HashMap<Integer, ConditionSet> conditionHashMap = new HashMap<>();
        
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectRuleCondition.sql").get());
            pstmt.setString(1, Main.domainName);
            
            rs = pstmt.executeQuery();
            Logger.info("Rule conditions loading...");
            while(rs.next()) {
                int conditionId = rs.getInt(1);
                int ruleId = rs.getInt(3);
                int attributeId = rs.getInt(4);
                int operatorId = rs.getInt(5);                
                String conditionValue = rs.getString(6);
                
                //System.out.println( conditionId + "," + ruleId + "," + attributeId + "," + operatorId + "," + conditionValue);                
                
                IAttribute attr = Main.domain.getCaseStructure().getAttributeByAttrId(attributeId);      
                Operator oper = new Operator(operatorId);
                Condition aCondition = RuleBuilder.buildRuleCondition(Main.domain.getCaseStructure(), 
                		                                              attr.getName(), 
                		                                              oper.getOperatorName(), 
                		                                              conditionValue);
                
                //Logger.info("Condition loaded: " + aCondition.toString());
                
                if(conditionHashMap.containsKey(ruleId)){
                    conditionHashMap.get(ruleId).addCondition(aCondition);
                } else {
                    ConditionSet tempConditionSet = new ConditionSet();
                    tempConditionSet.addCondition(aCondition);
                    conditionHashMap.put(ruleId, tempConditionSet);
                }
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        return conditionHashMap;
    }
    
    public ConclusionSet getConclusionSet() 
    {
        ConclusionSet conclusionSet = new ConclusionSet();    
        
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = c.prepareStatement(new SqlReader("selectRuleConclusion.sql").get());
            pstmt.setString(1, Main.domainName);
            
            rs = pstmt.executeQuery();
            Logger.info("Rule conclusions loading...");
            
            //adding conclusion id 0 as mysql does not provice auto_increment with id 0
            Conclusion rootConclusion = new Conclusion();
            rootConclusion.setConclusionId(0);
            rootConclusion.setConclusionValue(new Value(2, ""));
            conclusionSet.addConclusion(rootConclusion);
            
            while ( rs.next() ) {
                
                int conclusionId = rs.getInt(2);
                int valueTypeId = rs.getInt(3);
                String conclusionName = rs.getString(4);
//                System.out.println(conclusionId + "," + valueTypeId + "," + conclusionName );
                
                Conclusion aConclusion = new Conclusion();
                aConclusion.setConclusionId(conclusionId);
                aConclusion.setConclusionValue(new Value(valueTypeId, conclusionName));
                
//                Logger.info("Conclusion loaded: " + aConclusion.toString());
                
                conclusionSet.addConclusion(aConclusion);
            }
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return null;
        }
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
        return conclusionSet;
    }
    
    public boolean deleteRule(String domainName, int aRuleId, StringBuilder sb)
    {
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
    	boolean flag = doDeleteRule(domainName, aRuleId, sb);
    	
    	RDRDBManager.getInstance().doCommit(flag);
    	RDRDBManager.getInstance().setAutoCommit(true);
    	
    	return flag;
    }
	
    private boolean doDeleteRule(String domainName, int aRuleId, StringBuilder sb)
    {
    	Logger.info("@deleteRule, ruleId : " + aRuleId);
    	
    	ResultSet rs = null;
        PreparedStatement pstmt = null;
        
        String logStr;
        
        try 
        {
        	//삭제대상 Rule의 결론 id
        	int aConclusionId = -1;
        	
            pstmt = c.prepareStatement(new SqlReader("selectRuleStructure.sql").get());
            pstmt.setString(1, Main.domainName);
            
            HashMap<Integer, ArrayList<Integer>> conclusion2Rule 
            	= new HashMap<Integer, ArrayList<Integer>>();
            
            boolean found = false;
            
            rs = pstmt.executeQuery();
            while ( rs.next() ) 
            {
                int ruleId = rs.getInt(2);
                int parentId = rs.getInt(3);                
                int conclusionId = rs.getInt(4);
                
                //leaf rule만 삭제가능
                if (aRuleId == parentId)
                {
                	sb.append("child가 존재하는 rule은 삭제할 수 없습니다.");
                	Logger.info("child is exist, so can be deleted");
                	return false;
                }
                
                if (aRuleId == ruleId)
                {
                	aConclusionId = conclusionId;
                	found = true;
                }
                
                if (conclusion2Rule.containsKey(conclusionId))
                {
                	conclusion2Rule.get(conclusionId).add(ruleId);
                }
                else
                {
                	ArrayList<Integer> ruleList = new ArrayList<Integer>();
                	ruleList.add(ruleId);
                	conclusion2Rule.put(conclusionId, ruleList);
                }
            }
            
            if (!found)
            {
            	sb.append("rule이 존재하지 않습니다. ruleId : " + aRuleId);
            	Logger.warn("rule is not exist in tb_rule_structure, ruleId : " + aRuleId);
            	return false;
            }
            
            //rule conclusion 삭제
            if (conclusion2Rule.containsKey(aConclusionId) &&
                conclusion2Rule.get(aConclusionId).size() > 1)
            {
            	Logger.info("not delete conclusion, used in other rules, conclusionId : " + aConclusionId);
            }
            else
            {
            	pstmt = c.prepareStatement(new SqlReader("deleteRuleConclusionByID.sql").get());
    	    	pstmt.setString(1, domainName);
    	        pstmt.setInt(2,  aConclusionId);
    	        pstmt.execute();
    	        
    	        logStr = String.format("domain[%s], conclusionId[%d]",
                                       domainName, aConclusionId);
    	        Logger.info("@delete ruleConclusion, " + logStr);
            }
            
            HashMap<Integer, ArrayList<Integer>> rule2Cornerstone 
            	= this.getCornerstoneCaseIdsHashMap();
            
            if (rule2Cornerstone.containsKey(aRuleId))
            {
            	HashMap<Integer, HashSet<Integer>> cc2Rule
            		= new HashMap<Integer, HashSet<Integer>>();
            	
            	Iterator<Integer> keys = rule2Cornerstone.keySet().iterator();
                while (keys.hasNext())
                {
                    Integer rid = keys.next();
                    ArrayList<Integer> ccList = rule2Cornerstone.get(rid);
                    for (int ci = 0; ci < ccList.size(); ci++)
                    {
                    	Integer cid = ccList.get(ci);
                    	
                    	if (cc2Rule.containsKey(cid))
                    	{
                    		cc2Rule.get(cid).add(rid);
                    	}
                    	else
                    	{
                    		HashSet<Integer> newSet = new HashSet<Integer>();
                    		newSet.add(rid);
                    		cc2Rule.put(cid, newSet);
                    	}
                    }
                }
                                	
                //cornerstone case 삭제
            	ArrayList<Integer> ccList = rule2Cornerstone.get(aRuleId);
            	for (int ci = 0; ci < ccList.size(); ci++)
            	{
            		if (cc2Rule.get(ccList.get(ci)).size() > 1)
            		{
            			Logger.info("not delete cornerstoneCase, used in other rules, cornerstoneCaseId : " + ccList.get(ci));
            		}
            		else
            		{
            			pstmt = c.prepareStatement(new SqlReader("deleteCornerstoneCaseByID.sql").get());
            	    	pstmt.setString(1, domainName);
            	        pstmt.setInt(2, ccList.get(ci).intValue());
            	        pstmt.execute();
            	        
            	        logStr = String.format("domain[%s], cornerstoneCaseId[%d]",
                                				domainName, ccList.get(ci).intValue());
            	        Logger.info("@delete cornerstoneCase, " + logStr);
            		}
            	}
            }
            
            //rule cornerstone 삭제
			pstmt = c.prepareStatement(new SqlReader("deleteRuleCornerstonesByID.sql").get());
	    	pstmt.setString(1, domainName);
	    	pstmt.setInt(2,  aRuleId);
	        pstmt.execute();
	        
	        logStr = String.format("domain[%s], ruleId[%d]", domainName, aRuleId);
	        Logger.info("@delete ruleCornerstones, " + logStr);
            
	        //rule condition 삭제
	        pstmt = c.prepareStatement(new SqlReader("deleteRuleConditionsByID.sql").get());
	    	pstmt.setString(1, domainName);
	    	pstmt.setInt(2,  aRuleId);
	        pstmt.execute();
	        
	        logStr = String.format("domain[%s], ruleId[%d]", domainName, aRuleId);
	        Logger.info("@delete ruleConditions, " + logStr);

	        //rule structure 삭제
	        pstmt = c.prepareStatement(new SqlReader("deleteRuleStructureByID.sql").get());
	    	pstmt.setString(1, domainName);
	    	pstmt.setInt(2,  aRuleId);
	        pstmt.execute();
	        
	        logStr = String.format("domain[%s], ruleId[%d]", domainName, aRuleId);
	        Logger.info("@delete ruleStructure, " + logStr);
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            sb.append(e.getMessage());
            return false;
        } 
        finally
		{
        	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
		}
    	
    	return true;
    }

}
