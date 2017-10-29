package com.evmagile.xmlparsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.agile.api.IAgileSession;
import com.evmagile.filemanager.FileManager;

@SuppressWarnings("unused")
public class XMLParse_Main {

	static String sWorkingdirectory = null;
	static FileManager fileMangrInst = null;
	static Logger log = null;	
	static Properties prop = new Properties();
	
	public String sUsername  = null;
	public String sPassword = null;
	public String sUrl = null;
	String sSourceXMLDirPath = null;
    String sTargetXMLAbsPath = null;
    String sSourceCSVPath = null;     
	static IAgileSession agileSession = null;
	

	XMLParse_Main () 
	{		
		try {    			    			    				
				// Fetching values from property file
				this.sUsername = prop.getProperty("ComplianceProject.AgileApplication.UserName");
				this.sPassword = prop.getProperty("ComplianceProject.AgileApplication.Password");
				this.sUrl  = prop.getProperty("ComplianceProject.AgileApplication.URL");						
				this.sSourceXMLDirPath= sWorkingdirectory+File.separator+prop.getProperty("ComplianceProject.SourceXML.inputPath");
				this.sTargetXMLAbsPath=sWorkingdirectory+File.separator+prop.getProperty("ComplianceProject.TargetXML.inputPath");
				this.sSourceCSVPath = sWorkingdirectory+File.separator+prop.getProperty("ComplianceProject.CSV.inputPath");
						
				fileMangrInst = new FileManager();
				fileMangrInst.logFolderPath = sWorkingdirectory+prop.getProperty("ComplianceProject.LogFile.Log");
				System.out.println(fileMangrInst.logFolderPath);				
				
				log.info("all string constant/properties initilised successfully");								
				
		 }catch(Exception ex) {
	     	 
		        log.info("Error while loading properties1 " +ex.getMessage());		        
		 }        		
	
	}
	
	public static Properties loadPropertyFile( String filePath )
	{
		Properties prop = new Properties();
		InputStream is = null;		
		File file = null;
		file = new File(filePath);
		try
		{		
			is = new FileInputStream( file );
			if (is != null) 
			{
				prop = new Properties();
				prop.load(is);
				log.debug("Property file loaded to Property Object");
			}
		}
		catch ( FileNotFoundException excep ) 
		{
			log.info(excep.getMessage());
			excep.printStackTrace();		
		} 
		catch (IOException excep) {
			log.info(excep.getMessage());
			excep.printStackTrace();
		}
		finally
		{
			if ( is!=null )
				try {
					is.close();
				} catch (IOException excep) {
					log.info(excep.getMessage());
					excep.printStackTrace();
				}
		}
		log.info(filePath+ " Property file loaded successfully!");
		return prop;
	}
	
	public static void main(String[] args) 
	
	{		  
		  try
		  {			
			    sWorkingdirectory = System.getProperty("user.dir");                                
              
			    System.out.println("sWorkingdirectory--"+sWorkingdirectory);
			    
				String propertyFilePath = sWorkingdirectory+"\\properties\\ComplianceProject.properties";								
				
				log = Logger.getLogger(XMLParse_Main.class.getName());
				
			    prop = loadPropertyFile( propertyFilePath );
			    
			    String slog4jProp = prop.getProperty("ComplianceProject.Log4j.Properties.Path");			
			    
			    PropertyConfigurator.configure(sWorkingdirectory+slog4jProp);
			    
			    XMLParse_Main obj1 = new XMLParse_Main();
			    			   			    
			    log.info("ComplianceProject main method invoked followed with its constructor code execution at... "+fileMangrInst.timestamp);	
			   			   			   			    			    			    
                InitiateXMLReadWrite obj2 = new InitiateXMLReadWrite(obj1);
			    obj2.readWriteXML();			    			    
			    
			    log.info("loading completed for file...exitting main method at- "+fileMangrInst.dateFormat.format(new Date())); 
			    
			    //sendMail();
			    
		  }catch(Exception ex)
		  {
			  log.info("error in main "+ex.getMessage());
			  ex.printStackTrace();			  
		  }	
		  finally
		  {
				//agileSession.close(); temporarily commented
				log.info("Disconnected from Agile session");
				//sendMail();
		  }
		
	}

}
