package utils;

public enum TimeInMilliSeconds {
	HOUR(3600000),
	WEEK(604800000),
	MONTH((31556952L / 12)*1000);
	
	public long value;
	
	private TimeInMilliSeconds(long value){
		this.value = value;
	}
}
