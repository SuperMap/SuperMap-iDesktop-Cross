package com.supermap.desktop.process.types;

/**
 * Created by highsad on 2017/11/14.
 */
public class BasicTypes {
	public final static Type Byte = Type.registerNewUniqueType("byte");
	public final static Type SHORT = Type.registerNewUniqueType("short");
	public final static Type INTEGER = Type.registerNewUniqueType("integer");
	public final static Type LONG = Type.registerNewUniqueType("long");
	public final static Type FLOAT = Type.registerNewUniqueType("float");
	public final static Type DOUBLE = Type.registerNewUniqueType("double");
	public final static Type STRING = Type.registerNewUniqueType("string");
	public final static Type BOOLEAN = Type.registerNewUniqueType("boolean");
	public final static Type CHAR = Type.registerNewUniqueType("char");

	public final static Type ALL_INTEGER = Type.instance("AllInteger").and(Byte).and(SHORT).and(INTEGER).and(LONG);
	public final static Type NUMBER = Type.instance("number").and(ALL_INTEGER).and(FLOAT).and(DOUBLE);
	public final static Type BASICTYPES = Type.instance("allBasicTypes").and(Byte).and(SHORT).and(INTEGER)
			.and(LONG).and(FLOAT).and(DOUBLE).and(STRING).and(BOOLEAN).and(CHAR);
}
