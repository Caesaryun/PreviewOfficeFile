package com.it.Preview;

import java.io.File;



import com.it.OpenOfficeUtils.OpenOfficeUtil;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	File input = new File("D:\\OpenOffice\\apptx.pptx");
	    String openOfficePath = "C:\\Program Files (x86)\\OpenOffice 4";
    	try {
			  
			  OpenOfficeUtil.OfficeFileToPdf("D:\\OpenOffice\\apptx.pptx");
			  //OpenOfficeUtil.OfficeFileToPdf(input);
			  
		} catch (Exception e) {
		
			e.printStackTrace();
		}
    	
    	
    	
    }
}
