package com.clubank.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

public class DB {
	MyDBHelper helper;

	public DB(MyDBHelper helper) {
		this.helper = helper;
	}

	public MyData getData(String sql) {
		return getData(sql, new String[] {});
	}

	public MyData getData(String sql, String[] args) {
		return getData(sql, args, null);
	}

	public MyData getData(String sql, String[] args, String[] blob) {
		MyData data = new MyData();
		// MyDBHelper helper = new MyDBHelper(context, C.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();

		try {
			Cursor c = db.rawQuery(sql, args);

			c.moveToFirst();
			while (!c.isAfterLast()) {
				MyRow row = new MyRow();
				for (int i = 0; i < c.getColumnCount(); i++) {
					String name = c.getColumnName(i);
					int n = name.indexOf('.');
					if (n >= 0) {
						name = name.substring(n + 1);
					}

					if (blob != null && Arrays.asList(blob).indexOf(name) >= 0) {
						byte[] value = c.getBlob(i);
						if (value != null) {
							row.put(name, c.getBlob(i));
						}
					} else {
						String value = c.getString(i);
						if (value != null) {
							row.put(name, value);
						}
					}
				}
				data.add(row);
				c.moveToNext();
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.close();
		}
		return data;
	}

	public MyRow getByKey(String sql, String key) {
		MyData data = getData(sql, new String[] { key });
		if (data.size() > 0) {
			return data.get(0);
		}
		return null;
	}

	public long update(String table, ContentValues cv, String condition, String[] conditionValues) {
		// MyDBHelper helper = new MyDBHelper(context, C.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();

		long n = db.update(table, cv, condition, conditionValues);
		db.close();
		return n;
	}

	public void exec(String sql) {
		// MyDBHelper helper = new MyDBHelper(context, C.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.execSQL(sql);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exec(String sql, Object[] args) {
		// MyDBHelper helper = new MyDBHelper(context, C.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.execSQL(sql, args);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long insert(String table, ContentValues cv) {
		// MyDBHelper helper = new MyDBHelper(context, C.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();

		long n = db.insert(table, null, cv);
		db.close();
		return n;
	}

	// android点餐安吉轩版本获取做法 c.PageSize = 2000 卡顿问题,存入本地数据库方法耗时过长 ，改为事务操作
	public int diningInsert(String tablename, List<ContentValues> values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			int numValues = values.size();
			for (int i = 0; i < numValues; i++) {
				db.insert(tablename, null, values.get(i));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
		return values.size();
	}

	public long delete(String table, String where, String[] args) {
		// MyDBHelper helper = new MyDBHelper(context, C.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		long n = 0;
		try {
			n = db.delete(table, where, args);
		} catch (Exception e) {
			e.printStackTrace();

		}
		db.close();
		return n;
	}

	public static ContentValues getContent(MyRow row) {
		ContentValues cv = new ContentValues();
		for (String key : row.keySet()) {
			cv.put(key, row.get(key).toString());
		}
		return cv;
	}

	public static ContentValues getContent(MyRow row, String[] exclude) {
		ContentValues cv = new ContentValues();
		for (String key : row.keySet()) {
			if (Arrays.asList(exclude).indexOf(key) < 0) {
				cv.put(key, row.get(key).toString());
			}
		}
		return cv;
	}
}
