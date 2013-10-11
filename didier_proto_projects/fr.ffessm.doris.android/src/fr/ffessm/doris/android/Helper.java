package fr.ffessm.doris.android;

import java.util.Date;

public class Helper {

	public static String getCurrentDateString(){
		long millis = System.currentTimeMillis();
		return new Date(millis).toGMTString();
	}
	
}
