package com.sunny.main;

public enum Month {

	JANUARY("一月", "January", "Jan", 1),
	FEBRUARY("二月", "February", "Feb.", 2),
	MARCH("三月", "March", "Mar.", 3),
	APRIL("四月", "April", "Apr.", 4),
	MAY("五月", "May", "May.", 5),
	JUNE("六月", "June", "Jun.", 6),
	JULY("七月", "July", "Jul.", 7),
	AUGUST("八月", "August", "Aug", 8),
	SEPTEMBER("九月", "September", "Sep.", 9),
	OCTOBER("十月", "October", "Oct.", 10),
	NOVEMBER("十一月", "November", "Nov.", 11),
	DECEMBER("十二月", "December", "Dec.", 12);
	
	String name_cn;
	String name_en;
	String name_enShort;
	int number;
	
	Month(String name_cn, String name_en, String name_enShort, int number) {
		this.name_cn = name_cn;
		this.name_en = name_en;
		this.name_enShort = name_enShort;
		this.number = number;
	}
	
	public String getChineseName() {
		return name_cn;
	}

	public String getName() {
		return name_en;
	}

	public String getShortName() {
		return name_enShort;
	}

	public int getNumber() {
		return number;
	}
}