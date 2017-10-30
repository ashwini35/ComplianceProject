package com.evmagile.xmlparsing;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.evmagile.db.utility.SubstanceUtility;
import com.evmagile.filemanager.FileManager;

class InitiateXMLReadWrite
{
	
	Logger log = null;
	File[] arrFilesPath = null;
	XMLParse_Main obj1 = null;
	HashMap <String,String>csvInput = null;
	XMLOutputter xmlOutput = null;
	String sXMLAbsFileName = "";

	DateFormat dateFormat = null;
	Date date = null;
	long time ;
		
	InitiateXMLReadWrite(XMLParse_Main obj1)
	{
		this.obj1 = obj1;
		log = Logger.getLogger(InitiateXMLReadWrite.class.getName());
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		date = new Date();
		time = System.currentTimeMillis();
		log.info("program InitiateXMLReadWrite start date time--" + dateFormat.format(date));
		log.info("program InitiateXMLReadWrite start milisecond --" + time);
	}
	
	File[] getXMLFilesByDirName(String sDir)
	{
		File dir = new File(sDir);
		
		FileFilter filter = (File file) -> file.isFile() && file.getName().endsWith(".xml");
		
		return dir.listFiles(filter);
	}
	
	private Document getSAXParsedDocument(final String fileName) {
		
		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		
		try 
		{			
			document = builder.build(fileName);
			
		} catch (JDOMException | IOException e) 
		{
			
			log.info("Exception in method getSAXParsedDocument while parsing xml :: "+fileName);
			log.info("Stractrace :: "+e.getMessage());
			e.printStackTrace();
			
		}
		return document;
	}
	
	//util method to fetch FirstLevelChildByElementName
	private List <Element> getFirstLevelChildByElementName(String childName,List <Element> elementList ) throws Exception
	{	
		List <Element> outputList = new ArrayList<Element>();
		for(int i=0;i<=elementList.size()-1;i++){  
			Element element = elementList.get(i);  
			List <Element> childList = element.getChildren();
			for(int x=0;x<=childList.size()-1;x++) 
			{
				Element element1 = childList.get(x); 
	            if(element1.getName().equals(childName)) 
	            {
	            	outputList.add(element1);
	            }
			}			 
		} 
		return outputList;
	}
	
	private void populateSourceXMLSubstanceInfoMaps(List<Element> listSubstance_SubGrpLevel_2,HashMap<String, Element> hmCasNum_SubstancePair,HashMap<String, Element> hmSubGrpName_SubGrpElementPair) throws Exception 
	{
		for(int i=0;i<=listSubstance_SubGrpLevel_2.size()-1;i++)
		{  
           Element element_Substance_childLevel_2 = (Element)listSubstance_SubGrpLevel_2.get(i);
                
        		List<Element> templist = element_Substance_childLevel_2.getChildren();
        		HashMap temphm = new HashMap ();
        		for (Element element2 : templist) 
        		{
 			               			  
        		   temphm.put(element2.getName(), element2.getText());            			   
				}            		
        		if(temphm.get("SubstanceType").equals("substances")) 
        		{
        			hmCasNum_SubstancePair.put((String)temphm.get("CasNumber"), element_Substance_childLevel_2);
        		}else
        		{
        			hmSubGrpName_SubGrpElementPair.put((String)temphm.get("SubstanceName"), element_Substance_childLevel_2);
        		}            		
		}
	}
	private void modifySubstance_Name_Level(Element substancenode, String subsNameAsPerAgile,String sLevel) 
	{
		List<Element> templist = substancenode.getChildren();
		for (Element element2 : templist) 
		{		               			  
		   if("SubstanceName".equals(element2.getName())&&!subsNameAsPerAgile.equals(element2.getText())) 
		   {
			   element2.setText(subsNameAsPerAgile);
		   }
		   if("ChildLevel".equals(element2.getName())) 
		   {
			   element2.setText(sLevel);
		   }
		}		
	}
	
	private void modifySubstanceName(Element substancenode, String subsNameAsPerAgile) 
	{
		List<Element> templist = substancenode.getChildren();
		for (Element element2 : templist) 
		{		               			  
		   if("SubstanceName".equals(element2.getName())&&!subsNameAsPerAgile.equals(element2.getText())) 
		   {
			   element2.setText(subsNameAsPerAgile);
		   }
		}		
	}
	
	private void createNewSubGrpAttachChild(String subGrpNameAsPerAgile,List<Element> childSubNode, Element parentElement) 
	{
		Element SubstanceNode = new Element("SubstanceNode");
		Element SubstanceName = new Element("SubstanceName");
		SubstanceName.setText(subGrpNameAsPerAgile);
		Element SubstanceType = new Element("SubstanceType");
		SubstanceType.setText("Substance Groups");
		Element LifecyclePhase = new Element("LifecyclePhase");
		LifecyclePhase.setText("Active");		
		Element ChildLevel = new Element("ChildLevel");
		ChildLevel.setText("2");
		SubstanceNode.addContent(SubstanceName);
		SubstanceNode.addContent(SubstanceType);
		SubstanceNode.addContent(LifecyclePhase);
		SubstanceNode.addContent(ChildLevel);
		SubstanceNode.addContent(childSubNode);
		parentElement.addContent(SubstanceNode);
	}
	
	private void populateSubsGrpToBeCreatedMap(HashMap<String,List<Element>> hmSubsGrpToBeCreated, Element substanceElment, String sSubGrpNameAsPerAgile1 ) 
	{
	  	  Object obj3=hmSubsGrpToBeCreated.get(sSubGrpNameAsPerAgile1);	            	                	  
	  	  if(obj3==null) 
	  	  {	            	  
	  		  List<Element>tempList1 = new ArrayList<Element>();
	  		  tempList1.add(substanceElment);
	      	  hmSubsGrpToBeCreated.put(sSubGrpNameAsPerAgile1.toString(),tempList1);
	  	  }else 
	  	  {
	  		  List<Element>tempList1 = hmSubsGrpToBeCreated.get(sSubGrpNameAsPerAgile1);
	  		  tempList1.add(substanceElment);
	  		  hmSubsGrpToBeCreated.put(sSubGrpNameAsPerAgile1, tempList1);
	  	  }
	}
	
    private boolean modifyMaterialDOMBasedOn_SourceXMLMap_InputCSVMap(HashMap<String, Element> hmCasNum_SubstancePair,HashMap<String, Element> hmSubGrpName_SubGrpElementPair) throws Exception
    {
    	boolean bSuccess = true;
		if(!hmCasNum_SubstancePair.isEmpty()) 
		{
			HashMap<String,List<Element>> hmSubsGrpToBeCreated = new HashMap<String,List<Element>>();
			Set sSubGrpNameKeys = hmSubGrpName_SubGrpElementPair.keySet();
			Iterator iter=hmCasNum_SubstancePair.keySet().iterator();
			while(iter.hasNext())
			{
			  String casNumberKey = (String) iter.next();
			  Object obj = csvInput.get(casNumberKey);
			  if(obj != null)
			  {
				  String sSub_subGrp = (String)obj;
				  String sSubGrpNameAsPerAgile = sSub_subGrp.substring(0,sSub_subGrp.indexOf("|"));
                  String sSubNameAsPerAgile = sSub_subGrp.substring(sSub_subGrp.indexOf("|") + 1,sSub_subGrp.length());

				  Element substancenode = hmCasNum_SubstancePair.get(casNumberKey);
				  Element parentElement = substancenode.getParentElement();
				  Element substancegrpnode = hmSubGrpName_SubGrpElementPair.get(sSubGrpNameAsPerAgile);		
				  
				  if(sSubGrpNameKeys.contains(sSubGrpNameAsPerAgile)) //if true move level2 substance under its already existing substance grp within same material context
				  {				    						    
					  parentElement.removeContent(substancenode);
					  modifySubstance_Name_Level(substancenode,sSubNameAsPerAgile,"3"); //new code 1
					  substancegrpnode.addContent(substancenode);						    																						 												
				  }else 
				  {					  
					  if(sSubGrpNameAsPerAgile.isEmpty()) // true indicate that substance at level2 in source xml doesnt belong to any substance grp
					  {
						  modifySubstanceName(substancenode,sSubNameAsPerAgile); //new code 2
						  
					  }else 
					  {
						  //create new substance grp node and attach substance child with cas number -- start
						  parentElement.removeContent(substancenode);
						  modifySubstance_Name_Level(substancenode,sSubNameAsPerAgile,"3");
						  //createNewSubGrpAttachChild(sSubGrpNameAsPerAgile,substancenode,parentElement);						  
						  populateSubsGrpToBeCreatedMap(hmSubsGrpToBeCreated,substancenode,sSubGrpNameAsPerAgile);
						  //create new substance grp node and attach substance child with cas number -- end
					  }
				  }
			  }	
			  else 
			  {
				  //return code to abolish execution for entire source xmls- start
				  log.info("Error:: Compliance Import for Source XML abolished due casNumber: "+casNumberKey+" : not found in Agile...............");
				  bSuccess = false;
				  break;
				  //return code to abolish execution for entire source xmls- end
			  }			  
			}
			//loop through hmSubGrpName_SubGrpElementPair to modify/delete substance group child substance node as per Agile--start 
			Iterator iter1 = sSubGrpNameKeys.iterator();
			Element materialElement = null;
			while(iter1.hasNext()) // loop through multiple grp name key for a material
			{
				  String sSubGrpName = (String) iter1.next();
				  Element subGrpElement = (Element)hmSubGrpName_SubGrpElementPair.get(sSubGrpName);
				  if(subGrpElement != null)
				  {	            		
					    List<Element> tempElementList = new ArrayList<Element>();
					    tempElementList.add(subGrpElement);
	            		List<Element> tempChildSublist = getFirstLevelChildByElementName("SubstanceNode",tempElementList);
	            		for (Element childSubstance : tempChildSublist) // loop through child substance of a substance group
	            		{
						    List<Element> tempElementList1 = new ArrayList<Element>();
						    tempElementList1.add(childSubstance);
	            			List <Element> subGrpAllCasNumbElementList =  getFirstLevelChildByElementName("CasNumber",tempElementList1 );
	            			for(Element casNumber : subGrpAllCasNumbElementList) 
	            			{
	            				String sCasNumber = casNumber.getText();
	            				  Object obj2 = csvInput.get(sCasNumber);
	            				  if(obj2 != null)
	            				  {
	            					  String sAgileSub_SubGrp = (String)obj2;
	            					  String sSubGrpNameAsPerAgile1 = sAgileSub_SubGrp.substring(0,sAgileSub_SubGrp.indexOf("|"));
	            	                  String sSubNameAsPerAgile1 = sAgileSub_SubGrp.substring(sAgileSub_SubGrp.indexOf("|") + 1,sAgileSub_SubGrp.length());
	            	                 
            	                	  Element rootSubstance_Level3 = casNumber.getParentElement();
            	                	  Element subsGrpElement = hmSubGrpName_SubGrpElementPair.get(sSubGrpName);
            	                	  materialElement = subsGrpElement.getParentElement();
            	                	  
	            	                  if("".equals(sSubGrpNameAsPerAgile1))//if group name is empty 
	            	                  {
	            	                	  subsGrpElement.removeContent(rootSubstance_Level3);
	            						  modifySubstance_Name_Level(rootSubstance_Level3,sSubNameAsPerAgile1,"2"); //new code 1
	            						  materialElement.addContent(rootSubstance_Level3);
	            	                  } else if(sSubGrpName.equals(sSubGrpNameAsPerAgile1)) 
	            	                  {
	            						  modifySubstanceName(rootSubstance_Level3,sSubNameAsPerAgile1); //new code 2
	            	                  } else 
	            	                  {
	            	                	  subsGrpElement.removeContent(rootSubstance_Level3);
	            	                	  modifySubstance_Name_Level(rootSubstance_Level3,sSubNameAsPerAgile1,"3");
	            	                	  populateSubsGrpToBeCreatedMap(hmSubsGrpToBeCreated,rootSubstance_Level3,sSubGrpNameAsPerAgile1);
	            	                  }
	            				  }
	            				  else 
	            				  {
	            					  //return code to abolish execution for entire source xmls- start
	            					  log.info("Error:: Compliance Import for Source XML abolished due casNumber: "+sCasNumber+" : not found in Agile...............");
	            					  bSuccess = false;
	            					  break;
	            					  //return code to abolish execution for entire source xmls- end
	            				  }
	            			}	            			
	            			if(!bSuccess) 
	            			{
	            				break;
	            			}
						}
	            		tempChildSublist = getFirstLevelChildByElementName("SubstanceNode",tempElementList);
	            		if(tempChildSublist.isEmpty()) 
	            		{
	            			materialElement.removeContent(subGrpElement);
	            		}
				  }
				  
      			if(!bSuccess) 
      			{
      				break;
      			}
			}
			Set<String> grpsNameToBeCreated = hmSubsGrpToBeCreated.keySet();
			Iterator <String> iter3 = grpsNameToBeCreated.iterator();
			while(iter3.hasNext()) 
			{
				String sGrpName = iter3.next();
				List<Element> sChildSubstance = hmSubsGrpToBeCreated.get(sGrpName);
				createNewSubGrpAttachChild(sGrpName, sChildSubstance, materialElement);
			}
			
			//loop through hmSubGrpName_SubGrpElementPair to modify/delete substance group child substance node as per Agile-end
		}
		log.info("reading substances node for a material end---");
		return bSuccess;
    }
    
	private boolean modifyLevel2_substancesNodesForMaterial (List<Element> listSubstance_SubGrpLevel_2) throws Exception
	{
		HashMap<String, Element> hmCasNum_SubstancePair = new HashMap<String, Element>(); 
        HashMap<String, Element> hmSubGrpName_SubGrpElementPair = new HashMap<String, Element>();		
        populateSourceXMLSubstanceInfoMaps(listSubstance_SubGrpLevel_2,hmCasNum_SubstancePair,hmSubGrpName_SubGrpElementPair);                
        return modifyMaterialDOMBasedOn_SourceXMLMap_InputCSVMap(hmCasNum_SubstancePair,hmSubGrpName_SubGrpElementPair);        
	}
	
	private boolean modifyMaterialSubNodesForGivenCondition (List<Element> tempMaterial) throws Exception
	{
		List <Element> listSubstance_SubGrpLevel_2  = new ArrayList<Element>();
		listSubstance_SubGrpLevel_2 = getFirstLevelChildByElementName("SubstanceNode",tempMaterial);			
		
		if(listSubstance_SubGrpLevel_2!=null) 
		{
			log.info("reading substances node for a material starts---");
			return modifyLevel2_substancesNodesForMaterial(listSubstance_SubGrpLevel_2);	
		}else
		{
		    return true;	
		}
	}
	
	private boolean modifyAllMaterialSubNodesForGivenCondition (List<Element> listMaterial_ChildLevel_1) throws Exception
	{		
		    boolean bSuccess = true;
    		for (Element materialElement : listMaterial_ChildLevel_1) //for loop for each material of subpart
    		{			             			
    			List<Element> tempMaterial = new ArrayList<Element>();
    			tempMaterial.add(materialElement); // list size is always one. creating list to support method argument signature for method    			
    			bSuccess = modifyMaterialSubNodesForGivenCondition(tempMaterial);
    			if(bSuccess) 
    			{
    				continue;
    			}else 
    			{
    				break;
    			}
    		}
			return bSuccess;	
		}

	
	private List <Element> getMaterialList_Level_1_ByDocument(Document document) throws Exception
	{		
		
		Element rootNode = null;
		List <Element> rootNodeTemp = null;
		List <Element> HomogeneousMaterialDeclarations = null;
		List <Element> ManufacturerPartComposition  = null;
		List <Element> Subparts_ChildLevel_0  = null;
		List <Element> listMaterial_ChildLevel_1  = null;
		
		rootNode = document.getRootElement();
		rootNodeTemp =  new ArrayList<Element>();
		rootNodeTemp.add(rootNode);
								
		if(rootNode!=null) 
		{
			HomogeneousMaterialDeclarations = getFirstLevelChildByElementName("HomogeneousMaterialDeclarations",rootNodeTemp);
		}
		if(HomogeneousMaterialDeclarations!=null) 
		{
			ManufacturerPartComposition = getFirstLevelChildByElementName("ManufacturerPartComposition",HomogeneousMaterialDeclarations);
		}
		if(ManufacturerPartComposition!=null) 
		{
			Subparts_ChildLevel_0 = getFirstLevelChildByElementName("SubstanceNode",ManufacturerPartComposition);
		}
		if(Subparts_ChildLevel_0!=null) 
		{
			listMaterial_ChildLevel_1 = getFirstLevelChildByElementName("SubstanceNode",Subparts_ChildLevel_0);
		}		 
		return listMaterial_ChildLevel_1;
	}
	
	private void createTargetXMLByDocumentInfo(Document document, String sAbsTargetXMLPath) throws Exception
	{
		xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(document, new FileWriter(sAbsTargetXMLPath));
	}
	
	private String[] getCASNumberOfAllSourceFiles(File[] arrFilesPath) throws Exception
	{
		Document document = null;
    	List <Element> listMaterial_ChildLevel_1  = null;		    	
    	List <Element> listSourceXMLAllSubstance_Level2  = null;
    	List<String>lsSourceXMLCasNumber = new ArrayList();
		for(int x=0;x<arrFilesPath.length;x++) 
		{
			sXMLAbsFileName = arrFilesPath[x].getAbsolutePath() ;
			document = getSAXParsedDocument(sXMLAbsFileName);
            sXMLAbsFileName = arrFilesPath[x].getName();
        	
			if(document != null) 
			{					
				listMaterial_ChildLevel_1 = getMaterialList_Level_1_ByDocument(document);
			}
			
			if(listMaterial_ChildLevel_1!=null) 
			{
				listSourceXMLAllSubstance_Level2 = getFirstLevelChildByElementName("SubstanceNode",listMaterial_ChildLevel_1);
			}
			
			if(listSourceXMLAllSubstance_Level2!=null) 
			{

				for(int i=0;i<=listSourceXMLAllSubstance_Level2.size()-1;i++)
				{  
	                    Element element_Substance_childLevel_2 = (Element)listSourceXMLAllSubstance_Level2.get(i);	                    
	            		List<Element> templist = element_Substance_childLevel_2.getChildren();
	            		HashMap temphm = new HashMap ();
	            		for (Element element2 : templist) 
	            		{
	     			               			  
	            		   temphm.put(element2.getName(), element2.getText());            			   
						}            		
	            		if(temphm.get("SubstanceType").equals("substances")) 
	            		{
	            			String tempcasnumber = (String)temphm.get("CasNumber");
	            			System.out.println("tempcasnumber-------------------"+tempcasnumber);
	            			if(!lsSourceXMLCasNumber.contains(tempcasnumber)) 
	            			{
	            			
		            			lsSourceXMLCasNumber.add(tempcasnumber);
	            			}
	            		}            		
				 }
			 }			
		 }
		return lsSourceXMLCasNumber.toArray(new String[0]);
	}
	
	private HashMap <String,String> getSubstanceInfoMapfromAgile(File[] arrFilesPath) throws Exception
	{        
		String[] arrSourceXMLCasNumber = getCASNumberOfAllSourceFiles(arrFilesPath);
		SubstanceUtility obj2 = new SubstanceUtility(obj1);		
		return obj2.fetchSubstanceAndGrpInfoMapfromAgile(arrSourceXMLCasNumber);
	}
	
	void readWriteXML () throws Exception
	{
		log.debug("------------readWriteXML method start..................");				
		Document document = null;

		arrFilesPath = getXMLFilesByDirName(obj1.sSourceXMLDirPath);
		
		if(arrFilesPath==null) 
		{
			throw new IOException("Source XML file reading error.Please check source directory path");
		}
		
		String sTargetXMLFolderPath = obj1.sTargetXMLAbsPath;
		FileManager.createOrReplaceFileAndDirectories(sTargetXMLFolderPath);
		
		ParseCSV objCSV = new ParseCSV();
		csvInput = objCSV.getMapByCSV(obj1.sSourceCSVPath);
		
		if(csvInput==null) 
		{
			csvInput = getSubstanceInfoMapfromAgile(arrFilesPath); //Method 2 code
		}
    	List <Element> listMaterial_ChildLevel_1  = null;		    	
    	
    	long completedIn;
    	
		if(csvInput!= null)
		{
			for(int x=0;x<arrFilesPath.length;x++) 
			{
				boolean bSuccess = true;
				sXMLAbsFileName = arrFilesPath[x].getAbsolutePath() ;
				log.info("XML/Dom loading starts for file  "+ sXMLAbsFileName);	
				document = getSAXParsedDocument(sXMLAbsFileName);
                sXMLAbsFileName = arrFilesPath[x].getName();
            	
				if(document != null) 
				{					
					listMaterial_ChildLevel_1 = getMaterialList_Level_1_ByDocument(document);
					
					if(listMaterial_ChildLevel_1!=null) 
					{
						bSuccess = modifyAllMaterialSubNodesForGivenCondition(listMaterial_ChildLevel_1);	
					}
						
					if(bSuccess) 
					{
						createTargetXMLByDocumentInfo(document,sTargetXMLFolderPath+File.separator+sXMLAbsFileName);
					}
					
					log.info("program InitiateXMLReadWrite end date time--" + dateFormat.format(date));
					completedIn = System.currentTimeMillis() - time;
					log.info("program InitiateXMLReadWrite completedIn milisecond --" + completedIn);
				}
			}
		}
		else 
		{
			log.info("Program exits due to csv input file reading error.Please check source directory path");				
            throw new IOException("Program exits due to csv input file reading error");
		}
		
	}
    
}
