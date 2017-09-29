package util;

import java.util.Arrays;

public class Measures {
	public static long getMin(long[] measures) {
		long min = measures[0];
	    for (int i = 0; i < measures.length; i++) {
	        if (measures[i] < min) {
	            min = measures[i];
	        }
	    }
	    return min;
	}
	public static long getMax(long[] measures) {
		long max = measures[0];
	    for (int i = 0; i < measures.length; i++) {
	        if (measures[i] > max) {
	            max = measures[i];
	        }
	    }
	    return max;
	}
	public static double getAverage(long[] measures) {
		double sum = 0;
	    for (int i = 0; i < measures.length; i++) {
	        sum += measures[i];
	    }
	    return sum / measures.length;
	}
	public static double getMedian(long[] measures) {
		Arrays.sort(measures);
		if (measures.length % 2 == 0)
			return ((double)measures[measures.length/2] + (double)measures[measures.length/2 - 1])/2;
		else
		    return (double) measures[measures.length/2];
	}
	public static long getNumberOfQueries(long[] measures) {
		return measures.length;
	}
	public static String getAsJSON(long[] measures) {
	    if (null == measures || 0 == measures.length) return "";

	    StringBuilder sb = new StringBuilder(256);
	    sb.append(measures[0]);

	    for (int i = 1; i < measures.length; i++) {
	    	sb.append(",").append(measures[i]);
	    }

	    return "[" + sb.toString() + "]";
	}
}
