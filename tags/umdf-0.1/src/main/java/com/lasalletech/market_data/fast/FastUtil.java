package com.lasalletech.market_data.fast;

import org.openfast.GroupValue;

import com.lasalletech.market_data.fast.error.FieldNotFound;

public class FastUtil {
	public static String getString(GroupValue grp,String field) throws FieldNotFound {
		if(!grp.isDefined(field)) {
			throw new FieldNotFound(field);
		}
		return grp.getString(field);
	}
	public static int getInt(GroupValue grp,String field) throws FieldNotFound {
		if(!grp.isDefined(field)) {
			throw new FieldNotFound(field);
		}
		return grp.getInt(field);
	}
	public static double getDouble(GroupValue grp,String field) throws FieldNotFound {
		if(!grp.isDefined(field)) {
			throw new FieldNotFound(field);
		}
		return grp.getDouble(field);
	}
	public static char getChar(GroupValue grp,String field) throws FieldNotFound {
		if(!grp.isDefined(field)) {
			throw new FieldNotFound(field);
		}
		return (char)grp.getByte(field);
	}
	public static long getLong(GroupValue grp,String field) throws FieldNotFound {
		if(!grp.isDefined(field)) {
			throw new FieldNotFound(field);
		}
		return grp.getLong(field);
	}
	public static GroupValue[] getSequence(GroupValue grp,String field) throws FieldNotFound {
		if(!grp.isDefined(field)) {
			throw new FieldNotFound(field);
		}
		return grp.getSequence(field).getValues();
	}
	public static byte getByte(GroupValue grp,String field) throws FieldNotFound {
		if(!grp.isDefined(field)) {
			throw new FieldNotFound(field);
		}
		return grp.getByte(field);
	}
	
	public static String getString(GroupValue grp,String field, String def) {
		if(!grp.isDefined(field)) return def;
		else return grp.getString(field);
	}
	public static int getInt(GroupValue grp,String field, int def) {
		if(!grp.isDefined(field)) return def;
		else return grp.getInt(field);
	}
	public static double getDouble(GroupValue grp,String field, double def) {
		if(!grp.isDefined(field)) return def;
		else return grp.getDouble(field);
	}
	public static char getChar(GroupValue grp,String field, char def) {
		if(!grp.isDefined(field)) return def;
		else return (char)grp.getByte(field);
	}
}
