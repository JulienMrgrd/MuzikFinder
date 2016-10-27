package utils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MathUtils {

	public static List<Integer> getNbIdMaxOfList(List<Integer> list, int nb){
		int id = 0;
		ArrayList<Integer> listId = new ArrayList<Integer>();
		Integer max = 0;
		int j = 0;
		while(j<nb){
			for(int i=0;i<list.size();i++){
				id=0;
				if(max <= list.get(i)){
					id = i;
					max = list.get(i);
				}
			}
			listId.add(id);
			list.remove(id);
			j++;
		}
		return listId;
	}

	public static int calculAge(Date dateBirth, Date dateNow){
		
		Calendar calBirth = Calendar.getInstance();
		calBirth.setTime(dateBirth);
		Calendar calNow = Calendar.getInstance();
		calNow.setTime(dateNow);

		if(calBirth.get(Calendar.MONTH)>calNow.get(Calendar.MONTH)){
			return (calNow.get(Calendar.YEAR)-calBirth.get(Calendar.YEAR))-1;
		}
		if(calBirth.get(Calendar.MONTH)<calNow.get(Calendar.MONTH)){
			return calNow.get(Calendar.YEAR)-calBirth.get(Calendar.YEAR);
		}
		if(calBirth.get(Calendar.DAY_OF_MONTH)<calNow.get(Calendar.DAY_OF_MONTH)){
			return (calNow.get(Calendar.YEAR)-calBirth.get(Calendar.YEAR))-1;
		}
		return calNow.get(Calendar.YEAR)-calBirth.get(Calendar.YEAR);
	}
}
