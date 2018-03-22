package com.ec.epcore.net.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.service.EpCommClientService;
import com.ec.epcore.service.EpConcentratorService;
import com.ec.epcore.service.EpService;
import com.ec.epcore.service.StatService;
import com.ec.netcore.client.ECTcpClient;
import com.ec.utils.DateUtil;


public class EpCommClient extends ECTcpClient{
	
	private static final Logger logger = LoggerFactory.getLogger(EpCommClient.class);
	
	
	private int mode; //1:离散桩;2集中器
	
	
	private int gateId; //电桩网关程序id号

	private int revINum;  //add by hly
	
	private int sendINum;  //
	 
	private int bootStatus; //boot状态
	
	protected int commSignal;//通讯信号
	
	private long initTime;
	
	public EpCommClient()
	{
		mode = 0; //1:通讯建立连接，2：初始化成功;其它  
		
		gateId =0; //电桩网关程序id号
		
		lastUseTime =0;

		revINum = 0;  //add by hly
		
		sendINum = 0;  //
		
		this.version =2;
		bootStatus = 0;
		
		this.commSignal =0;
		
		initTime=0;
	}
	
	public int getCommSignal() {
		return commSignal;
	}
	
	
	public void setCommSignal(int commSignal) {
		this.commSignal = commSignal;
	}

	public int getBootStatus() {
		return bootStatus;
	}


	public void setBootStatus(int bootStatus) {
		this.bootStatus = bootStatus;
	}

	
	
	public Integer getSendINum() {
		return sendINum;
	}
	public Integer getSendINum2() {
		int t=sendINum;
		sendINum+=1;
		return t;
	}
	
	public void setSendINum(Integer sendINum) {
		this.sendINum = sendINum;
	}
	

	
	public Integer getRevINum() {
		return revINum;
	}
	public void setRevINum(Integer revINum) {
		this.revINum = revINum;
	}

	

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}
	
	
	
	public long getInitTime() {
		return initTime;
	}

	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}
	

	
	public void close() {
		super.close();
		logger.debug("close, identity:{},channel:{}",this.identity,this.channel);
		
		StatService.unRegProtocolVersion(2,version);
	}

	
	public void handleNetTimeOut() {
		if (this.channel == null) return;
		logger.debug("handleNetTimeOut, identity:{},channel:{}",this.identity,this.channel);
		super.handleNetTimeOut();	
		int len = 0;
		if(identity==null)
			return ;
		len =identity.length();
		if(len <=0 )
			return ;
	
		if(len == 16)
		{
			StatService.subCommDiscreteEp();
			EpService.handleCommClientTimeOut(identity,0);
		}
		else
		{
			StatService.subCommConcentrator();
			EpConcentratorService.handleCommClientTimeOut(identity,0);
		}
	}
	public void initNetSuccess(String identity,int version,int mode)
	{
		super.initNetSuccess(identity, version);
		
		long now = DateUtil.getCurrentSeconds(); 
		setInitTime(now);
		setMode(mode);
		
		EpCommClientService.addClient( this);
	}

	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("EpCommClient\n");
        
        switch(status)
        {
        case 1:
        	sb.append("通讯状态 = 1(通讯建立连接)\n");
        	break;
        case 2:
        	sb.append("通讯状态 = 2(初始化成功)\n");
        	break;
        default:
        	sb.append("通讯状态  = "+status+"(断开)\n");
        	break;
        }
        switch(mode)
        {
        case 1:
        	sb.append("设备类型 = 1(离散桩)\n");
        	break;
        case 2:
        	sb.append("设备类型 = 2(集中器)\n");
        	break;
        default:
        	sb.append("设备类型 = "+mode+"(其他)\n");
        	break;
        }
        if(channel==null)
        {
        	sb.append("通道 = null\n");
        }
        else
        {
        	sb.append("通道 = " + channel +"\n");
        }
        	
        sb.append("通讯版本 = " + version +"\n");
        sb.append("gateId = " + gateId +"\n");
        
        sb.append("identity = " + identity +"\n");
        
        sb.append("接收帧数 = " + revINum +"\n");
        
        sb.append("发送帧数 = " + sendINum +"\n");
        
        String sTime= DateUtil.StringYourDate(DateUtil.toDate(lastUseTime*1000));
        
        sb.append("通讯更新时间 = " + sTime +"\n");
        
        String iTime= DateUtil.StringYourDate(DateUtil.toDate(initTime*1000));
        
        sb.append("初始化时间 = " + iTime +"\n");
        
        sb.append("信号强度 = " + commSignal +"\n");
        
        switch(bootStatus)
        {
        case 0:
        	sb.append("boot = 0(正常)\n");
        	break;
        case 1:
        	sb.append("boot = 1(bootloader)\n");
        	break;
        default:
        	sb.append("boot = "+bootStatus+"(其他)\n");
        	break;
        }
        
        return sb.toString();
		
	}
	
}
