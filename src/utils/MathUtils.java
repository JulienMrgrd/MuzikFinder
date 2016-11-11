package utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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

	public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
	}
}
