package com.ec.epcore.test;

import java.math.BigDecimal;

import com.ec.constants.EpConstants;
import com.ec.epcore.cache.RealACChargeInfo;
import com.ec.epcore.cache.RealChargeInfo;
import com.ec.epcore.cache.RealDCChargeInfo;
import com.ec.epcore.cache.UserCache;
import com.ec.epcore.service.EpChargeService;
import com.ec.epcore.service.UserService;
import com.ormcore.dao.DB;
import com.ormcore.model.TblChargeACInfo;
import com.ormcore.model.TblChargeDCInfo;

public class UnitTest {
	
	public static void testPauseStat(int usrId,String account)
	{
		EpChargeService.jmsgPauseOrderStat(usrId, 1, "1111111111111111", "12.12");
		EpChargeService.jmsgPauseStat(usrId, account, 1, "1111111111111111");
		
	}
	
	public static void testACRealData(String epCode, int epGunNo)
    { 
		RealChargeInfo tmpRealChargeInfo =null;
		
		int currentType= EpConstants.EP_AC_TYPE;
		
		RealACChargeInfo chargeInfo = new RealACChargeInfo();
		tmpRealChargeInfo = chargeInfo;
		
		tmpRealChargeInfo.init();
		tmpRealChargeInfo.setCurrentType(currentType);
		tmpRealChargeInfo.setEpCode(epCode);
		tmpRealChargeInfo.setEpGunNo(epGunNo);
		
		
		boolean loadSuccess = tmpRealChargeInfo.loadFromDb(epCode, epGunNo);
		chargeInfo.setLinkCarStatus((short)1);
		chargeInfo.setGunCloseStatus((short)1);
		chargeInfo.setGunLidStatus((short)1);
		chargeInfo.setCommStatusWithCar((short)1);
		chargeInfo.setCarPlaceStatus((short)1);
		chargeInfo.setCardReaderFault((short)1);//读卡器通讯故障
		chargeInfo.setUrgentStopFault((short)1);
		chargeInfo.setArresterFault((short)1);
		chargeInfo.setInsulationCheckFault((short)1);
		
		chargeInfo.setGunUnconnectWarn((short)1);
		chargeInfo.setTransRecordFullWarn((short)1);
		chargeInfo.setMeterError((short)1);
		
		chargeInfo.setAcInVolWarn((short)2);
		chargeInfo.setChargeOverTemp((short)1);
		chargeInfo.setAcCurrentLoadWarn((short)2);
		chargeInfo.setOutRelayStatus((short)1);
		
		//公共遥测
		chargeInfo.setWorkingStatus(31);
		chargeInfo.setOutVoltage(1231);
		chargeInfo.setOutCurrent(321);
		chargeInfo.setCarPlaceLockStatus(2);
		chargeInfo.setSoc(99);
		chargeInfo.setChargedTime(98);
		chargeInfo.setChargeRemainTime(10);
		
		//公共的变长遥测
		chargeInfo.setTotalActivMeterNum(9999);
		chargeInfo.setChargedMeterNum(9998);
		chargeInfo.setChargePrice(888);
		
		//充电相关
		//chargeUserId  = tblRealData.getChargeUserId() ;
		//chargeSerialNo  = tblRealData.getChargeSerialNo();
		//chargeStartTime = tblRealData.getChargeStartTime();
		//chargeStartMeterNum  = tblRealData.getChargeStartMeterNum().multiply(Global.DecTime3).intValue();
		//chargedCost  = tblRealData.getChargedCost().multiply(Global.DecTime2).intValue();
		//fronzeAmt = tblRealData.getFronzeAmt().multiply(Global.DecTime2).intValue();//冻结金额
		
		
		
		tmpRealChargeInfo.saveDb();
		
			
		TblChargeACInfo tblChargeInfo =  new TblChargeACInfo();
		tblChargeInfo.setPk_chargeinfo(tmpRealChargeInfo.getPkChargeInfo());
		tblChargeInfo.setEp_code(epCode);
		tblChargeInfo.setEp_gun_no(epGunNo);
		String serialNo= new String("1234567890123456");
		tblChargeInfo.setChargeSerialNo(serialNo);
		BigDecimal bdMeterNum= new BigDecimal(123.1);
		tblChargeInfo.setChargeStartMeterNum(bdMeterNum);
		int startTime=10;
		tblChargeInfo.setChargeStartTime(startTime);
		int userId=1;
		tblChargeInfo.setChargeUserId(userId);
		BigDecimal fronzeAmt= new BigDecimal(223.1);
		tblChargeInfo.setFronzeAmt(fronzeAmt);
		
		DB.chargeACInfoDao.updateStartChargeInfo(tblChargeInfo);
		
    }
	
	public static void testDCRealData(String epCode, int epGunNo)
    { 
		RealChargeInfo tmpRealChargeInfo =null;
		
		int currentType= EpConstants.EP_DC_TYPE;
		
		RealDCChargeInfo chargeInfo = new RealDCChargeInfo();
		tmpRealChargeInfo = chargeInfo;
		
		tmpRealChargeInfo.init();
		tmpRealChargeInfo.setCurrentType(currentType);
		tmpRealChargeInfo.setEpCode(epCode);
		tmpRealChargeInfo.setEpGunNo(epGunNo);
		
		
		boolean loadSuccess = tmpRealChargeInfo.loadFromDb(epCode, epGunNo);
		chargeInfo.setLinkCarStatus((short)1);
		chargeInfo.setGunCloseStatus((short)1);
		chargeInfo.setGunLidStatus((short)1);
		chargeInfo.setCommStatusWithCar((short)1);
		chargeInfo.setCarPlaceStatus((short)1);
		chargeInfo.setCardReaderFault((short)1);//读卡器通讯故障
		chargeInfo.setUrgentStopFault((short)1);
		chargeInfo.setArresterFault((short)1);
		chargeInfo.setInsulationCheckFault((short)1);
		
		chargeInfo.setGunUnconnectWarn((short)1);
		chargeInfo.setTransRecordFullWarn((short)1);
		chargeInfo.setMeterError((short)1);
		
		chargeInfo.setAcInVolWarn((short)2);
		chargeInfo.setChargeOverTemp((short)1);
		chargeInfo.setAcCurrentLoadWarn((short)2);
		chargeInfo.setOutRelayStatus((short)1);
		
		//公共遥测
		chargeInfo.setWorkingStatus(31);
		chargeInfo.setOutVoltage(1231);
		chargeInfo.setOutCurrent(321);
		chargeInfo.setCarPlaceLockStatus(2);
		chargeInfo.setSoc(99);
		chargeInfo.setChargedTime(98);
		chargeInfo.setChargeRemainTime(10);
		
		//公共的变长遥测
		chargeInfo.setTotalActivMeterNum(9999);
		chargeInfo.setChargedMeterNum(9998);
		chargeInfo.setChargePrice(888);
		
		//充电相关
		//chargeUserId  = tblRealData.getChargeUserId() ;
		//chargeSerialNo  = tblRealData.getChargeSerialNo();
		//chargeStartTime = tblRealData.getChargeStartTime();
		//chargeStartMeterNum  = tblRealData.getChargeStartMeterNum().multiply(Global.DecTime3).intValue();
		//chargedCost  = tblRealData.getChargedCost().multiply(Global.DecTime2).intValue();
		//fronzeAmt = tblRealData.getFronzeAmt().multiply(Global.DecTime2).intValue();//冻结金额
		
		//===============直流特别部分=====================
		chargeInfo.setBattryErrorLink((short)1);
		//单位遥信	18	烟雾报警故障	0:正常;1:告警	
		chargeInfo.setFogsWarn((short)1);
		//单位遥信	19	BMS 通信异常	0:正常;1:告警
		chargeInfo.setBmsCommException((short)1);
		//单位遥信	20	直流电度表异常故障	0:正常;1:告警	
		chargeInfo.setDcMeterException((short)1);

		//双位遥信	9	充电模式	0:不可信;1:恒压;2:恒流		爱充直流特别遥信
		chargeInfo.setCarChargeMode((short)2);
		//双位遥信	10	整车动力蓄电池SOC告警	0:正常;1:过高;2:过低
		chargeInfo.setCarSocWarn((short)2);
		//双位遥信	11	蓄电池模块采样点过温告警	0:正常;1:过温;2:不可信
		chargeInfo.setChargeModSampleOverTemp((short)1); 
		//双位遥信	12	输出连接器过温	0:正常;1:过温;2:不可信
		chargeInfo.setChargeOutLinkerOverTemp((short)1);
		//双位遥信	13	整车动力蓄电池组输出连接器连接状态	0:正常;1:不正常;2:不可信	
		chargeInfo.setChargeOutLinkerStatus((short)1);
		//双位遥信	14	整车蓄电池充电过流告警	0:正常;1:过流;2:不可信	
		chargeInfo.setChargeWholeOverCurrentWarn((short)1);
		//双位遥信	15	直流母线输出过压/欠压	0:正常;1:过压;2:欠压
		chargeInfo.setChargeVolWarn((short)2);
		
		

		//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
		chargeInfo.setSignleBattryHighVol(987);
		//34	蓄电池最高温度
		chargeInfo.setBpHighestTemperature(99);
		//35	蓄电池最低温度
		chargeInfo.setBpLowestTemperature(99);
		
		//36	整车动力电池总电压	精度0.1，单位v	
		chargeInfo.setCarBattryTotalVol(99);

		//直流专用遥测
		//41	A相电压	精度0.1，单位v	输入
		chargeInfo.setInAVol(99);
		//42	B相电压	精度0.1，单位v
		chargeInfo.setInBVol(98);
		//43	C相电压	精度0.1，单位v
		chargeInfo.setInCVol(97);
		//44	A相电流	精度0.1，单位v
		chargeInfo.setInACurrent(96);
		//45	B相电流	精度0.1，单位v
		chargeInfo.setInBCurrent(95);
		//46	C相电流	精度0.1，单位v	
		chargeInfo.setInCCurrent(94);
		//47	最高输出电压	精度0.1，单位v	充电中，充电机输出
		chargeInfo.setOutHighestVol(93);
		//48	最低输出电压	精度0.1，单位v	
		chargeInfo.setOutLowestVol(92);
		//49	最大输出电流	精度0.1，单位A	
		chargeInfo.setOutHighestCurrent(91);
		
		tmpRealChargeInfo.saveDb();
		
			
		TblChargeDCInfo tblChargeInfo =  new TblChargeDCInfo();
		tblChargeInfo.setPk_chargeinfo(tmpRealChargeInfo.getPkChargeInfo());
		tblChargeInfo.setEp_code(epCode);
		tblChargeInfo.setEp_gun_no(epGunNo);
		String serialNo= new String("1234567890123456");
		tblChargeInfo.setChargeSerialNo(serialNo);
		BigDecimal bdMeterNum= new BigDecimal(123.1);
		tblChargeInfo.setChargeStartMeterNum(bdMeterNum);
		int startTime=10;
		tblChargeInfo.setChargeStartTime(startTime);
		int userId=1;
		tblChargeInfo.setChargeUserId(userId);
		BigDecimal fronzeAmt= new BigDecimal(223.1);
		tblChargeInfo.setFronzeAmt(fronzeAmt);
		
		DB.chargeDCInfoDao.updateStartChargeInfo(tblChargeInfo);
		
		 
    }
	public void testBussis()
	{
        UserCache userInfo= UserService.getUserIdByOrgNo(1002);
        userInfo= UserService.getUserIdByOrgNo(1001);
	}

}
