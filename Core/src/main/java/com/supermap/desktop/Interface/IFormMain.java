package com.supermap.desktop.Interface;

import com.supermap.desktop.event.FormLoadedListener;

public interface IFormMain {

	/**
	 * Get the text
	 */
	String getText();

	/**
	 * Set the text
	 */
	void setText(String text);

	/**
	 * 获取子窗体管理器。
	 */
	IFormManager getFormManager();

	/**
	 * 获取窗体菜单管理器。
	 */
	IFrameMenuManager getFrameMenuManager();

	/**
	 * 获取右键菜单管理器。
	 */
	IContextMenuManager getContextMenuManager();

	/**
	 * 获取工具条管理器。
	 */
	IToolbarManager getToolbarManager();

	IRibbonManager getRibbonManager();
	/**
	 * 获取浮动窗口管理器。
	 */
	IDockbarManager getDockbarManager();

	/**
	 * 获取状态栏管理器
	 * 
	 * @return
	 */
	IStatusbarManager getStatusbarManager();

	/**
	 * 获取属性管理器
	 * 
	 * @return
	 */
	IPropertyManager getPropertyManager();

	/**
	 * Method doSomething documentation comment…
	 */
	void loadUI();

    void addFormLoadedListener(FormLoadedListener formLoadedListener);

	void removeFormLoadedListener(FormLoadedListener formLoadedListener);
}
