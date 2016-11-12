package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class MathUtils {

	final static String DATE_FORMAT = "dd-MM-yyyy";
	
	public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
	}
	
	public static boolean isDateValid(int day, int month, int year){
		String date =day+"-"+month+"-"+year;
		Date dateNow = new Date();
		LocalDate localDate = dateNow.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		System.out.println(localDate);
		if(year>localDate.getYear()) return false;
		if(year==localDate.getYear()){
			if(month>localDate.getMonthValue()) return false;
			if(month==localDate.getMonthValue()) 
				if(day>localDate.getDayOfMonth()) return false;
		}
		System.out.println("on passe la");
		/*int year  = localDate.getYear();
		int month = localDate.getMonthValue();
		int day   = localDate.getDayOfMonth();*/
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
	}
}