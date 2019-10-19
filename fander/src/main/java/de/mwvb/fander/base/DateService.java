package de.mwvb.fander.base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateService {
	
	public static String now() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
	}
}
