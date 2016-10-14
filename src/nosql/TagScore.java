package nosql;

public class TagScore implements Comparable<TagScore>{
	
	private Integer score;
	private String tag;
	
	public TagScore(Integer score, String tag) {
		this.score = score;
		this.tag = tag;
	}

	public Integer getScore() {
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
	public int compareTo(TagScore t2) {
		return (this.getScore() > t2.getScore()) ? 1 : (this.getScore() < t2.getScore()) ? -1 : 0;
	}
}
