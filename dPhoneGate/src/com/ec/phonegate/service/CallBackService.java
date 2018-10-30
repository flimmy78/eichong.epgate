package com.ec.phonegate.service;

import com.ec.config.Global;
import com.ec.logs.LogConstants;
import com.ec.net.message.JPushUtil;
import com.ec.phonegate.client.PhoneClient;
import com.ec.phonegate.config.GameConfig;
import com.ec.phonegate.proto.PhoneConstant;
import com.ec.phonegate.proto.PhoneProtocol;
import com.ec.phonegate.sender.PhoneMessageSender;
import com.ec.usrcore.server.IEventCallBack;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NetUtils;
import com.ec.utils.NumUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblJpush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CallBackService implements IEventCallBack {

	private final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(CallBackService.class.getName()));

	/**
	 * 手机连接应答（usrGate->phone）
	 */
	public void onCanUseEp(int orgNo,String userIdentity,String epCode,int epGunNo,String account,int ret,int errorCode,int status,int currentType) {
		logger.info(LogUtil.addBaseExtLog("ret|errorCode|status|currentType"),
				new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,userIdentity,ret,errorCode,status,currentType});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		} else {
			phoneClient.setAccount(account);
			phoneClient.setIdentity(account);
		}

		if (ret == 1) {
			PhoneService.sendEPMessage(phoneClient.getChannel(), (short)phoneClient.getCmd(), ret, errorCode, status, currentType);
			phoneClient.setConnectFlg(true);
		} else {
			PhoneService.sendEPMessage(phoneClient.getChannel(), (short)phoneClient.getCmd(), ret, errorCode, 0, 0);
		}
	}

	/**
	 * 充电事件（usrGate->phone）
	 */
	public void onChargeEvent(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int status) {
		logger.info(LogUtil.addBaseExtLog("extra|status"),
				new Object[]{LogConstants.FUNC_ONCHARGEEVENT,epCode,epGunNo,userIdentity,extra,status});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		}

		int ret = 1;
		if (status != 0) ret = 0;
		byte[] data = PhoneProtocol.do_start_charge_event(ret);

		PhoneMessageSender.sendMessage(phoneClient.getChannel(), data);
	}

	/**
	 * 充电应答（usrGate->phone）
	 */
	public void onStartCharge(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int ret,int errorCode) {
		logger.info(LogUtil.addBaseExtLog("extra|ret|errorCode"),
				new Object[]{LogConstants.FUNC_ONSTARTCHARGE,epCode,epGunNo,userIdentity,extra,ret,errorCode});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		}

		int status = 1;
		if (ret != 0) status = 0;
		PhoneService.sendMessage(phoneClient.getChannel(), PhoneConstant.D_START_CHARGE, status, errorCode);
	}

	/**
	 * 停止充电应答（usrGate->phone）
	 */
	public void onStopCharge(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int ret,int errorCode) {
		logger.info(LogUtil.addBaseExtLog("extra|ret|errorCode"),
				new Object[]{LogConstants.FUNC_ONSTOPCHARGE,epCode,epGunNo,userIdentity,extra,ret,errorCode});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		}

		PhoneService.sendMessage(phoneClient.getChannel(), PhoneConstant.D_STOP_CHARGE, ret, errorCode);
	}

	public void onQueryOrderInfo(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int ret,int errorCode) {
	}
	public void onQueryCommonRealData(String epCode, int epGunNo, String extra, String ranRuiQueryData) {
	}

	/**
	 * 充电实时数据（usrGate->phone）
	 */
	public void onRealData(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,Map<String ,Object> realData) {
		logger.info(LogUtil.addBaseExtLog("extra|data"),
				new Object[]{LogConstants.FUNC_ONREALDATA,epCode,epGunNo,userIdentity,extra,realData});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		}

		byte[] data = PhoneProtocol.do_real_charge_info(realData);
		PhoneMessageSender.sendMessage(phoneClient.getChannel(), data);
	}

	@Override
	public void onChargeReal4Html(final int i, final String s, final String s1, final int i1, final String s2, final Map<String, Object> map) {

	}

	/**
	 * 消费记录（usrGate->phone）
	 */
	public void onChargeOrder(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,Map<String ,Object> data) {
		logger.info(LogUtil.addBaseExtLog("extra|data"),
				new Object[]{LogConstants.FUNC_ONCHARGEORDER,epCode,epGunNo,userIdentity,extra,data});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		}

		String chargeOrder = (String) data.get("orderNo");
		long lst = (long) data.get("st");
		long let = (long) data.get("et");

		int st = (int) lst;
		int et = (int) let;

		int totalMeterNum = (int) data.get("elect");
		int totalAmt = (int) data.get("elect_money");
		int serviceAmt = (int) data.get("service_money");
		int pkEpId = (int) data.get("pkEpId");
		// BUG4348修复
		if (Integer.valueOf(GameConfig.serverType) != 2) {
			totalAmt = NumUtil.BigDecimal2ToInt(NumUtil.intToBigDecimal42(totalAmt).multiply(Global.DecTime2));
			serviceAmt = NumUtil.BigDecimal2ToInt(NumUtil.intToBigDecimal42(serviceAmt).multiply(Global.DecTime2));
		}

		int version = phoneClient.getVersion();
		int couPonAmt = 0;
		int userFirst = 0;
		int realCouPonAmt = 0;
		int personalAmt = 0;//个性化优惠金额
		int chargeStyle = -1;
		if(version>=2)
		{
			couPonAmt = (int) data.get("New_conpon");
			userFirst = (int) data.get("Conpon_face_value");
			realCouPonAmt = (int) data.get("Conpon_discount_value");
			personalAmt = (int) data.get("personalAmt");
			if (data.get("chargeStyle") != null) chargeStyle = (int) data.get("chargeStyle");
		}
		
		byte[] extraData = PhoneProtocol.do_consume_record((short)version,chargeOrder,st,et,totalMeterNum,totalAmt,serviceAmt,
				pkEpId,userFirst,couPonAmt,realCouPonAmt,personalAmt,chargeStyle);

		PhoneMessageSender.sendRepeatMessage(phoneClient.getChannel(), extraData, chargeOrder, phoneClient.getVersion());
	}

	/**
	 * 枪与车连接状态变化通知事件（usrGate->phone）
	 */
	public void onGunLinkStatusChange(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int status) {
		logger.info(LogUtil.addBaseExtLog("extra|status"),
				new Object[]{LogConstants.FUNC_GUNLINK_STATUS,epCode,epGunNo,userIdentity,extra,status});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		}

		byte[] hmsTime = NetUtils.timeToByte();
		int gunStatus = status;
		gunStatus +=1;
		byte[] data = PhoneProtocol.do_gun2car_status(gunStatus, PhoneConstant.D_GUN_CAR_STATUS, hmsTime);

		String messagekey = userIdentity + PhoneConstant.D_GUN_CAR_STATUS;

		PhoneMessageSender.sendRepeatMessage(phoneClient.getChannel(), data, messagekey, phoneClient.getVersion());
	}

	/**
	 * 枪工作状态变化通知事件（usrGate->phone）
	 */
	public void onGunWorkStatusChange(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int status) {
		logger.info(LogUtil.addBaseExtLog("extra|status"),
				new Object[]{LogConstants.FUNC_GUNWORK_STATUS,epCode,epGunNo,userIdentity,extra,status});

		PhoneClient phoneClient = CachePhoneService.getPhoneClientByAccountId(Integer.valueOf(userIdentity));
		if (phoneClient == null) {
			if (status == 3 && !"".equals(extra)) {
				jmsgChargeStat(Integer.valueOf(userIdentity), epCode, epGunNo, status);
			} else {
				logger.error(LogUtil.getExtLog("phoneClient is null"));
			}
			return;
		}

		byte[] hmsTime = NetUtils.timeToByte();
		byte[] data = PhoneProtocol.do_gun2car_status(status, PhoneConstant.D_GUN_WORK_STATUS, hmsTime);

		String messagekey = userIdentity + PhoneConstant.D_GUN_WORK_STATUS;

		PhoneMessageSender.sendRepeatMessage(phoneClient.getChannel(), data, messagekey, phoneClient.getVersion());
	}

	@Override
	public void onGunWorkStatusChange4Html(final String s, final int i, final int i1, final String s1) {

	}

	public void jmsgChargeStat(int userId,String epCode,int epGunNo,int status)
	{
		TblJpush ju=DB.jpushDao.getByuserInfo(userId);
		if(ju==null)
		{
			logger.error(LogUtil.addExtLog("msgChargeNotic do not find userId|epCode|epGunNo"), new Object[]{userId,epCode,epGunNo});
			return ;
		}
		
		logger.info(LogUtil.addExtLog("msgChargeNotic userId|epCode|epGunNo"), new Object[]{userId,epCode,epGunNo});
		
		String msg= String.format("您在电桩{}上的轮充等待，已经开始充电。", epCode+epGunNo);
		
		Map<String, String> extras = new HashMap<String, String>();
        extras.put( "msg", msg );
        extras.put( "epCode", ""+epCode );
        extras.put("epGunNo", ""+epGunNo );
        extras.put("type", "99" );
        extras.put( "title", "轮充等待开始充电" );
        extras.put( "tm", ""+DateUtil.getCurrentSeconds());
		
		JPushUtil.point2point("开始充电",msg,extras,ju.getJpushRegistrationid(),ju.getJpushDevicetype());
	}
}
