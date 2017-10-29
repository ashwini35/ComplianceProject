//Author Ashwini
//Agile sdk utility methods

package com.evmagile.db.utility;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.agile.api.AgileSessionFactory;
import com.agile.api.IAgileSession;
import com.evmagile.filemanager.FileManager;

public class AgileSDKManager {
	
	public int maxRetryCount = 2;
	public int retryCount = 0;

	FileManager fileMngrIns = new FileManager();		
	private Logger log = null;
	
	  public AgileSDKManager()
	  {
		  log = Logger.getLogger(com.evmagile.db.utility.SubstanceSearch.class.getName());
	  }
	
	//Author Ashwini
	//this method to get Agile session
	//throws APIException
	public  IAgileSession getAgileSession(String sUserName, String sPassword, String sURL) throws APIException
	{
		
		    log.info("connecting to Agile Session...");
		    
			AgileSessionFactory factory  = null;
			factory = AgileSessionFactory.getInstance(sURL);
			HashMap params = new HashMap();
			params.put(AgileSessionFactory.USERNAME, sUserName);
			params.put(AgileSessionFactory.PASSWORD, sPassword);
			IAgileSession session = factory.createSession(params);
			
			log.info("Agile Session obtained successfully..."+session);
			
			return session;
			
	  }		
	
}
