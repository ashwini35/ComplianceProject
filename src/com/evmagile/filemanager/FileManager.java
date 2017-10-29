package com.evmagile.filemanager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

 public class FileManager {
			
		  public static File errorLogFile = null;		  
		  public static File successLogFile = null;
		  private Logger log = null;
		  public SimpleDateFormat dateFormat = null;
		  Date date = null;
		  public String logFolderPath = null;
		  public String timestamp = null;
		  public FileManager()
		  {
			  log = Logger.getLogger(com.evmagile.filemanager.FileManager.class.getName());
			  date = new Date() ;
			  dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
			  timestamp = dateFormat.format(date);
		  }
		  
		  void addMessageToLog(String message)throws FileNotFoundException 
		  {
				
				try
				{
					File errLogFile = getErrorLogFile();									
					PrintWriter pw = new PrintWriter(new FileWriter(errLogFile,true));
					pw.write("\n"+message.toString());	
					//pw.append("\n");	
					pw.close();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					log.info(ex.getMessage());
				}
					
			}
			
		  public File getErrorLogFile() throws Exception  
				{		    
					if(errorLogFile==null){
						
						errorLogFile = new File(logFolderPath+"Error_"+ timestamp + ".csv");
						
					}			   
					
				      return errorLogFile;			
			    }				
			
		  void addSuccessMesstoLog(String message)throws FileNotFoundException 
		    {
				
				try
				{
					File successLog = getSuccessLogFile();									
					PrintWriter pw = new PrintWriter(new FileWriter(successLog,true));
					pw.write("\n"+message.toString());	
					pw.append("\n");	
					pw.close();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					log.info(ex.getMessage());
				}
					
			}
		  
			public File getSuccessLogFile() throws Exception  
			{		    
				if(successLogFile==null)
				{
					successLogFile = new File(logFolderPath+"Success_"+ timestamp + ".log");
				}			     
			    return successLogFile;			
		    }
			


				public static void createOrReplaceFileAndDirectories(String path) throws IOException
				{

				    File file = new File(path);
				    if(file.exists())
				    {
				        do{
				            delete(file);
				        }while(file.exists());
				    }
				    
				    file.mkdir();

				}
				
				private static void delete(File file) 
				{
				    if(file.isDirectory())
				    {
				        String fileList[] = file.list();
				        if(fileList.length == 0)
				        {
				            file.delete();
				        }else
				        {
				            int size = fileList.length;
				            for(int i = 0 ; i < size ; i++)
				            {
				                String fileName = fileList[i];
				                String fullPath = file.getPath()+"/"+fileName;
				                File fileOrFolder = new File(fullPath);
				                delete(fileOrFolder);
				            }
				        }
				    }else
				    {
				        file.delete();
				    }

                }
    }

