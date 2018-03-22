package com.ec.epcore.cache;

import com.ec.utils.NumUtil;


public class ChargePowerModInfo {
	
	private int pkId;
	
	private int modId;
	
	private int outVol;
	private int outCurrent;
	private int aVol;
	private int bVol;
	private int cVol;
	
	private int aCurrent;
	private int bCurrent;
	private int cCurrent;
	
	private int temp;
	
	private int updateTime;//5分钟后取消.
	
	private int change;
	
	public ChargePowerModInfo()
	{
		pkId =0;
		
		modId=0;
		
		change=0;
		
		init();
	}
	private void init()
	{
		outVol=0;
		outCurrent=0;
		aVol=0;
		bVol=0;
		cVol=0;
		
		aCurrent=0;
		bCurrent=0;
		cCurrent=0;
		
		temp=0;
		
	}
	public void setChange(int change)
	{
		this.change=change;
	}
	public int getChange()
	{
		return this.change;
	}

	public int getPkId() {
		return pkId;
	}

	public void setPkId(int pkId) {
		this.pkId = pkId;
	}

	
	public int getModId() {
		return modId;
	}

	public void setModId(int modId) {
		this.modId = modId;
	}

	public int getOutVol() {
		return outVol;
	}

	public int  setOutVol(int outVol) {
		if(this.outVol == outVol)
		{
			return 0;
		}
		this.outVol= outVol;
		return 1;
	}

	public int getOutCurrent() {
		return outCurrent;
	}

	public int setOutCurrent(int outCurrent) {
		
		if(this.outCurrent == outCurrent)
		{
			return 0;
		}
		this.outCurrent= outCurrent;
		return 1;
	}

	public int getaVol() {
		return aVol;
	}

	public int setaVol(int aVol) {

		if(this.aVol == aVol)
		{
			return 0;
		}
		this.aVol= aVol;
		return 1;
	}

	public int getbVol() {
		return bVol;
	}

	public int setbVol(int bVol) {
		
		if(this.bVol == bVol)
		{
			return 0;
		}
		this.bVol= bVol;
		return 1;
	}

	public int getcVol() {
		return cVol;
	}

	public int setcVol(int cVol) {
		if(this.cVol == cVol)
		{
			return 0;
		}
		this.cVol= cVol;
		return 1;
	}

	public int getaCurrent() {
		return aCurrent;
	}

	public int setaCurrent(int aCurrent) {
		
		if(this.aCurrent == aCurrent)
		{
			return 0;
		}
		this.aCurrent= aCurrent;
		return 1;
	}

	public int getbCurrent() {
		return bCurrent;
	}

	public int setbCurrent(int bCurrent) {
		if(this.bCurrent == bCurrent)
		{
			return 0;
		}
		this.bCurrent= bCurrent;
		return 1;
	}

	public int getcCurrent() {
		return cCurrent;
	}

	public int setcCurrent(int cCurrent) {
		if(this.cCurrent == cCurrent)
		{
			return 0;
		}
		this.cCurrent= cCurrent;
		return 1;
	}

	public int getTemp() {
		return temp;
	}

	public int setTemp(int temp) {
		if(this.temp == temp)
		{
			return 0;
		}
		this.temp= temp;
		return 1;
	}
	
	public int setYcValue(int addr,int value)
	{
		if(addr<1 || addr>9)
			return -2;
		switch(addr)
		{
		case 1:
			return this.setOutVol(value);
		case 2:
			return this.setOutCurrent(value);
		case 3:
			return this.setaVol(value);
		case 4:
			return this.setbVol(value);
		case 5:
			return this.setcVol(value);
		case 6:
			return this.setaCurrent(value);
		case 7:
			return this.setbCurrent(value);
		case 8:
			return this.setcCurrent(value);
		default:
			return this.setTemp(value);
		
			
		
		}
		
	}
	public void endCharge()
	{
		init();
	}
	
	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder();
		
		
		sb.append("模块pkId = ").append(pkId).append("\n");
		sb.append("模块号 = ").append(modId).append("\n");
		
		sb.append("输出电压 = ").append(NumUtil.intToBigDecimal1(outVol)).append("\n");
		sb.append("输出电流 = ").append(NumUtil.intToBigDecimal2(outCurrent)).append("\n");
		sb.append("A相电压 = ").append(NumUtil.intToBigDecimal1(aVol)).append("\n");
		sb.append("B相电压 = ").append(NumUtil.intToBigDecimal1(bVol)).append("\n");
		sb.append("C相电压 = ").append(NumUtil.intToBigDecimal1(cVol)).append("\n");
		sb.append("A相电流 = ").append(NumUtil.intToBigDecimal2(aCurrent)).append("\n");
		sb.append("B相电流 = ").append(NumUtil.intToBigDecimal2(bCurrent)).append("\n");
		sb.append("C相电流 = ").append(NumUtil.intToBigDecimal2(cCurrent)).append("\n");
		sb.append("模块温度 = ").append(NumUtil.intToBigDecimal1(temp)).append("\n\r\n");
		
		return sb.toString();
	}
	
	
}
