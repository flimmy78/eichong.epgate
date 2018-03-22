package com.ec.usrcore.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtoVersionStat {
	
	private  Map<Integer, ProtoVersion> versionMap = new ConcurrentHashMap<Integer, ProtoVersion>();

	public  void addVersion(int version, String  user)
	{
		ProtoVersion protocolVersion = versionMap.get(version);
		if (protocolVersion == null) {
			protocolVersion = new ProtoVersion(version, user);
		} else {
			protocolVersion.addCount(user);
		}
		versionMap.put(protocolVersion.getVersion(), protocolVersion);
	}

    public void offProtocol(int version)
	{
		ProtoVersion userVersion = versionMap.get(version);
		if (userVersion != null) {
			userVersion.minusCount();
			//if (userVersion.getCount() == 0) versionMap.remove(version);
		}
	}
    @Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
       
      
        sb.append("count = ").append(versionMap.size()).append("\n");
        Iterator iter = versionMap.entrySet().iterator();
		int index=1;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ProtoVersion o=(ProtoVersion) entry.getValue();
			if(o==null)
				continue;
			
			sb.append(""+index).append("st ").append(o).append("\n");	
			index++;
		}
        
   		return sb.toString();
	} 
}
