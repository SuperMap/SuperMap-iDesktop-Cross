package com.supermap.desktop.geometryoperation.CtrlAction;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.geometryoperation.editor.FillRegionEditor;
import com.supermap.desktop.geometryoperation.editor.IEditor;

/**
 * Created by ChenS on 2017/11/24 0024.
 */
public class CtrlActionFillRegion extends CtrlActionEditorBase {
    private FillRegionEditor fillRegionEditor = new FillRegionEditor();

    public CtrlActionFillRegion(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    public IEditor getEditor() {
        return this.fillRegionEditor;
    }
}
