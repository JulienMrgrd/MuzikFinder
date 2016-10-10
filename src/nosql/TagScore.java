package nosql;

import java.util.Comparator;

public class TagScore implements Comparator<TagScore>{
	
	private int score;
	private String tag;
	
	public TagScore(int score, String tag) {
		this.score = score;
		this.tag = tag;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public int compare(TagScore t1, TagScore t2) {
		return (t1.getScore() >= t2.getScore()) ? 1 : (t1.getScore() < t2.getScore()) ? -1 : 0;
	}

}
