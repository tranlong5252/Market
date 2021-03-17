package me.manaki.plugin.market.util;

import java.text.DecimalFormat;

public class Utils {
	
	public static double round(double i) {
		DecimalFormat df = new DecimalFormat("#.##"); 
		String s = df.format(i).replace(",", ".");
		double newDouble = Double.valueOf(s);
		
		return newDouble;
	}
	
}
