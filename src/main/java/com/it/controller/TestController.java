package com.it.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it.utils.OpenOfficeUtil;

import cn.hutool.core.util.IdUtil;

@RestController
@RequestMapping("/test")
public class TestController {

	 /*
	  * 简单示例
	  * 
	  */
	@RequestMapping("/previewpdf")
	public ResponseEntity<byte[]> filePreview() throws Exception {
		//1.文件的字节数组，文件类型扩展名，文件名称
		//2.将其他的office文件转换为字节数组
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF); 
		
	    String inputfilepath = "D:\\OpenOffice\\java基础.txt";
	    File officefile = new File("D:\\OpenOffice\\java基础.txt");
	    
	    //当用户提供字节数组时，如何操作，首先传入字节数组，和文件扩展名
	    byte[] buffFile = OpenOfficeUtil.fileToByteArray(officefile);
	    
	    try {
			byte[] pdfbyte = OpenOfficeUtil.OfficeFileToPdf(buffFile,"txt");
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(pdfbyte,headers,HttpStatus.OK);
			
			return responseEntity;
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	   return null;
	}
	
}
