package com.ec.epcore.task;




public class ChinaMobileTask  implements Runnable {
	private int tokenType;
	public ChinaMobileTask(int type)
	{
		tokenType = type;
		
	}
	@Override
	public void run() {
		//System.out.print("CheckRateTask-----------------------");
		
		//ChinaMobileService.fetchToken(tokenType);
	}

}
