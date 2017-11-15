package com.supermap.desktop.process.types;

/**
 * Created by highsad on 2017/11/14.
 */
public class DatasetTypes {
	public final static Type POINT = Type.registerNewUniqueType("point");
	public final static Type LINE = Type.registerNewUniqueType("line");
	public final static Type REGION = Type.registerNewUniqueType("region");
	public final static Type TEXT = Type.registerNewUniqueType("text");
	public final static Type CAD = Type.registerNewUniqueType("cad");
	public final static Type LINKTABLE = Type.registerNewUniqueType("linkTable");
	public final static Type NETWORK = Type.registerNewUniqueType("network");
	public final static Type NETWORK3D = Type.registerNewUniqueType("network3D");
	public final static Type LINEM = Type.registerNewUniqueType("lineM");
	public final static Type PARAMETRICLINE = Type.registerNewUniqueType("ParametericLine");
	public final static Type PARAMETRICREGION = Type.registerNewUniqueType("ParametericRegion");
	public final static Type GRIDCOLLECTION = Type.registerNewUniqueType("GridCollection");
	public final static Type IMAGECOLLECTION = Type.registerNewUniqueType("ImageCollection");
	public final static Type MODEL = Type.registerNewUniqueType("model");
	public final static Type TEXTURE = Type.registerNewUniqueType("texture");
	public final static Type IMAGE = Type.registerNewUniqueType("image");
	public final static Type WMS = Type.registerNewUniqueType("WMS");
	public final static Type WCS = Type.registerNewUniqueType("WCS");
	public final static Type GRID = Type.registerNewUniqueType("grid");
	public final static Type VOLUME = Type.registerNewUniqueType("volume");
	public final static Type TOPOLOGY = Type.registerNewUniqueType("topology");
	public final static Type POINT3D = Type.registerNewUniqueType("point3D");
	public final static Type LINE3D = Type.registerNewUniqueType("line3D");
	public final static Type REGION3D = Type.registerNewUniqueType("region3D");
	public final static Type DEM = Type.registerNewUniqueType("DEM");
	public final static Type POINTEPS = Type.registerNewUniqueType("pointEPS");
	public final static Type LINEEPS = Type.registerNewUniqueType("lineEPS");
	public final static Type REGIONEPS = Type.registerNewUniqueType("regionEPS");
	public final static Type TEXTEPS = Type.registerNewUniqueType("textEPS");
	public final static Type TABULAR = Type.registerNewUniqueType("tabular");

	public final static Type SIMPLE_VECTOR = Type.instance("SimpleVector").and(POINT).and(LINE).and(REGION);
	public final static Type SIMPLE_VECTOR_AND_GRID = Type.instance("SimpleVectorAndGrid").and(SIMPLE_VECTOR).and(GRID);
	public final static Type VECTOR = Type.instance("Vector").and(SIMPLE_VECTOR).and(TABULAR).and(TEXT).and(CAD);
	public final static Type ALL_RASTER = Type.instance("AllRaster").and(IMAGE).and(GRID);
	public final static Type LINE_POLYGON_VECTOR = Type.instance("LinePolygon").and(LINE).and(REGION);

	public final static Type DATASET = Type.instance("dataset").and(POINT).and(LINE).and(REGION).and(TEXT).and(CAD).and(LINKTABLE)
			.and(NETWORK).and(NETWORK3D).and(LINEM).and(PARAMETRICLINE).and(PARAMETRICREGION).and(GRIDCOLLECTION)
			.and(IMAGECOLLECTION).and(MODEL).and(TEXTURE).and(IMAGE).and(WMS).and(WCS).and(GRID).and(VOLUME).and(TOPOLOGY)
			.and(POINT3D).and(LINE3D).and(REGION3D).and(DEM).and(POINTEPS).and(LINEEPS).and(REGIONEPS).and(TEXTEPS).and(TABULAR);

	public static void main(String[] args) {
		Type t1 = SIMPLE_VECTOR;
		Type t2 = SIMPLE_VECTOR_AND_GRID;
		Type t3 = VECTOR;
		Type t4 = Type.instance("test").and(SIMPLE_VECTOR).and(TABULAR).and(TEXT).and(CAD);

		System.out.println(t2.contains(t1)); // true
		System.out.println(t3.contains(t1)); // true
		System.out.println(t3.contains(t2)); // false
		System.out.println(t3.intersects(t2)); // true
		System.out.println(t4.contains(t1)); // true
		System.out.println(t4.contains(t2)); // false
		System.out.println(t4.intersects(t2)); // true
		System.out.println(t3.equals(t4)); // true
		System.out.println(t4.equals(t3)); // true
		System.out.println(t4.equals(t2)); // false

		System.out.println(BasicTypes.ALL_INTEGER.intersects(SIMPLE_VECTOR)); // false
		System.out.println(BasicTypes.NUMBER.intersects(BasicTypes.ALL_INTEGER)); // true
		System.out.println(BasicTypes.NUMBER.contains(BasicTypes.ALL_INTEGER)); // true
		System.out.println(BasicTypes.ALL_INTEGER.contains(BasicTypes.NUMBER)); // false
	}
}
