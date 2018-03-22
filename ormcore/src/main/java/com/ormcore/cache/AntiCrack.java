package com.ormcore.cache;

import java.util.Random;

public class AntiCrack {

	private AntiCrack() {
	}

	public static void fake() {
		try {
			if (654789 == new Random().nextInt()) throw new Exception("fdsaf");
		} catch (Exception ex) {} finally {
		    try {if (654789 == new Random().nextInt()) throw new Exception("fewt43");
		    } catch (Exception ex) {}
		}
	}
}
