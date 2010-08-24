package de.phleisch.app.itsucks.util;

public class StringUtils {

	public static String trimLeadingWhitespace(String str) {
		if(str == null) {
			return str;
		}
		
    	int len = str.length();
    	char[] val = str.toCharArray();

    	while (val[len - 1] <= ' ') {
    	    len--;
    	}
    	return (len < str.length()) ? str.substring(0, len) : str;
	}

	
}
