package com.clubank.domain;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.Vector;

public class IntegerArray extends Vector<Integer> implements KvmSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4880259519671152346L;

	public Object getProperty(int index) {
		return super.get(index);
	}

	public int getPropertyCount() {
		return size();
	}

	@SuppressWarnings("rawtypes")
	public void getPropertyInfo(int index, Hashtable properties,
			PropertyInfo info) {
		info.name = "int";
		info.type = Integer.class;
	}

	public void setProperty(int index, Object value) {
		add((Integer) value);
	}

}
