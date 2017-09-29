package util;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;

public class Logger {
	private static PrintStream out = System.out;
	public static void log(String message) {
		LocalDateTime time = LocalDateTime.now();
		out.print(MessageFormat.format("[{0,number,#}-{1}-{2} {3}:{4}:{5}]",time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond()));
		out.println(message);
	}
}
