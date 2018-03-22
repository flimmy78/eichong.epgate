package com.ormcore.model;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 
 * tbl_RateInformation表
 * @author mew
 *
 */
public class RateInfo  implements Serializable{
	private java.lang.Integer id; // 主键
	private java.lang.Integer modelId; // 计费模型ID
	private java.math.BigDecimal freezingMoney; // 预冻结金额
	private java.math.BigDecimal minFreezingMoney; // 最小冻结金额
	private java.lang.String quantumDate; // 采用JSON形式存储实际格式
	private java.math.BigDecimal j_Rate; // 尖时段电价
	private java.math.BigDecimal f_Rate; // 峰时段电价
	private java.math.BigDecimal p_Rate; // 平时段电价
	private java.math.BigDecimal g_Rate; // 谷时段电价
	
	private java.math.BigDecimal bespokeRate; // 预约费率
	private java.math.BigDecimal ServiceRate; // 服务费
	
	private java.math.BigDecimal WarnAmt; //
    private java.math.BigDecimal j_RateMoney; // 尖时段服务费
    private java.math.BigDecimal f_RateMoney; // 峰时段服务费
    private java.math.BigDecimal p_RateMoney; // 平时段服务费
    private java.math.BigDecimal g_RateMoney; // 谷时段服务费

	public RateInfo()
	{
		ServiceRate= new BigDecimal(0.0);
	}
	
	public java.math.BigDecimal getWarnAmt() {
		return WarnAmt;
	}

	public void setWarnAmt(java.math.BigDecimal warnAmt) {
		WarnAmt = warnAmt;
	}



	public java.lang.Integer getId() {
		return id;
	}



	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.Integer getModelId() {
		return modelId;
	}

	public void setModelId(java.lang.Integer modelId) {
		this.modelId = modelId;
	}

	public java.math.BigDecimal getFreezingMoney() {
		return freezingMoney;
	}



	public void setFreezingMoney(java.math.BigDecimal freezingMoney) {
		this.freezingMoney = freezingMoney;
	}



	public java.math.BigDecimal getMinFreezingMoney() {
		return minFreezingMoney;
	}



	public void setMinFreezingMoney(java.math.BigDecimal minFreezingMoney) {
		this.minFreezingMoney = minFreezingMoney;
	}



	public java.lang.String getQuantumDate() {
		return quantumDate;
	}



	public void setQuantumDate(java.lang.String quantumDate) {
		this.quantumDate = quantumDate;
	}



	public java.math.BigDecimal getJ_Rate() {
		return j_Rate;
	}



	public void setJ_Rate(java.math.BigDecimal j_Rate) {
		this.j_Rate = j_Rate;
	}



	public java.math.BigDecimal getF_Rate() {
		return f_Rate;
	}



	public void setF_Rate(java.math.BigDecimal f_Rate) {
		this.f_Rate = f_Rate;
	}



	public java.math.BigDecimal getP_Rate() {
		return p_Rate;
	}



	public void setP_Rate(java.math.BigDecimal p_Rate) {
		this.p_Rate = p_Rate;
	}

	public java.math.BigDecimal getBespokeRate() {
		return bespokeRate;
	}



	public void setBespokeRate(java.math.BigDecimal bespokeRate) {
		this.bespokeRate = bespokeRate;
	}

	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Rateinfo:\n");
        sb.append("{费率ID=").append(id).append("}\n");
       
        sb.append(",{预冻结金额=").append(freezingMoney).append("}\n");
        sb.append(",{最小冻结金额=").append(minFreezingMoney).append("}\n");
        sb.append(",{ Quantumdate=").append(quantumDate).append("}\n");
        sb.append(",{尖时段电价=").append(j_Rate).append("}\n");
        sb.append(",{峰时段电价=").append(f_Rate).append("}\n");
        sb.append(",{平时段电价=").append(p_Rate).append("}\n");
        sb.append(",{谷时段电价=").append(g_Rate).append("}\n");
        sb.append(",{预约费率=").append(bespokeRate).append("}\n");
        sb.append(",{服务费=").append(ServiceRate);
		sb.append('}');
        return sb.toString();
    }



	public java.math.BigDecimal getG_Rate() {
		return g_Rate;
	}



	public void setG_Rate(java.math.BigDecimal g_Rate) {
		this.g_Rate = g_Rate;
	}



	public java.math.BigDecimal getServiceRate() {
		return ServiceRate;
	}



	public void setServiceRate(java.math.BigDecimal serviceRate) {
		ServiceRate = serviceRate;
	}

    public BigDecimal getJ_RateMoney() {
        return j_RateMoney;
    }

    public void setJ_RateMoney(BigDecimal j_RateMoney) {
        this.j_RateMoney = j_RateMoney;
    }

    public BigDecimal getF_RateMoney() {
        return f_RateMoney;
    }

    public void setF_RateMoney(BigDecimal f_RateMoney) {
        this.f_RateMoney = f_RateMoney;
    }

    public BigDecimal getP_RateMoney() {
        return p_RateMoney;
    }

    public void setP_RateMoney(BigDecimal p_RateMoney) {
        this.p_RateMoney = p_RateMoney;
    }

    public BigDecimal getG_RateMoney() {
        return g_RateMoney;
    }

    public void setG_RateMoney(BigDecimal g_RateMoney) {
        this.g_RateMoney = g_RateMoney;
    }
}