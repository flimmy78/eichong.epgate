package com.ec.epcore.cache;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.config.Global;
import com.ec.net.proto.TimeStage;
import com.ec.net.proto.WmIce104Util;
import com.ec.utils.LogUtil;
import com.ormcore.model.RateInfo;


/**
 * 
 * tbl_RateInformation表
 * @author mew
 *
 */
public class RateInfoCache {
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(RateInfoCache.class.getName()));

	private RateInfo rateInfo;

	public RateInfo getRateInfo() {
		return rateInfo;
	}

	public void setRateInfo(RateInfo rateInfo) {
		this.rateInfo = rateInfo;
	}
	
	private ArrayList<TimeStage> timeStageList = new ArrayList<TimeStage>();
	
	private byte[] comm_data;

	public byte[] getComm_data() {
		return comm_data;
	}

	public ArrayList<TimeStage> getTimeStageList() {
		return timeStageList;
	}

	private java.lang.Integer modelId = 0;

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public boolean parseStage() {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(256);

			bout.write(WmIce104Util.int2Bytes(rateInfo.getId()));
			// 3 生效日期 BIN码 7Byte 年月日
			byte[] date = new byte[] { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
			bout.write(date);

			// 2015年1月29日
			// 4 失效日期 BIN码 7Byte
			bout.write(date);

			byte[] bPrepareFrozenAmt = WmIce104Util.int2Bytes((int) (
					rateInfo.getFreezingMoney().multiply(Global.DecTime2).doubleValue()));

			bout.write(bPrepareFrozenAmt);

			byte[] bMinFrozenAmt = WmIce104Util.int2Bytes((int) (
					rateInfo.getMinFreezingMoney().multiply(Global.DecTime2).doubleValue()));
			bout.write(bMinFrozenAmt);

			JSONObject jb = JSONObject.fromObject(rateInfo.getQuantumDate());
			JSONArray ja = jb.getJSONArray("data");

			timeStageList.clear();

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
				bout.write(ts.toByteArray());
			}

			BigDecimal dec;
			if (rateInfo.getModelId() == 1) {
				dec = Global.DecTime3;
			} else {
				dec = Global.DecTime4;
			}
			byte[] b_j_rate = WmIce104Util.int2Bytes((int) (
					rateInfo.getJ_Rate().multiply(dec).doubleValue()));
			bout.write(b_j_rate);
            if (rateInfo.getModelId() == 3) {
                byte[] b_j_rate_Money = WmIce104Util.int2Bytes((int) (
                        rateInfo.getJ_RateMoney().multiply(dec).doubleValue()));
                bout.write(b_j_rate_Money);
            }

			byte[] b_f_rate = WmIce104Util.int2Bytes((int) (
					rateInfo.getF_Rate().multiply(dec).doubleValue()));
			bout.write(b_f_rate);
            if (rateInfo.getModelId() == 3) {
                byte[] b_f_rate_Money = WmIce104Util.int2Bytes((int) (
                        rateInfo.getF_RateMoney().multiply(dec).doubleValue()));
                bout.write(b_f_rate_Money);
            }

			byte[] b_p_rate = WmIce104Util.int2Bytes((int) (
					rateInfo.getP_Rate().multiply(dec).doubleValue()));
			bout.write(b_p_rate);
            if (rateInfo.getModelId() == 3) {
                byte[] b_p_rate_Money = WmIce104Util.int2Bytes((int) (
                        rateInfo.getP_RateMoney().multiply(dec).doubleValue()));
                bout.write(b_p_rate_Money);
            }

			byte[] b_g_rate = WmIce104Util.int2Bytes((int) (
					rateInfo.getG_Rate().multiply(dec).doubleValue()));
			bout.write(b_g_rate);
            if (rateInfo.getModelId() == 3) {
                byte[] b_g_rate_Money = WmIce104Util.int2Bytes((int) (
                        rateInfo.getG_RateMoney().multiply(dec).doubleValue()));
                bout.write(b_g_rate_Money);
            }

			byte[] b_ordering_rate = WmIce104Util.int2Bytes((int) (
					rateInfo.getBespokeRate().multiply(Global.DecTime3).doubleValue()));
			bout.write(b_ordering_rate);

			byte[] b_service_rate = WmIce104Util.int2Bytes((int) (
					rateInfo.getServiceRate().multiply(dec).doubleValue()));
			bout.write(b_service_rate);

			byte[] b_warn_amt = WmIce104Util.int2Bytes((int) (
					rateInfo.getWarnAmt().multiply(Global.DecTime3).doubleValue()));
			bout.write(b_warn_amt);

			comm_data = bout.toByteArray();

			return true;
		} catch (Exception e) {
			comm_data = null;
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
			return false;
		}
		
	}
	
	
}