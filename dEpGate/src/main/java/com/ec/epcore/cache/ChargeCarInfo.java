package com.ec.epcore.cache;


import com.ec.constants.YXCConstants;
import com.ec.net.proto.SingleInfo;
import com.ec.utils.NumUtil;

public class ChargeCarInfo {
	
	private int pkId;
	
	//17	电池类型	"1铅酸电池;2:皋氢电池3：磷酸铁锂电池；4锰酸锂电池;5：钴酸锂电池；6：三元材料电池；07聚合物锂离子电池；08钛酸锂电池"
	private short battryType;
	
	//18	整车动力蓄电池系统额定容量	0~1000A.h
	private int totalBattryCapacity;
	//19	电池组生产日期年	4位整数	
	private int battryMadeYear;
	//20	电池组生产日期月，日	月*100+日	
	private int battryMadeDay;
	//21	电池组充电次数
	private int battryChargeTimes;
	//22	单体蓄电池最高允许充电电压	精度0.1，单位v,倍数10
	private int signleAllowableHighVol;
	//23	最高允许充电电流
	private int signleAllowableHighCurrent;
	////24	动力蓄电池标称总能量	0.1kw/h，倍数10	
	private int totalBattryPower;
	//25	最高允许充电总电压(额定总电压)	精度0.1，单位v倍数10	
	private int allowableHighTotalVol;
	//26	最高允许温度	精度0.1，单位度(待定)，倍数10	
	private int allowableHighTotalTemp;
	
	private String batteryManufacturer;//电池厂商
	private String carIdenty;//车辆识别码
	
	private int change;
	
	private void init()
	{
		//17	电池类型	"1铅酸电池;2:皋氢电池3：磷酸铁锂电池；4锰酸锂电池;5：钴酸锂电池；6：三元材料电池；07聚合物锂离子电池；08钛酸锂电池"
		battryType=0;
		
		//18	整车动力蓄电池系统额定容量	0~1000A.h
		totalBattryCapacity=0;
		//19	电池组生产日期年	4位整数	
		battryMadeYear=1985;
		//20	电池组生产日期月，日	月*100+日	
		battryMadeDay=101;
		//21	电池组充电次数
		battryChargeTimes=0;
		//22	单体蓄电池最高允许充电电压	精度0.1，单位v,倍数10
		signleAllowableHighVol=0;
		//23	最高允许充电电流
		signleAllowableHighCurrent=0;
		////24	动力蓄电池标称总能量	0.1kw/h，倍数10	
		totalBattryPower=0;
		//25	最高允许充电总电压(额定总电压)	精度0.1，单位v倍数10	
		allowableHighTotalVol=0;
		//26	最高允许温度	精度0.1，单位度(待定)，倍数10	
		allowableHighTotalTemp=0;
		
		batteryManufacturer="";//电池厂商
		carIdenty="";//车辆识别码		
	}
	
	public ChargeCarInfo()
	{
		pkId=0;
		change=0;
		
		init();
	}
	
	
	
	public int getChange() {
		return change;
	}



	public void setChange(int change) {
		this.change = change;
	}



	public int getPkId() {
		return pkId;
	}



	public void setPkId(int pkId) {
		this.pkId = pkId;
	}



	public String getBatteryManufacturer() {
		return batteryManufacturer;
	}

	public int setBatteryManufacturer(String batteryManufacturer) {
		
        int ret=0;
		
		if(this.batteryManufacturer.compareTo(batteryManufacturer) != 0)
		{
			this.batteryManufacturer = batteryManufacturer;
			ret =1;
		}
		
		return ret;
	}

	public String getCarIdenty() {
		return carIdenty;
	}

	public int setCarIdenty(String carIdenty) {
		
        int ret=0;
		
		if(this.carIdenty.compareTo(carIdenty) != 0)
		{
			this.carIdenty = carIdenty;
			ret =1;
		}
		
		return ret;
		
	}



	public short getBattryType() {
		return battryType;
	}
    public String getBattryTypeDesc() {
		
		String desc="";
		switch(this.battryType)
        {
        case 1:
        	desc="铅酸电池";
        	 break;
        case 2:
        	desc="皋氢电池";
       	 	break;
        case 3:
        	desc="磷酸铁锂电池";
       	 	break;
        case 4:
        	desc="锰酸锂电池";
       	 	break;
        case 5:
        	desc="钴酸锂电池";
       	 	break;
        case 6:
        	desc="三元材料电池";
       	 	break;
        case 7:
        	desc="聚合物锂离子电池";
       	 	break;
        case 8:
        	desc="钛酸锂电池";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
     }

	public int setBattryType(short battryType) {
		int ret=0;
		if(battryType<1|| battryType>8)
			ret=-4;
		{
			if(this.battryType != battryType)
			{
				this.battryType = battryType;
				ret =1;
			}
		}
		
		return ret;
	}

	public int getTotalBattryCapacity() {
		return totalBattryCapacity;
	}

	public int setTotalBattryCapacity(int totalBattryCapacity) {
		int ret=0;
		
		if(this.totalBattryCapacity != totalBattryCapacity)
		{
			this.totalBattryCapacity = totalBattryCapacity;
			ret =1;
		}
		
		return ret;
	}

	public int getBattryMadeYear() {
		return battryMadeYear;
	}

	public int setBattryMadeYear(int battryMadeYear) {
		int ret=0;
		//2240 = （初始）1985+0xff
		if(battryMadeYear<1985 || battryMadeYear>=2240)
		{
			return -4;
		}
		if(this.battryMadeYear != battryMadeYear )
		{
			this.battryMadeYear = battryMadeYear;
			ret =1;
		}
		
		return ret;
	}

	public int getBattryMadeDay() {
		return battryMadeDay;
	}

	public int setBattryMadeDay(int battryMadeDay) {
		int ret=0;
		int month =battryMadeDay/100;
		int day = battryMadeDay%100;
		
		if((month<1 || month>12 )||
				(day<1 || day>31 ))	
		{
			return -4;
		}
		
		if(this.battryMadeDay != battryMadeDay )
		{
			this.battryMadeDay = battryMadeDay;
			ret =1;
		}
		
		return ret;
	}

	public int getBattryChargeTimes() {
		return battryChargeTimes;
	}

	public int setBattryChargeTimes(int battryChargeTimes) {
		int ret=0;
		if(battryChargeTimes<0 || battryChargeTimes>30000)
		{
			return -4;
		}
		if(this.battryChargeTimes != battryChargeTimes )
		{
			this.battryChargeTimes = battryChargeTimes;
			ret =1;
		}
		
		return ret;
	}

	public int getSignleAllowableHighVol() {
		return signleAllowableHighVol;
	}

	public int setSignleAllowableHighVol(int signleAllowableHighVol) {
		int ret=0;
		
		if(this.signleAllowableHighVol != signleAllowableHighVol)
		{
			this.signleAllowableHighVol = signleAllowableHighVol;
			ret =1;
		}
		
		return ret;
	}

	public int getSignleAllowableHighCurrent() {
		return signleAllowableHighCurrent;
	}

	public int setSignleAllowableHighCurrent(int signleAllowableHighCurrent) {
		int ret=0;
		
		if(this.signleAllowableHighCurrent != signleAllowableHighCurrent)
		{
			this.signleAllowableHighCurrent = signleAllowableHighCurrent;
			ret =1;
		}
		
		return ret;
	}

	public int getTotalBattryPower() {
		return totalBattryPower;
	}

	public int setTotalBattryPower(int totalBattryPower) {
		int ret=0;
		
		if(this.totalBattryPower != totalBattryPower)
		{
			this.totalBattryPower = totalBattryPower;
			ret =1;
		}
		
		return ret;
	}

	public int getAllowableHighTotalVol() {
		return allowableHighTotalVol;
	}

	public int setAllowableHighTotalVol(int allowableHighTotalVol) {
		int ret=0;
		
		if(this.allowableHighTotalVol != allowableHighTotalVol)
		{
			this.allowableHighTotalVol = allowableHighTotalVol;
			ret =1;
		}
		
		return ret;
	}

	public int getAllowableHighTotalTemp() {
		return allowableHighTotalTemp;
	}

	public int setAllowableHighTotalTemp(int allowableHighTotalTemp) {
		int ret=0;
		
		if(this.allowableHighTotalTemp != allowableHighTotalTemp)
		{
			this.allowableHighTotalTemp = allowableHighTotalTemp;
			ret =1;
		}
		return ret;
	}
	public int getYcIntValue(int addr)
	{
		
		switch(addr)
		{
	 	case YXCConstants.YC_BATTARY_TYPE://电池类型
		{
			return this.getBattryType();
		}
		
	    
	    case YXCConstants.YC_BATTARY_RATED_CAPACITY://整车动力蓄电池系统额定容量
		{
			return getTotalBattryCapacity();
		}
		
	    case YXCConstants.YC_BATTARY_MAKE_YEAR://电池组生产日期年
		{
			return getBattryMadeYear();
			
		}
		
	    case YXCConstants.YC_BATTARY_MAKE_DATE://电池组生产日期年
		{
			return getBattryMadeDay();
		}
		
	    case YXCConstants.YC_BATTARY_CHARGE_TIME://电池组生产日期年
		{
			return getBattryChargeTimes();
		}
		
	    case YXCConstants.YC_SIGNLE_BATTRY_CAN_HIGH_VOL://单体蓄电池最高允许充电电压
		{
			return getSignleAllowableHighVol();
			
		}
		
	    case YXCConstants.YC_SIGNLE_BATTRY_HIGH_CURRENT://最高允许充电电流
		{
			return getSignleAllowableHighCurrent();
			
		}
		
	    case YXCConstants.YC_BATTRY_TOTAL_POWER://动力蓄电池标称总能量
		{
			return getTotalBattryPower();
			
		}
		
	    case YXCConstants.YC_BATTRY_HIGH_VOL://最高允许充电总电压(额定总电压)
		{
			return getAllowableHighTotalVol();
			
		}
		
	    case YXCConstants.YC_BATTRY_CAN_HIGH_TEMP://最高允许温度
		{
			return getAllowableHighTotalTemp();
			
		}
	    
		}
		
		
		return 0;
	}

	public String getYcStrValue(int addr) {

		switch (addr) {

			case YXCConstants.YC_VAR_CAR_VIN://
			{
				return this.getCarIdenty();
			}
	
			case YXCConstants.YC_VAR_BATTARY_FACTORY:// 整车动力蓄电池系统额定容量
			{
				return this.getBatteryManufacturer();
			}
		}

		return "";
	}

	/////
	public int setYcValue(int addr,SingleInfo singleInfo)
	{
		
		switch(addr)
		{
	 	case YXCConstants.YC_BATTARY_TYPE://电池类型
		{
			return this.setBattryType((short)singleInfo.getIntValue());
		}
		
	    
	    case YXCConstants.YC_BATTARY_RATED_CAPACITY://整车动力蓄电池系统额定容量
		{
			return setTotalBattryCapacity(singleInfo.getIntValue());
		}
		
	    case YXCConstants.YC_BATTARY_MAKE_YEAR://电池组生产日期年
		{
			return setBattryMadeYear(singleInfo.getIntValue());
			
		}
		
	    case YXCConstants.YC_BATTARY_MAKE_DATE://电池组生产日期年
		{
			return setBattryMadeDay(singleInfo.getIntValue());
		}
		
	    case YXCConstants.YC_BATTARY_CHARGE_TIME://电池组生产日期年
		{
			return setBattryChargeTimes(singleInfo.getIntValue());
		}
		
	    case YXCConstants.YC_SIGNLE_BATTRY_CAN_HIGH_VOL://单体蓄电池最高允许充电电压
		{
			return setSignleAllowableHighVol(singleInfo.getIntValue());
			
		}
		
	    case YXCConstants.YC_SIGNLE_BATTRY_HIGH_CURRENT://最高允许充电电流
		{
			return setSignleAllowableHighCurrent(singleInfo.getIntValue());
			
		}
		
	    case YXCConstants.YC_BATTRY_TOTAL_POWER://动力蓄电池标称总能量
		{
			return setTotalBattryPower(singleInfo.getIntValue());
			
		}
		
	    case YXCConstants.YC_BATTRY_HIGH_VOL://最高允许充电总电压(额定总电压)
		{
			return setAllowableHighTotalVol(singleInfo.getIntValue());
			
		}
		
	    case YXCConstants.YC_BATTRY_CAN_HIGH_TEMP://最高允许温度
		{
			return setAllowableHighTotalTemp(singleInfo.getIntValue());
			
		}
	    case YXCConstants.YC_VAR_CAR_VIN://车辆识别码
		{
			return this.setCarIdenty(singleInfo.getStrValue());
		}
		
	    
	    case YXCConstants.YC_VAR_BATTARY_FACTORY://整车动力蓄电池系统额定容量
		{
			return this.setBatteryManufacturer(singleInfo.getStrValue());
		}
		}
		
		
		return 0;
	}
	public void endCharge()
	{
		init();
	}
	
	
	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder();
		
		 sb.append("输电池类型 = ").append(battryType).append(getBattryTypeDesc()).append("\n");
         sb.append("整车动力蓄电池系统额定容量 = ").append(totalBattryCapacity).append("\n");
         sb.append("电池组生产日期年 = ").append(battryMadeYear).append("\n");
         sb.append("电池组生产日期月，日	月*100+日 = ").append(battryMadeDay).append("\n");
         sb.append("电池组充电次数 = ").append(battryChargeTimes).append("\n");
         sb.append("单体蓄电池最高允许充电电压 = ").append(NumUtil.intToBigDecimal1(signleAllowableHighVol)).append("\n");
         sb.append("最高允许充电电流 = ").append(NumUtil.intToBigDecimal2(signleAllowableHighCurrent)).append("\n");
         sb.append("动力蓄电池标称总能量 = ").append(totalBattryPower).append("\n");
         sb.append("最高允许充电总电压(额定总电压) = ").append(NumUtil.intToBigDecimal1(allowableHighTotalVol)).append("\n");
         sb.append("最高允许温度  = ").append(NumUtil.intToBigDecimal1(allowableHighTotalTemp)).append("\n");
         sb.append("电池厂商 = ").append(batteryManufacturer).append("\n");
         sb.append("车辆识别码 = ").append(carIdenty).append("\n\r\n");   
		
		return sb.toString();
	}
	
	

}
