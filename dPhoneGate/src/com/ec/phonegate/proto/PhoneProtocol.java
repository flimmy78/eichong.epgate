package com.ec.phonegate.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.net.proto.WmIce104Util;
import com.ec.utils.LogUtil;


public class PhoneProtocol{
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(PhoneProtocol.class.getName()));
	
	public static  byte[] Package(byte cos,short cmdtype) {
		
		try
		{
		PhoneHeader Header = new PhoneHeader();
		
		Header.setLength(3);

		ByteArrayOutputStream bmsg = new ByteArrayOutputStream( PhoneConstant.PHONE_SENDBUFFER);
		
		bmsg.write(Header.toByteArray());
		
		bmsg.write(cos);
		
		byte cmdtypeL = (byte)(cmdtype&0x00ff);		
		bmsg.write(cmdtypeL);
		
		byte cmdtypeH = (byte)((cmdtype>>8)&0x00ff);
		bmsg.write(cmdtypeH);
		
		return bmsg.toByteArray();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	public static  byte[] Package(byte[] data,byte cos,short cmdtype) 
	{
		
		try
		{
		PhoneHeader Header = new PhoneHeader();
		
		Header.setLength(3 + data.length);

		ByteArrayOutputStream bmsg = new ByteArrayOutputStream( PhoneConstant.PHONE_SENDBUFFER);
		
		bmsg.write(Header.toByteArray());
		
		bmsg.write(cos);
		
		byte cmdtypeL = (byte)(cmdtype&0x00ff);		
		bmsg.write(cmdtypeL);
		
		byte cmdtypeH = (byte)((cmdtype>>8)&0x00ff);
		bmsg.write(cmdtypeH);
		
		bmsg.write(data);
		
		return bmsg.toByteArray();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}

    public static byte[] do_confirm(short cmdtype,byte successflag,short errorcode){
		try
		{
			ByteArrayOutputStream bout = new ByteArrayOutputStream(PhoneConstant.PHONE_SENDBUFFER);
			
			//成功标识 BIN码 1Byte  1:成功0:失败
			bout.write(successflag);
			//成功标识 BIN码 1Byte  1:成功0:失败
			bout.write(WmIce104Util.short2Bytes(errorcode));
			
			
			return Package(bout.toByteArray(),(byte)1, cmdtype);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
		
	}
    
    public static byte[] do_connect_ep_resp(short cmdtype,byte successflag,short errorcode,short pos, short currentType) {
		try
		{
			logger.debug(LogUtil.addExtLog("cmdtype"),cmdtype);
			ByteArrayOutputStream bout = new ByteArrayOutputStream(PhoneConstant.PHONE_SENDBUFFER);
			
			//成功标识 BIN码 1Byte  1:成功0:失败
			bout.write(successflag);
			//2	错误编码	BIN码	2Byte	参见附录一	小端字节序
			bout.write(WmIce104Util.short2Bytes(errorcode));
			
			bout.write((byte)pos);
			
			if(cmdtype != 1)//新版本加交直流类型
			{
				bout.write((byte)currentType);
			}
			
			
			return Package(bout.toByteArray(),(byte)1, cmdtype);
		}
		catch(IOException e)
		{
			return null;
		}
		
	}
	
    public static  byte[] do_consume_record(short version,String chargeOrder,int st,int et,int totalMeterNum,int totalAmt,int serviceAmt,
    		int pkEpId,int userFirst,int CouPonAmt,int realCouPonAmt,int personalAmt,int chargeStyle) {
		
		try
		{
		ByteArrayOutputStream bout = new ByteArrayOutputStream(PhoneConstant.PHONE_SENDBUFFER);
		//1.订单编号
		bout.write(chargeOrder.getBytes());
		//2	开始时间
		bout.write(WmIce104Util.int2Bytes(st));
		//3.结束时间
		bout.write(WmIce104Util.int2Bytes(et));
		//4总度量
		bout.write(WmIce104Util.int2Bytes(totalMeterNum));
		//5总金额
		bout.write(WmIce104Util.int2Bytes(totalAmt));
		//6服务费
		bout.write(WmIce104Util.int2Bytes(serviceAmt));
		
		//7服务费
		bout.write(WmIce104Util.int2Bytes(pkEpId));
		if(version>=2)
		{
			//是否首次体验
			bout.write((byte)userFirst);
			//优惠券面额
			bout.write(WmIce104Util.int2Bytes(CouPonAmt));
			//优惠券抵扣金额
			bout.write(WmIce104Util.int2Bytes(realCouPonAmt));
			if (chargeStyle >= 0)  {
				bout.write((byte)chargeStyle);
			} else {
				//个性化优惠金额
				bout.write(WmIce104Util.int2Bytes(personalAmt));
			}
		}
		
	
		return Package(bout.toByteArray(),(byte)1,PhoneConstant.D_CONSUME_RECORD);
		}
		catch(IOException e)
		{
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
			return null;
		}
	}

    public static  byte[] do_start_charge_event(int state) 
	{
		byte[] value = new byte[1];
		value[0] = (byte)state;
		return Package(value,(byte)0,PhoneConstant.D_START_CHARGE_EVENT);
		
	}
    
    public static  byte[] do_real_charge_info(Map<String ,Object> realData) {
		try
		{
	
			ByteArrayOutputStream bout = new ByteArrayOutputStream(PhoneConstant.PHONE_SENDBUFFER);
		
			bout.write((byte)realData.get("workStatus"));
		
			bout.write(WmIce104Util.short2Bytes((short)realData.get("totalTime")));
			
			//System.out.print("getOutVol:"+chargingInfo.getOutVol()+"\n");
			
			bout.write(WmIce104Util.short2Bytes((short)realData.get("outVol")));
			bout.write(WmIce104Util.short2Bytes((short)realData.get("outCurrent")));
			
			//电桩传上来的值是精确到三位小数，1000倍，手机需要精确到小数点后两位，倍数100倍
			
			int meterNum = (int)realData.get("chargeMeterNum")/10;
			bout.write(WmIce104Util.int2Bytes(meterNum));
			short price = (short)realData.get("rateInfo");
			bout.write(WmIce104Util.short2Bytes(price));
			bout.write(WmIce104Util.int2Bytes((int)realData.get("fronzeAmt")));
			bout.write(WmIce104Util.int2Bytes((int)realData.get("chargeAmt")));
			bout.write((byte)realData.get("soc"));
			bout.write(WmIce104Util.int2Bytes((int)realData.get("deviceStatus")));
			bout.write(WmIce104Util.int2Bytes((int)realData.get("warns")));
		
			return Package(bout.toByteArray(),(byte)0,PhoneConstant.D_CHARGING_INFO);
		}
		catch(IOException e)
		{
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
			return null;
		}

	}
	
    public static  byte[] do_ep_net_status(int epNetStatus) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(PhoneConstant.PHONE_SENDBUFFER);

		bout.write((byte)epNetStatus);

		return Package(bout.toByteArray(),(byte)0,PhoneConstant.D_EP_NET_STATUS);

	}
    
    public static  byte[] do_gun2car_status(int status,byte type,byte[] time) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(PhoneConstant.PHONE_SENDBUFFER);

		try {
			bout.write((byte)status);
			bout.write(time);
			return Package(bout.toByteArray(),(byte)0,type);
		} catch (Exception e) {
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
			return null;
		}
	}		

    public static  byte[] do_cannel_spoke_ret(String epCode, byte epGunNo,byte successflag,short errorcode,String bespno,double FreezingMoney, double RealMoney) throws IOException {
		
		try
		{
		//assert(epCode.length()==EpProtocolConstant.LEN_CODE);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(PhoneConstant.PHONE_SENDBUFFER);
		//1	终端机器编码	BCD码	8Byte	16位编码
		bout.write(WmIce104Util.str2Bcd(epCode));
		
		//2	枪口	bin码	1Byte
		bout.write(epGunNo);
		
		//成功标识 BIN码 1Byte  1:成功0:失败
		bout.write(successflag);
		//错误编码 参见附录一 BIN码 2Byte
		bout.write(WmIce104Util.short2Bytes(errorcode));
		
		bout.write(bespno.getBytes());
		
		//冻结金额 BIN码 4Byte 精确到小数点后两位倍数100
		/*BigDecimal rate = new BigDecimal(EpProtocolConstant.MONEY_TIME);
		BigDecimal money = new BigDecimal(FreezingMoney);
		money = money.multiply(rate);
		int Amt = money.intValue();
	    bout.write(WmIce104Util.int2Bytes(Amt));
		
		//实际金额 BIN码 4Byte 精确到小数点后两位倍数100
	    rate = new BigDecimal(EpProtocolConstant.MONEY_TIME);
	    money = new BigDecimal(RealMoney);
		money = money.multiply(rate);
		Amt = money.intValue();
		bout.write(WmIce104Util.int2Bytes(Amt));
*/
		return Package(bout.toByteArray(),(byte)1,PhoneConstant.D_CANCEL_SPOKE);
		}
		catch(IOException e)
		{
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
			return null;
		}
	}
	
    public static  byte[] do_selftest_dc(byte status) {
		

    	byte[] bytes = new byte[1];
    	bytes[0] = status;
	
		return Package(bytes,(byte)0,PhoneConstant.D_SELF_TEST);
    	
	}

			
}

