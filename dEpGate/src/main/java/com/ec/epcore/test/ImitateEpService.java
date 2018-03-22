package com.ec.epcore.test;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.cache.ChargeCache;
import com.ec.epcore.net.proto.ChargeCmdResp;
import com.ec.epcore.net.proto.EpBespResp;
import com.ec.epcore.net.proto.EpCancelBespResp;
import com.ec.epcore.service.EpBespokeService;
import com.ec.epcore.service.EpChargeService;
import com.ec.epcore.service.EpService;
import com.ec.net.proto.WmIce104Util;

public class ImitateEpService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ImitateEpService.class);
	
	private static boolean startImitate = false;
	
	public static boolean IsStart()
	{
		return startImitate;
	}
	public static void init()
	{
		startImitate =true;
		EpService.imitateInitDiscreteEp(3, "3301021010000008");	
	}
    public static void ImitateBespoke(String epCode, int epGunNo,int nRedo,String OrderNo)
     {
	    EpBespResp bespResp = new EpBespResp(epCode,epGunNo,nRedo,OrderNo,1,0);
	    byte[] cmdTimes = WmIce104Util.timeToByte();

	    EpBespokeService.handleEpBespRet(null,bespResp,cmdTimes);
	     
    }
    
	public static void ImitateCancelBespoke(String epCode, int epGunNo,String bespokeNo)
	{
		byte successFlag = 1;
		
		java.util.Date dtEt = new Date();
		long et = dtEt.getTime() / 1000;
		// }}
		
		EpCancelBespResp cancelBespResp = new EpCancelBespResp(epCode, epGunNo,
				bespokeNo, (short)1, successFlag, et,0);
		  byte[] cmdTimes = WmIce104Util.timeToByte();

		EpBespokeService.onEpCancelBespRet(null,cancelBespResp,cmdTimes);
	}
	
	
	public static void startCharge(String epCode, int epGunNo,String Account)
	{
		ChargeCmdResp chargeCmdResp= new ChargeCmdResp(epCode, epGunNo, 1, (short)0);
		
		 byte[] cmdTimes = WmIce104Util.timeToByte();
		 
		EpChargeService.handEpStartChargeResp(null, chargeCmdResp,cmdTimes);
		
		
		ImitateChargeEvent(epCode,epGunNo,Account);
	}
	
	private static int chargeIndex=0;
	public static void ImitateChargeEvent(String epCode, int epGunNo,String Account)
	{
		/*
		chargeIndex = chargeIndex+1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHH");

    
    	String pattern="00000000";
    	java.text.DecimalFormat df = new java.text.DecimalFormat(pattern);
    	
    	Date now = new Date();  
		// 2 ������ˮ�� BCD�� 10Byte 16λ���״���
		String SerialNo = epCode + dateFormat.format( now ) + df.format(chargeIndex);
		// 3 ���ʾ�� ��ȷ��С������λ����λ��
		int meterNum = 10000;
		// 4 ԤԼ�� BCD�� 6Byte ��ԤԼ����ԤԼ�ţ���ԤԼ�Ŷ�Ϊ0x00
		String OrderNo = StringUtil.repeat("0", 12);
		// 5 ��翪ʼʱ�� CP56Time2a
		//byte[] bStartTimes = StreamUtil.readCP56Time2a(in);
		long st = DateUtil.getCurrentSeconds();//WmIce104Util.getP56Time2aTime(bStartTimes);
		// 6 ���ǹ��� BIN�� 1Byte
		//int epGunNo = StreamUtil.read(in);
		// 7 ��緽ʽ BIN�� 1Byte 1:����
		// 2:���
		// 3:ʱ��
		// 4:�Զ�����
		byte b_cd_style = 1;
		// 8 �����ʣ��ʱ�� BIN�� 4Byte ��ȷ������

		int remainSeconds = 0;

		//String Account = StreamUtil.readBCDWithLength(in, 6);
		// 10 Ԥ�����
		int momeny = 0;

		byte offstates = 1;

		//byte flag = StreamUtil.read(in);

		byte successflag = 1;
		
		
		ChargeEvent chargeEvent = 
				new ChargeEvent(epCode,epGunNo,SerialNo,meterNum,(int)st,remainSeconds,successflag,0);

		
		int commVer=0;
		ElectricPileCache epClient = EpService.getEpByCode(epCode);
		if(epClient!=null)
		{
			EpCommClient commClient = (EpCommClient)epClient.getEpNetObject();
		    if(commClient!=null)
		    {
		    	commVer = commClient.getCommVersion();
		    }
		}
		byte[] cmdTimes = WmIce104Util.timeToByte();
		
		    EpChargeService.handleStartElectricizeEventV3(
				null, chargeEvent,cmdTimes);
		*/
	}
	
	
	public static void ImitateStopCharge(String epCode, int epGunNo,String account,ChargeCache chargeCacheObj)
	{
		ChargeCmdResp chargeCmdResp= new ChargeCmdResp(epCode, epGunNo, 1, (short)0);
		
		byte[] cmdTimes = WmIce104Util.timeToByte();
		EpChargeService.handEpStartChargeResp(null, chargeCmdResp,cmdTimes);
		
		ImitateChargeConsumeRecord(epCode,epGunNo,account,chargeCacheObj);
	}
	
	
    public static void ImitateChargeConsumeRecord(String epCode, int epGunNo,String account,ChargeCache chargeCacheObj)
	{
    	/*
		NoCardConsumeRecord consumeRecord = new NoCardConsumeRecord();
		
		consumeRecord.setEpCode(epCode);
		consumeRecord.setEpGunNo(epGunNo);

		// 2 ������ˮ�� BCD�� 10Byte 16λ���״���
	     consumeRecord.setSerialNo(chargeCacheObj.getChargeSerialNo());
		// 3 �û���� BCD�� 8Byte 16λ�豸����
	     consumeRecord.setAccountType(1);
		consumeRecord.setEpUserAccount(account);
//		// 4 ���߽������� BIN�� 1Byte 0:
		consumeRecord.setTransType(0);
//		// 5 ��ʼʱ�� BIN�� 7Byte CP56Time2a
		consumeRecord.setStartTime(chargeCacheObj.getSt());
		// 6 ����ʱ�� BIN�� 7Byte CP56Time2a
		consumeRecord.setEndTime(chargeCacheObj.getEt());		
//		// 7 �����
		consumeRecord.setjDl(2);
//		// 8 ����
		consumeRecord.setjAmt(2);
//		
//		// 9 �����
		consumeRecord.setfDl(2);
//		// 10 ����
		consumeRecord.setfAmt(2);
//		
//		// 11ƽ����
    	consumeRecord.setpDl(2);
//		// 12 ƽ���
		consumeRecord.setpAmt(2);
//		
//		// 13�ȶ���
		consumeRecord.setgDl(2);
//		// 14 �Ƚ��
		consumeRecord.setgAmt(2);
//		
//		// 15 �Ƚ��
		consumeRecord.setTotalDl(2);
//		
//		// 16�ȶ���
		consumeRecord.setTotalAmt(2);
	
//		// 17�ȶ���
		consumeRecord.setStartMeterNum(2);
//		// 18 �Ƚ��
		consumeRecord.setEndMeterNum(2);
//		//19ֹͣ���ԭ��
		consumeRecord.setStopCause(chargeCacheObj.getStopCause());
	
//		
		byte[] cmdTimes = WmIce104Util.timeToByte();
		try {
			EpChargeService.handleConsumeRecord(null,consumeRecord,cmdTimes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
     }
    
  
	

	
}
