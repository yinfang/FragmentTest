package com.clubank.domain;

public class IconMenu {
	public int resId;
	public String name;

	public IconMenu(String name) {
		this.name = name;
	}

	public IconMenu(int resId, String name) {
		this.resId = resId;
		this.name = name;
	}

}
