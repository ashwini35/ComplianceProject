package com.evmagile.xmlparsing;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class ParseCSV {
	
	Logger log = null;
	String inputFilePathName = null;

    ParseCSV ()
	{
    	log = Logger.getLogger(InitiateXMLReadWrite.class.getName());
    	this.inputFilePathName = inputFilePathName;
    	//inputFilePathName = "C:\\help\\Agile PLM\\zebra\\ComplianceProject\\SubstanceGroup_Substance_CAS.csv";
	}
	 public CSVReader getCSVReaderByFileAndDelimiter(String sSourceCSVPath,Character separator) 
	 {
			try
			 {					
				return new CSVReader(new FileReader(sSourceCSVPath),separator);
			 }
			 catch(Exception ex)
			 {
				log.info("ERROR while reading input csv file1 --------------------------------------- "+sSourceCSVPath+"---"+ex.getMessage());
				ex.printStackTrace();
				return null;
			 }
	 }
	 public HashMap getMapByCSV (String sSourceCSVPath) throws Exception
	 {

		 log.info("Parsing source csv input/Agile input data starts after referring  : --------------- "+sSourceCSVPath);
		 boolean bSuccess = true;
		 String [] arrHeaderNames = null;
		 HashMap globalHmRow = new HashMap();
		 int rownumber=0;
	     String lTotalExecutionTime = "";
	     HashMap<String,String> hmCasNum_SubstanceInfo = new HashMap <String,String>();
	     //ArrayList<HashMap> inputRowList = new ArrayList<HashMap>(); //Alternate Option to store input info to avoid overriding of duplicate cas number
		 CSVReader csvObjInputDetail = null;	
		 String sDelimeter = "|";	
			
		 try{
				 csvObjInputDetail = getCSVReaderByFileAndDelimiter(sSourceCSVPath,',');
				 if(csvObjInputDetail==null) 
				 {
					 bSuccess = false;
				 }
				
			     log.info("Parsing source csv input/Agile input data ends after referring  : --------------- "+sSourceCSVPath);				
				 log.info("Creating map from CSV input/Agile Input starts ------------------ --------------- ");	
				 
				 
				if(csvObjInputDetail!=null)
				{
					 long start = System.currentTimeMillis();					 
				     String [] nextLine;				     				   
				     while ((nextLine = csvObjInputDetail.readNext()) != null)
				     {
				    	 //HashMap hmRow = hmRow=new HashMap();
				    	 HashMap hmRow = hmRow=new HashMap();
				        if(rownumber>0)
				        {
				        	try
				        	{
				        		rownumber++;
					        	for(int x=0;x<arrHeaderNames.length;x++)
					        	{					        		
					        		hmRow.put(arrHeaderNames[x], nextLine[x]);
					        		globalHmRow = hmRow;
					        	}					        	
				        		//inputRowList.add(hmRow);	
					        	//System.out.println("last hmRow ------ "+hmRow);
					        	hmCasNum_SubstanceInfo.put((String)hmRow.get("SUBSTANCE_CAS"), (String)hmRow.get("SUBSTANCE_GROUP")+sDelimeter+(String)hmRow.get("SUBSTANCE"));
					        	
				        	}catch(Exception ex)
				        	{		
				        		bSuccess = false;
				        		log.info("~ERROR::~ROW "+rownumber+" ~ "+hmRow+" ~ CSV Reading skipped due to error :: ");		
								log.info("~ERROR::~ while reading input csv file2 --------------------------------------- "+ex.getMessage());
								ex.printStackTrace();
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
				log.info("Creating map from CSV input/Agile Input ends with TotalExecutionTime :: ------------------"+lTotalExecutionTime);				 
				log.info("CSV input/Agile Input map : hmCasNum_SubstanceInfo :: size :: ------------------ "+ hmCasNum_SubstanceInfo.size());
				
				//log.debug("---------hmCasNum_SubstanceInfo values:: ----"+ hmCasNum_SubstanceInfo);
				
				return bSuccess ? hmCasNum_SubstanceInfo : null;
				
		 }catch(Exception ex)
		 {
     		log.info("~ERROR::~After ROW "+rownumber+" ~ "+globalHmRow+" ~ CSV Reading skipped due to error :: ");		
			log.info("~ERROR::~ while reading input csv file3 --------------------------------------- "+ex.getMessage());
			 bSuccess = false;
			 ex.printStackTrace();
			 return null;
		 }
/*		 finally
		 {
			    bSuccess = false;
	     		log.info("~ERROR::~after ROW "+rownumber+" ~ "+globalHmRow+" ~ CSV Reading skipped due to error from finally :: ");		
		 } */
	 }
}
