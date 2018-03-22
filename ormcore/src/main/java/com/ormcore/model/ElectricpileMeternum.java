package com.ormcore.model;


/**
 * @description:
 * @author: autoCode
 * @history:
 */
public class ElectricpileMeternum {
				
	/** 读表日期**/
	private String readDate;
			
	/** 桩体编号**/
	private String epCode;
			
	/** 桩体总电度**/
	private String totalMeter;
			
	/** 枪口1总电度**/
	private String gun1Meter;
			
	/** 枪口2总电度**/
	private String gun2Meter;
			
	/** 枪口3总电度**/
	private String gun3Meter;
			
	/** 枪口4总电度**/
	private String gun4Meter;
					
	public void setReadDate(String readDate){
		this.readDate = readDate;
	} 
	
	public String getReadDate(){
		return readDate;
	} 
			
	public void setEpCode(String epCode){
		this.epCode = epCode;
	} 
	
	public String getEpCode(){
		return epCode;
	} 
			
	public void setTotalMeter(String totalMeter){
		this.totalMeter = totalMeter;
	} 
	
	public String getTotalMeter(){
		return totalMeter;
	} 
			
	public void setGun1Meter(String gun1Meter){
		this.gun1Meter = gun1Meter;
	} 
	
	public String getGun1Meter(){
		return gun1Meter;
	} 
			
	public void setGun2Meter(String gun2Meter){
		this.gun2Meter = gun2Meter;
	} 
	
	public String getGun2Meter(){
		return gun2Meter;
	} 
			
	public void setGun3Meter(String gun3Meter){
		this.gun3Meter = gun3Meter;
	} 
	
	public String getGun3Meter(){
		return gun3Meter;
	} 
			
	public void setGun4Meter(String gun4Meter){
		this.gun4Meter = gun4Meter;
	} 
	
	public String getGun4Meter(){
		return gun4Meter;
	} 
}
