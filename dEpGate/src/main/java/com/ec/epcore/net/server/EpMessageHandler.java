package com.ec.epcore.net.server;

import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.EpConcentratorCache;
import com.ec.epcore.cache.MsgWhiteList;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpDecoder;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.net.proto.ApciHeader;
import com.ec.epcore.net.proto.AsduHeader;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.epcore.service.*;
import com.ec.net.proto.ByteBufferUtil;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.WmIce104Util;
import com.ec.utils.DateUtil;
import com.ec.utils.FileUtils;
import com.ec.utils.LogUtil;
import com.ec.utils.StringUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * 接受电桩客户端数据并处理
 * 
 * @author 2014-12-1 下午2:58:22
 */
public class EpMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil
			.getLogName(EpMessageHandler.class.getName()));
	/**
	 * 调度线程池服务
	 * <p>
	 * Param:
	 * corePoolSize - 池中所保存的线程数，包括空闲线程。
	 * maximumPoolSize - 池中允许的最大线程数(采用LinkedBlockingQueue时没有作用)。
	 * keepAliveTime -当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间，线程池维护线程所允许的空闲时间,即非核心线程的等待时间。
	 * unit - keepAliveTime参数的时间单位，线程池维护线程所允许的空闲时间的单位:秒 。
	 * workQueue - 执行前用于保持任务的队列（缓冲队列）。此队列仅保持由execute 方法提交的 Runnable 任务。
	 * RejectedExecutionHandler -线程池对拒绝任务的处理策略(重试添加当前的任务，自动重复调用execute()方法)
	 * <p>
	 * <p>
	 * 向线程池添加一个任务时会触发execute方法
	 * 1. 如果正在运行的线程数量小于 corePoolSize，那么马上创建线程运行这个任务；
	 * 2. 如果正在运行的线程数量大于或等于 corePoolSize，那么将这个任务放入队列。
	 * 3. 如果这时候队列满了，而且正在运行的线程数量小于 maximumPoolSize，那么还是要创建线程运行这个任务；
	 * 4. 如果队列满了，而且正在运行的线程数量大于或等于 maximumPoolSize，那么线程池会抛出异常，告诉调用者“我不能再接受任务了”。
	 * <p>
	 * <p>
	 * 使用无界队列（例如，不具有预定义容量的 LinkedBlockingQueue，DelayQueue，PriorityBlockingQueue）将导致在所有 corePoolSize
	 * 线程都忙的情况下将新任务加入队列。这样，创建的线程就不会超过 corePoolSize。（因此，maximumPoolSize,keepAliveTime,unit参数无意义。）
	 * 当每个任务完全独立于其他任务，即任务执行互不影响时，适合于使用无界队列
	 * LinkedBlockingQueue会默认一个类似无限大小的容量（Integer.MAX_VALUE）
	 * <p>
	 * <p>
	 * 线程池的拒绝策略 之 CallerRunsPolicy
	 * 只要线程池未关闭，该策略直接在调用者线程中运行当前被丢弃的任务。显然这样不会真的丢弃任务，但是，调用者线程性能可能急剧下降
	 * 由调用者线程去执行，在任务提交速度过快的情况，可减少任务提交的速度，同时可以保证任务的执行 《队列满的时候 并行转串行》
	 */
   // 第一次给的数据是 400 500（7月4号上线）用的guava 第二次给的是800  1000  (7月24号上线 )用的原生  第三次 500 ,600 (8月14号)用的原生
   // 第四次 500 ,600 (9月6号上线)用的原生   第五次 350 ,450(9月18 上线)用的原生
	public static BlockingQueue<Runnable> workQueue4BizExecutor = new LinkedBlockingQueue<>(20000);
	public static  ThreadPoolExecutor bizExecutorService = new ThreadPoolExecutor(350, 450, 30L, TimeUnit.SECONDS, workQueue4BizExecutor, new ThreadPoolExecutor.CallerRunsPolicy());
	/**
	 * 接受电桩发送的消息进行处理
	 * 
	 * @author lwz 2015-3-19
	 * @param channel
	 * @param message
	 */
	public static void handleMessage(Channel channel, EpMessage message) {

		EpCommClient epCommClient = EpCommClientService.getCommClient(channel);
		if (epCommClient == null) {
			logger.error(LogUtil.addFuncExtLog("not find EpCommClient"), channel.toString());
			return;
		}

		short nFrameType = message.getFrameType();
		byte[] msg = message.getBytes();

		if (nFrameType != 1 && nFrameType != 2 && nFrameType != 3 && nFrameType != 4) {
			logger.error(LogUtil.addFuncExtLog("nFrameType|identity"), nFrameType, epCommClient.getIdentity());
			message = null;
			return;
		}
		if (nFrameType == 1) {
			int ret = Iec104ProcessProtocolFrame(epCommClient, msg);
			if (ret < 0) {
				logger.error(LogUtil.addFuncExtLog("ret|channel"), ret, channel);
				EpCommClientService.removeEpCommClient(channel);
				channel.close();
			}
		} else if (nFrameType == 2)// I Frame
		{
			Iec104ProcessFormatI(epCommClient, msg);
		} else if (nFrameType == 3)// S Frame
		{

		} else // U Frame
		{
			Iec104ProcessFormatU(epCommClient, msg);
		}

		message = null;
	}

	public static int Iec104ProcessProtocolFrame(EpCommClient epCommClient, byte[] msg) {
		int msgLen = msg.length;
		// 离散桩
		if (msgLen != 15 && msgLen != 16) {
			// 2.无效桩，强制关闭
			logger.error(LogUtil.addExtLog("msgLen|msg|channel"),
					new Object[] { msgLen, WmIce104Util.ConvertHex(msg, 1), epCommClient.getChannel() });
			return -1;
		}

		boolean initSuccess = false;
		String commClientIdentity = "";
		byte[] retMsg = msg;
		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(msg);
			ByteBufferUtil.readWithLength(byteBuffer, ApciHeader.NUM_HEAD + ApciHeader.NUM_LEN_FIELD + 1);
			int commVersion = (int) byteBuffer.get(); //
			byte boot = 0;
			if (msgLen == 16) {
				boot = byteBuffer.get();
			}
			if (boot > 1) {
				// 2.无效桩boot不正常，强制关闭
				logger.error(LogUtil.addExtLog("boot|msg|channel"),
						new Object[] { boot, WmIce104Util.ConvertHex(msg, 1), epCommClient.getChannel() });
				return -2;
			}

			String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
			short nStationId = 0;
			if (msgLen == 16) // v4版本bin码
				nStationId = (short) ByteBufferUtil.readUB2(byteBuffer);
			else // v3版本BCD码
			{
				String station = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
				nStationId = (short) Integer.parseInt(station);
			}
			String epCodeZero = StringUtil.repeat("0", 15);
			if (nStationId > 0 && epCode.compareTo(epCodeZero) == 0)// 集中器
			{
				commClientIdentity = "" + nStationId;
				// logger.info("Iec104ProcessProtocolFrame,initConnect,stationId:{},start",nStationId);
				if (EpConcentratorService.initStationConnect(commVersion, nStationId, epCommClient, boot)) {
					StatService.addCommConcentrator();
					initSuccess = true;
				} else {
					return -3;
				}
			} else if (nStationId == 0 && epCode.compareTo(epCodeZero) != 0)// 电桩
			{
				commClientIdentity = epCode;
				// logger.info("Iec104ProcessProtocolFrame,initConnect,epCode:{},start",epCode);
				if (EpService.initDiscreteEpConnect(commVersion, epCode, epCommClient, boot)) {
					StatService.addCommDiscreteEp();
					initSuccess = true;

				} else {
					return -4;
				}
			} else // 错误
			{
				logger.error(LogUtil.addFuncExtLog("fail,nStationId!=0&&epCode.compareTo(epCodeZero)!=0, epCode|nStationId|channel"),
						new Object[] { epCode, nStationId, epCommClient.getChannel() });
				return -7;
			}

		} catch (IOException e) {
			logger.error(LogUtil.addFuncExtLog("exception|channel"), e.getStackTrace(), epCommClient.getChannel());
			return -6;
		}

		// 反馈协议侦
		if (initSuccess) {
			if (MsgWhiteList.isOpen() && MsgWhiteList.find(commClientIdentity)) {
				FileUtils.CreateCommMsgLogFile(commClientIdentity + ".log");
				logger.debug(LogUtil.addFuncExtLog("FileUtils.CreateCommMsgLogFile"), commClientIdentity);
			}
			//toto 重发了一个数据包,  5.0 版本考虑去掉
			InnerApiMessageSender.sendMessage(epCommClient.getChannel(), retMsg);
			// 启动侦
			byte startData[] = EpEncoder.do_startup();
			InnerApiMessageSender.sendMessage(epCommClient.getChannel(), startData);
			return 0;
		} else {
			return -5;
		}

	}

	public static void Iec104ProcessFormatU(EpCommClient commClient, byte[] msg) {
		byte UCommand = msg[0];

		if (null == commClient || commClient.getStatus() != 2) {
			logger.error(LogUtil.addFuncExtLog("force close,no init commClient|channel"),
					commClient, commClient.getChannel());
			// 没有发协议侦的客户端都关闭
			commClient.close();
			EpCommClientService.removeEpCommClient(commClient);
			return;
		}

		try {
			long now = DateUtil.getCurrentSeconds();
			commClient.setLastUseTime(now);

			if ((UCommand & Iec104Constant.WM_104_CD_STARTDT_CONFIRM) == Iec104Constant.WM_104_CD_STARTDT_CONFIRM) {
				if (commClient.getBootStatus() == 0)// boot正常时发送
				{
					int sendINum = commClient.getSendINum2();
					int recvINum = commClient.getRevINum();
					byte[] bSetTimes = EpEncoder.do_set_time((short) 0,
							sendINum, recvINum, 0, commClient.getVersion());
					EpMessageSender.sendMessage(commClient, bSetTimes);

					sendINum = commClient.getSendINum2();
					recvINum = commClient.getRevINum();

					byte[] bAllCall = EpEncoder.Package_all_call((short) 6,
							sendINum, recvINum, 0, commClient.getVersion());
					EpMessageSender.sendMessage(commClient, bAllCall);
				}
			} else if ((UCommand & Iec104Constant.WM_104_CD_TESTFR) == Iec104Constant.WM_104_CD_TESTFR) {// add
				byte[] testdata = EpEncoder.do_test_confirm();
				EpMessageSender.sendMessage(commClient, testdata);
				logger.debug("receive :{} heart beat! ", commClient.getIdentity());
			}
		} catch (Exception e) {
			logger.error(LogUtil.addFuncExtLog("Channel|exception"), e.getStackTrace());
		}
	}

	public static void Iec104ProcessFormatI(EpCommClient epCommClient,
			byte[] msg) {

		if (null == epCommClient || epCommClient.getStatus() != 2) {
			logger.error(LogUtil.addFuncExtLog("no init, force close Channel"), epCommClient.getChannel());
			// 没有发协议侦的客户端都关闭
			epCommClient.close();
			EpCommClientService.removeEpCommClient(epCommClient);
			return;
		}
		if (epCommClient.getVersion() >= YXCConstants.PROTOCOL_VERSION_V4) {
			short calcCrc = WmIce104Util.CRCSum(msg, 0, 2);

			short revCrc = (short) (msg[msg.length - 2] & 0xff);
			revCrc |= (msg[msg.length - 1] & 0xff) << 8;

			if (calcCrc != revCrc) {
				logger.error(LogUtil.addFuncExtLog("crc error,Identity|msg"),
						epCommClient.getIdentity(), WmIce104Util.ConvertHex(msg, 1));
				return;
			}
		}
		long now = DateUtil.getCurrentSeconds();
		epCommClient.setLastUseTime(now);

		// add by hly
		int revInum = epCommClient.getRevINum();
		revInum = (revInum + 1) & 0x07FFF;
		epCommClient.setRevINum(revInum);

		ByteBuffer byteBuffer = ByteBuffer.wrap(msg);
		boolean logMsg = true;

		byte bbyte = msg[ApciHeader.NUM_CTRL];
		short type = (short) (bbyte & 0xFF);
		try {
			switch (bbyte) {
			case Iec104Constant.C_IC_NA:// 总召唤确认帧
			{
				ProcessCallAck(epCommClient, msg);
			}
				break;
			case Iec104Constant.M_SP_NA:// 1
			{
				byte[] sdata = EpEncoder.do_sframe(revInum);// add by
				EpMessageSender.sendMessage(epCommClient, sdata);// add by hly
//				logger.debug(LogUtil.addFuncExtLog("1 Identity|channel"),
//						epCommClient.getIdentity(), epCommClient.getChannel());
				// 信息体地址
//				EpDecoder.decodeOneBitYx(epCommClient.getChannel(), byteBuffer);
				bizExecutorService.execute(()->{
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeOneBitYx");
						EpDecoder.decodeOneBitYx(epCommClient.getChannel(), byteBuffer);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("decodeOneBitYx:{}",e);
					}
				});

			}
				break;
			case Iec104Constant.M_DP_NA:// 3
			{
				byte[] sdata = EpEncoder.do_sframe(revInum);// add by
				EpMessageSender.sendMessage(epCommClient, sdata);// add by hly
//				logger.debug(LogUtil.addFuncExtLog("3 Identity|channel"),
//						epCommClient.getIdentity(), epCommClient.getChannel());
				// 信息体地址
//				EpDecoder.decodeTwoBitYx(epCommClient.getChannel(), byteBuffer);
				bizExecutorService.execute(() ->{
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeTwoBitYx");
						EpDecoder.decodeTwoBitYx(epCommClient.getChannel(), byteBuffer);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("EpDecoder.decodeTwoBitYx exception",e);
					}
				});
			}
				break;
			case Iec104Constant.M_ME_NB:// 11
			{
				byte[] sdata = EpEncoder.do_sframe(revInum);// add by
				EpMessageSender.sendMessage(epCommClient, sdata);// add by hly

//				logger.debug(LogUtil.addFuncExtLog("11 Identity|channel"),
//						epCommClient.getIdentity(), epCommClient.getChannel());
//				EpDecoder.decodeYc(epCommClient.getChannel(), byteBuffer);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeYc");
						EpDecoder.decodeYc(epCommClient.getChannel(), byteBuffer);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("EpDecoder.decodeYc,exception :{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_MD_NA: { //132 测量值
				byte[] sdata = EpEncoder.do_sframe(revInum);// add by
				EpMessageSender.sendMessage(epCommClient, sdata);// add by hly
//				logger.debug(LogUtil.addFuncExtLog("132 Identity|channel"),
//						epCommClient.getIdentity(), epCommClient.getChannel());
				// 信息体地址
//				EpDecoder.decodeVarYc(epCommClient.getChannel(), byteBuffer);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeVarYc");
						EpDecoder.decodeVarYc(epCommClient.getChannel(), byteBuffer);
						Thread.currentThread().setName(currentThreadName);
					}catch (Exception e){
						logger.error("decodeVarYc exception :{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_IT_NA:// 15
				break;
			case Iec104Constant.M_JC_NA:// 实时数据   134 充电桩实时监测数据项
			{
				byte[] sdata = EpEncoder.do_sframe(revInum);// add by
				EpMessageSender.sendMessage(epCommClient, sdata);// add by hly

				int record_type = msg[ApciHeader.NUM_CTRL + AsduHeader.H_LEN];

//				logger.debug(LogUtil.addFuncExtLog("134 record_type|Identity|channel"),
//						new Object[] { record_type, epCommClient.getIdentity(), epCommClient.getChannel() });

				if (record_type == 1 || record_type == 3) {
//					EpDecoder.decodeAcRealInfo(epCommClient.getVersion(),
//							record_type, byteBuffer);
					bizExecutorService.execute(() -> {
						try {
							String currentThreadName = Thread.currentThread().getName();
							Thread.currentThread().setName("decodeAcRealInfo");
							EpDecoder.decodeAcRealInfo(epCommClient.getVersion(), record_type, byteBuffer);
							Thread.currentThread().setName(currentThreadName);
						} catch (IOException e) {
							e.printStackTrace();
							logger.error("EpDecoder.decodeAcRealInfo exception");
						}
					});
				} else {
//					EpDecoder.decodeWholeDcRealInfo(epCommClient.getVersion(),
//							record_type, byteBuffer);
					bizExecutorService.execute(() -> {
						try {
							String currentThreadName = Thread.currentThread().getName();
							Thread.currentThread().setName("decodeWholeDcRealInfo");
							EpDecoder.decodeWholeDcRealInfo(epCommClient.getVersion(),
                                    record_type, byteBuffer);
							Thread.currentThread().setName(currentThreadName);
						} catch (IOException e) {
							logger.error("EpDecoder.decodeWholeDcRealInfo exception,{}",e);
						}
					});

				}
			}
				break;
			case Iec104Constant.M_RE_NA:// 130  充电桩业务数据
			{
				int record_type = (short) msg[ApciHeader.NUM_CTRL
						+ AsduHeader.H_LEN] & 0xff;
				Process130Record(epCommClient, record_type, msg);
			}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(LogUtil.addFuncExtLog("exception ch|msg"),
					epCommClient.getChannel(), WmIce104Util.ConvertHex(msg, 0));
		}

	}

	public static void ProcessCallAck(EpCommClient epCommClient, byte[] msg) {
		int stationAddr = 0;
		String epCode = "0000000000000000";
		EpConcentratorCache stationClient = null;
		if (epCommClient.getMode() == 2) {
			stationClient = EpConcentratorService.getConCentrator(epCommClient.getIdentity());
			stationAddr = stationClient.getPkId();
		} else if (epCommClient.getMode() == 1) {
			epCode = epCommClient.getIdentity();
		} else {
			logger.debug(LogUtil.addFuncExtLog("Iec104Constant.C_IC_NA invalid epCommClient.getMode()|Identity"),
					epCommClient.getMode(), epCommClient.getIdentity());
			return;
		}
		if (epCode.length() < 16)
			return;

		byte cos = msg[ApciHeader.NUM_CTRL + 2];
		if (cos == 0x07) {
			EqVersionService.sendVersion(epCommClient, epCode, stationAddr);
			if (stationClient != null) {
				stationClient.onEpSendVersion();// 发送查询集中器下所有电桩版本信息
				stationClient.onEpSendTempChargeMaxNum();// 发送查询电桩临时充电次数
			}
			if (epCommClient.getMode() == 1) {// 电桩
				//EpService.queryTempChargeNum(epCode);
			}
		}
	}

	public static void Process130Record(EpCommClient epCommClient,
			int record_type, byte[] msg) {
//		logger.info(LogUtil.addFuncExtLog("record_type|Identity|channel"),
//				new Object[] { record_type, epCommClient.getIdentity(), epCommClient.getChannel() });

		ByteBuffer byteBuffer = ByteBuffer.wrap(msg);
		try {
			ByteBufferUtil.readWithLength(byteBuffer, ApciHeader.NUM_CTRL
					+ AsduHeader.H_LEN + 1);

			switch (record_type) {
			case Iec104Constant.M_CONSUME_MODEL_REQ: {
				EpDecoder.decodeConsumeModelReq(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_CONSUME_MODEL4_REQ: {
				EpDecoder.decodeConsumeModelReq(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_CONSUME_MODEL6_REQ: {
     			EpDecoder.decodeConsumeModelReq(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_CONSUME_MODE_RET:// 计费模型结果上行数据
			{
				// 1 终端机器编码 BCD码 8Byte 16位编码
				String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
			}
				break;
			case Iec104Constant.M_BUSINESS_TIME_RET:
				// 私有充电桩下发充电桩运营时间上行结果数据
				break;
			case Iec104Constant.M_NOCARD_PW_AUTH: {
				EpDecoder.decodeNoCardAuthByPw(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_BESPOKE_RET: {
				EpDecoder.decodeEpBespRet(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_CANCEL_BESPOKE_RET: {
				EpDecoder.decodeEpCancelBespRet(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_NOCARD_YZM_AUTH: {
				EpDecoder.decodeNoCardAuthByYZM(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_START_CHARGE_EVENT: {
    			EpDecoder.decodeStartElectricizeEventV3(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_STOP_ELECTRICIZE_EVENT: {
//				EpDecoder.decodeStopElectricizeEvent(epCommClient, byteBuffer);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeStopElectricizeEvent");
						EpDecoder.decodeStopElectricizeEvent(epCommClient, byteBuffer);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("decodeStopElectricizeEvent exception:{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_START_ELECTRICIZE_RET: {
				EpDecoder.decodeEpStartChargeResp(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_STOP_ELECTRICIZE_RET: {
				EpDecoder.decodeEpStopChargeResp(epCommClient, byteBuffer);
				break;
			}
			case Iec104Constant.M_CONSUME_RECORD: {
//				EpDecoder.decodeConsumeRecord(epCommClient, byteBuffer, msg);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeConsumeRecord");
						EpDecoder.decodeConsumeRecord(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);

					} catch (IOException e) {
						e.printStackTrace();
						logger.error("EpDecoder.decodeConsumeRecord exception");
					}
				});
			}
				break;
			case Iec104Constant.M_CONSUME_RECORD_WITH_VINCODE: {
//				EpDecoder.decodeConsumeRecordWithVinCode(epCommClient, byteBuffer, msg);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeConsumeRecordWithVinCode");
						EpDecoder.decodeConsumeRecordWithVinCode(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("EpDecoder.decodeConsumeRecordWithVinCode exception:{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_CONSUME_RECORD_WITH_SOC: {
//				EpDecoder.decodeConsumeRecordWithSOC(epCommClient, byteBuffer, msg);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeConsumeRecordWithSOC");
						EpDecoder.decodeConsumeRecordWithSOC(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("EpDecoder.decodeConsumeRecordWithSOC exception:{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_CONSUME_RECORD_WITH_RATE: {
//				EpDecoder.decodeConsumeRecordWithRate(epCommClient, byteBuffer, msg);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeConsumeRecordWithRate");
						EpDecoder.decodeConsumeRecordWithRate(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);

					} catch (IOException e) {
						logger.error("EpDecoder.decodeConsumeRecordWithRate:{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_CONSUME_RECORD_WITH_REASEON: {
//				EpDecoder.decodeConsumeRecordWithReaseon(epCommClient, byteBuffer, msg);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeConsumeRecordWithReaseon");
						EpDecoder.decodeConsumeRecordWithReaseon(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("EpDecoder.decodeConsumeRecordWithReaseon:{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_CONSUME_RECORD_WITH_BAT: {
				EpDecoder.decodeConsumeRecordWithBat(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_QUERY_CONSUME_RECORD_RET: {
//				EpDecoder.decodeQueryConsumeRecord(epCommClient, byteBuffer, msg);
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeQueryConsumeRecord");
						EpDecoder.decodeQueryConsumeRecord(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						logger.error("EpDecoder.decodeQueryConsumeRecord exception:{}",e);
					}
				});
			}
				break;
			case Iec104Constant.M_BALANCE_WARNING: {
				EpDecoder.decodeBalanceWarning(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_EP_HEX_FILE_SUMARY_REQ: {
				EpDecoder.decodeEpHexFileSumaryReq(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_EP_HEX_FILE_DOWN_REQ: {
				EpDecoder.decodeEpHexFileDownReq(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_EP_STAT_RET: {
				EpDecoder.decodeStatReq(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_COMM_SIGNAL_RET: {
				EpDecoder.decodeCommSignal(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_DC_SELF_CHECK_FINISHED:
				break;
			case Iec104Constant.M_EP_IDENTYCODE: {
				bizExecutorService.execute(()->{
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeEpIdentyCodeQuery");
						EpDecoder.decodeEpIdentyCodeQuery(epCommClient, byteBuffer);
						Thread.currentThread().setName(currentThreadName);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

			}
				break;
			case Iec104Constant.M_LOCK_GUN_FAIL_WARNING: {
				EpDecoder.decodeLockGunFailWaring(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_EP_REPORT_DEVICE: {
				EpDecoder.decodeEpDevices(epCommClient, byteBuffer);
			}
				break;
			//不圈存卡充电冻结金额上行数据
			case Iec104Constant.C_CARD_FRONZE_AMT: {
				EpDecoder.decodeCardFronzeAmt(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.C_VIN_FRONZE_AMT: {
				EpDecoder.decodeVinFronzeAmt(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_CARD_AUTH: {
				EpDecoder.decodeUserCardAuth(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_VIN_AUTH: {
				EpDecoder.decodeVINAuth(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_CARVIN_AUTH: {
				EpDecoder.decodeCARVINAuth(epCommClient, byteBuffer, msg);
			}
				break;
			case Iec104Constant.M_DEVICE_VERSION_RET: {
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeVersionAck");
						EpDecoder.decodeVersionAck(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
				break;
			case Iec104Constant.M_HEX_FILE_UPDATE_RET: {
				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeUpdateAck");
						EpDecoder.decodeUpdateAck(epCommClient, byteBuffer, msg);
						Thread.currentThread().setName(currentThreadName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
				break;
			case Iec104Constant.M_CONCENTROTER_SET_EP_RET: {
				EpDecoder.decodeConcentroterSetEPRet(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_CONCENTROTER_GET_EP_RET: {
				EpDecoder.decodeConcentroterGetEPRet(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_GET_CONSUME_MODEL_RET: {
				EpDecoder.decodeGetConsumeModelRet(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_GET_FLASH_RAM_RET: {
				EpDecoder.decodeGetFlashRamRet(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_GET_TEMPCHARGE_NUM_RET: {
				EpDecoder.decodeGetTempChargeRet(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_SET_TEMPCHARGE_NUM_RET: {
				EpDecoder.decodeSetTempChargeRet(epCommClient, byteBuffer);
			}
				break;
			case Iec104Constant.M_GET_EP_TIMINGCHARGE_RET: {//电桩设置定时充电结果上行数据

				bizExecutorService.execute(() -> {
					try {
						String currentThreadName = Thread.currentThread().getName();
						Thread.currentThread().setName("decodeSetTimingChargeRet");
						EpDecoder.decodeSetTimingChargeRet(epCommClient, byteBuffer);
						Thread.currentThread().setName(currentThreadName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
            }
            	break;
			case Iec104Constant.M_SET_EP_WORK_ARG_RET: {
                EpDecoder.decodeSetWorkArgRet(epCommClient, byteBuffer);
            }
            	break;
            case Iec104Constant.M_SET_EP_INFO_RET: {
                EpDecoder.decodeSetEpInfoRet(epCommClient, byteBuffer);
            }
            case Iec104Constant.M_GET_EP_OFFLINE_INFO_RET: {
                    EpDecoder.decodeGetEpOffLineRet(byteBuffer);
            }
            break;
            case Iec104Constant.FAULT_CODE_FROM_PILE: {
	            bizExecutorService.execute(() -> {
		            try {
			            String currentThreadName = Thread.currentThread().getName();
			            Thread.currentThread().setName("decodeFaultCodeFromPile");
			            EpDecoder.decodeFaultCodeFromPile(byteBuffer);
			            Thread.currentThread().setName(currentThreadName);
		            } catch (Exception e) {
			            e.printStackTrace();
		            }
	            });

				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(LogUtil.addFuncExtLog("Channel|exception"), epCommClient.getChannel(), e.getStackTrace());
		}
	}
}
