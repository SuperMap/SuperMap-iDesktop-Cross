package com.supermap.desktop.utilities.fieldTypeConverted;

import com.supermap.data.FieldType;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.JOptionPaneUtilities;

/**
 * Created by ChenS on 2017/11/2 0002.
 */
public class FieldTypeConvertedUtilities {
    //region FieldTypeConvertedAllowed
    private static FieldTypeConvertedAllowed byteConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            return type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN;
        }
    };

    private static FieldTypeConvertedAllowed int16ConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            } else if (type == FieldType.BYTE || type == FieldType.CHAR) {
                return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
            }
            return true;
        }
    };

    private static FieldTypeConvertedAllowed int32ConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            } else if (type == FieldType.BYTE || type == FieldType.CHAR || type == FieldType.INT16) {
                return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
            }
            return true;
        }
    };

    private static FieldTypeConvertedAllowed int64ConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            } else if (type == FieldType.BYTE || type == FieldType.CHAR || type == FieldType.INT16 || type == FieldType.INT32) {
                return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
            }
            return true;
        }
    };

    private static FieldTypeConvertedAllowed singleConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.TEXT || type == FieldType.WTEXT || type == FieldType.DOUBLE) {
                return true;
            } else if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            }
            return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
        }
    };

    private static FieldTypeConvertedAllowed doubleConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.TEXT || type == FieldType.WTEXT) {
                return true;
            } else if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            }
            return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
        }
    };

    private static FieldTypeConvertedAllowed booleanConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY) {
                return false;
            } else if (type == FieldType.TEXT || type == FieldType.WTEXT) {
                return true;
            }
            return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
        }
    };

    private static FieldTypeConvertedAllowed dateTimeConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            return type == FieldType.TEXT || type == FieldType.WTEXT;
        }
    };

    private static FieldTypeConvertedAllowed longBinaryConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            return type == FieldType.TEXT || type == FieldType.WTEXT;
        }
    };

    private static FieldTypeConvertedAllowed charConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.TEXT || type == FieldType.WTEXT) {
                return true;
            } else if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            }
            return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
        }
    };

    private static FieldTypeConvertedAllowed textConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.WTEXT) {
                return true;
            } else if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            }
            return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
        }
    };

    private static FieldTypeConvertedAllowed wtextConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.TEXT) {
                return true;
            } else if (type == FieldType.JSONB || type == FieldType.DATETIME || type == FieldType.LONGBINARY || type == FieldType.BOOLEAN) {
                return false;
            }
            return JOptionPaneUtilities.showConfirmDialog(CoreProperties.getString("String_ConfirmChangeFieldType")) == 0;
        }
    };

    private static FieldTypeConvertedAllowed jsonBConvertedAllowed = new FieldTypeConvertedAllowed() {
        @Override
        public boolean isConverted(FieldType type) {
            if (type == FieldType.TEXT || type == FieldType.WTEXT) {
                return true;
            }
            return false;
        }
    };


    //endregion

    public static FieldType getConvertedFieldType(FieldType origin, FieldType current) {
        if (origin != current) {
            if (origin == FieldType.BYTE) {
                return byteConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.INT16) {
                return int16ConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.INT32) {
                return int32ConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.INT64) {
                return int64ConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.SINGLE) {
                return singleConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.DOUBLE) {
                return doubleConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.LONGBINARY) {
                return longBinaryConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.BOOLEAN) {
                return booleanConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.DATETIME) {
                return dateTimeConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.CHAR) {
                return charConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.TEXT) {
                return textConvertedAllowed.isConverted(current) ? current : origin;
            } else if (origin == FieldType.WTEXT) {
                return wtextConvertedAllowed.isConverted(current) ? current : origin;
            } else {
                return jsonBConvertedAllowed.isConverted(current) ? current : origin;
            }
        }
        return origin;
    }
}
