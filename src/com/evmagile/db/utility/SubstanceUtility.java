package com.evmagile.db.utility;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.agile.api.IAgileSession;
import com.agile.api.IQuery;
import com.agile.api.IRow;
import com.agile.api.ITable;
import com.agile.api.SubstanceConstants;
import com.evmagile.xmlparsing.XMLParse_Main;

public class SubstanceUtility {
	
	XMLParse_Main obj2;
	IAgileSession agileSession;
	String sUsername  = null;
	String sPassword = null;
	String sUrl = null;
	Logger log = null;
	
	public SubstanceUtility (XMLParse_Main obj2) throws Exception
	{
		log = Logger.getLogger(SubstanceUtility.class.getName());
		this.obj2 = obj2;
		this.sUsername = obj2.sUsername;
		this.sPassword = obj2.sPassword;
		this.sUrl  = obj2.sUrl;
		AgileSDKManager agileSDKObj = new AgileSDKManager();
		agileSession = agileSDKObj.getAgileSession(sUsername, sPassword, sUrl);	
	}
  
	public HashMap <String,String> fetchSubstanceAndGrpInfoMapfromAgile(String[] arrSourceXMLCasNumber) throws Exception
	{

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
}
