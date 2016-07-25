package com.kiven.kutils.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KDate {
	/**
	 * 两个时间是否是同一天
	 */
	public static boolean isSameDay(Date day1, Date day2){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    
	    return ds1.equals(ds2);
	}

}
