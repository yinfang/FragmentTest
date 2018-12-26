package com.clubank.domain;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListObject {
	public ListView listView;
	public ArrayAdapter<?> adapter;
	public Criteria criteria;
	public int noDataTip;// res
	public View footer;
	public boolean hasMore;

}
