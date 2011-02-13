package nz.gen.wellington.guardian.contentapi.dates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContentApiDateHelper {
	
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	public static Date parseDate(String dateString) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		 try {
			 if (dateString.endsWith("Z")) {
				 dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				 dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
			 }		 
			 return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

}
