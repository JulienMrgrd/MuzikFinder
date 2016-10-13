package server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import nosql.TagScore;

public class Search {

	public static void searchbyTag(List<String> tag){

		MuzikFinderService mf = new MuzikFinderService();

		TreeSet<TagScore> tagScore = new TreeSet<TagScore>();
		String s;
		String[] split;
		Boolean presentInList = false;
		for(String tmp:tag){

			s=mf.getMusicByTagInNoSQL(tmp);
			split = s.split(";");
			System.out.println("s = "+s);
			for(String tmpSplit : split){
				for(TagScore ts:tagScore){
					if(ts.getTag().equals(tmpSplit)){
						ts.setScore(ts.getScore()+1);
						presentInList=true;
						break;
					}
				}
				if(!presentInList){
					tagScore.add(new TagScore(new Integer(1),tmpSplit));
				}
			}
		}

		for(TagScore ts:tagScore){
			System.out.println(ts.getTag()+" +++ "+ts.getScore());
		}
		
		//int maxValue=(Collections.max(idMusicCpt.values())); 
		//TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(idMusicCpt);
		//System.out.println(sorted_map);
		
		/*	ArrayList<String> res = sortedList(idMusicCpt);
		System.out.println(res);
		System.out.println("1 = "+idMusicCpt.get("1"));
		System.out.println("2 = "+idMusicCpt.get("2"));
		System.out.println("3 = "+idMusicCpt.get("3"));*/
	}
	
	public static ArrayList<String> sortedList(HashMap<String,Integer> map){
		ArrayList<String> keySet = new ArrayList<String>(map.keySet());
		ArrayList<Integer> valueSet = new ArrayList<Integer>(map.values());
		ArrayList<String> res = new ArrayList<String>();
		
		while(!valueSet.isEmpty()){
			int index = getIndexMaxValue(valueSet);
			res.add(keySet.get(index));
			valueSet.remove(index);
			keySet.remove(index);
		}
		return res;
	}
	
	public static int getIndexMaxValue(ArrayList<Integer> list){
		Integer val = new Integer(0);
		int index = 0;
		int i=0;
		for(Integer value : list){
			if(val<value){
				val = value;
				index = i;
			}
			i++;
		}
		return index;
	}

}
