package com.clubank.domain;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.Vector;

public class StringArray extends Vector<String> implements
        KvmSerializable {
	private static final long serialVersionUID = -1166006770093411055L;

	public Object getProperty(int index) {
		return super.get(index);
	}

	public int getPropertyCount() {
		return size();
	}

	@SuppressWarnings("rawtypes")
	public void getPropertyInfo(int index, Hashtable properties,
			PropertyInfo info) {
		info.name = "string";
		info.type = String.class;
	}

	public void setProperty(int index, Object value) {
		add((String) value);
	}

}
