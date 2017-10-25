package com.supermap.desktop.CtrlAction;

import com.supermap.data.EngineType;
import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormTabular;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.implement.CtrlAction;

/**
 * Created by ChenS on 2017/10/23 0023.
 * 修改字段类型
 */
public abstract class CtrlActionUpgradeField extends CtrlAction {
    public CtrlActionUpgradeField(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        IFormTabular tabular = (IFormTabular) Application.getActiveApplication().getActiveForm();
        FieldInfos fieldInfos = tabular.getRecordset().getFieldInfos();
        FieldInfo fieldInfoOrigin = fieldInfos.get(tabular.getSelectColumnName(tabular.getModelColumn(tabular.getSelectedColumns()[0])));

        FieldInfo clone = fieldInfoOrigin.clone();
        clone.setType(getType());

        Application.getActiveApplication().getOutput().output(fieldInfos.modify(fieldInfoOrigin.getName(), clone) ?
                ControlsProperties.getString("String_ChangeFieldTypeSuccessful") :
                ControlsProperties.getString("String_ChangeFieldTypeFailed"));
    }

    protected abstract FieldType getType();

    @Override
    public boolean enable() {
        IFormTabular tabular = (IFormTabular) Application.getActiveApplication().getActiveForm();
        EngineType engineType = tabular.getDataset().getDatasource().getEngineType();
        FieldInfo fieldInfo = tabular.getRecordset().getFieldInfos().get(tabular.getSelectColumnName(tabular.getModelColumn(tabular.getSelectedColumns()[0])));
        return (!fieldInfo.isSystemField() && tabular.getSelectColumnCount() == 1
                && (engineType.equals(EngineType.MYSQL) || engineType.equals(EngineType.ORACLEPLUS)
                || engineType.equals(EngineType.SQLPLUS) || engineType.equals(EngineType.MYSQLPlus)
                || engineType.equals(EngineType.KINGBASE) || engineType.equals(EngineType.POSTGRESQL)));
    }
}
