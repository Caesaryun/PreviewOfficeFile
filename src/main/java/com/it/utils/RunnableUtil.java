package com.it.utils;



/*
*  @Author:    王发云
*  @DateTime:  2021年4月10日 下午5:23:47
*  @Description: TODO 该类为一个线程工具类，主要为配合删除预览产生的文件pdf
*/

public class RunnableUtil implements Runnable{

	private Thread t;
	private String threadName;
	private String newFilePdfName;
	private boolean excute;
    
	public RunnableUtil(String name,String pdfname){
		threadName = name;
		newFilePdfName = pdfname;
	}
	
	public void run() {
	    excute = true;
	    
		while(excute)
		{   
			try { 
			 boolean delok = OpenOfficeUtil.deletePdfFile(newFilePdfName);
	 		 if(delok)  
	 		 {
	 			 System.out.println("delete file success.");
	 			 excute = false;  //停止循环操作
	 		 }
	         Thread.sleep(3000);  
		     
			} catch (InterruptedException e) {
			   
		    } 
		}
	
		
		
	}
	
	
	public void start() {
		if(t==null)  
		{
			t = new Thread(this,threadName);
			t.start();
		}
	}

}
	
