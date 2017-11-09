package com.evmagile.db.utility;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
 
import com.agile.api.APIException;
import com.agile.api.AgileSessionFactory;
import com.agile.api.DeclarationConstants;
import com.agile.api.IAgileList;
import com.agile.api.IAgileSession;
import com.agile.api.IDeclaration;
import com.agile.api.IManufacturerPart;
import com.agile.api.IRow;
import com.agile.api.ISupplier;
import com.agile.api.ITable;
import com.agile.api.IUser;
import com.agile.api.ManufacturerPartConstants;
 
/**
* @author SKrishnan
*
*/
public class SaveAsHMD {
 
                /**
                **/
                private static IAgileSession agileSession=null;
 
                public SaveAsHMD() {
                                // TODO Auto-generated constructor stub
                }
 
                /**
                * @param args
                */
                   @SuppressWarnings({ "unchecked", "rawtypes" })
                public String getMDName_saveAsHMD (IAgileSession agileSession)
                   {
                   try {              
                       //connect to Agile Server
                        //agileSession = connect();
                       
                                    String MD_Number="MD64426"; //Get data from properties file
                                    String Proc_Analyst="skrishnan"; //Get data from Properties file
                                    String Form_Type="Request/Reply"; //Get data from Properties file
                                    String Mfr_Part_Number="HDSP-5603"; //Get data from Feed file
                                    String Mfr_Name="AGILENT TECHNOLOGIES"; //Get data from Feed file. One Mfr Name per feed file
                                    String Sup_Number="SUP000005405"; //Get data from Feed file. One Supplier Number per feed file
                                   
                                    //Initialize the params for Material Declaration
                        Map MDparams = new HashMap();
                                   
                        //Format date for Due Date
                                    SimpleDateFormat dueDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                   
                                    //Get current date
                                    Date currDate = new Date ();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(currDate);
                       
                        //setting due date one month
                        cal.add(Calendar.MONTH, 1);
                       
                                    Date dueDate = cal.getTime();
                                    MDparams.put(DeclarationConstants.ATT_COVER_PAGE_DUE_DATE, dueDateFormat.format(dueDate));
                                   
                                    //Prefix
                                                String MDPrefix="REVA_EC_";
                                               
                                                //Suffix incremental sequence
                                                String  MDSuffix="_003";
                                               
                                    //Format date for MD Number
                                                SimpleDateFormat mdDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                   
                                    //Suffix with Date & sequence number
                                    String MDnumber=MDPrefix + mdDateFormat.format(currDate) + MDSuffix;
                                    MDparams.put(DeclarationConstants.ATT_COVER_PAGE_REQUEST_DOCUMENT_ID, MDnumber);
                                   
                                    //Get Declaration object
                                                IDeclaration MD = (IDeclaration) agileSession.getObject(IDeclaration.OBJECT_TYPE, MD_Number);
                        //System.out.println("Old Declaration=" + MD.getName());
                       
                        
                        //Get Supplier (related to Manufacturer) object
                        ISupplier sup = (ISupplier) agileSession.getObject(ISupplier.OBJECT_TYPE, Sup_Number);
                        MDparams.put(DeclarationConstants.ATT_COVER_PAGE_SUPPLIER, sup);
                        //System.out.println("Supplier =" + sup.getName());
                       
                        
                        //Get Process Analyst object
                        IUser procMgr = (IUser) agileSession.getObject(IUser.OBJECT_TYPE, Proc_Analyst);
                        MDparams.put(DeclarationConstants.ATT_COVER_PAGE_COMPLIANCE_MANAGER, procMgr);
                        //System.out.println("Process Analyst =" + procMgr.getName());
                       
                        
                        //Get Form Type object
                        IAgileList frmType = (IAgileList) MD.getCell(DeclarationConstants.ATT_PAGE_TWO_FORM_TYPE).getAvailableValues();
                        frmType.setSelection(new Object[] {Form_Type});
                        MDparams.put(DeclarationConstants.ATT_PAGE_TWO_FORM_TYPE, frmType);
                       // System.out.println("Form Type=" + frmType.getSelection());
 
                      //  System.out.println(">>>> MAP =" + MDparams);
 
                        //Save As with details
                        IDeclaration NewMD=(IDeclaration) MD.saveAs(DeclarationConstants.CLASS_HOMOGENEOUS_MATERIAL_DECLARATION, MDparams);
                        //System.out.println("New Declaration=" + NewMD.getName()); //Get New MD Number
                       
                      //Initialize the params for Manufacturer Parts
                        Map MFRparams = new HashMap();
                        MFRparams.put(ManufacturerPartConstants.ATT_GENERAL_INFO_MANUFACTURER_PART_NUMBER,Mfr_Part_Number);
                        MFRparams.put(ManufacturerPartConstants.ATT_GENERAL_INFO_MANUFACTURER_NAME,Mfr_Name);
 
                        //Getting Mfr Part Number
                        IManufacturerPart mfrPart = (IManufacturerPart) agileSession.getObject(IManufacturerPart.OBJECT_TYPE ,MFRparams);
                        //Get affected Mfr Part Table
                        ITable mfrTable = NewMD.getTable(DeclarationConstants.TABLE_MANUFACTURERPARTS);
                        //Add Mfr Part to MD's affected Mfr Part Tab
                                                IRow row = mfrTable.createRow(mfrPart);
                       return NewMD.getName();
                      } 
                      catch (APIException e) 
                      {
                         e.printStackTrace();
                         return "";
                      }
                   }
}