package com.it.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

import cn.hutool.core.util.IdUtil;

/*
 * 1.该类主要为OpenOffice工具类，其中包含多个方法
 * 2.使用示例1：byte[] pdfbyte = OpenOfficeUtil.方法名（参数...);
 *   ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(pdfbyte,headers,HttpStatus.OK);   
 * 3.使用示例2: ResponseEntity<byte[]> responseEntity = OpenOfficeUtil.方法名Use(...)
 * 4.实现预览，通过在前台页面发送请求获取ResponseEntity<byte[]>即可自动预览pdf文件
 * 5.后续：后续会将FastDFS也整合到该项目，以便做到轻松的通过获取远程文件进行预览操作
 */

public class OpenOfficeUtil {
	/*
	 * 1.将文件转为字节数组
	 * file change to byte[]
	 * @param:File file 为文件
	 */
	public static byte[] fileToByteArray(File file) throws IOException {
		byte[] fileByte = new byte[(int)file.length()];
		
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while((len = fis.read(b)) != -1 ) {
            bos.write(b, 0, len);
        }
        fileByte = bos.toByteArray();
        
        
        return  fileByte;
	}
	
	/*
	 * 将byte[] 转为 File 文件
	 * @param: bfile 文件的byte数组 byte[]
	 * @param: filepath 要写入的文件位置  "D:\\OpenOffice" + File.separator
	 * @param: filename 写入之后的文件名称 "xxx.docx"
	 * 
	 */
	public static File byteToFile(byte[] bfile,String filePath,String fileName)
	{
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		
		try {
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){
                dir.mkdirs();
            }
            file = new File(filePath+File.separatorChar+fileName);  
            fos = new FileOutputStream(file);     
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);  
            
            return file;
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }	
		
		return null;
	}
	
	
	/*
	 *1. Object转化为byte[]
	 *@param:Object类型
	 */
	public static byte[] Object2Bytes(Object obj)
    {
        
        return null;
    }
	
	/*
	 * 1.判断文件是否为office格式文件
	 * @param：String ext 为文件扩展名
	 */
	
	public static boolean isOfficeFormat(String ext)
	{
		if("doc".equals(ext) || "docx".equals(ext) || "xls".equals(ext)|| 
	      "xlsx".equals(ext) || "ppt".equals(ext)  || "pptx".equals(ext)
				             || "txt".equals(ext)) 
		{    /* 满足要求 */   
			return true;
	    }
		
		return false;
	}
	
	/*
	 * 1.用于删除产生的pdf文件
	 * 这里和RunnableUtil配合使用
	 * 且应该在这里判断一下系统，以便配合linux系统的路径进行删除文件
	 * @param : String pdfName 为pdf文件名称
	 */
	
	public static boolean deletePdfFile(String pdfName) {
		
		/*
		 * 这里先做个判断，当前项目所处的系统，因为windows和linux的文件路径有所区别
		 */
		 
		String filePath = " ";
		String osName = System.getProperty("os.name");
		if(Pattern.matches("Linux.*",osName))
		{      
			   filePath = "/usr/local/TempOpenOffice/";
		}
		else if(Pattern.matches("Windows.*", osName)) {
			   filePath = "D:\\OpenOfficeTemp\\";
		}
		
		boolean isokdel = false;
		File f = new File(filePath+pdfName);
		if(f.exists())
		{
		    isokdel =  f.delete();
		}
		
		return isokdel;
	}
	
	
	/*
	 * 1.在windows环境下访问调用该方法
	 * 关于将office类的文件转换为pdf格式的方法即可
	 * 返回pdf文件字节数组
	 * 2.若将该项目放置于linux上的时候，则会在linux环境下进行创建相关数据，然后进行返回相关信息给客户端
	 * 3.之后还应调用删除pdf文件的线程
	 * 4.传入文件路径即可，无论是windows或linux皆可
	 * @param： String inputfilepath 为文件的路径字段，window linux都ok.
	 */
	public static byte[]  OfficeFileToPdf(String inputfilepath) throws Exception{
		/* 1.传入office文件，且进行判断是否满足office文件  */
		File officefile = new File(inputfilepath);
		
		String fileName = officefile.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		
		if(!isOfficeFormat(fileType))
		 {   /*2.不满足文件要求 */
		    throw new Exception("请选择office格式文件!");
		 }
		
		String newFilePdfName =  IdUtil.fastSimpleUUID() + ".pdf";
		
        byte[] buffFile = OpenOfficeUtil.fileToByteArray(officefile);
		
		InputStream inputStream = new ByteArrayInputStream(buffFile);
		
		String filePath = " ";
		String osName = System.getProperty("os.name");
		if(Pattern.matches("Linux.*",osName))
		{     
			   filePath = "/usr/local/TempOpenOffice";
		}
		else if(Pattern.matches("Windows.*", osName)) {
			   filePath = "D:\\OpenOfficeTemp";
		}
		
		File dir = new File(filePath); 
        if(!dir.exists()){ 
            dir.mkdirs(); 
        }
		
		File outputfile = new File(filePath+File.separatorChar+newFilePdfName);
		if(outputfile.exists())  
		{
			outputfile.delete();
		}
		outputfile.createNewFile(); 
		OutputStream outputStream = new FileOutputStream(outputfile);
		
		DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
		DocumentFormat officeFormat = formatReg.getFormatByFileExtension(fileType);
		DocumentFormat pdfFormat = formatReg.getFormatByFileExtension("pdf");
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100); 
		connection.connect();
		DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
		
		System.out.println("Connect: connect openoffice success.");
		
		converter.convert(inputStream,officeFormat,outputStream,pdfFormat);
		
		connection.disconnect();
		
		System.out.println("Convert: office format file converts to pdf success.");
		
		byte[] pdfByte =  OpenOfficeUtil.fileToByteArray(outputfile);
		System.out.println("Convert: pdf file converts to byte[] success.");
		
		RunnableUtil R1 = new RunnableUtil("delete pdf file",newFilePdfName);
		R1.start();
		
		return pdfByte;
	}
	
	/*
	 * 1.这里传入office文件的字节数组即可
	 * 先通过字节数组获取到文件扩展名，判断是否满足要求
	 * 返回pdf的字节数组
	 * @param byte[] buffFile :为传递来的office文件字节数组
	 * @param fileType 为office文件的扩展名
	 */
	public static  byte[]  OfficeFileToPdf(byte[] buffFile,String fileType) throws Exception{
		/* 1.传入文件，将字节数组传入其中进行转化为文件，通过文件获取扩展名等信息  */
		String filePath = " ";
		String osName = System.getProperty("os.name");
		if(Pattern.matches("Linux.*",osName))
		{      
			   filePath = "/usr/local/TempOpenOffice";
		}
		else if(Pattern.matches("Windows.*", osName)) 
		{
			   filePath = "D:\\OpenOfficeTemp";
		}
		File dir = new File(filePath); 
		
        if(!dir.exists()){ 
            dir.mkdirs(); 
        }
		
		
		if(!isOfficeFormat(fileType))
		 {   /*2.不满足文件要求 */
		    throw new Exception("请选择office格式文件!");
		 }
		
		System.out.println("Accept : accept fastdfs' data success.");
		
		String newFilePdfName =  IdUtil.fastSimpleUUID() + ".pdf";
		
		InputStream inputStream = new ByteArrayInputStream(buffFile);
		
		System.out.println("inputStream: "+inputStream.toString());
		File outputfile = new File(filePath+File.separatorChar+newFilePdfName);
		
		if(outputfile.exists())  
		{
			outputfile.delete();
		}
		outputfile.createNewFile(); 
		OutputStream outputStream = new FileOutputStream(outputfile);
		DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
		DocumentFormat officeFormat = formatReg.getFormatByFileExtension(fileType);
		DocumentFormat pdfFormat = formatReg.getFormatByFileExtension("pdf");
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100); 
		connection.connect();
		DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
		System.out.println("Connect: connect openoffice success.");
		
		converter.convert(inputStream,officeFormat,outputStream,pdfFormat);
		
		connection.disconnect();
		
		System.out.println("Convert: office format file converts to pdf success.");

		byte[] pdfByte =  OpenOfficeUtil.fileToByteArray(outputfile);
		System.out.println("Convert: pdf file converts to byte[] success.");
		
		RunnableUtil R2 = new RunnableUtil("delete pdf file",newFilePdfName);
		R2.start();
		
		return pdfByte;
	}
	
	/*
	 * 1.传入office文件
	 * @param: 为传入的文件
	 */
	
	public static byte[]  OfficeFileToPdf(File officefile) throws Exception{
		/* 1.传入office文件，且进行判断是否满足office文件  */
		String fileName = officefile.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		
		if(!isOfficeFormat(fileType))
		 {   /*2.不满足文件要求 */
		    throw new Exception("请选择office格式文件!");
		 }
		
		String newFilePdfName =  IdUtil.fastSimpleUUID() + ".pdf";
		
        byte[] buffFile = OpenOfficeUtil.fileToByteArray(officefile);
		System.out.println("Accept : accept fastdfs' data success.");
		InputStream inputStream = new ByteArrayInputStream(buffFile);
		
		String filePath = " ";
		String osName = System.getProperty("os.name");
		if(Pattern.matches("Linux.*",osName))
		{      
			   filePath = "/usr/local/TempOpenOffice";
		}
		else if(Pattern.matches("Windows.*", osName))
		{
			   filePath = "D:\\OpenOfficeTemp";
		}
		
		File dir = new File(filePath); 
        if(!dir.exists()){ 
            dir.mkdirs(); 
        }
		
		File outputfile = new File(filePath+File.separatorChar+newFilePdfName);
		if(outputfile.exists())  
		{
			outputfile.delete();
		}
		outputfile.createNewFile(); 
		OutputStream outputStream = new FileOutputStream(outputfile);
		
		DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
		DocumentFormat officeFormat = formatReg.getFormatByFileExtension(fileType);
		DocumentFormat pdfFormat = formatReg.getFormatByFileExtension("pdf");
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100); 
		connection.connect();
		DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
		System.out.println("Connect: connect openoffice success.");
		
		converter.convert(inputStream,officeFormat,outputStream,pdfFormat);
		
		connection.disconnect();
		
		System.out.println("Convert: office format file converts to pdf success.");
		
		byte[] pdfByte =  OpenOfficeUtil.fileToByteArray(outputfile);
		System.out.println("Convert: pdf file converts to byte[] success.");
		
		RunnableUtil R2 = new RunnableUtil("delete pdf file",newFilePdfName);
		R2.start();
		
		return pdfByte;
	}
	
	/*
	 * 1.选择直接返回可用的ResponseEntity<byte[]>类型数据
	 * @param:为传入文件
	 */
	
	public static ResponseEntity<byte[]>  OfficeFileToPdfUse(File officefile) throws Exception{
		/* 1.传入office文件，且进行判断是否满足office文件  */
		String fileName = officefile.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		
		if(!isOfficeFormat(fileType))
		 {   /*2.不满足文件要求 */
		    throw new Exception("请选择office格式文件!");
		 }
		
		String newFilePdfName =  IdUtil.fastSimpleUUID() + ".pdf";
		
        byte[] buffFile = OpenOfficeUtil.fileToByteArray(officefile);
		
		
		
		System.out.println("Accept : accept fastdfs' data success.");

		InputStream inputStream = new ByteArrayInputStream(buffFile);
		
		System.out.println("inputStream: "+inputStream.toString());
		String filePath = " ";
		String osName = System.getProperty("os.name");
		if(Pattern.matches("Linux.*",osName))
		{      
			   filePath = "/usr/local/TempOpenOffice";
		}
		else if(Pattern.matches("Windows.*", osName)) {
			   filePath = "D:\\OpenOfficeTemp";
		}
		
		File dir = new File(filePath); 
        if(!dir.exists()){ 
            dir.mkdirs();  
        }

		File outputfile = new File(filePath+File.separatorChar+newFilePdfName);
		
		if(outputfile.exists())  
		{
			outputfile.delete();
		}
		
		outputfile.createNewFile(); 
		OutputStream outputStream = new FileOutputStream(outputfile);
		DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
		DocumentFormat officeFormat = formatReg.getFormatByFileExtension(fileType);
		DocumentFormat pdfFormat = formatReg.getFormatByFileExtension("pdf");
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100); 
		connection.connect();
		DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
		System.out.println("Connect: connect openoffice success.");
		
		converter.convert(inputStream,officeFormat,outputStream,pdfFormat);
		
		connection.disconnect();
		
		System.out.println("Convert: office format file converts to pdf success.");
		byte[] pdfByte =  OpenOfficeUtil.fileToByteArray(outputfile);
		System.out.println("Convert: pdf file converts to byte[] success.");

		RunnableUtil R1 = new RunnableUtil("delete pdf file",newFilePdfName);
		R1.start();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF); 
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(pdfByte,headers,HttpStatus.OK);
		
		return responseEntity;
	}
	
	/*
	 * 1.选择直接返回可用的ResponseEntity<byte[]>类型数据
	 * @param：传入需要转换的office文件路径
	 */
	
	public static ResponseEntity<byte[]>  OfficeFileToPdfUse(String inputfilepath) throws Exception{
		/* 1.传入office文件，且进行判断是否满足office文件  */
		File officefile = new File(inputfilepath);
		String fileName = officefile.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		
		if(!isOfficeFormat(fileType))
		 {   /*2.不满足文件要求 */
		    throw new Exception("请选择office格式文件!");
		 }
		
		String newFilePdfName =  IdUtil.fastSimpleUUID() + ".pdf";
		
        byte[] buffFile = OpenOfficeUtil.fileToByteArray(officefile);
		System.out.println("Accept : accept fastdfs' data success.");

		InputStream inputStream = new ByteArrayInputStream(buffFile);
		
		System.out.println("inputStream: "+inputStream.toString());
		
		String filePath = " ";
		String osName = System.getProperty("os.name");
		if(Pattern.matches("Linux.*",osName))
		{     
			   filePath = "/usr/local/TempOpenOffice";
		}
		else if(Pattern.matches("Windows.*", osName)) 
		{
			   filePath = "D:\\OpenOfficeTemp";
		}
		
		File dir = new File(filePath); 
        if(!dir.exists()){ 
        	
            dir.mkdirs();  
        }
		
		File outputfile = new File(filePath+File.separatorChar+newFilePdfName);
		
		if(outputfile.exists())  
		{
			outputfile.delete();
		}
		
		outputfile.createNewFile(); 
		
		OutputStream outputStream = new FileOutputStream(outputfile);
		
		DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
		DocumentFormat officeFormat = formatReg.getFormatByFileExtension(fileType);
		DocumentFormat pdfFormat = formatReg.getFormatByFileExtension("pdf");
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100); 
		connection.connect();
		DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
		System.out.println("Connect: connect openoffice success.");
		
		converter.convert(inputStream,officeFormat,outputStream,pdfFormat);
		
		connection.disconnect();
		
		System.out.println("Convert: office format file converts to pdf success.");
		
		byte[] pdfByte =  OpenOfficeUtil.fileToByteArray(outputfile);
		System.out.println("Convert: pdf file converts to byte[] success.");
		
		RunnableUtil R1 = new RunnableUtil("delete pdf file",newFilePdfName);
		R1.start();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF); 
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(pdfByte,headers,HttpStatus.OK);
		
		return responseEntity;
	}
	
	
}
