package com.clubank.domain;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Hashtable;

public class SoapData implements KvmSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	public Object getProperty(int arg0) {
		Field[] fields = getClass().getFields();
		try {
			return fields[arg0].get(this);
		} catch (Exception e) {
		}
		return null;
	}

	public int getPropertyCount() {
		Field[] fields = getClass().getFields();
		return fields.length;
	}

	@SuppressWarnings("rawtypes")
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {

		Field[] fields = getClass().getFields();

		arg2.type = fields[arg0].getType();
		arg2.name = fields[arg0].getName();
	}

	public void setProperty(int arg0, Object arg1) {
		Field[] fields = getClass().getFields();
		try {
			fields[arg0].set(this, arg1);
		} catch (Exception e) {
		}
	}
}
