package com.supermap.desktop.ui.controls.prjcoordsys;

/**
 * 增加自定义和收藏夹
 * yuanR2017.10.24
 */
public class XMLProjectionTag {
	public static final String FILE_STARTUP_XML = "../Configuration/SuperMap.Desktop.Startup.xml";
	public static final String PROJECTION = "projection";
	public static final String DEFAULT = "default";
	public static final String PROJECTION_XML = "../Templates/Projection/Projection.xml";
	//public static final String CUSTOMPROJECTION_XML = "../Templates/Projection/CustomProjection.xml";
	//public static final String FAVORITEPROJECTION_XML = "../Templates/Projection/FavoriteProjection.xml";
	public static final String CUSTOMPROJECTION_FOLDER = "../Templates/Projection/Customize";
	public static final String FAVORITEPROJECTION_FOLDER = "../Templates/Projection/Favorite";

	public static final String NAMESPACEURL = "http://www.supermap.com/sml";
	// 1
	public static final String PROJECTION_ROOT = "SuperMapProjectionDefine";
	// {{ 隶属于Root
	public static final String PRJCOORDSYS_DEFINES = "sml:PJCoordSysDefines";
	// {{隶属于g_PJCoorSysDefines
	public static final String PRJGROUP_CAPTION_DEFAULT = "sml:DefaultPJGroupCaption";
	public static final String PRJCOORDSYS_CAPTION_DEFAULT = "sml:DefaultPJCoordSysCaption";
	public static final String PRJCOORDSYS_DEFINE = "sml:PJCoordSysDefine";
	// {{隶属于g_PJCoordSysDefine
	public static final String PRJGROUP_CAPTION = "sml:PJGroupCaption";
	public static final String PRJCOORDSYS_CAPTION = "sml:PJCoordSysCaption";
	public static final String PRJCOORDSYS_TYPE = "sml:PJCoordSysType";
	public static final String COORDINATE_REFERENCE_SYSTEM = "sml:CoordinateReferenceSystem";
	// {{隶属于g_CoordinateReferenceSystem
	public static final String NAMESET = "sml:Nameset";
	public static final String NAME = "sml:name"; // 隶属于g_Nameset
	public static final String TYPE = "sml:Type";
	public static final String UNITS = "sml:Units";
	public static final String GEOGRAPHIC_COORDINATE_SYSTEM = "sml:GeographicCoordinateSystem";
	public static final String MAP_PROJECTION = "sml:MapProjection";
	public static final String PARAMETERS = "sml:Parameters";
	public static final String HORIZONAL_GEODETIC_DATUM = "sml:HorizonalGeodeticDatum";
	public static final String PRIME_MERIDIAN = "sml:PrimeMeridian";
	public static final String ELLIPSOID = "sml:Ellipsoid";
	public static final String SEMI_MAJOR_AXIS = "sml:SemiMajorAxis";
	public static final String INVERSE_FLATTENING = "sml:InverseFlattening";
	public static final String FALSE_EASTING = "sml:FalseEasting";
	public static final String FALSE_NORTHING = "sml:FalseNorthing";
	public static final String CENTRAL_MERIDIAN = "sml:CentralMeridian";
	public static final String STANDARD_PARALLEL1 = "sml:StandardParallel1";
	public static final String STANDARD_PARALLEL2 = "sml:StandardParallel2";
	public static final String SCALE_FACTOR = "sml:ScaleFactor";
	public static final String CENTRAL_PARALLEL = "sml:CentralParallel";
	public static final String AZIMUTH = "sml:Azimuth";
	public static final String FIRSTPOINT_LONGITUDE = "sml:FirstPointLongitude";
	public static final String SECONDPOINT_LONGITUDE = "sml:SecondPointLongitude";

	//  地理坐标系
	public static final String GEOCOORDSYS_DEFINES = "sml:PJGeoCoordSysDefines";
	public static final String GEOCOORDSYS_CAPTION_DEFAULT = "sml:DefaultPJGeoCoordSysCaption";
	public static final String GEOCOORDSYS_DEFINE = "sml:PJGeoCoordSysDefine";
	public static final String GEOGROUP_CATION = "sml:PJGeoGroupCaption"; // 新增内容
	public static final String GEOCOORDSYS_CAPTION = "sml:PJGeoCoordSysCaption";
	public static final String GEOCOORDSYS_TYPE = "sml:PJGeoCoordSysType";

	//  自定义坐标系
	//public static final String CUSTOMCOORDSYS_DEFINES = "sml:CustomCoordSysDefines";
	//public static final String CUSTOMCOORDSYS_CAPTION_DEFAULT = "sml:DefaultCustomCoordSysCaption";
	//public static final String CUSTOMCOORDSYS_DEFINE = "sml:CustomCoordSysDefine";
	//public static final String CUSTOMGROUP_CATION = "sml:CustomGroupCaption"; // 新增内容
	//public static final String CUSTOMCOORDSYS_CAPTION = "sml:CustomCoordSysCaption";
	//public static final String CUSTOMCOORDSYS_TYPE = "sml:CustomCoordSysType";

	// 收藏夹坐标系
	//public static final String FAVORITECOORDSYS_DEFINES = "sml:FavoriteCoordSysDefines";
	//public static final String FAVORITECOORDSYS_CAPTION_DEFAULT = "sml:DefaultFavoriteCoordSysCaption";
	//public static final String FAVORITECOORDSYS_DEFINE = "sml:FavoriteCoordSysDefine";
	//public static final String FAVORITEGROUP_CATION = "sml:FavoriteGroupCaption"; // 新增内容
	//public static final String FAVORITECOORDSYS_CAPTION = "sml:FavoriteCoordSysCaption";
	//public static final String FAVORITECOORDSYS_TYPE = "sml:FavoriteCoordSysType";

	private XMLProjectionTag() {
		// 工具类不提供构造函数
	}
}
