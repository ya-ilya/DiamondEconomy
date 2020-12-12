package com.ilya.economy.util;

public class Util {

	public static boolean isNumeric(String str)  
	{  
	   try  
	   {  
	     int i = Integer.parseInt(str);  
	   }  
	   catch(NumberFormatException nfe)  
	   {  
	     return false;  
	   }  
	   return true;  
	}
}
