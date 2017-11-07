package com.supermap.desktop.properties;

import java.util.ResourceBundle;

public class CoreProperties extends Properties {
	public static final String CORE = "Core";

	public static final String getString(String key) {
		return getString(CORE, key);
	}

	public static final String getString(String baseName, String key) {
		String result = "";

		ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, getLocale());
		if (resourceBundle != null) {
			result = resourceBundle.getString(key);
		}
		return result;
	}

	public static final String ReadOnly = "String_ReadOnly";
	public static final String Exclusive = "String_Exclusive";
	public static final String Default = "String_Default";
	public static final String Clear = "String_Clear";
	public static final String Other = "String_Other";
	public static final String Boolean = "String_Boolean";
	public static final String Byte = "String_Byte";
	public static final String Char = "String_Char";
	public static final String Double = "String_Double";
	public static final String Short = "String_Short";
	public static final String Long = "String_Long";
	public static final String Integer = "String_Integer";
	public static final String LongBinary = "String_LongBinary";
	public static final String Float = "String_Float";
	public static final String Text = "String_Text";
	public static final String WText = "String_WText";
	public static final String DateTime = "String_DateTime";
	public static final String JSONB = "String_JSONB";
	public static final String Left = "String_Left";
	public static final String Right = "String_Right";
	public static final String Top = "String_Top";
	public static final String Bottom = "String_Bottom";

	//common转移
	public static final String Reset = "String_Button_Reset";
	public static final String Apply = "String_Button_Apply";
	public static java.lang.String down = "String_Button_down";
	public static java.lang.String up = "String_Button_up";
	public static final String Cancel = "String_Button_Cancel";
	public static final String Cancelling = "String_Button_Cancelling";
	public static final String Close = "String_Button_Close";
	public static final String OK = "String_Button_OK";
	public static final String Index = "String_Index";
	public static final String Name = "String_Name";
	public static final String PixelFormat = "String_PixelFormat";
	public static final String Button_Setting = "String_Button_Setting";
	public static final String NoValue = "String_Label_NoValue";
	public static final String FieldName = "String_FieldName";
	public static final String Caption = "String_Field_Caption";
	public static final String FieldType = "String_Type";
	public static final String FieldValue = "String_FieldValue";
	public static final String Length = "String_Length";
	public static final String Add = "String_Add";
	public static final String AddField = "String_AddField";
	public static final String Pause = "String_Pause";
	public static final String Run = "String_Run";
	public static final String ReRun = "String_ReRun";
	public static final String Delete = "String_Delete";
	public static final String Modify = "String_Modify";
	public static final String True = "String_True";
	public static final String False = "String_False";
	public static final String NULL = "String_NULL";
	public static final String Label_Datasource = "String_Label_Datasource";
	public static final String Label_Dataset = "String_Label_Dataset";
	public static final String Type = "String_Type";
	public static final String Unknown = "String_Unknown";
	public static final String Total = "String_SumTotal";
	public static final String DatasetGrid = "String_DatasetType_Grid";
	public static final String DatasetVector = "String_DatasetVector";
	public static final String Extremum = "String_Extremum";
	public static final String DefaultValue = "String_DefaultValue";
	public static final String IsRequired = "String_IsRequired";
	public static final String Next = "String_Button_Next";
	public static final String CloseDialog = "String_CheckBox_CloseDialog";
	public static final String Create = "String_Create";
	public static final String Size = "String_Size";
	public static final String File = "String_File";
	public static final String Directory = "String_Directory";
	public static final String IsEditable = "String_IsEditable";
	public static final String stringDataset = "String_ColumnHeader_Dataset";
	public static final String stringDatasource = "String_ColumnHeader_Datasource";
	public static final String STRING_DATASET_TYPE = "String_DatasetType";
	public static final String yes = "String_yes";
	public static final String no = "String_no";
	public static final String Operation = "String_Operation";
	public static final String Edit = "String_Edit";
	public static final String Property = "String_Property";
	public static final String UnSupport = "String_UnSupport";
	public static final String selectAll = "String_ToolBar_SelectAll";
	public static final String selectInverse = "String_ToolBar_SelectInverse";
	public static final String IMPORT = "String_ToolBar_Import";
	public static final String EXPORT = "String_ToolBar_Export";
	public static final String open = "String_Button_Open";
	public static final String SourceDataset = "String_ColumnHeader_SourceDataset";
	public static final String TargetDataset = "String_ColumnHeader_TargetDataset";
	public static final String SourceDatasource = "String_ColumnHeader_SourceDatasource";
	public static final String ResultDataset = "String_Label_Dataset";
	public static final String ResultDatasource = "String_Label_Datasource";
	public static final String keepThisChose = "String_KeepThisChose";
	public static final String moveToFrist = "String_MoveToFrist";
	public static final String moveToNext = "String_MoveToNext";
	public static final String moveToForward = "String_MoveToForward";
	public static final String moveToLast = "String_MoveToLast";
	public static final String createField = "String_FieldCreate";
}
