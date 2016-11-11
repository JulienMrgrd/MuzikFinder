package utils;

import org.bson.Document;

import nosql.mongo.MongoCollectionsAndKeys;

public class IdMusicScore implements Comparable<IdMusicScore>{
	
	private Integer score;
	private String idMusic;
	private int nbOccur;
	
	
	public IdMusicScore(String tag, Integer score) {
		this.score = score;
		this.idMusic =tag;
		this.nbOccur = 0;
	}
	
	public IdMusicScore(String tag, Integer score, int nbOccur) {
		this.score = score;
		this.idMusic = tag;
		this.nbOccur = nbOccur;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void incrementScore() {
		this.score++;
	}

	public String getIdMusic() {
		return idMusic;
	}

	public void setIdMusic(String idMusic) {
		this.idMusic = idMusic;
	}
	
	public int getNbOccur() {
		return nbOccur;
	}

	public void setNbOccur(int nbOccur) {
		this.nbOccur = nbOccur;
	}

	@Override
	public int compareTo(IdMusicScore t2) {
		if(this.getScore() > t2.getScore()){
			return -1;
		}else if (this.getScore() < t2.getScore()){
			return 1;
		}else{
			if(this.getNbOccur() > t2.getNbOccur()){
				return -1;
			}else if(this.getNbOccur() < t2.getNbOccur()){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	public Document idMusicScoreToDoc(){
		Document doc = new Document();
		doc.put(MongoCollectionsAndKeys.MUSICID_TAGS, this.getIdMusic());
		doc.put(MongoCollectionsAndKeys.SCORE_TAGS, this.getScore());
		return doc;
	}
}
