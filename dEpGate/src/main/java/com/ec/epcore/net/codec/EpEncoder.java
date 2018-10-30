package com.ec.epcore.net.codec;

import com.ec.config.Global;
import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.RateInfoCache;
import com.ec.epcore.net.proto.ApciHeader;
import com.ec.epcore.net.proto.AsduHeader;
import com.ec.epcore.net.proto.BlankUser;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.TimeStage;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.netty.buffer.DynamicByteBuffer;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ormcore.model.ElectricpileWorkarg;
import com.ormcore.model.TblCarVin;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * 发消息，编码
 * 
 * 消息结构：byte混淆码1 + byte混淆吗2 + int长度  + short协议号  + byte是否压缩  + byte[] 数据内容 + byte混淆码3 + byte混淆码4
 * 
 * @author haojian
 * Mar 27, 2013 4:11:15 PM
 */

public class EpEncoder extends MessageToByteEncoder{


	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpEncoder.class.getName()));
	
	
	/**
	 * 不管channel.write(arg0)发送的是什么类型，
	 * 最终都要组装成 ByteBuf 发送,
	 * 所以encode需要返回 ByteBuf 类型的对象
	 * @author haojian
	 * Mar 27, 2013 5:18:00 PM
	 * @param chc
	 * @param bb   (Message)
	 * @param byteBuf   (Byte)
	 * @return
	 * @throws Exception
	 */
	@Override
	protected void encode(ChannelHandlerContext chc, Object msg, ByteBuf byteBuf)
			throws Exception {
		
		if(msg instanceof ByteBuf){
			
			ByteBuf byteBufIn = (ByteBuf)msg;
			byte[] bb = new byte[byteBufIn.readableBytes()];
			byteBufIn.getBytes(0, bb);
			
			byteBuf.writeBytes(bb);
			
		}else if(msg instanceof byte[]){
			
			byte[] bb = (byte[])msg;
			byteBuf.writeBytes(bb);
			
		}else{
			logger.debug(LogUtil.addExtLog("ep 未知的消息类型... channel"),chc.toString());
		}
		
		
	}
	
	public static byte[]  Package(int sendINum,int recvINum,int cos,int common_address,byte recordType,byte[] msg
			,byte []time,int commVersion) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		byteBuffer.put(recordType);
		byteBuffer.put(msg);
		if(commVersion>=YXCConstants.PROTOCOL_VERSION_V4)
		{
			byteBuffer.put(time);
		}
		    
		return PackageI(byteBuffer.getBytes(),Iec104Constant.C_SD_NA,(short)cos,sendINum, recvINum, common_address,commVersion);
		
	}
	public static byte[]  Package(byte type,int sendINum,int recvINum,int cos,int common_address,byte[] msg
			,byte []time,int commVersion) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		byteBuffer.put(msg);
		if(commVersion>=YXCConstants.PROTOCOL_VERSION_V4)
		{
			byteBuffer.put(time);
		}
		    
		return PackageI(byteBuffer.getBytes(),type,(short)cos,sendINum, recvINum, common_address, commVersion);
		
	}
	
	public static  byte[] PackageI(byte[] data,byte type,short cos,int sendINum,int recvINum,int common_address,int commVersion) {
		
			ApciHeader apciHeader = new ApciHeader();
			if(commVersion>=YXCConstants.PROTOCOL_VERSION_V4)
			   apciHeader.setLength(ApciHeader.getHLen() + AsduHeader.H_LEN + data.length+2- Iec104Constant.INITIAL_BYTES_TO_STRIP);
			else
			   apciHeader.setLength(ApciHeader.getHLen() + AsduHeader.H_LEN + data.length- Iec104Constant.INITIAL_BYTES_TO_STRIP);
			
			apciHeader.setINr(sendINum, recvINum);
			
			AsduHeader asduHeader = new AsduHeader();
			asduHeader.setType((byte) type);
			asduHeader.setCon(WmIce104Util.short2Bytes(cos));
			asduHeader.setConn_address(WmIce104Util.short2Bytes((short)common_address));
		
			DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
			byteBuffer.put(apciHeader.toByteArray());
			byteBuffer.put(asduHeader.toByteArray());
			
			byteBuffer.put(data);
			
			if(commVersion>=YXCConstants.PROTOCOL_VERSION_V4)
			{
				short crc= WmIce104Util.CRCSum(byteBuffer.getBytes(),3,0);
				byteBuffer.put(WmIce104Util.short2Bytes(crc));
			}
	
		return byteBuffer.getBytes();
	}
	
    public static  byte[] do_sframe(int NR) {//add by hly 
		
		ApciHeader apciHeader = new ApciHeader();
		apciHeader.setLength(4);
		apciHeader.setSFrame(NR);
		
		return apciHeader.toByteArray();
	}
	
	public static byte[] do_startup() {
		ApciHeader apciHeader = new ApciHeader();
		apciHeader.setLength(4);
		apciHeader.setUFrameType((byte)Iec104Constant.WM_104_CD_STARTDT);
		
		return apciHeader.toByteArray();
	}
	
	public static byte[] do_test() {//add by hly
		ApciHeader apciHeader = new ApciHeader();
		apciHeader.setLength(4);
		apciHeader.setUFrameType((byte) (Iec104Constant.WM_104_CD_TESTFR));
		
		return apciHeader.toByteArray();
	}
	public static byte[] do_test_confirm() {//add by hly
		ApciHeader apciHeader = new ApciHeader();
		apciHeader.setLength(4);
		apciHeader.setUFrameType((byte) (Iec104Constant.WM_104_CD_TESTFR_CONFIRM));
		
		return apciHeader.toByteArray();
	}
	
	public static byte[] do_set_time(short cos,int sendINum,int recvINum,int common_address,
			int commVersinon) {
		
		byte data[] = WmIce104Util.getP56Time2a();
		byte time[] = WmIce104Util.timeToByte();
		return Package(Iec104Constant.C_CS_NA,sendINum,recvINum,cos,common_address,data,time,commVersinon);
	}
	
	public static byte[] Package_all_call(short cos,int sendINum,int recvINum,int common_address,
			int commVersinon) {
		byte data[] = new byte[1];
		data[0] =20;
		byte time[] = WmIce104Util.timeToByte();
		
		return Package(Iec104Constant.C_IC_NA,sendINum,recvINum,cos,common_address,data,time,commVersinon);
}
	/***
	 * 
	 * @param cos
	 * @param sendINum
	 * @param recvINum
	 * @param common_address
	 * @param epCode
	 * @param nInterFace
	 * @param bSucess
	 * @param strSucessDesc
	 * @param blance
	 * @param Account
	 * @return
	 */
	public static  byte[] do_nocard_auth_by_yzm(short cos,int sendINum,int recvINum,int common_address,String epCode,int nInterFace,byte bSucess,String strSucessDesc, 
			int blance,String Account,byte []time,int commVersion) {
		
		assert(epCode.length()==YXCConstants.LEN_CODE);
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		//1	终端机器编码	BCD码	8Byte	16位编码
			
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			
		//2	充电桩接口标识	BIN码	1Byte
		byteBuffer.put((byte)nInterFace);
		//3	鉴权成功标识	BIN码	1Byte	1:鉴权成功
		//0:鉴权失败
		byteBuffer.put((byte)bSucess);
		//4	鉴权失败原因	BCD码	2Byte	0001密码不对
		//0002余额不足
		//0003套餐余额不足
		//0004非法用户
		//0005挂失卡
		//0006车卡不匹配
		byteBuffer.put(WmIce104Util.str2Bcd(strSucessDesc));
		
		//5	剩余金额	BIN码	4Byte	精确到小数点后两位,倍率100
		byteBuffer.put(WmIce104Util.int2Bytes(blance));
			
		byteBuffer.put(WmIce104Util.str2Bcd(Account));
			
		return byteBuffer.getBytes();
	}
	/**
	 * 
	 * @param cos
	 * @param sendINum
	 * @param recvINum
	 * @param common_address
	 * @param epCode
	 * @param reqRet
	 * @param len
	 * @param sections
	 * @param FileMd5
	 * @return
	 */
	public static  byte[] do_qrcode_sumary(String epCode,byte reqRet,int len,short sections,String FileMd5) {
		
		assert(epCode.length()==YXCConstants.LEN_CODE);
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		//1	终端机器编码	BCD码	8Byte	16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		byteBuffer.put(reqRet);
		//3	字节数	BIN码	4Byte	
		byteBuffer.put(WmIce104Util.int2Bytes(len));
		//4	分段数	BIN码	2Byte	
		byteBuffer.put(WmIce104Util.short2Bytes(sections));
		byteBuffer.put(FileMd5.getBytes());
	
		return byteBuffer.getBytes();
	}
	
	/**
	 * 
	 * @param cos
	 * @param sendINum
	 * @param recvINum
	 * @param common_address
	 * @param epCode
	 * @param SectionIndexReq
	 * @param reqFlag
	 * @param len
	 * @param SectionData
	 * @return
	 */
	public static  byte[] do_qrcode_down(String epCode,short SectionIndexReq,byte reqFlag,short len,byte[] SectionData) {
		
		assert(epCode.length()==YXCConstants.LEN_CODE);
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		//1	终端机器编码	BCD码	8Byte	16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2	段索引
		byteBuffer.put(WmIce104Util.short2Bytes(SectionIndexReq));
		//3	成功与否标识
		byteBuffer.put(reqFlag);
		//
		byteBuffer.put(WmIce104Util.short2Bytes(len));
		if(reqFlag ==1)
		{
			byteBuffer.put(SectionData);
		}
		return byteBuffer.getBytes();
	}
	/***
	 * 
	 * @param cos
	 * @param sendINum
	 * @param recvINum
	 * @param common_address
	 * @param epCode
	 * @param len
	 * @param sections

	 * @return
	 */
			
	public static  byte[] do_ep_hex_file_sumary(String epCode,short stationAddr,
			String hardwareNumber,int hardwareM,int hardwareA,
			String softNumber,int softM,int softA,int softC,
			int existFlag,int len,short sections,int updateFlag,String Md5Value) {
		
		
		assert(epCode.length()==YXCConstants.LEN_CODE);
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		//1	终端机器编码	BCD码	8Byte	16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2.站地址
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));
		//3 强制更新标识
		byteBuffer.put((byte)updateFlag);
		
		//4.硬件1型号
		if(hardwareNumber.length()>10)
		{
			return null;
		}
		
		byteBuffer.put(hardwareNumber.getBytes());
		int lenn = hardwareNumber.length();
		for(int i=0;i<10-lenn;i++)
		{
			byteBuffer.put((byte)0);
		}
		
		//5	硬件1主版本号	BIN码	1Byte	
		byteBuffer.put((byte)hardwareM);
		//6	硬件1子版本号	BIN码	1Byte	
		byteBuffer.put((byte)hardwareA);
		//7 固件1型号
		if(softNumber.length()>8)
		{
			return null;
		}
		
		byteBuffer.put(softNumber.getBytes());
		for(int i=0;i<8-softNumber.length();i++)
		{
			byteBuffer.put((byte)0);
		 }
	
		//8	固件1主版本号	BIN码	1Byte	
		byteBuffer.put((byte)softM);
		//9	固件1副版本号	BIN码	1Byte	
		byteBuffer.put((byte)softA);
		//10	固件1编译版本号	BIN码	2Byte	
		byteBuffer.put(WmIce104Util.short2Bytes((short)softC));
		
		//11	充电桩程序文件是否存在	BIN码	1Byte	
		byteBuffer.put((byte)existFlag);
		//12文件总字节数	BIN码	4Byte	
		byteBuffer.put(WmIce104Util.int2Bytes(len));
		//11	分段数	BIN码	2Byte	
		byteBuffer.put(WmIce104Util.short2Bytes(sections));
		//12 文件信息MD5验证码
		if(Md5Value ==null)
		{
			Md5Value = new String("00000000000000000000000000000000");
		}
		byteBuffer.put(Md5Value.getBytes());
	
		return byteBuffer.getBytes();
	}
	
	
	
		
	public static  byte[] do_ep_hex_file_down(String epCode,short stationAddr,String softNumber,
			int softM,int softA,short softC,short SectionIndexReq,
			short len,byte[] SectionData,int successFlag) {
		
		assert (epCode.length() == YXCConstants.LEN_CODE);
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		// 1 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));

		// 2.站地址
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));
		// 3.固件型号
		if (softNumber.length() > 8) {
				return null;
		}
			
		byteBuffer.put(softNumber.getBytes());
		for (int i = 0; i < 8 - softNumber.length(); i++) {
				byteBuffer.put((byte)0);
		}
			
		// 4 固件主版本号 1Byte
		byteBuffer.put((byte) softM);
		// 5 固件副版本号 1Byte
		byteBuffer.put((byte) softA);
		// 6.固件编译版本号2Byte
		byteBuffer.put(WmIce104Util.short2Bytes(softC));
		// 7 段索引
		byteBuffer.put(WmIce104Util.short2Bytes(SectionIndexReq));
		// 8 成功与否标识
		byteBuffer.put((byte) successFlag);
		if(successFlag == 1)
		{
               //9 文件信息MD5验证码
			   String Md5Value = WmIce104Util.MD5Encode(SectionData);
			    
			   // logger.info("Md5Value:{}",Md5Value);
			    byteBuffer.put(Md5Value.getBytes());
			    //10 段大小
			    byteBuffer.put(WmIce104Util.short2Bytes(len));
		        //11 段数据
		        byteBuffer.put(SectionData);
		}
		
		return byteBuffer.getBytes();
	}
	
	public static  byte[] do_force_update_ephex(short stationAddr,String epCode,String hardwareNumber,
			int hardwareM,int hardwareA) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
	    //集中器id(站地址)
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));

		// 2终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));

		// 3.硬件1型号
		if (hardwareNumber.length() > 10) {
			return null;
		}
			
		byteBuffer.put(hardwareNumber.getBytes());
		for (int i = 0; i < 10 - hardwareNumber.length(); i++)
		{
			byteBuffer.put((byte)0);
		}
			
		// 4 硬件1主版本号 BIN码 1Byte
		byteBuffer.put((byte) hardwareM);
		// 5 硬件1子版本号 BIN码 1Byte
		byteBuffer.put((byte) hardwareA);
			

		return byteBuffer.getBytes();
	}
	public static  byte[] do_eqversion_req(String epCode,short stationAddr) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		// 1 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2站地址
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));

		return byteBuffer.getBytes();
	}	
		

	public static  byte[] do_blank_list(String timeStap,Vector<BlankUser> vBlankUsers) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		//bout.write(this.bcdcode);
		byteBuffer.put(WmIce104Util.str2Bcd(timeStap));
		int blankNum = vBlankUsers.size();
		
		for(int i=0;i< blankNum; i++)
		{
			BlankUser bu = vBlankUsers.get(i);
			byteBuffer.put(bu.toByteArray());
		}
		
		return byteBuffer.getBytes();
	}
	public static  synchronized byte[] do_start_electricize_v4(String epCode,byte cdq_no,String Account,int Amt,byte ermFlag,int fronzeAmt,int deductFeeMethod,
			String password,String serialNo,byte showPriceFlag,int jPrice,int fPrice,int pPrice,int gPrice,int serPrice)
	{
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		//1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
			
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2 枪口编号 BIN码 1Byte
		byteBuffer.put(cdq_no);
			
		//3
		byteBuffer.put(WmIce104Util.str2Bcd(Account));
		//4
		byteBuffer.put(WmIce104Util.int2Bytes(Amt));
		//5
		byteBuffer.put(WmIce104Util.int2Bytes(0));
		//6
		byteBuffer.put(ermFlag);
		//7
		byteBuffer.put((byte)deductFeeMethod);
		//8
		byteBuffer.put(WmIce104Util.int2Bytes(fronzeAmt));
		//9
		byteBuffer.put(password.getBytes());
		//10
		byteBuffer.put(WmIce104Util.str2Bcd(serialNo));
			
		//11
		byteBuffer.put(showPriceFlag);
		//12
		byteBuffer.put(WmIce104Util.int2Bytes(jPrice));
		//13
		byteBuffer.put(WmIce104Util.int2Bytes(fPrice));
		//14
		byteBuffer.put(WmIce104Util.int2Bytes(pPrice));
		//15
		byteBuffer.put(WmIce104Util.int2Bytes(gPrice));
		//16
		byteBuffer.put(WmIce104Util.int2Bytes(serPrice));
		
		return byteBuffer.getBytes();
	}	
	public static  synchronized byte[] do_start_electricize(String epCode,byte cdq_no,String Account,int Amt,
            byte ermFlag,int fronzeAmt,int deductFeeMethod,String password,String serialNo,RateInfoCache rateInfo)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		//1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
			
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2 枪口编号 BIN码 1Byte
		byteBuffer.put(cdq_no);
			
		//3
		byteBuffer.put(WmIce104Util.str2Bcd(Account));
		//4
		byteBuffer.put(WmIce104Util.int2Bytes(Amt));
		//5
		byteBuffer.put(WmIce104Util.int2Bytes(0));
		//6
		byteBuffer.put(ermFlag);
		//7
		byteBuffer.put((byte)deductFeeMethod);
		//8
		byteBuffer.put(WmIce104Util.int2Bytes(fronzeAmt));
		//9
		byteBuffer.put(password.getBytes());
		//10
		byteBuffer.put(WmIce104Util.str2Bcd(serialNo));
        if (rateInfo.getModelId() == 3) {
			ArrayList<TimeStage> timeStageList = new ArrayList<TimeStage>();
			ByteArrayOutputStream bout = new ByteArrayOutputStream(256);
			byte[] comm_data;
			JSONObject jb = JSONObject.fromObject(rateInfo.getRateInfo().getQuantumDate());
			JSONArray ja = jb.getJSONArray("data");

			// 循环添加Employee对象（可能有多个）
			for (int i = 0; i < ja.size(); i++) {
				TimeStage timestage = new TimeStage();

				timestage.setStartTime(ja.getJSONObject(i).getInt("st"));
				timestage.setEndTime(ja.getJSONObject(i).getInt("et"));
				timestage.setFlag(ja.getJSONObject(i).getInt("mark"));

				timeStageList.add(timestage);
			}

			int nTimeState = timeStageList.size();
			byte bTimeStage = (byte) timeStageList.size();
				bout.write(bTimeStage);
			for (int i = 0; i < nTimeState; i++) {
				TimeStage ts = timeStageList.get(i);
				try {
					bout.write(ts.toByteArray());
				} catch (Exception e) {
					logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
					return null;
				}
			}
			comm_data = bout.toByteArray();

            //byteBuffer.put((byte)1);
			byteBuffer.put(comm_data);
            BigDecimal dec = Global.DecTime4;
            byte[] b_j_rate = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getJ_Rate().multiply(dec).doubleValue()));
            byteBuffer.put(b_j_rate);
            byte[] b_j_rate_Money = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getJ_RateMoney().multiply(dec).doubleValue()));
            byteBuffer.put(b_j_rate_Money);

            byte[] b_f_rate = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getF_Rate().multiply(dec).doubleValue()));
            byteBuffer.put(b_f_rate);
            byte[] b_f_rate_Money = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getF_RateMoney().multiply(dec).doubleValue()));
            byteBuffer.put(b_f_rate_Money);
 
            byte[] b_p_rate = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getP_Rate().multiply(dec).doubleValue()));
            byteBuffer.put(b_p_rate);
            byte[] b_p_rate_Money = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getP_RateMoney().multiply(dec).doubleValue()));
            byteBuffer.put(b_p_rate_Money);

            byte[] b_g_rate = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getG_Rate().multiply(dec).doubleValue()));
            byteBuffer.put(b_g_rate);
            byte[] b_g_rate_Money = WmIce104Util.int2Bytes((int) (
                    rateInfo.getRateInfo().getG_RateMoney().multiply(dec).doubleValue()));
            byteBuffer.put(b_g_rate_Money);
        }

		return byteBuffer.getBytes();
	}
	public static  synchronized byte[] do_stop_electricize(String epCode,byte epGunNo) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
			
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		// 枪口编号 BIN码 1Byte
		byteBuffer.put(epGunNo);
		
		return byteBuffer.getBytes();
		
	}
	
	public static  synchronized byte[] do_cdz_business(byte BusinessFlag,int StartTime,int EndTime) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		
		//bout.write(this.bcdcode);
		//2--0:离线;1:运营
		byteBuffer.put(BusinessFlag);
		//3
		byteBuffer.put(WmIce104Util.int2Bytes(StartTime));
		//4
		byteBuffer.put(WmIce104Util.int2Bytes(EndTime));
		
		return byteBuffer.getBytes();
	}

	public static byte[] do_bespoke(byte[] bcdCode,String  bespokeNo,byte cdq_no,byte redo,byte bcdAccount[],byte bcdCardNo[],byte start_time[], short order_time,byte CarCardNo[]) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(bcdCode);
		// 枪口编号 BIN码 1Byte
		byteBuffer.put(cdq_no);
		byteBuffer.put(redo);
		// 3 预约开始时间 CP56Time2a 7Byte
		//byte P56Time2a[] = getP56Time2a();
		byteBuffer.put(start_time);
		// 4 预约等待时间 BIN码 2Byte 单位为分钟
		byteBuffer.put(WmIce104Util.short2Bytes(order_time));
		// 5账号号 BCD码 6Byte
		byteBuffer.put(bcdAccount);
		// 6 卡号 BCD码 8Byte
		byteBuffer.put(bcdCardNo);
		// 7 预约号 BCD码 6Byte 由运营系统分配
		byteBuffer.put(WmIce104Util.str2Bcd(bespokeNo));
		// 8 车牌号 ASCII码 16Byte
		byteBuffer.put(CarCardNo);
			
		return byteBuffer.getBytes();
	}

	public static  byte[] do_cancel_bespoke(String epCode,int epGunNo,String bespokeNo) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			// 枪口编号 BIN码 1Byte
		byteBuffer.put((byte)epGunNo);
			// 3
		byteBuffer.put(WmIce104Util.str2Bcd(bespokeNo));
			
		return byteBuffer.getBytes();
	}
	public static  byte[] do_bespoke_confirm(String epCode,int epGunNo,String bespokeNo,int redo,int result) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		// 枪口编号 BIN码 1Byte
		byteBuffer.put((byte)epGunNo);
		// 3
		byteBuffer.put(WmIce104Util.str2Bcd(bespokeNo));
			
		byteBuffer.put((byte)redo);
			
		byteBuffer.put((byte)result);
			
		return byteBuffer.getBytes();
	}

	public static  byte[] do_consume_model(String epCode,RateInfoCache rateData) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			
		byteBuffer.put(rateData.getComm_data());
			
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_charge_event_confirm(String epCode, int  epGunNo, String chargeSerialNo,int ret ) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			
		byteBuffer.put((byte)epGunNo);
			
		byteBuffer.put(WmIce104Util.str2Bcd(chargeSerialNo));
			
		byteBuffer.put((byte)ret);
			
		return byteBuffer.getBytes();
	}
    public static byte[] do_confirm(String epCode, byte epGunNo,byte cmd,short packIndex,byte offtates,byte timeoutflag,byte[]time ) {
    	
    	DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
    	byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			
		byteBuffer.put(epGunNo);
			
		byteBuffer.put(cmd);
			
		byteBuffer.put(WmIce104Util.short2Bytes(packIndex));
			
		byteBuffer.put(offtates);
			
		byteBuffer.put(timeoutflag);
			
		byteBuffer.put(time);
			
		return byteBuffer.getBytes();
	}
    
    public static byte[] do_consumerecord_confirm(String epCode,int epGunNo,String serialNo, int ret ) {
    	
    	DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
			// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
    	byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			
		byteBuffer.put((byte)epGunNo);
			
		byteBuffer.put(WmIce104Util.str2Bcd(serialNo));
			
		byteBuffer.put((byte)ret);
			
		return byteBuffer.getBytes();
	}
    public static byte[] do_card_frozen_amt(String epCode,int epGunNo,String cardInNo, int ret,int errorCode ) {
    	
    	DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
			// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
    	byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			
    	byteBuffer.put((byte)epGunNo);
				
    	byteBuffer.put(WmIce104Util.appendZero(cardInNo,32));
			
    	byteBuffer.put((byte)ret);
			
    	byteBuffer.put(WmIce104Util.short2Bytes((short)errorCode));
			
		return byteBuffer.getBytes();
	}
 
 
	public static byte[] do_ep_stat(String epCode) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_query_consume_record(String epCode,int startPos, short enteryNum) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		// 2 枪口号
       //  bout.write(epGunNo);
		//起始位置
		byteBuffer.put(WmIce104Util.int2Bytes(startPos));
		//条目数
		byteBuffer.put(WmIce104Util.short2Bytes(enteryNum));
		
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_query_comm_signal(String epCode,short stationAddr) {

		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		// 2 站址 short
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));
		
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_update_server_ip(String epCode, byte epGunNo,int freezeAmt) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		byteBuffer.put(epGunNo);
		
		byteBuffer.put(WmIce104Util.int2Bytes(freezeAmt));
		
		return byteBuffer.getBytes();
	}
		
	public static byte[] do_near_call_ep(String epCode, int type,int time) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		byteBuffer.put((byte)1);
		
		byteBuffer.put((byte)type);
		
		byteBuffer.put(WmIce104Util.short2Bytes((short)time));
		
		return byteBuffer.getBytes();
	}
	
	
	
	
	public static byte[] do_open_gun_lock(String epCode, int epGunNo,int freezeAmt) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		byteBuffer.put((byte)epGunNo);
		
		byteBuffer.put(WmIce104Util.int2Bytes(freezeAmt));
		
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_drop_carplace_lock(String epCode, int epGunNo) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		byteBuffer.put((byte)epGunNo);
		
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_card_auth_resq(String epCode, int epGunNo,String innerNo,String outerNo,int cardStatus,int isFrozenAmt,
			int remainAmt,int ret,int errorCode) {
		
			
			/*1	终端机器编码	BCD码	8Byte	16位编码	
			2	充电桩接口标识	BIN码	1Byte	从1开始	
			3	内卡号	ASCII码	32Byte	长度不够,用0x00在尾部补齐	
			4	外卡号	ASCII码	20Byte	长度不够用0x00补齐	
			5	卡状态	BIN码	1Byte	1:正常；2：挂失	
			6	余额	BIN码	4Byte	精确到小数点后两位，倍数100	
			7	成功标识	Bin码	1Byte	1：成功；0：失败	
			8	错误编码	Bin码	2Byte	*/
			

		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		byteBuffer.put((byte)epGunNo);
		
		byteBuffer.put(WmIce104Util.appendZero(innerNo,32));
		byteBuffer.put(WmIce104Util.appendZero(outerNo,20));
		
		byteBuffer.put((byte)cardStatus);
		
		byteBuffer.put((byte)isFrozenAmt);
		
		
		byteBuffer.put(WmIce104Util.int2Bytes(remainAmt));
		
		byteBuffer.put((byte)ret);
		
		byteBuffer.put(WmIce104Util.short2Bytes((short)errorCode));
		
		return byteBuffer.getBytes();
	}

	public static byte[] do_vin_auth_resq(String epCode, int epGunNo,String innerNo,String outerNo,int cardStatus,int isFrozenAmt,
										  int remainAmt,int ret,int errorCode, List<TblCarVin> carVinList) {

		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();

		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));

		byteBuffer.put((byte)epGunNo);

		byteBuffer.put(WmIce104Util.appendZero(innerNo,32));
		byteBuffer.put(WmIce104Util.appendZero(outerNo,20));

		byteBuffer.put((byte)cardStatus);

		byteBuffer.put((byte)isFrozenAmt);


		byteBuffer.put(WmIce104Util.int2Bytes(remainAmt));

		if (errorCode > 0) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte) ret);
		}

		byteBuffer.put(WmIce104Util.short2Bytes((short)errorCode));

		if (carVinList == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte) carVinList.size());
			for (TblCarVin carVin : carVinList) {
				byteBuffer.put(WmIce104Util.appendZero(carVin.getVinCode(), 17));
			}
		}

		return byteBuffer.getBytes();
	}

	public static byte[] do_carvin_auth_resq(String epCode, int epGunNo, String carVinCode,int isFrozenAmt,
										  int remainAmt,int ret,int errorCode) {

		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();

		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));

		byteBuffer.put((byte)epGunNo);

		byteBuffer.put(WmIce104Util.appendZero(carVinCode,17));

		if (errorCode > 0) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte) ret);
		}

		byteBuffer.put(WmIce104Util.short2Bytes((short)errorCode));

		byteBuffer.put((byte)isFrozenAmt);

		byteBuffer.put(WmIce104Util.int2Bytes(remainAmt));

		return byteBuffer.getBytes();
	}

	public static byte[] do_card_auth_resp(byte[] bcdCode,String  bespokeNo,byte cdq_no,byte redo,byte bcdAccount[],byte bcdCardNo[],byte start_time[], short order_time,byte CarCardNo[]) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(bcdCode);
			// 枪口编号 BIN码 1Byte
		byteBuffer.put(cdq_no);
		byteBuffer.put(redo);
			// 3 预约开始时间 CP56Time2a 7Byte
			//byte P56Time2a[] = getP56Time2a();
		byteBuffer.put(start_time);
			// 4 预约等待时间 BIN码 2Byte 单位为分钟
		byteBuffer.put(WmIce104Util.short2Bytes(order_time));
			// 5账号号 BCD码 6Byte
		byteBuffer.put(bcdAccount);
			// 6 卡号 BCD码 8Byte
		byteBuffer.put(bcdCardNo);
			// 7 预约号 BCD码 6Byte 由运营系统分配
		byteBuffer.put(WmIce104Util.str2Bcd(bespokeNo));
			// 8 车牌号 ASCII码 16Byte
		byteBuffer.put(CarCardNo);
			
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_ep_identyCode(String  epCode,byte epGunNo,String identyCode,long createTime ,
			byte cmdHour,byte cmdMin,byte cmdSec,int commVersion) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
			// 枪口编号 BIN码 1Byte
		byteBuffer.put(epGunNo);
		if(commVersion<YXCConstants.PROTOCOL_VERSION_V4)
		{
			//  3,命令时间，时
			byteBuffer.put(cmdHour);
			//  4, 命令时间，分
		    byteBuffer.put(cmdMin);
			//  6, 命令时间，秒
		    byteBuffer.put(cmdSec);
		}
		// 7,识别码
		byteBuffer.put(WmIce104Util.appendFF(identyCode,10));
			
		// 8 识别码产生时间
		Date date = DateUtil.toDate(createTime*1000);
		byteBuffer.put(WmIce104Util.getP56Time2a(date));
			
			
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_concentroter_setep(short stationAddr,short epNum,String epcodes) 
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 站址 short
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));
		byteBuffer.put(WmIce104Util.short2Bytes(epNum));
		
		String[] epCodeArray = epcodes.split(",+"); 
   	    for(String eachEpCode : epCodeArray)
  	    { 
   	    	byteBuffer.put(WmIce104Util.str2Bcd(eachEpCode));
  	    }
		
   	    return byteBuffer.getBytes();
		
	}
    
	public static byte[] do_query_ep_rateInfo(String epCode) {
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		
		return byteBuffer.getBytes();
	}
	
	public static byte[] do_concentroter_getep(short stationAddr) {
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
			// 1 站址 short
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));
		return byteBuffer.getBytes();
	}
	public static byte[] do_get_flashRam(String epCode,short stationAddr,short type,int startPos,short len) {
				
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		// 1 站址 short
		byteBuffer.put(WmIce104Util.short2Bytes(stationAddr));
		//类型
		byteBuffer.put(WmIce104Util.short2Bytes(type));
		//开始位置
		byteBuffer.put(WmIce104Util.int2Bytes(startPos));
		//len
		byteBuffer.put(WmIce104Util.short2Bytes(len));
		
		
		return byteBuffer.getBytes();
	
	
	}
	public static byte[] do_set_tempChargeNum(String epCode,byte maxNum) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2. 最大零时充电次数
		byteBuffer.put(maxNum);
		
		
		return byteBuffer.getBytes();
	
	
	}
	
    public static byte[] do_query_tempChargeNum(String epCode) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		
		return byteBuffer.getBytes();
	
	
	}

   /**
    * 
    * @param epCode 电桩编号
    * @param time   定时充电时间
    * @param timingChargeFlag  定时充电开关
    * @return
    */
    public static byte[] doIssuedTimingCharge(String epCode, String time, int timingChargeFlag) {
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		//定时充电时间（传2个字节）：第一个字节为时、第二个字节为分。
		byte times[] = new byte[2];
		times[0] = (byte) Integer.parseInt(time.substring(0, 2));
		times[1] = (byte) Integer.parseInt(time.substring(2));
		byteBuffer.put(times);
		
		byteBuffer.put((byte)timingChargeFlag);
		
		return byteBuffer.getBytes();
	}

    public static byte[] doIssuedWorkArg(String epCode, List<ElectricpileWorkarg> list) {
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		
		byteBuffer.put((byte)list.size());
		byte times[] = new byte[4];
		int id;
		
 		for (int i = 0; i < list.size(); i++) {
 			id = list.get(i).getArgId().intValue();
 			byteBuffer.put((byte)id);
 			if (id == 2) {
	 			times[0] = (byte) Integer.parseInt(list.get(i).getArgValue().substring(0, 2));
	 			times[1] = (byte) Integer.parseInt(list.get(i).getArgValue().substring(2));
	 			byteBuffer.put(times);
 			} else {
 				byteBuffer.put(WmIce104Util.int2Bytes(Integer.valueOf(list.get(i).getArgValue()).intValue()));
 			}
 		}
		
		return byteBuffer.getBytes();
	}
	public static byte[] doQueryEpInfo(String epCode){
        DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();

        // 1 ZDJQBM 终端机器编码 BCD码 8Byte 16位编码
        byteBuffer.put(WmIce104Util.str2Bcd(epCode));
        //2. 最大零时充电次数
        byteBuffer.put((byte) 2);
        byteBuffer.put((byte)21);
        byteBuffer.put((byte)22);
        return byteBuffer.getBytes();
    }
}
