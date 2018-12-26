package com.clubank.domain;

public class Criteria extends SoapData {
	private static final long serialVersionUID = 1L;
	public int PageSize = 20;
	public int PageIndex = C.PageIndex; // from 1
	public String OrderKey = "";
	public int Distance = -1;// km

	public double Latitude;// 经度
	public double Longitude;// 纬度
}
