package com.evmagile.db.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.agile.api.APIException;
import com.agile.api.IAgileSession;
import com.agile.api.ICell;
import com.agile.api.IDataObject;
import com.agile.api.IItem;
import com.agile.api.IQuery;
import com.agile.api.IRelationshipContainer;
import com.agile.api.IRow;
import com.agile.api.ISubstance;
import com.agile.api.ITable;
import com.agile.api.ItemConstants;
import com.agile.api.QueryConstants;
import com.agile.api.SubstanceConstants;
import com.agile.api.UserGroupConstants;
import com.evmagile.filemanager.FileManager;

public class SubstanceSearch {
	
	
	public static String sWorkingdirectory = null;
	public static FileManager fileMangrInst = null;
	private static Logger log = null;
	public static Properties prop = new Properties();
	 
	public static String sUsername  = null;
	public static String sPassword = null;
	public static String sUrl = null;
	public static AgileSDKManager agileSDKInst = null;
	public static  IAgileSession agileSession = null;
	public static ISubstance substanceObj = null;
	
	public SubstanceSearch () 
	{
       
	}
	

	    private static ISubstance getSubstanceUsingMap(IAgileSession agileSession) throws APIException {

	    	int value = 2000001687;
	        Map params = new HashMap();
	        params.put(SubstanceConstants.ATT_GENERAL_INFO_CAS_NUMBER, "7440-36-0");
	        //params.put("2000001123", "75-35-4"); 
	        ISubstance substanceObj = (ISubstance) agileSession.getObject(ISubstance.OBJECT_TYPE, "Antimony");
	        
	        ICell[] cellvalues = substanceObj.getCells();
			for(int x=0;x<cellvalues.length;x++) 
			{
				ICell  icell = (ICell)cellvalues[x]; 
				log.info("table name in cellvalues icell... "+icell.getName());
				log.info("table name in cellvalues icell... "+icell.getValue());

				//log.info("table name in subgrp tables... "+table.getAvailableValues(table));
			}
	        
	        
	        ITable[] subTable = substanceObj.getTables();
			for(int x=0;x<subTable.length;x++) 
			{
				ITable  table = (ITable)subTable[x]; 
				log.info("table name in subgrp tables... "+table.getName());
				//log.info("table name in subgrp tables... "+table.getAvailableValues(table));
			}
	        
	        //ISubstance substanceObj = (ISubstance) agileSession.getObject(SubstanceConstants.CLASS_SUBSTANCE, params);
	        //IRelationshipContainer relationshipCont = (IRelationshipContainer)substanceObj.getRelationship();
	        ITable obj1 = substanceObj.getRelationship();
	        
			if (!obj1.isEmpty())
			{
				
				Iterator it = obj1.getReferentIterator();
				while(it.hasNext())
				{
					IRow row = (IRow) it.next();
					System.out.println("getting parent-- "+row.getParent().getName());

					System.out.println(row.getValues());
				}
			}
	        
	        return substanceObj;
	    }

		/**
		 * @param casDetail
		 * @param agileSession
		 * @return
		 * @throws APIException
		 */
		public static HashMap getAgileSubstanceNameFromCAS(String[] casDetail,IAgileSession agileSession) throws APIException {
			HashMap result = new HashMap();
			//IQuery query = (IQuery)agileSession.createObject(IQuery.OBJECT_TYPE,SubstanceConstants.CLASS_SUBSTANCE);
			IQuery query = (IQuery)agileSession.createObject(IQuery.OBJECT_TYPE,SubstanceConstants.CLASS_SUBSTANCE_GROUP);
			
			//String strCriteria="[2020] In ('Yes') and [2000001873] In ('Active') and [2000001123] In (";			
			//String strCriteria= "[2000004723] In (";
			String strCriteria="["+SubstanceConstants.ATT_COMPOSITION_LIFECYCLE_PHASE+"] In ('Active') and ["+SubstanceConstants.ATT_COMPOSITION_CAS_NUMBER+"] In (";
			for(int casNum =0; casNum<casDetail.length; casNum++)
			{ 
				if (casNum==0)
					strCriteria += "'" + casDetail[casNum] + "'";
				else
					strCriteria += ",'" + casDetail[casNum] + "'";	
			}
			strCriteria += ")";
			query.setCriteria(strCriteria);

			Integer[] intAttributes = new Integer[3];
			intAttributes[0] = SubstanceConstants.ATT_GENERAL_INFO_CAS_NUMBER; 
			intAttributes[1] = SubstanceConstants.ATT_GENERAL_INFO_NAME; 
			intAttributes[2] = SubstanceConstants.ATT_GENERAL_INFO_LIFECYCLE_PHASE;

			query.setResultAttributes(intAttributes);
			log.debug("[Method:getAgileSubstanceNameFromCAS()]: Substance Query: " + query.getCriteria());
			ITable resultTable = query.execute();
			System.out.println(query.getCriteria());
			if (resultTable != null)
			{
				Iterator it = resultTable.iterator();
				while(it.hasNext())
				{
					IRow row = (IRow) it.next();
					System.out.println("getting parent-- "+row.getParent().getName());

					System.out.println(row.getValues());
					result.put(row.getValue(SubstanceConstants.ATT_GENERAL_INFO_CAS_NUMBER), row.getValue(SubstanceConstants.ATT_GENERAL_INFO_NAME));
				}
			}
			log.debug("[Method:getAgileSubstanceNameFromCAS()]Got all the Substance Names for the CAS numbers from Agile");
			return result;
		}    
		
		public static void getSubstanceGroupByName (String subGrpName, IAgileSession agileSession ) throws APIException
		{
			
/*			ISubstance dObj1 = (ISubstance)agileSession.getObject(SubstanceConstants.CLASS_SUBSTANCE_GROUP, subGrpName);
			IRelationshipContainer relationshipCont = (IRelationshipContainer)dObj1.getRelationship();	        
			if (relationshipCont != null)
			{
				ITable relationships=(ITable)relationshipCont.getRelationship();
			}*/
			
			IDataObject dObj = (IDataObject)agileSession.getObject(SubstanceConstants.CLASS_SUBSTANCE_GROUP, subGrpName);			
			
			ITable[] subGrpTableArr  = dObj.getTables();
			for(int x=0;x<subGrpTableArr.length;x++) 
			{
				ITable  table = (ITable)subGrpTableArr[x]; 
				log.info("table name in subgrp tables... "+table.getName());
				if("Composition".equals(table.getName())) 
				{
					Iterator i = table.getTableIterator();

					while (i.hasNext()) {

						IRow row = (IRow)i.next();
                        HashMap rowvalues = (HashMap)row.getValues();
        				log.info("table rowvalues... "+rowvalues);			
        				log.info("cas number1... "+rowvalues.get(Integer.parseInt("2000004723")));
						log.info("cas number2... "+row.getValue(Integer.parseInt("2000004723")));

						//log.info("substance name... "+row.getValue("2000001077"));
					    ICell[] icellvalues = row.getCells();
					    for(int z=0;z<icellvalues.length;z++) 
					    {
					    	ICell icell = (ICell)icellvalues[z];
							log.info("icell getName... "+icell.getName());
							log.info("icell getDataType... "+icell.getDataType());
							log.info("icell getType... "+icell.getType());
							log.info("icell getAttribute... "+icell.getAttribute());
							log.info("icell getAvailableValues... "+icell.getAvailableValues());
							log.info("icell getValue... "+icell.getParent());							
							log.info("icell getValue... "+icell.getValue());

					    }
					}
				}
				
			}
			
		}
		
	public static void main(String[] args) {
		  try
		  {			
			    sWorkingdirectory = System.getProperty("user.dir");                                
              
			    System.out.println("sWorkingdirectory--"+sWorkingdirectory);
			    
				String propertyFilePath = sWorkingdirectory+"\\properties\\ItemDataLoadProp.properties";
				
				fileMangrInst = new FileManager();
				
				log = Logger.getLogger(com.evmagile.db.utility.SubstanceSearch.class.getName());
				
			    prop = loadPropertyFile( propertyFilePath );
			    
			    String slog4jProp = prop.getProperty("ItemDataLoad.Log4j.Properties.Path");			
			    
			    PropertyConfigurator.configure(sWorkingdirectory+slog4jProp);
			    			   			    
			    log.info("ItemDataLoad main method invoked followed with its constructor code execution at... "+fileMangrInst.timestamp);	
			   
			    SubstanceSearch SubstanceSearchInstance = new SubstanceSearch();
			    
			    log.info("ItemDataLoad constructor loaded successfully and initiating Agile session... ");	
			    			    			    
				agileSession = agileSDKInst.getAgileSession(sUsername,sPassword,sUrl);			    							   
			    
				//String []casDetail = {"7440-36-0"};
				String []casDetail = {"65997-18-4"};
				
				HashMap temp = getAgileSubstanceNameFromCAS(casDetail,agileSession);
				
				String subGrpname = "ANTIMONY AND ANTIMONY COMPOUNDS";
//				getSubstanceGroupByName(subGrpname,agileSession);
				
//			    substanceObj = getSubstanceUsingMap(agileSession);
//				
//				//System.out.println("substanceObj-- name --- type "+substanceObj.getName() + " -- " +substanceObj.getType());
//				
//				//HashMap dataobjmap = (HashMap) substanceObj.getValues();
//				//ICell[] icellvalues = substanceObj.getCells();
//				
//				String casNumber = "75-35-4";
//				String baseid = "2000001123";
//				
//	            String condition = ""+SubstanceConstants.ATT_GENERAL_INFO_CAS_NUMBER+" == '" + casNumber + "'";
//				//String condition = ""+Gener+" == '" + casNumber + "'";
//	           // condition = ""+baseid+" == '" + casNumber + "'";
//	            Map map = new java.util.HashMap();
//	            map.put(QueryConstants.ATT_CRITERIA_CLASS, SubstanceConstants.CLASS_SUBSTANCE);
//	            map.put(QueryConstants.ATT_CRITERIA_STRING, condition);
//	            IQuery query = (IQuery)agileSession.createObject(IQuery.OBJECT_TYPE, map);
//	            query.setCaseSensitive(false);
//	            // Query results
//	            ITable results = query.execute();
				
			    log.info("loading completed for file...exitting main method at- "+fileMangrInst.dateFormat.format(new Date())); 
			    
			    //sendMail();
			    
		  }catch(Exception ex)
		  {
			  log.info("error in main "+ex.getMessage());
			  ex.printStackTrace();			  
		  }	
		  finally
		  {
				agileSession.close();
				log.info("Disconnected from Agile session");
				//sendMail();
		  }

	}

}
