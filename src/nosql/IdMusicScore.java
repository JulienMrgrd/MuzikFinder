package nosql;

public class IdMusicScore implements Comparable<IdMusicScore>{
	
	private Integer score;
	private String idMusic;
	
	public IdMusicScore(String tag, Integer score) {
		this.score = score;
		this.idMusic = tag;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getIdMusic() {
		return idMusic;
	}

	public void setIdMusic(String idMusic) {
		this.idMusic = idMusic;
	}

	@Override
	public int compareTo(IdMusicScore t2) {
		return (this.getScore() > t2.getScore()) ? -1 : (this.getScore() < t2.getScore()) ? 1 : 0;
	}
}
