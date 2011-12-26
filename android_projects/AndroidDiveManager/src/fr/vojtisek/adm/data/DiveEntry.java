package fr.vojtisek.adm.data;

public class DiveEntry {
	private String date;
	private String location;
	private Integer maxdepth;
	private String duration;
	
	
	
	
	public DiveEntry(String date, String location, Integer maxdepth,
			String duration) {
		super();
		this.date = date;
		this.location = location;
		this.maxdepth = maxdepth;
		this.duration = duration;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Integer getMaxdepth() {
		return maxdepth;
	}
	public void setMaxdepth(Integer maxdepth) {
		this.maxdepth = maxdepth;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	

	
}
