//package com.ec.epcore.epdata;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.cooperate.utils.HttpUtils;
//import com.ormcore.model.TblChargingOrder;
//import org.apache.http.conn.HttpHostConnectException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.Properties;
//import java.util.Set;
//import java.util.TreeMap;
//import java.util.concurrent.LinkedBlockingDeque;
//
///**
// *  @author Huangyipeng
// */
//public class EpDataClientService {
//
//	private static final Logger logger = LoggerFactory.getLogger("EpDataClientLog");
//
//	private String epdataSrvURL = null;
//
//	LinkedBlockingDeque<Object> httpSendQueue = new LinkedBlockingDeque<Object>();
//
//	private static EpDataClientService instance = null;
//	public EpDataClientService()
//	{
//		instance = this;
//	}
//	public static EpDataClientService  getInstance()
//	{
//		return instance;
//	}
//
//	long pileStatusMinute = System.currentTimeMillis() / 1000/60; // 分钟计数
//	TreeMap<String,JSONObject> currentPileStatusMap = new TreeMap<String,JSONObject>();
//
//	TreeMap<String,JSONObject> getPileStatusMap()	{
//		long tmpPileStatusMinute = System.currentTimeMillis() / 1000/60;
//		if(tmpPileStatusMinute == pileStatusMinute){
//			return currentPileStatusMap;
//		}else{
//			httpSendPileStatusMap(pileStatusMinute,currentPileStatusMap);
//			pileStatusMinute = tmpPileStatusMinute;
//			currentPileStatusMap = new TreeMap<String,JSONObject>();
//			return currentPileStatusMap;
//		}
//	}
//
//	static void clearZeroValue(JSONObject jobj)
//	{
//		 Set<String> keys = jobj.keySet();
//		 LinkedList<String> delKeys = new LinkedList<String>();
//		 for(String key:keys){
//			 Object value = jobj.get(key);
//			 if(value.toString().equals("0")){
//				 delKeys.add(key);
//			 }
//		 }
//		 for(String key:delKeys) {
//			 jobj.remove(key);
//		 }
//	}
//
//	void httpSendPileStatusMap(long pileStatusMinute,TreeMap<String,JSONObject> pileStatusMap){
//		if(pileStatusMap.size() > 0){
//			//发送
//
////			Set<String> keySet =  pileStatusMap.keySet();
////			JSONArray jarr = new JSONArray();
////			for(String key:keySet){
////				JSONObject jobj = pileStatusMap.get(key);
////				clearZeroValue(jobj);
////				jarr.add(jobj);
////			}
//
//			//addToHttpSendQueue(jarr);
//			addToHttpSendQueue(pileStatusMap);
//		}
//	}
//
//	public class HttpSendThreader extends Thread
//	{
//		public HttpSendThreader(){
//			super("epdata_http_sender");
//		}
//		private void sendHttpDataToEpData(String data) throws IOException {
//			TreeMap<String,String> reqMap = new TreeMap<String,String>();
//			reqMap.put( "data" , data );
//			String res = HttpUtils.httpPost(epdataSrvURL + "httpDataInput.do", reqMap);
//			logger.info("epdata HttpSendThreader.sendHttpDataToEpData " + res);
//		}
//		public void run(){
//			logger.info("epdata HttpSendThreader.run .... started ");
//			while(true){
//				try{
//					Object sendObj = httpSendQueue.takeFirst();
//					logger.info( "epdata httpSendQueue httpSendQueue.takeFirst() " + httpSendQueue.size() );
//					if( sendObj instanceof JSONObject ){
//						logger.info( "epdata sendObj instanceof JSONObject " );
//						JSONObject jobj = (JSONObject) sendObj;
//						//logger.info(jobj.toJSONString() );
//						sendHttpDataToEpData(jobj.toJSONString());
//					}else if( sendObj instanceof TreeMap ){
//						logger.info( "epdata sendObj instanceof TreeMap " );
//						TreeMap<String,JSONObject> tmpPileStatusMap = (TreeMap<String,JSONObject>)sendObj;
//
//						long t1 = System.currentTimeMillis();
//
//						Set<String> keySet =  tmpPileStatusMap.keySet();
//						JSONArray jarr = new JSONArray();
//						for(String key:keySet){
//							JSONObject jobj = tmpPileStatusMap.get(key);
//							clearZeroValue(jobj);
//							jarr.add(jobj);
//						}
//						logger.info("epdata step one complete.");
//
//						String sendStr = jarr.toJSONString();
//						long t2 = System.currentTimeMillis();
//						sendHttpDataToEpData(sendStr  );
//						long t3 = System.currentTimeMillis();
//						logger.info("epdata toJSONString:" + ( t2 - t1 ) + " httpsend:" + ( t3 - t2 ) + " send size:" + sendStr.length()   );
//					}else {
//						logger.error( "epdata sendObj instanceof others " + sendObj.getClass().getName() );
//					}
//				}catch(HttpHostConnectException hhce){
//					logger.error( "epdata httpSendQueue.takeFirst(): "  + hhce.toString() );
//				}catch(Exception ex){
//					logger.error( "epdata httpSendQueue.takeFirst()" , ex);
//				}
//			}
//		}
//	}
//
//	public void addToHttpSendQueue(Object obj){
//		if( httpSendQueue.size() > 500 ){
//			httpSendQueue.removeFirst();
//			logger.error( "epdata addToHttpSendQueue httpSendQueue.removeFirst()" );
//		}
//		httpSendQueue.push(obj);
//		logger.info( "epdata httpSendQueue httpSendQueue.push() " + httpSendQueue.size() );
//	}
//
//	private JSONObject getMapedPileStatus(String epCode,int epGunNo)
//	{
//		TreeMap<String,JSONObject> pileStatusMap = getPileStatusMap();
//		String key = epCode + "_" +  epGunNo;
//		JSONObject pileStatus = pileStatusMap.get(key);
//		if(pileStatus == null){
//			pileStatus = new JSONObject();
//			pileStatus.put("epCode", epCode);
//			pileStatus.put("epGunNo", epGunNo);
//			pileStatusMap.put(key, pileStatus);
//		}
//		return pileStatus;
//	}
//	/**
//	 * 记录电流
//	 * @param epCode
//	 * @param epGunNo
//	 * @param value
//	 */
//	public void recordAC(String epCode,int epGunNo,int value){
//		JSONObject pileStatus = getMapedPileStatus(epCode,epGunNo);
//		pileStatus.put("acValue", value);
//	}
//
//	/**
//	 * 记录电压
//	 * @param epCode
//	 * @param epGunNo
//	 * @param value
//	 */
//	public void recordVolt(String epCode,int epGunNo,int value){
//		JSONObject pileStatus = getMapedPileStatus(epCode,epGunNo);
//		pileStatus.put("voltValue", value);
//	}
//
//	public void recordPileStatusValue(String epCode , int epGunNo , int dataType,int address , String strValue){
//		if( epdataSrvURL== null ){
//			return;
//		}
//		if(logger.isDebugEnabled()){
//			logger.debug( "epdata recordPileStatusValue:" + " " + epCode + " " +epGunNo + " " +  dataType + " " + address + " " + strValue);
//		}
//		recordPileStatusValue( epCode , epGunNo , "" + dataType + "_" + address, strValue );
//	}
//
//	public void recordPileStatusValue(String epCode , int epGunNo , int dataType,int address , int value){
//		if( epdataSrvURL== null ){
//			return;
//		}
//		if(logger.isDebugEnabled()){
//			logger.debug( "epdata recordPileStatusValue:" + " " + epCode + " " +epGunNo + " " +  dataType + " " + address + " " + value );
//		}
//		recordPileStatusValue(epCode,epGunNo,"" + dataType + "_" + address,"" + value);
//	}
//	/**
//	 * 记录其他类型的电桩状态值
//	 * @param epCode
//	 * @param epGunNo
//	 * @param name
//	 * @param value
//	 */
//	public void recordPileStatusValue(String epCode,int epGunNo,String name,String value){
//		JSONObject pileStatus = getMapedPileStatus(epCode,epGunNo);
//		pileStatus.put(name, value);
//	}
//
//	/**
//	 * 记录状态
//	 * @param epCode
//	 * @param epGunNo
//	 * @param status
//	 */
//	public void recordStat(String epCode,int epGunNo,int status){
//		JSONObject pileStatus = getMapedPileStatus(epCode,epGunNo);
//		pileStatus.put("status", status);
//	}
//
//	/**
//	 * 开始充电
//	 * @param order
//	 */
//	public void reportStartCharge(TblChargingOrder order){
//		if( epdataSrvURL== null ){
//			return;
//		}
//		logger.info("epdata EpDataClientService.reportStartCharge " + order.getChorPilenumber()
//				+ "," + order.getChorMuzzle()
//				+ "," + order.getChorTransactionnumber() );
//		//开始充电时间
//		//order.getChargeBegintime()
//
//		//流水号
//		//order.getChorTransactionnumber()
//
//		//UserId
//		//order.getChorUserid()
//
//		//orgNo
//		//order.getUserOrgNo();
//
//		//pkUserCard
//		//order.getPkUserCard()
//
//		//桩编号
//		String epCode = order.getChorPilenumber();
//
//		//枪头编号
//		int epGunNo = order.getChorMuzzle();
//
//		JSONObject jobj = new JSONObject();
//		jobj.put("__type",  "startCharge" );
//		jobj.put("chargeBegintime", order.getChargeBegintime());
//		jobj.put("transactionnumber", order.getChorTransactionnumber());
//		jobj.put("userId", order.getChorUserid() );
//		jobj.put("orgNo", order.getUserOrgNo() );
//		jobj.put("pkUserCard", order.getPkUserCard() );
//		jobj.put("pkUserCard", order.getPkUserCard() );
//		jobj.put("epCode", epCode);
//		jobj.put("epGunNo", epGunNo);
//		addToHttpSendQueue(jobj);
//	}
//
//	/**
//	 * 结束充电
//	 * @param record
//	 */
//	public void reportStopCharge(String epCode,int epGunNo, String transactionnumber){
//		//流水号
//		//record.getChreTransactionnumber()
//		if( epdataSrvURL== null ){
//			return;
//		}
//		logger.info("epdata EpDataClientService.reportStopCharge " + epCode + "," + epGunNo + "," + transactionnumber );
//
//		//record.get
//		JSONObject jobj = new JSONObject();
//		jobj.put("__type",  "stopCharge" );
//		jobj.put("epCode", epCode);
//		jobj.put("epGunNo", epGunNo);
//		jobj.put("transactionnumber", transactionnumber);
//		addToHttpSendQueue(jobj);
//	}
//
//
////	#epdata.srv=http://127.0.0.1:8080/epdata/
////	#epdata.srv=http://127.0.0.1:8080/
//	public void init()
//	{
//		logger.info("epdata EpDataClientService.init");
//		File f =new File("conf/system.properties");
//		try{
//			if(f.exists()){
//				Properties prop = new Properties();
//				FileInputStream fis = new FileInputStream(f);
//				prop.load(fis);
//				fis.close();
//				String epdataSrvURL = prop.getProperty("epdata.srv");
//				if(epdataSrvURL == null || epdataSrvURL.trim().length() == 0){
//					//throw new RuntimeException( "system.properties epdata.srv 配置为空" );
//					logger.warn("system.properties epdata.srv 配置为空");
//					epdataSrvURL = null;
//				}
//				logger.info("epdata EpDataClientService.init epdataSrvURL=" + epdataSrvURL);
//				 init(epdataSrvURL);
//			}else{
//				logger.error( "conf/system.properties 配置文件不存在!" );
//			}
//		}catch(Exception ex){
//			logger.error( "EpDataClientService.init" , ex );
//		}
//
//	}
//
//	private HttpSendThreader httpSendThreader = null;
//	public void init(String epdataSrvURL)
//	{
//			this.epdataSrvURL = epdataSrvURL ;
//			if(epdataSrvURL != null){
//				httpSendThreader = new HttpSendThreader();
//				httpSendThreader.start();
//			}
//	}
//
//	/**
//	 * 发送上行数据
//	 * @param data
//	 */
//	public void sendUpDataToEpData(byte[] data)
//	{
//
//	}
//
//	/**
//	 * 发送下行数据
//	 * @param data
//	 */
//	public void sendDownDataToEpData(byte[] data)
//	{
//
//	}
//
//	public String statInfo()
//	{
//		return "";
//	}
//
//}
