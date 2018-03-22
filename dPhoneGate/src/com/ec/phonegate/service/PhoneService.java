package com.ec.phonegate.service;

import com.ec.usrcore.cache.UserRealInfo;
import com.ec.usrcore.service.UserService;
import com.ormcore.model.TblUserInfo;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.CommStatusConstant;
import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.constants.UserConstants;
import com.ec.logs.LogConstants;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.phonegate.client.PhoneClient;
import com.ec.phonegate.config.GameConfig;
import com.ec.phonegate.proto.PhoneConstant;
import com.ec.phonegate.proto.PhoneProtocol;
import com.ec.phonegate.sender.PhoneMessageSender;
import com.ec.phonegate.task.CheckUsrNetTimeOutTask;
import com.ec.usrcore.server.CommonServer;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.CompanyRela;
import com.ormcore.model.TblElectricPile;

public class PhoneService {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(PhoneService.class.getName()));

	public static String getDebugInfo() {
		final StringBuilder sb = new StringBuilder();
		sb.append("PhoneService:\n");

		sb.append("mapCh2PhoneClient count:")
				.append(CachePhoneService.getCh2PhoneClientCount()).append("\n");
		sb.append("mapPhoneClients count:")
				.append(CachePhoneService.getPhoneClientCount()).append("\n");

		return sb.toString();

	}
	
	public static void sendMessage(Channel channel, short cmdtype, int ret, int errorCode) {
		if (errorCode > 0) {
			logger.info(LogUtil.addExtLog("fail cmdtype|fail errorCode"), cmdtype, errorCode);
		}

		byte[] data = PhoneProtocol.do_confirm(cmdtype, (byte) ret,
				(short) errorCode);
		PhoneMessageSender.sendMessage(channel, data);
	}
	
	public static void sendEPMessage(Channel channel, short cmdtype, int ret, int errorCode,int status, int currentType) {
		if (errorCode > 0) {
			logger.info(LogUtil.addExtLog("fail cmdtype|errorCode"), cmdtype, errorCode);
		}

		byte[] data = PhoneProtocol.do_connect_ep_resp(cmdtype, (byte) ret,
				(short) errorCode, (short) status, (short) currentType);
		PhoneMessageSender.sendMessage(channel, data);
	}

	/**
	 * 连接充电桩（phone->usrGate）
	 */
	public static void handleConnectEp(Channel ch, int accountId,
			String epCode, int epGunNo, String checkCode, int version, int cmd) {
		logger.info(LogUtil.addBaseExtLog("checkCode|version|cmd"),
				new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,accountId,checkCode,version,cmd});

		// 判断通道是否正常
		PhoneClient phoneClient = CachePhoneService.getPhoneClientByChannel(ch);
		if (phoneClient == null) {
			logger.error(LogUtil.getExtLog("phoneClient is null"));
			return;
		}
		UserRealInfo userInfo = UserService.findUserRealInfo(accountId, Integer.valueOf(GameConfig.serverType));
		if (null == userInfo) {
			logger.info(LogUtil.addBaseExtLog("errorCode"),
					new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,accountId,ErrorCodeConstants.INVALID_ACCOUNT});
			sendEPMessage(ch, (short)cmd, 0, ErrorCodeConstants.INVALID_ACCOUNT, 0, 0);
			return;
		}

		PhoneClient oldPhoneClient = CachePhoneService.getPhoneClientByAccountId(accountId);
		if (oldPhoneClient != null) {
			if (oldPhoneClient.getChannel() != phoneClient.getChannel()
					&& oldPhoneClient.getChannel() != null) {
				logger.error(LogUtil.addExtLog("phoneClient is reconnect,oldPhoneClient"),oldPhoneClient);
				oldPhoneClient.getChannel().close();
				oldPhoneClient.setStatus(0);
			}
		}

		int errorCode = checkOrgAuth(userInfo.getCpy_number(), epCode);
		if (errorCode == 0) {
			errorCode = CommonServer.getInstance().initClientConnect(UserConstants.ORG_I_CHARGE, String.valueOf(accountId),
				epCode, epGunNo, checkCode, GameConfig.serverType);
		}
		if (errorCode > 0) {
			logger.info(LogUtil.addBaseExtLog("errorCode"),
					new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,accountId,errorCode});
			sendEPMessage(ch, (short)cmd, 0, errorCode, 0, 0);
		} else {
			phoneClient.setAccountId(accountId);
			phoneClient.setEpCode(epCode);
			phoneClient.setEpGunNo(epGunNo);
			phoneClient.setStatus(CommStatusConstant.INIT_SUCCESS);
			phoneClient.setVersion(version);
			phoneClient.setCmd(cmd);

			CachePhoneService.addPhoneClientByAccount(phoneClient);
		}
	}

	/**
	 * 充电（phone->usrGate）
	 */
	public static void handleStartCharge(PhoneClient phoneClient,
			int fronzeAmt, short chargeType) {
		logger.info(LogUtil.addBaseExtLog("fronzeAmt|chargeType"),
				new Object[]{LogConstants.FUNC_START_CHARGE,phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getAccountId(),fronzeAmt,chargeType});

		int errorCode = CommonServer.getInstance().startChange(phoneClient.getOrgNo(),
				String.valueOf(phoneClient.getAccountId()), phoneClient.getEpCode(), phoneClient.getEpGunNo(),
				(short) EpConstants.CHARGE_TYPE_QRCODE,
				fronzeAmt, 1, StringUtils.EMPTY, StringUtils.EMPTY, 1, StringUtils.EMPTY);
		if (errorCode > 0) {
			logger.info(LogUtil.addBaseExtLog("errorCode"),
					new Object[]{LogConstants.FUNC_START_CHARGE,phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getAccountId(),errorCode});
			sendMessage(phoneClient.getChannel(), PhoneConstant.D_START_CHARGE, 0, errorCode);
		}
	}

	private static int checkOrgAuth(int orgNo, String epCode)
	{
		List<TblElectricPile> epList = DB.epClientDao.findResultObject(epCode);
		if (epList == null || epList.size() != 1) {
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		TblElectricPile dbEp = epList.get(0);

		CompanyRela cpyRelaCond = new CompanyRela();
		cpyRelaCond.setPkElectricpile(dbEp.getPkEpId());
		List<CompanyRela> companyRelaList = DB.companyRelaDao.CompanyRela_custlist(cpyRelaCond);
		for (CompanyRela companyRela : companyRelaList)
			if (companyRela.getCpyCompanyNumber() == orgNo) return 0;

		return ErrorCodeConstants.EP_NOT_OPERATE;
	}

	/**	
	 * 停止充电（phone->usrGate）
	 */
	public static void handleStopCharge(PhoneClient phoneClient) {
		logger.info(LogUtil.getBaseLog(),
				new Object[]{LogConstants.FUNC_STOP_CHARGE,phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getAccountId()});

		int errorCode = CommonServer.getInstance().stopChange(
				phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getOrgNo(),
				String.valueOf(phoneClient.getAccountId()),StringUtils.EMPTY);
		if (errorCode > 0) {
			logger.info(LogUtil.addBaseExtLog("errorCode"),
					new Object[]{LogConstants.FUNC_STOP_CHARGE,phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getAccountId(),errorCode});
			sendMessage(phoneClient.getChannel(), PhoneConstant.D_STOP_CHARGE, 0, errorCode);
		}
	}

	/**
	 * 消费记录确认（phone->usrGate）
	 */
	public static void handleConsumeRecordConfirm(PhoneClient phoneClient,String chargeOrder,short result) {
		logger.info(LogUtil.addBaseExtLog("chargeOrder|result"),
				new Object[]{LogConstants.FUNC_CONSUME_RECORD,phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getAccountId(),chargeOrder,result});

		String messagekey = chargeOrder;
		CachePhoneService.removePhoneRepeatMsg(messagekey);
	}

	/**
	 * 枪与车连接状态确认（phone->usrGate）
	 */
	public static void handleGun2CarLinkStatusResp(PhoneClient phoneClient )
	{
		if(phoneClient == null)
		{
			logger.error(LogUtil.getExtLog("PhoneClient is null"));
			return ;
		}

		logger.info(LogUtil.getBaseLog(),
				new Object[]{LogConstants.FUNC_GUNLINK_STATUS,phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getAccountId()});

		String messagekey = String.valueOf(phoneClient.getAccountId()) + PhoneConstant.D_GUN_CAR_STATUS;
		CachePhoneService.removePhoneRepeatMsg(messagekey);
		
	}

	/**
	 * 枪工作状态确认（phone->usrGate）
	 */
	public static void handleGun2CarWorkStatusResp(PhoneClient phoneClient )
	{
		if(phoneClient == null)
		{
			logger.error(LogUtil.getExtLog("PhoneClient is null"));
			
			return ;
		}

		logger.info(LogUtil.getBaseLog(),
				new Object[]{LogConstants.FUNC_GUNWORK_STATUS,phoneClient.getEpCode(), phoneClient.getEpGunNo(),phoneClient.getAccountId()});

		String messagekey = String.valueOf(phoneClient.getAccountId()) + PhoneConstant.D_GUN_WORK_STATUS;
		CachePhoneService.removePhoneRepeatMsg(messagekey);
		
	}

	/**
	 * 心跳（phone->usrGate）
	 */
	public static void handleHeart(PhoneClient phoneClient) {
		// java.util.concurrent.
		byte[] data = PhoneProtocol.Package((byte) 1, PhoneConstant.D_HEART);
		PhoneMessageSender.sendMessage(phoneClient.getChannel(), data);
	}

	public static void offLine(Channel channel) {
		
		PhoneClient phoneClient = CachePhoneService.getPhoneClientByChannel(channel);
		
		if (phoneClient != null) {
			logger.debug(LogUtil.addExtLog("offLine,phoneClient"), phoneClient);
			
			phoneClient.handleNetTimeOut();
		}
		
		CachePhoneService.removePhoneClientByChannel(channel);
	}

	public static void startCommClientTimeout(long initDelay) {

		CheckUsrNetTimeOutTask checkTask = new CheckUsrNetTimeOutTask();

		TaskPoolFactory.scheduleAtFixedRate("CHECK_PHONECLIENT_TIMEOUT_TASK",
				checkTask, initDelay, 10, TimeUnit.SECONDS);
	}

	@SuppressWarnings("rawtypes")
	public static synchronized void checkTimeOut() {
		
		
		Iterator iter = CachePhoneService.getMapCh2PhoneClient().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			PhoneClient phoneClient = (PhoneClient) entry.getValue();
			if (null == phoneClient) {
				continue;
			}

			long now = DateUtil.getCurrentSeconds();
			long diff = now - phoneClient.getLastUseTime();

			if (diff > GameConfig.phoneConnectTimeout) {
				logger.debug(LogUtil.addExtLog("phone"),phoneClient.getIdentity());
				
				phoneClient.handleNetTimeOut();
				
				phoneClient.close();
				
				iter.remove();
			}

		}
	}
}
