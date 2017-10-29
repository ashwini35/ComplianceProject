package com.evmagile.xmlparsing;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.opencsv.CSVReader;

public class ParseCSV {
	
	Logger log = null;
	String inputFilePathName = null;

    ParseCSV ()
	{
    	log = Logger.getLogger(InitiateXMLReadWrite.class.getName());
    	this.inputFilePathName = inputFilePathName;
    	//inputFilePathName = "C:\\help\\Agile PLM\\zebra\\ComplianceProject\\SubstanceGroup_Substance_CAS.csv";
	}
	
	 public HashMap getMapByCSV (String sSourceCSVPath) throws Exception
	 {
				
		 boolean bSuccess = true;
		 String [] arrHeaderNames = null;
		 int rownumber=0;
	     String lTotalExecutionTime = "";
	     HashMap<String,String> hmCasNum_SubstanceInfo = new HashMap <String,String>();
	     //ArrayList<HashMap> inputRowList = new ArrayList<HashMap>(); //Alternate Option to store input info to avoid overriding of duplicate cas number
		 CSVReader csvObjInputDetail = null;	
		 String sDelimeter = "|";	
			
		 try{
				try
				 {					
					csvObjInputDetail = new CSVReader(new FileReader(sSourceCSVPath));
				 }
				 catch(Exception ex)
				 {
					bSuccess = false;
					log.info("Error while reading input csv file "+ex.getMessage());
					ex.printStackTrace();
					return null;
				 }
				
				if(csvObjInputDetail!=null)
				{
					 long start = System.currentTimeMillis();
					 
					 log.info(" ~ INFO  :: ~ looping through input csv data file -Start... ");	
				     String [] nextLine;				     				   
				     while ((nextLine = csvObjInputDetail.readNext()) != null)
				     {
				    	 HashMap hmRow = hmRow=new HashMap();
				        if(rownumber>0)
				        {
				        	try
				        	{
				        		rownumber++;
					        	for(int x=0;x<arrHeaderNames.length;x++)
					        	{					        		
					        		hmRow.put(arrHeaderNames[x], nextLine[x]);
					        	}					        	
				        		//inputRowList.add(hmRow);				        							        		
					        	hmCasNum_SubstanceInfo.put((String)hmRow.get("SUBSTANCE_CAS"), (String)hmRow.get("SUBSTANCE_GROUP")+sDelimeter+(String)hmRow.get("SUBSTANCE"));
					        	
				        	}catch(Exception ex)
				        	{		
				        		bSuccess = false;
				        		log.info(" ~ ERROR :: ~ ROW "+rownumber+" ~ "+hmRow+" ~ CSV Reading skipped due to error :: "+ex.getMessage());					        		
				        	}

				        }else
				        {
				        	rownumber++;
				        	arrHeaderNames=nextLine;
				        }
				     }	
				     
					  long end = System.currentTimeMillis();
					  NumberFormat formatter = new DecimalFormat("#0.00000");
					  lTotalExecutionTime = formatter.format((end - start) / 1000d);
					  
				 }				
				log.info("CSV Input Data read completed from file:: "+ inputFilePathName+ " TotalExecutionTime "+lTotalExecutionTime);

				//log.info("inputRowList size :: "+ inputRowList.size());
				//log.info("inputRowList values :: "+ inputRowList);

				log.info("hmCasNum_SubstanceInfo size:: "+ hmCasNum_SubstanceInfo.size());
				//log.info("hmCasNum_SubstanceInfo values:: "+ hmCasNum_SubstanceInfo);
				
				return bSuccess ? hmCasNum_SubstanceInfo : null;
				
		 }catch(Exception ex)
		 {
			 bSuccess = false;
			 ex.printStackTrace();
			 return null;
		 }	  
	 }
}
