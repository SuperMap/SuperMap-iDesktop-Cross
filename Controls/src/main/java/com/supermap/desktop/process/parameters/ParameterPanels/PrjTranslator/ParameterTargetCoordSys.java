package com.supermap.desktop.process.parameters.ParameterPanels.PrjTranslator;

import com.supermap.data.PrjCoordSys;
import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;

import java.beans.PropertyChangeEvent;

/**
 * Created by yuanR on 2017/10/12 0012.
 * 目标坐标系
 */
public class ParameterTargetCoordSys extends AbstractParameter implements ISelectionParameter {
	@ParameterField(name = "value")
	private PrjCoordSys targetPrjCoordSys = null;

	public ParameterTargetCoordSys() {

	}

	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public void setSelectedItem(Object item) {
		PrjCoordSys oldValue = null;
		if (item == null) {
			oldValue = this.targetPrjCoordSys;
			this.targetPrjCoordSys = null;
		} else if (item instanceof PrjCoordSys) {
			oldValue = this.targetPrjCoordSys;
			this.targetPrjCoordSys = (PrjCoordSys) item;
		}
		firePropertyChangeListener(new PropertyChangeEvent(this, "value", oldValue, this.targetPrjCoordSys));
	}


	/**
	 * yuanR存疑：拖动功能到幕布上，会先走该方法，来判断参数是否为空，此时还没有setSelectedItem()
	 * 2017.10.20
	 *
	 * @return
	 */
	@Override
	public PrjCoordSys getSelectedItem() {
		return this.targetPrjCoordSys;
	}

	@Override
	public String getType() {
		return ParameterType.TARGET_COORDSYS;
	}
}