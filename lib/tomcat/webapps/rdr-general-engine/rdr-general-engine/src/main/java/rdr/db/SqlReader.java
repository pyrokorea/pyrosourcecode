package rdr.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import rdr.logger.Logger;
import rdr.utils.RDRConfig;
import rdr.utils.Utility;

public class SqlReader {

	private String sql;
	
	public String get()
	{
		return sql;
	}
	
	public SqlReader(String fn)
	{
		/** DBType별 directory에서 먼저 찾고 없으면 sql dir 에서 찾는다 */
		String sqlFn = RDRConfig.getSqlPath() + 
				       RDRConfig.getDBType() + File.separator + fn;
		
		if (Utility.isFileExist(sqlFn) == false)
			sqlFn = RDRConfig.getSqlPath() + fn;
		
		File file = new File(sqlFn);
		char[] ch = new char[(int)file.length()];
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.read(ch);
			
			boolean flag = false;
			boolean quoted = false;
			for ( int i = 0; i < ch.length; i++)
			{
				if ( ch[i] == '"' ) quoted = true;
			    if ( quoted && ch[i] == '"' ) quoted = false;
			    if ( !quoted && ch[i] == '#' ) flag = true;
			    if ( !quoted && ch[i] == '-'  && ch[i+1] == '-' ) flag = true;
			    if ( !quoted && ch[i] == '\n' ) flag = false;
			    if ( flag ) ch[i] = ' ';
			}
			
			sql = new String(ch);
			
			br.close();
			
			if (RDRConfig.isDebugSql())
			{
				Logger.info("SqlReader, read " + sqlFn);
			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			Logger.error("SQL NotFound : " + sqlFn, e);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.error("SQL NotFound : " + sqlFn, e);
		}
	
	}
	
}
