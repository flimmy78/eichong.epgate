package com.ec.usrcore.cache;

import com.ec.utils.DateUtil;

public class ProtoVersion {
	
	private int version;//版本号
	private int count;//次数
	private String user;//最后使用者
	private long useTime;//最后使用时间

	public ProtoVersion(int version, String user){
		this.version = version;
		this.count = 1;
		this.user = user;
		useTime = DateUtil.getCurrentSeconds();
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getCount() {
		return count;
	}

	public void setUser(int count) {
		this.count = count;
	}

	public void addCount(String user) {
		this.count += 1;
		this.user = user;
		useTime = DateUtil.getCurrentSeconds();
	}

	public void minusCount() {
		this.count -= 1;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getLoginTime() {
		return useTime;
	}

	public void setLoginTime(long loginTime) {
		this.useTime = loginTime;
	}

	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("Version:\n");
      
        sb.append("{版本号 = ").append(version).append("\n");
		sb.append("连接数 = ").append(count).append("\n");
		sb.append("最后使用者 = ").append(user).append("\n");
        String sTime= DateUtil.StringYourDate(DateUtil.toDate(useTime*1000));
        sb.append("最后使用时间 = ").append(sTime).append("}\n");
        
   		return sb.toString();
	}

}
