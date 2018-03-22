package com.ec.usrcore.cache;

import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.usrcore.net.client.EpGateNetConnect;
import com.ec.usrcore.net.codec.EpGateEncoder;
import com.ec.usrcore.net.sender.EpGateMessageSender;
import com.ec.usrcore.service.CacheService;

public class ElectricPileCache {
		
	private Integer pkEpId; // 
	private String code; //
	private int gunNum;
	private int currentType; // 
	private int epType;
	private int rateid; // 
	
	private int gateid; //
	private int company_number; //
	private int state;//电桩状态
	private int deleteFlag;//删除标识

	private int netStatus;

	public ElectricPileCache()
	{
		gateid=0;
		pkEpId=0; // 
		code=""; //
		gunNum=0;
		currentType=0; // 
		epType=0;
		rateid=0; // 
		gateid=0; //
		
		state = 0;
		deleteFlag = 0;
		
	}
	
   
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public Integer getEpType() {
		return epType;
	}
	public void setEpType(Integer epType) {
		this.epType = epType;
	}

	public int getGunNum() {
		return gunNum;
	}
	public void setGunNum(int gunNum) {
		this.gunNum = gunNum;
	}
	public void setPkEpId(int pkEpId) {
		this.pkEpId = pkEpId;
	}
	
	
	
	public int getCurrentType() {
		return currentType;
	}
	public void setCurrentType(int currentType) {
		this.currentType = currentType;
	}
	public int getRateid() {
		return rateid;
	}
	public void setRateid(int rateid) {
		this.rateid = rateid;
	}
	public int getPkEpId() {
		return pkEpId;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	
	public int getGateid() {
		return gateid;
	}
	public void setGateid(int gateid) {
		this.gateid = gateid;
	}
	
	public int getCompany_number() {
		return company_number;
	}

	public void setCompany_number(int company_number) {
		this.company_number = company_number;
	}

	public int getNetStatus() {
		return netStatus;
	}

	public void setNetStatus(int netStatus) {
		this.netStatus = netStatus;
	}

	public int callEpAction(int type,int time,float lng,float lag)
	{
		EpGateNetConnect commClient = CacheService.getEpGate(this.code);
		if(commClient==null)
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		if(!commClient.isComm())
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
			
		}
		byte[] data = EpGateEncoder.do_near_call_ep(this.code, type,time);
		
		EpGateMessageSender.sendMessage(commClient.getChannel(),data);
		
		return 0;
	}

	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder();
        sb.append("ElectricPileCache");
    
        sb.append("{电桩pkEpId = ").append(pkEpId).append("}\n");
        sb.append("{电桩编号 = ").append(code).append("}\n");
        sb.append("{枪口数量 = ").append(gunNum).append("}\n");
        sb.append("{epType(电桩类型) = ").append(epType).append(getEpTypeDesc()).append("}\n");
        
        switch(currentType)
        {
        case 5:
        	sb.append("{电桩类型 = ").append("5直流").append("}\n");
        	break;
        case 14:
        	sb.append("{电桩类型 = ").append("14交流").append("}\n");
        	break;
        default:
        	sb.append("{电桩类型 = ").append(currentType).append("未知").append("}\n");
        	break;
		
        }
        sb.append("{gateid = ").append(gateid).append("}\n");
        
        sb.append("{公司标识 = ").append(company_number).append("}\n\r\n");
        
        sb.append("{费率id = ").append(rateid).append("}\n");
        
      	return sb.toString();
       
    }

	/**
	 * 
	 * @param reload,true.重新加载电桩实时数据
	 * @param pkUserCard,如果没有绑卡，那么不在线的桩不能充电
	 * @return
	 */
	public int canCharge(int epGunNo)
	{
		if (currentType != EpConstants.EP_DC_TYPE && currentType!= EpConstants.EP_AC_TYPE)
		{
			return ErrorCodeConstants.EPE_CHARGE_STYLE;
		}

		if(epGunNo<1|| epGunNo> getGunNum())
		{
			return ErrorCodeConstants.INVALID_EP_GUN_NO;
		}
		
		/*if(state == EpConstants.EP_STATUS_OFFLINE )
		{
			return ErrorCodeConstants.EP_PRIVATE;
		}
		else if(state < EpConstants.EP_STATUS_OFFLINE)
		{
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}*/
		if (deleteFlag != 0)
		{
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}
		
		return 0;
	}

	public int canCharge(int pkUserCard,int cardType,int cardOrgNo,int epGunNo)
	{
		if(epGunNo<1|| epGunNo> getGunNum())
		{
			return ErrorCodeConstants.INVALID_EP_GUN_NO;//
		}
		
		/*if(getState()==EpConstants.EP_STATUS_OFFLINE )
		{
			if(pkUserCard==0)
			{
				return ErrorCodeConstants.EP_PRIVATE;
			}
			else
			{
				if(cardType == EpConstants.CARD_NORMAL || cardType == EpConstants.CARD_THIRD_NORMAL)
				{
					return ErrorCodeConstants.EP_PRIVATE;
				}
				else
				{
					if(company_number != cardOrgNo)
					{
						return ErrorCodeConstants.EP_PRIVATE;
					}
				}
			}
		}
		else if(getState()<EpConstants.EP_STATUS_OFFLINE)
		{
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}*/
		if(getDeleteFlag() !=0)
		{
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}
		
		return 0;
	}
	
	  public String getEpTypeDesc() {
			
			String desc="";
			switch(this.epType)
	        {
	        case 0:
	        	desc="落地式";
	        	 break;
	        case 1:
	        	desc="壁挂式";
	       	 	break;
	        case 2:
	        	desc="拉杆式";
	       	 	break;
	        case 3:
	        	desc="便携式";
	       	 	break;
	       	 
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}
 }


