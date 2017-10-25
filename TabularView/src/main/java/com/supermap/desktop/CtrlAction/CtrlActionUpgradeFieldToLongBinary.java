package com.supermap.desktop.CtrlAction;

import com.supermap.data.FieldType;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;

/**
 * Created by ChenS on 2017/10/23 0023.
 */
public class CtrlActionUpgradeFieldToLongBinary extends CtrlActionUpgradeField {
    public CtrlActionUpgradeFieldToLongBinary(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected FieldType getType() {
        return FieldType.LONGBINARY;
    }
}
