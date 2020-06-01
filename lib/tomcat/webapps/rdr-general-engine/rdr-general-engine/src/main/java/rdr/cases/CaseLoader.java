/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
     
package rdr.cases;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import rdr.apps.Main;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.model.AttributeFactory;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.db.RDRDBManager;
import rdr.mysql.MysqlConnection;
import rdr.utils.RDRConfig;
import rdr.utils.RDRConstants;
import rdr.utils.StringUtil;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class CaseLoader {
   
    /**
     * insert case structure details into db
     * 
     * @param caseStructure
     */
    public static boolean insertCaseStructure(CaseStructure caseStructure){
      
    	RDRDBManager.getInstance().setAutoCommit(false);
    	boolean flag = true;
    	
        String[] attributeNames = caseStructure.getAttributeNameArray();
        for(int i=0; i< attributeNames.length; i++) 
        {
            IAttribute atrr = caseStructure.getAttributeByName(attributeNames[i]);
            
            if (attributeNames[i].equals(RDRConstants.RDRClassAttributeName))
            	continue;
            
            if (RDRDBManager.getInstance().insertAttribute(atrr) == false)
            {
            	flag = false;
            	break;
            }
            
            //if the attribute is categorical, add categorical valuse
            if(atrr.isThisType("CATEGORICAL"))
            {
            	if (insertCategoricalValues(atrr.getAttributeId(), 
            			                    atrr.getCategoricalValues()) == false)
            	{
            		flag = false;
            		break;
            	}
            }
        }
        
        RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
        return flag;
    }
    
    /**
     * insert attribute (casestructure) to db
     * 
     * @param attr
     */
    public static boolean insertAttribute(ArrayList<IAttribute> attrList)
    {
    	if (attrList.isEmpty()) return true;
    	
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
    	boolean flag = true;
    	
    	for (int i = 0; i < attrList.size(); i++)
    	{
    		IAttribute attr = attrList.get(i);
    		
	    	flag &= RDRDBManager.getInstance().insertAttribute(attr);
	    	
	        if (attr.isThisType("CATEGORICAL")) 
	        {
	        	flag &= insertCategoricalValues(attr.getAttributeId(), 
	        			                        attr.getCategoricalValues());
	        }
	        
	        if (flag)
	        	Logger.info("attribute (" + attr.getName() + ") added.");
	        else
	        {
	        	Logger.info("attribute (" + attr.getName() + ") add failed.");
	        	break;
	        }
    	}
    	
    	RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
        return flag;
    }
    
    /**
     * insert case structure details into db
     * 
     * @return 
     */
    public static CaseStructure loadCaseStructureFromDB() {
        CaseStructure caseStructure = RDRDBManager.getInstance().getCaseStructure();
        return caseStructure;
    }
    
    /**
     * insert categorical value for the case structure 
     * 
     * @param attribute_id
     * @param catValues
     */
    public static boolean insertCategoricalValues(int attributeId, ArrayList<String> catValues){
        return RDRDBManager.getInstance().insertCategoricalValue(attributeId, catValues);
    }
    
    public static boolean addCategoricalValue(String attrName, String catValue, StringBuilder sb)
    {
    	CaseStructure aCaseStructure = CaseLoader.loadCaseStructureFromDB();
    	if (aCaseStructure == null) 
    	{
    		sb.append("case structure loading failed");
    		return false;
    	}
    	
    	IAttribute attr = aCaseStructure.getAttributeByName(attrName);
    	if (attr == null) 
    	{
    		sb.append("attribute is not found, attribute : " + attrName);
    		return false;
    	}
    	
    	if (attr.getValueType().getTypeCode() != ValueType.CATEGORICAL)
    	{
    		sb.append("attribute is not categorical value type, attribute : " + attrName);
    		return false;
    	}
    	
    	ArrayList<String> catList = attr.getCategoricalValues();
    	for (int i = 0; i < catList.size(); i++)
    	{
    		if (catValue.equals(catList.get(i)))
    		{
    			sb.append("categorical value is already exist, attribute : " + attrName + " value : " + catValue);
    			return false;
    		}
    	}
    	
    	ArrayList<String> catValues = new ArrayList<String>();
    	catValues.add(catValue);
    	
    	return RDRDBManager.getInstance().insertCategoricalValue(attr.getAttributeId(), catValues); 
    }
    
    public static boolean deleteAttribute(String domainName, String attrName)
    {
    	if (RDRDBManager.getInstance().isAttributeUsedCondition(domainName, attrName) ||
    		RDRDBManager.getInstance().isAttributeUsedCornerstone(domainName, attrName))
    	{
    		Logger.warn("deleteAttribute attribute used, cannot be deleted, attrName : " + attrName);
    		return false;
    	}
    	
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
    	boolean flag = true;
    	flag &= RDRDBManager.getInstance().deleteCategoricalValueByAttribute(domainName, attrName);
    	flag &= RDRDBManager.getInstance().deleteCaseStructureByAttribute(domainName, attrName);
    	
    	if ( flag ) RDRDBManager.getInstance().commit();
        else RDRDBManager.getInstance().rollback();
        
        if (flag )
        	Logger.info("attribute (" + attrName + ") deleted successfully.");
        
        RDRDBManager.getInstance().setAutoCommit(true);
        return flag;
    }
    
    public static boolean modifyAttributeName(String domainName, String attrName, String newAttrName)
    {
    	return RDRDBManager.getInstance().updateAttributeName(domainName, attrName, newAttrName);
    }
    
    public static boolean modifyAttributeDesc(String domainName, String attrName, String attrDesc)
    {
    	return RDRDBManager.getInstance().updateAttributeDesc(domainName, attrName, attrDesc);
    }
    
    /**
     * create arff file
     * 
     * @throws java.lang.Exception
     */
    public static void createArffFileWithCaseStructure() throws Exception {
//        try (PrintWriter writer = new PrintWriter(arffCaseLocation)) {
//            writer.println("% DO NOT CHANGE CASE STRUCTURE.");
//            writer.println("% CHAGING CASE STRUCTURE WILL CAUSE FAILURE IN RETRIEVING KNOWLEDGE BASE.");
//            writer.println("@relation	" +  Main.domain.getDomainName());
//            CaseStructure caseStructure = Main.domain.getCaseStructure();
//            String[] attrNameArray = caseStructure.getAttributeNameArray();
//            
//            for (String attrName : attrNameArray) {
//                IAttribute attr = caseStructure.getAttributeByName(attrName);
//                writer.print("@attribute	" + attr.getName());
//                writer.println("	" + convertAttributeStringFromRDRAttrToArff(attr));
//            }
//            writer.println("");
//            writer.println("");
//            writer.print("@data");
//            
//            writer.close();
//        }
    }
    
    /**
     * insert case into arff file
     * 
     * @param aCase
     * @throws java.io.FileNotFoundException
     */
    public static void insertCase(Case aCase) throws FileNotFoundException{
    	
    	String arffFile = RDRConfig.getArffFile();
    	
        PrintWriter writer 
        	= new PrintWriter(new FileOutputStream(new File(arffFile), true /* append = true */)); 
        
        //give a newline
        writer.append("\n");
        
        String caseValueString = "";
        
        LinkedHashMap<String, Value> valHashMap = aCase.getValues();       
        Set set = valHashMap.entrySet();
        Iterator iter = set.iterator();
        int cnt = 0;
        while (iter.hasNext()) 
        {
            Map.Entry me = (Map.Entry) iter.next();
            Value value = (Value) me.getValue();
            
            String caseValue = "";
            if (value.isNullValue())
            {
            	caseValue = "?"; //null은 missing value로, by ucciri@gmail.com
            }
            else
            {
            	caseValue = value.getActualValue().toString();      
            }
            
            caseValueString += caseValue;      
            cnt++;
            if(cnt != valHashMap.size()){
                caseValueString += ",";
            }
        }
        writer.append(caseValueString);
        writer.close();
    }
    
    
    
    /**
     * import arff dataset into rdr case set
     * - db caseStructure에 arff 추가항목을 반영 (DB에 insert 하지 않음)
     * - arff 의 모든 case data loading
     * 단, arff에 data 가 없는 경우는 arff caseStructure를 읽어 domain.caseStructure로  setting 
     * - DB caseStructure와 비교 없음
     * 
     * @throws java.lang.Exception
     */
    public static void caseImportWithCaseStructure() throws Exception {
    	
    	String arffFile = RDRConfig.getArffFile();
    	
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFile);
        
        if (source.getDataSet()!=null)
        {
            Instances data = source.getDataSet();
            // setting class attribute if the data format does not provide this information
            // For example, the XRFF format saves the class attribute information as well
            if (data.classIndex() == -1){
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            // Getting case structure
            CaseStructure caseStructure = Main.domain.getCaseStructure();

            int arffAttrAmount = source.getStructure().numAttributes();
            for(int i=0; i<arffAttrAmount; i++) {
                Attribute arffAttr = source.getStructure().attribute(i);

                IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
                if(!caseStructure.isAttributeExist(convertedAttr)){
                    caseStructure.addAttribute(convertedAttr);
                }
            }

            //set case structure
            Main.domain.setCaseStructure(caseStructure);

            int attrAmount = caseStructure.getAttrAmount();

            System.out.print("Case loading.");
            // Add cases
            int caseAmount = source.getDataSet().numInstances();
            for(int i=0; i<caseAmount; i++) 
            {
                Case rdrCase = new Case(caseStructure);

                // if case amount is bigger than 100, skip case loading and will load when user clicks the case
                if (i<=1)
                {
                    Instance arffCase = source.getDataSet().instance(i);
                    String[] attrNames = caseStructure.getAttributeNameArray();
                    
                    //storing null value
                    rdrCase.initNullValue();
                    
                    for (int j=0; j<arffAttrAmount; j++) 
                    {                
                        String attrName = arffCase.attribute(j).name();
                        if(caseStructure.getAttributeByName(attrName)!=null)
                        {
                            String attrType = caseStructure.getAttributeByName(attrName).getValueType().getTypeName();
                            String valStr = "";
                            
                            if(arffCase.isMissing(j))
                            {
                                ValueType valType = new ValueType(ValueType.NULL_TYPE);
                                Value value = new Value(valType);
                                rdrCase.setValue(attrName, value);
                            } 
                            else 
                            {
                                switch (attrType) 
                                {
                                    case "CONTINUOUS":
                                            double dVal = arffCase.value(j);
                                            valStr = String.valueOf(dVal);
                                        break;
                                    case "CATEGORICAL":
                                            valStr = arffCase.stringValue(j);
                                        break;
                                    default:
                                            valStr = arffCase.stringValue(j);
                                        break;
                                }
                                
                                if (RDRConfig.isNullValueString(valStr))
                                {
                                	ValueType valType = new ValueType(ValueType.NULL_TYPE);
                              		Value value = new Value(valType);
                              		rdrCase.setValue(attrName, value);
                                }
                                else
                                {
            	                    ValueType valType = caseStructure.getAttributeByName(attrName).getValueType();
            	                    Value value = new Value(valType, valStr);
            	                    rdrCase.setValue(attrName, value);
                                }
                            }
                        }
                    }
                }
                int caseId = i+1;
                rdrCase.setCaseId(caseId);

                //add case into allCaseSet
                Main.allCaseSet.addCase(rdrCase);
                System.out.print(".");
            }
            System.out.println("..");
            
            System.out.println("Total " + Main.allCaseSet.getCaseAmount() + " cases(s) loaded.");
            if(caseAmount==0){
                Logger.info("There is no case can be imported");
            } else if(caseAmount==1){
                Logger.info("Total 1 case imported");
            } else {
                Logger.info("Total " + caseAmount + " cases imported");
            }
        } 
        else 
        {
            
            // Construct case structure
            CaseStructure caseStructure = new CaseStructure();

            int attrAmount = source.getStructure().numAttributes();
            for(int i=0; i<attrAmount; i++) {
                Attribute arffAttr = source.getStructure().attribute(i);

                IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
                caseStructure.addAttribute(convertedAttr);
            }

            //set case structure
            Main.domain.setCaseStructure(caseStructure);
        }
        
    }
        
    /**
     * arff file의 caseStructure 반환
     * 
     * @return
     * @throws Exception
     */
    public static CaseStructure getArffCaseStructure() throws Exception {
    	
    	String arffFile = RDRConfig.getArffFile();
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFile);
        
        if(source.getDataSet()!=null){
            Instances data = source.getDataSet();
            // setting class attribute if the data format does not provide this information
            // For example, the XRFF format saves the class attribute information as well
            if (data.classIndex() == -1){
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            // Getting case structure
            CaseStructure caseStructure = new CaseStructure();

            int arffAttrAmount = source.getStructure().numAttributes();
            for(int i=0; i<arffAttrAmount; i++) {
                Attribute arffAttr = source.getStructure().attribute(i);

                IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
                if(!caseStructure.isAttributeExist(convertedAttr)){
                    caseStructure.addAttribute(convertedAttr);
                }
            }
            return caseStructure;
        }
        return null;
    }
    
    /**
     * import arff dataset into rdr case set
     * - aCaseStructure 기준으로  Case를 생성하고 arff의 caseStructure에 해당하는 data 를 setting
     * @param aCaseStructure
     * @throws java.lang.Exception
     */
    public static void caseImport(CaseStructure aCaseStructure, 
    		                      int caseCount,
    		                      String pArffFn) throws Exception {

    	
    	String arffFile;
    	if (pArffFn == null) arffFile = RDRConfig.getArffFile();
    	else arffFile = pArffFn;
    	
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFile);
        
        if (source.getDataSet()!=null)
        {
            Instances data = source.getDataSet();
            // setting class attribute if the data format does not provide this information
            // For example, the XRFF format saves the class attribute information as well
            if (data.classIndex() == -1)
            {
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            int arffAttrAmount = source.getStructure().numAttributes();
            int attrAmount = aCaseStructure.getAttrAmount();

            System.out.print("Case loading.");
            // Add cases
            int caseAmount = source.getDataSet().numInstances();
            for (int i=0; i<caseAmount; i++) 
            {
                Case rdrCase = new Case(aCaseStructure);

                // if case amount is bigger than 100, skip case loading and will load when user clicks the case
                if (i+1 <= caseCount)
                {
                    String[] attrNames = aCaseStructure.getAttributeNameArray();
                    
                    //storing null value
                    rdrCase.initNullValue();
                    
                    Instance arffCase = source.getDataSet().instance(i);
                    for(int j=0; j<arffAttrAmount; j++) 
                    {                
                        String attrName = arffCase.attribute(j).name();
                        if (aCaseStructure.getAttributeByName(attrName)!=null)
                        {
                            String attrType = aCaseStructure.getAttributeByName(attrName).getValueType().getTypeName();
                            String valStr = "";
                            if(arffCase.isMissing(j)){
                                ValueType valType = new ValueType(ValueType.NULL_TYPE);
                                Value value = new Value(valType);
                                rdrCase.setValue(attrName, value);
                            } 
                            else 
                            {
                                switch (attrType) 
                                {
                                    case "CONTINUOUS":
                                            double dVal = arffCase.value(j);
                                            valStr = String.valueOf(dVal);
                                        break;
                                    case "CATEGORICAL":
                                            valStr = arffCase.stringValue(j);
                                        break;
                                    default:
                                            valStr = arffCase.stringValue(j);
                                        break;
                                }

                                if (RDRConfig.isNullValueString(valStr))
                                {
                                	ValueType valType = new ValueType(ValueType.NULL_TYPE);
                              		Value value = new Value(valType);
                              		rdrCase.setValue(attrName, value);
                                }
                                else
                                {
            	                    ValueType valType = aCaseStructure.getAttributeByName(attrName).getValueType();
            	                    Value value = new Value(valType, valStr);
            	                    rdrCase.setValue(attrName, value);
                                }
                            }
                        }
                    }
                }
                int caseId = i+1;
                rdrCase.setCaseId(caseId);

                //add case into allCaseSet
                Main.allCaseSet.addCase(rdrCase);
                System.out.print(".");
            }
            System.out.println("..");            
            System.out.println("Total " + Main.allCaseSet.getCaseAmount() + " cases(s) loaded.");
            if(caseAmount==0){
                Logger.info("There is no case can be imported");
            } else if(caseAmount==1){
                Logger.info("Total 1 case imported");
            } else {
                Logger.info("Total " + caseAmount + " cases imported");
            }
        } else {
            
        	//@todo data가 없는 경우인데 case structure는 무시해야...
        	
            // Construct case structure
            CaseStructure caseStructure = new CaseStructure();

            int attrAmount = source.getStructure().numAttributes();
            for(int i=0; i<attrAmount; i++) {
                Attribute arffAttr = source.getStructure().attribute(i);

                IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
                caseStructure.addAttribute(convertedAttr);
            }

            //set case structure
            Main.domain.setCaseStructure(caseStructure);
        }
        
    }
    
    /**
     * case loading from arff
     * - domain의 caseStructure와 arff 의 caseStructure가 일치해야 함
     * - missing value 처리하지 않음
     * 
     * @throws Exception
     */
    public static void caseImportForTesting() throws Exception {
    	
    	String testArffFile = RDRConfig.getTestArffFile();
    	
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(testArffFile);
        
        Instances data = source.getDataSet();
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1){
            data.setClassIndex(data.numAttributes() - 1);
        }
        
        // Get case structure
        CaseStructure caseStructure = Main.domain.getCaseStructure();
        
        // Add cases
        int caseAmount = source.getDataSet().numInstances();
        for(int i=0; i<caseAmount; i++) {
            Instance arffCase = source.getDataSet().instance(i);
            String[] attrNames = caseStructure.getAttributeNameArray();
            
            LinkedHashMap<String, Value> values = new LinkedHashMap<>();
              
            for(int j=0; j<caseStructure.getAttrAmount(); j++) 
            {
                String attrName = attrNames[j];
                String attrType = caseStructure.getAttributeByName(attrName).getValueType().getTypeName();
                String valStr = "";
                switch (attrType) {
                    case "CONTINUOUS":
                        double dVal = arffCase.value(j);
                        valStr = String.valueOf(dVal);
                        break;
                    case "CATEGORICAL":
                        valStr = arffCase.stringValue(j);
                        break;
                    default:
                        valStr = arffCase.stringValue(j);
                        break;
                }
                ValueType valType = caseStructure.getAttributeByName(attrName).getValueType();
                Value val = new Value(valType, valStr);
                values.put(attrName, val);
            }
            
            Case rdrCase = new Case(caseStructure, values);
            rdrCase.setCaseId(i+1);
            
            //add case into allCaseSet
            Main.testingCaseSet.addCase(rdrCase);
        }
    }
    
    
    /**
     * load case using case id from arff dataset 
     * - arff의 추가된 항목을 domain의 caseStructure에 추가 (DB추가 없음)
     * - case loading (arff data의 (targetCaseId-1) index 의 data)
     * 
     * @param targetCaseId
     * @return 
     * @throws java.lang.Exception
     */
    public static Case caseLoad(int targetCaseId, String pArffFn) throws Exception 
    {
        int arffCaseId = targetCaseId-1;
        
        String arffFile;
        if (pArffFn == null) arffFile = RDRConfig.getArffFile();
        else arffFile = pArffFn;
        
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFile);
        Instances data = source.getDataSet();
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1){
            data.setClassIndex(data.numAttributes() - 1);
        }
        
        //System.out.println("data size : " + data.numInstances());
        if (targetCaseId > data.numInstances() )
        	return null;
        
        // Getting case structure
        CaseStructure caseStructure = Main.domain.getCaseStructure();

        //arff파일에 추가된 항목 add
        int arffAttrAmount = source.getStructure().numAttributes();
        for(int i=0; i<arffAttrAmount; i++) {
            Attribute arffAttr = source.getStructure().attribute(i);

            IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
            if(!caseStructure.isAttributeExist(convertedAttr)){
                caseStructure.addAttribute(convertedAttr);
            }
        }

        //set case structure
        Main.domain.setCaseStructure(caseStructure);
        
        int attrAmount = Main.domain.getCaseStructure().getAttrAmount();
        
        String[] attrNames = caseStructure.getAttributeNameArray();
        
        Case rdrCase = new Case(caseStructure);
        
        //storing null value
        rdrCase.initNullValue();

        Instance arffCase = source.getDataSet().instance(arffCaseId);
        for (int j=0; j<arffAttrAmount; j++) 
        {                
            String attrName = arffCase.attribute(j).name();
            if(caseStructure.getAttributeByName(attrName)!=null)
            {
                String attrType = caseStructure.getAttributeByName(attrName).getValueType().getTypeName();
                String valStr = "";
                
                if(arffCase.isMissing(j))
                {
                    ValueType valType = new ValueType(ValueType.NULL_TYPE);
                    Value value = new Value(valType);
                    rdrCase.setValue(attrName, value);
                } 
                else 
                {
                    switch (attrType) 
                    {
                        case "CONTINUOUS":
                                double dVal = arffCase.value(j);
                                valStr = String.valueOf(dVal);
                            break;
                        case "CATEGORICAL":
                                valStr = arffCase.stringValue(j);
                            break;
                        default:
                                valStr = arffCase.stringValue(j);
                            break;
                    }
                    
                    if (RDRConfig.isNullValueString(valStr))
                    {
                    	ValueType valType = new ValueType(ValueType.NULL_TYPE);
                		Value value = new Value(valType);
                		rdrCase.setValue(attrName, value);
                    }
                    else
                    {
	                    ValueType valType = caseStructure.getAttributeByName(attrName).getValueType();
	                    Value value = new Value(valType, valStr);
	                    rdrCase.setValue(attrName, value);
                    }
                }
            }
        }

        rdrCase.setCaseId(targetCaseId);

        if (RDRConfig.isDebugInputCase())
        	Logger.info("caseLoad : " + rdrCase.toString());
        
        return rdrCase;

    }
    
    /**
     * valueMap의 값으로 Case를 생성
     * valueMap : 값이 항목별 값으로 continuous 인 경우도  문자열로 저장된다.
     *            null 인 경우 는 map에 포함되지 않거나 RdrConfig의 nullValue string 으로 setting된다.
     * 
     * @param aCaseStructure
     * @param valueMap
     * @param msg
     * @return
     * @throws Exception
     */
    public static Case caseLoadFromValueMap(CaseStructure aCaseStructure,
    		                                HashMap<String, String> valueMap,
    		                                String[] msg) throws Exception 
    {
    	//null value는 포함하지 않을 수 있기때문에 에러체크하지 않는다.
    	//if ( aCaseStructure.getAttrAmount() != valueMap.size())
    	//{
    	//	msg[0] = "사례구조와 Case의 항목수가 다릅니다. 사례구조 : " + aCaseStructure.getAttrAmount() +
		//			 "Case(JSON) : " + valueMap.size();
		//	return null;
    	//}
    	
        int attrAmount = aCaseStructure.getAttrAmount();
        String[] attrNames = aCaseStructure.getAttributeNameArray();
        
        Case rdrCase = new Case(aCaseStructure);
        rdrCase.initNullValue();

        //loop caseStructure's attribute
        for (int j = 0; j < attrAmount; j++) 
        {
            String attrName = attrNames[j];
            int typeCode = aCaseStructure.getAttributeByName(attrName).getValueType().getTypeCode();
            String valStr = "";
            
            if (valueMap.containsKey(attrName))
            {
            	valStr = valueMap.get(attrName);
            }
            else continue;
            
            if (RDRConfig.isNullValueString(valStr) == false)
            {
	            if (typeCode == ValueType.CONTINUOUS)
	            {
	            	if (StringUtil.isNumeric(valStr) == false)
	            	{
	            		msg[0] = "invalid number for [" + attrName + "] : " + valStr;
	            		
	            		Logger.warn(msg[0]);
	            		//return null;
	            	}
	            }
	            else if (typeCode == ValueType.CATEGORICAL)
	            {
	            	if (aCaseStructure.getAttributeByName(attrName).isValidCategoricalValue(valStr) == false)
	            	{
	            		msg[0] = "invalid categorical value for [" + attrName + "] : " + valStr;
	            		
	            		Logger.warn(msg[0]);
	            		//return null;
	            	}
	            }
            }
            
            //null value string 이거나 continuous인데 숫자가 아니면 null 처리
            if (RDRConfig.isNullValueString(valStr) ||
                (typeCode == ValueType.CONTINUOUS && StringUtil.isNumeric(valStr) == false) )
            {
            	ValueType valType = new ValueType(ValueType.NULL_TYPE);
            	Value value = new Value(valType);
            	rdrCase.setValue(attrName, value);
            }
            else
            {
            	ValueType valType = aCaseStructure.getAttributeByName(attrName).getValueType();
            	Value value = new Value(valType, valStr);
            	rdrCase.setValue(attrName, value);
            }
            
        }

        //rdrCase.setCaseId(0);

        if (RDRConfig.isDebugInputCase())
        	Logger.info("caseLoad : " + rdrCase.toString());
        
        return rdrCase;
    }
    
    
    /**
     * import arff dataset into rdr case set
     * 
     * @throws java.lang.Exception
     */
    public static int getCaseAmount() throws Exception {
    	
    	String arffFile = RDRConfig.getArffFile();
    	
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFile);
        Instances data = source.getDataSet();
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1){
            data.setClassIndex(data.numAttributes() - 1);
        }
        
        // Add cases
        int caseAmount = source.getDataSet().numInstances();
        
        return caseAmount;
    }
    
    /**
     * arff의 caseStructure를 읽어서 domain의 caseStructure에 setting
     * 
     * @throws Exception
     */
    public static void caseStructureImport() throws Exception{
    	
    	String arffFile = RDRConfig.getArffFile();
    	
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFile);
        Instances data = source.getDataSet();
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1){
            data.setClassIndex(data.numAttributes() - 1);
        }
        
        // Construct case structure
        CaseStructure caseStructure = new CaseStructure();
        
        int attrAmount = source.getStructure().numAttributes();
        for(int i=0; i<attrAmount; i++) {
            Attribute arffAttr = source.getStructure().attribute(i);
            
            IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
            caseStructure.addAttribute(convertedAttr);
        }
        
        //set case structure
        Main.domain.setCaseStructure(caseStructure);
        Logger.info("Case structure imported successfully");
    }
    
    
    /**
     * converts arff attribute into rdr attribute
     * 
     * @param arffAttr
     * @return 
     */
    private static IAttribute convertAttributeFromArffToRDRAttr(Attribute arffAttr){        
        String RDRAttrType = "";
        
        // check the attribute type of arff dataset
        if(arffAttr.isDate()){
            RDRAttrType = "Date";
        } else if(arffAttr.isNumeric()){
            RDRAttrType = "Continuous";
        } else if(arffAttr.isString()){
            RDRAttrType = "Text";
        } else if(arffAttr.isNominal()){
            RDRAttrType = "Categorical";            
        } 
        IAttribute convertedAttr = AttributeFactory.createAttribute(RDRAttrType);
                
        //if nominal type, add categories
        if(arffAttr.isNominal()){
            int valueAmount = arffAttr.numValues();     
            
            //adding null value -> blocked, 20180222
            //convertedAttr.addCategoricalValue("");
            
            for (int i=0; i<valueAmount; i++){
                // get single value of nominal values
                String valueInstance = arffAttr.value(i);
                // add categorical value
                convertedAttr.addCategoricalValue(valueInstance);
            }      
            if(convertedAttr.getCategoricalValues().size()==2){
                if(convertedAttr.getCategoricalValues().contains("true") && convertedAttr.getCategoricalValues().contains("false") ){
                    RDRAttrType = "Boolean";
                    convertedAttr = AttributeFactory.createAttribute(RDRAttrType);
                }
            }
        } 
        
        convertedAttr.setName(arffAttr.name());
        convertedAttr.setAttributeType("Case Attribute");
        convertedAttr.setValueType(new ValueType(RDRAttrType));
        
        
        return convertedAttr;
    }
    
    
    /**
     * converts rdr attribute into arff attribute string
     * 
     * @param RDRAttr
     * @return 
     */
    private static String convertAttributeStringFromRDRAttrToArff(IAttribute RDRAttr){        
        String arffAttrType = "";
        
        if(RDRAttr.isThisType(ValueType.DATE)){
            arffAttrType =  "DATE \"yyyy-mm-dd hh:mm:ss.S\"";
        } else if(RDRAttr.isThisType(ValueType.CONTINUOUS)){
            arffAttrType =  "NUMERIC";
        } else if(RDRAttr.isThisType(ValueType.TEXT)){
            arffAttrType =  "STRING";
        } else if(RDRAttr.isThisType(ValueType.CATEGORICAL)){            
            arffAttrType =  "{";
            ArrayList<String> arrayList =RDRAttr.getCategoricalValues();
            for(int i=0; i<arrayList.size(); i++){
                String eachCat = arrayList.get(i);
                arffAttrType += eachCat;
                if( i < arrayList.size()-1) {
                    arffAttrType += ", ";
                }
            }
            arffAttrType += "}";
        } else if(RDRAttr.isThisType(ValueType.BOOLEAN)){
            arffAttrType =  "{true, false}";
        }
        return arffAttrType;
    }

    /** 
    public static CaseStructure getRecentArffCaseStructure() throws Exception {
        
    	String fileName = System.getProperty("user.dir") + "/domain/cases/recent.arff";
    	
    	File file = new File(fileName);
    	if ( !file.exists()) return null;
        
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(fileName);
        
        if(source.getDataSet()!=null){
            Instances data = source.getDataSet();
            // setting class attribute if the data format does not provide this information
            // For example, the XRFF format saves the class attribute information as well
            if (data.classIndex() == -1){
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            // Getting case structure
            CaseStructure caseStructure = new CaseStructure();

            int arffAttrAmount = source.getStructure().numAttributes();
            for(int i=0; i<arffAttrAmount; i++) {
                Attribute arffAttr = source.getStructure().attribute(i);

                IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
                if(!caseStructure.isAttributeExist(convertedAttr)){
                    caseStructure.addAttribute(convertedAttr);
                }
            }
            
            return caseStructure;
        }
        return null;
    }

    
    //20170912 added by ucciri
    private static void checkArffLocation()	throws MalformedURLException, IOException
    {
    	if (arffCaseLocation.contains("http://"))
    	{
    		String fileName = System.getProperty("user.dir") + "/domain/cases/temp.arff";
    		URL link = new URL(arffCaseLocation);
    	      
    		InputStream in = new BufferedInputStream(link.openStream());
    		ByteArrayOutputStream out = new ByteArrayOutputStream();

    		byte[] buf = new byte[16384]; 
    		int n = 0;
    		while (-1 != (n = in.read(buf))) {
    			out.write(buf, 0, n);
    		}
    		out.close();
    		in.close();
    		byte[] response = out.toByteArray();
    	      
    		FileOutputStream fos = new FileOutputStream(fileName);
    		fos.write(response);
    		fos.close();
    	      
    		arffCaseLocation = fileName;
    	}
    }
     */
}


