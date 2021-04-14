package com.it.OpenOfficeUtils;



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
    private int count=0; //到60次自动关闭，一般情况也到不了，且若使用环境为web则应不会出现这些情况
    
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
	 		 if(delok || count == 0)   
	 		 {
	 			 //System.out.println("delete file success." + delok + "." + count);
	 			 if(!delok)
	 			 { 
	 			  throw new Exception("当前可能并未删除产生的pdf文件，请手动删除或选择deletePdfFileUseThread方法！");
	 			 }
	 			 excute = false;  //停止循环操作
	 		 }
	         Thread.sleep(10);  
		     count++;
		     
			} catch (Exception e) {
			   
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
	
