package com.supermap.desktop.ui;

import com.supermap.desktop.Application;
import com.supermap.desktop.GlobalParameters;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.desktop.Interface.IFormManager;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.Interface.IFormScene;
import com.supermap.desktop.controls.utilities.ToolbarUIUtilities;
import com.supermap.desktop.dialog.DialogSaveChildForms;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.event.ActiveFormChangedEvent;
import com.supermap.desktop.event.ActiveFormChangedListener;
import com.supermap.desktop.event.FormClosedEvent;
import com.supermap.desktop.event.FormClosedListener;
import com.supermap.desktop.event.FormClosingEvent;
import com.supermap.desktop.event.FormClosingListener;
import com.supermap.desktop.event.FormEventHelper;
import com.supermap.desktop.event.FormShownEvent;
import com.supermap.desktop.event.FormShownListener;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.mdi.MdiPage;
import com.supermap.desktop.ui.mdi.MdiPane;
import com.supermap.desktop.ui.mdi.events.Operation;
import com.supermap.desktop.ui.mdi.events.PageActivatedEvent;
import com.supermap.desktop.ui.mdi.events.PageActivatedListener;
import com.supermap.desktop.ui.mdi.events.PageAddedEvent;
import com.supermap.desktop.ui.mdi.events.PageAddedListener;
import com.supermap.desktop.ui.mdi.events.PageClosedEvent;
import com.supermap.desktop.ui.mdi.events.PageClosedListener;
import com.supermap.desktop.ui.mdi.events.PageClosingEvent;
import com.supermap.desktop.ui.mdi.events.PageClosingListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

public class FormManager extends MdiPane implements IFormManager {
	private WindowType activatedChildFormType = WindowType.UNKNOWN;
	private FormEventHelper eventHelper = new FormEventHelper();

	private PageActivatedListener pageActivatedListener = new PageActivatedListener() {

		@Override
		public void pageActivated(PageActivatedEvent event) {
			try {
				if (event.getOldActivedPage() != null
						&& !event.getOldActivedPage().isClosed() // 如果 Page 已被关闭，则不发送 deactive 事件，因为在关闭的时候，已经发送过了
						&& event.getOldActivedPage().getComponent() instanceof FormBaseChild) {
					((FormBaseChild) event.getOldActivedPage().getComponent()).deactived();
					((FormBaseChild) event.getOldActivedPage().getComponent()).fireFormDeactivated();
				}

				if (event.getActivedPage() != null && event.getActivedPage().getComponent() instanceof FormBaseChild) {
					((FormBaseChild) event.getActivedPage().getComponent()).actived();
					((FormBaseChild) event.getActivedPage().getComponent()).fireFormActivated();
				}
				IForm newActive = event.getActivedPage() != null && event.getActivedPage().getComponent() instanceof IForm ? (IForm) event.getActivedPage().getComponent() : null;
				IForm oldActive = event.getOldActivedPage() != null && event.getOldActivedPage().getComponent() instanceof IForm ? (IForm) event.getOldActivedPage().getComponent() : null;
				FormManager.this.eventHelper.fireActiveFormChanged(new ActiveFormChangedEvent(this, oldActive, newActive));
				refreshMenusAndToolbars(getActiveForm());
				ToolbarUIUtilities.updataToolbarsState();
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			}
		}
	};

	private PageClosingListener pageClosingListener = new PageClosingListener() {
		@Override
		public void pageClosing(PageClosingEvent e) {
			if (e.getOperation() == Operation.CLOSE && e.getPage() != null && e.getPage().getComponent() instanceof FormBaseChild) {
				FormClosingEvent event = new FormClosingEvent((IForm) e.getPage().getComponent(), false);
				((FormBaseChild) e.getPage().getComponent()).formClosing(event);

				if (!event.isCancel()) {
					((FormBaseChild) e.getPage().getComponent()).fireFormClosing(event);
					FormManager.this.eventHelper.fireFormClosing(event);
				}
				e.setCancel(event.isCancel());
			}
		}
	};

	private PageClosedListener pageClosedListener = new PageClosedListener() {
		@Override
		public void pageClosed(PageClosedEvent e) {
			if (e.getOperation() == Operation.CLOSE && e.getPage() != null && e.getPage().getComponent() instanceof FormBaseChild) {
				if (e.isDeactived()) {
					((FormBaseChild) e.getPage().getComponent()).deactived();
					((FormBaseChild) e.getPage().getComponent()).fireFormDeactivated();
				}

				FormClosedEvent event = new FormClosedEvent((IForm) e.getPage().getComponent());
				((FormBaseChild) e.getPage().getComponent()).formClosed(event);
				((FormBaseChild) e.getPage().getComponent()).fireFormClosed(event);
				if (e.getPage().getComponent() instanceof IFormMap) {
					UICommonToolkit.getLayersManager().getLayersTree().removeIFormMap((IFormMap) e.getPage().getComponent());
				}

				FormManager.this.eventHelper.fireFormClosed(event);
				// 关闭后，刷新ToolBar-yuanR2017.11.30
				ToolbarUIUtilities.updataToolbarsState();
			}

			if (getPageCount() == 0) {
				refreshMenusAndToolbars(null);
			}
		}
	};

	private PageAddedListener pageAddedListener = new PageAddedListener() {
		@Override
		public void pageAdded(PageAddedEvent e) {
			if (e.getOperation() == Operation.ADD && e.getPage() != null && e.getPage().getComponent() instanceof FormBaseChild) {
				FormShownEvent event = new FormShownEvent((IForm) e.getPage().getComponent());
				((FormBaseChild) e.getPage().getComponent()).formShown(event);
				((FormBaseChild) e.getPage().getComponent()).fireFormShown(event);
				if (e.getPage().getComponent() instanceof IFormMap) {
					UICommonToolkit.getLayersManager().getLayersTree().addIFormMap((IFormMap) e.getPage().getComponent());
				}
				FormManager.this.eventHelper.fireFormShown(event);
			}
		}
	};

	public FormManager() {
		addPageAddedListener(this.pageAddedListener);
		addPageActivatedListener(this.pageActivatedListener);
		addPageClosingListener(this.pageClosingListener);
		addPageClosedListener(this.pageClosedListener);
	}

	private IForm[] getMdiChildren() {
		IForm[] forms = new IForm[0];
		try {
			forms = new IForm[getPageCount()];
			for (int i = 0; i < getPageCount(); i++) {
				MdiPage page = getPageAt(i);
				if (page.getComponent() instanceof IForm) {
					forms[i] = (IForm) page.getComponent();
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return forms;
	}

	@Override
	public IForm get(int index) {
		try {
			if (index < getPageCount()) {
				MdiPage page = getPageAt(index);
				if (page.getComponent() instanceof IForm) {
					return (IForm) page.getComponent();
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return null;
	}


	@Override
	public void add(IForm form) {
		add(form, -1);
	}

	@Override
	public void add(IForm form, int index) {
		try {
			if (form instanceof FormBaseChild) {
				final MdiPage page = MdiPage.createMdiPage((FormBaseChild) form, form.getText(), true, false);
				((FormBaseChild) form).addPropertyChangeListener(FormBaseChild.TITLE_PROPERTY, new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getNewValue() != null) {
							page.setTitle(evt.getNewValue().toString());
						}
					}
				});
				addPage(page, index);
			} else {
				Application.getActiveApplication().getOutput().output("error.");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

	}

	@Override
	public int getCount() {
		return getPageCount();
	}

	@Override
	public IForm getActiveForm() {
		if (getActivePage() == null) {
			return null;
		} else {
			return (FormBaseChild) getActivePage().getComponent();
		}
	}

	@Override
	public void setActiveForm(IForm form) {
		activePage((FormBaseChild) form);
	}

	@Override
	public void resetActiveForm() {
		if (getActivePage() != null && getActivePage().getComponent() instanceof IForm) {
			MdiPage page = getActivePage();
			this.eventHelper.fireActiveFormChanged(new ActiveFormChangedEvent(this, (IForm) page.getComponent(), (IForm) page.getComponent()));
		}
	}

	@Override
	public void showChildForm(IForm childForm) {
		try {
			if (!(childForm instanceof FormBaseChild)) {
				return;
			}

			MdiPage page = getPage((FormBaseChild) childForm);
			if (page == null) {
				add(childForm);
			} else {
				activePage(page);
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean close(IForm form) {
		boolean isClosed = false;
		if (form instanceof FormBaseChild) {
			isClosed = super.close((FormBaseChild) form);
		}
		return isClosed;
	}

	@Override
	public boolean closeAll(boolean isSave) {
		return closeForms(getMdiChildren(), isSave);
	}

	/**
	 * 保存子窗口时是否进行提示
	 *
	 * @param notify 如果为true 则会根据窗体的具体修改情况弹出子窗体统一管理窗体，进行统一设置，否则直接进行保存。
	 * @return 是否执行完了保存操作，包括用户主动取消的保存
	 */
	@Override
	public boolean saveAll(boolean notify) {
		return saveForms(getMdiChildren(), notify);
	}

	/**
	 * 关闭指定的子窗口
	 *
	 * @param forms
	 * @param isSave
	 * @return
	 */
	public boolean closeForms(IForm[] forms, boolean isSave) {
		boolean result = true;

		try {
			if (isSave) {
				result = this.saveForms(forms, true);
			} else {
				for (int i = 0; i < forms.length; i++) {
					forms[i].setNeedSave(false);
				}
			}

			if (result) {
				HashMap<IForm, Boolean> formsNeedSaveStatus = new HashMap<IForm, Boolean>();

				for (IForm child : forms) {
					formsNeedSaveStatus.put(child, child.isNeedSave());
					child.setNeedSave(false);
				}
				result = closeForms(forms);

//				result = super.closeAll();
				for (IForm child : forms) {
					child.setNeedSave(formsNeedSaveStatus.get(child));
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return result;
	}

	/**
	 * 关闭指定的子窗口
	 *
	 * @param forms
	 * @return
	 */
	public boolean closeForms(IForm[] forms) {
		boolean result = false;

		try {
			for (int i = forms.length - 1; i >= 0; i--) {
				close(forms[i]);
			}
			result = true;
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return result;
	}

	/**
	 * 保存指定的子窗口
	 *
	 * @param forms
	 * @param notify 如果为true 则会根据窗体的具体修改情况弹出子窗体统一管理窗体，进行统一设置，否则直接进行保存。
	 * @return 无论是否保存，都返回 true，取消操作返回 false
	 */
	public boolean saveForms(IForm[] forms, boolean notify) {
		boolean result = false;

		try {
			if (notify && GlobalParameters.isShowFormClosingInfo()) {
				ArrayList<IForm> canSaveForms = new ArrayList<IForm>();
				for (IForm child : forms) {
					boolean canSaved = false;
					if (child.getWindowType() == WindowType.MAP) {
						canSaved = ((IFormMap) child).getMapControl().getMap().isModified();
					} else if (child instanceof IFormScene) {
						// 场景没有实现，始终需要保存
						canSaved = true;
					} else if (child instanceof IFormLayout) {
						canSaved = ((IFormLayout) child).getMapLayoutControl().getMapLayout().isModified();
					}

					if (canSaved) {
						canSaveForms.add(child);
					}
				}

				if (!canSaveForms.isEmpty()) {
					DialogSaveChildForms saveChildForms = new DialogSaveChildForms();
					saveChildForms.setAllForms(canSaveForms.toArray(new IForm[canSaveForms.size()]));
					DialogResult dialogResult = saveChildForms.showDialog();
					boolean saveLayer3DKML = saveChildForms.isSaveLayer3DKML();

					// 保存被选择的窗口
					if (dialogResult == DialogResult.YES) {

						IForm[] selectedForms = saveChildForms.getSelectedForms();
						for (IForm form : selectedForms) {
							if (form instanceof IFormMap) {
								if (Application.getActiveApplication().getWorkspace().getMaps().indexOf(form.getText()) >= 0) {
									form.save();
								} else {
									form.save(false, true);
								}
							} else if (form instanceof IFormScene) {
								if (saveLayer3DKML) {
									form.saveFormInfos();
								}

								if (Application.getActiveApplication().getWorkspace().getScenes().indexOf(form.getText()) >= 0) {
									form.save();
								} else {
									form.save(false, true);
								}
							} else if (form instanceof IFormLayout) {
								if (Application.getActiveApplication().getWorkspace().getLayouts().indexOf(form.getText()) >= 0) {
									form.save();
								} else {
									form.save(false, true);
								}
							} else {
								form.save();
							}
						}

						result = true;
					} else if (dialogResult == DialogResult.NO) {
						result = true;

						for (IForm form : saveChildForms.getSelectedForms()) {
							if (saveLayer3DKML && form instanceof IFormScene) {
								form.saveFormInfos();
							}
						}
					} else {
						result = false;
					}
				} else {
					result = true;
				}
			} else {
				result = true;
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return result;
	}

	@Override
	public void addFormShownListener(FormShownListener listener) {
		this.eventHelper.addFormShownListener(listener);
	}

	@Override
	public void removeFormShownListener(FormShownListener listener) {
		this.eventHelper.removeFormShownListener(listener);
	}

	/**
	 * 不同于 PageClosing 事件，该事件是 FormManager 独有事件
	 *
	 * @param listener
	 */
	@Override
	public void addFormClosingListener(FormClosingListener listener) {
		this.eventHelper.addFormClosingListener(listener);
	}

	public void removeFormClosingListener(FormClosingListener listener) {
		this.eventHelper.removeFormClosingListener(listener);
	}

	/**
	 * 不同于 PageClosed 事件，该事件是 FormManager 独有事件
	 *
	 * @param listener
	 */
	@Override
	public void addFormClosedListener(FormClosedListener listener) {
		this.eventHelper.addFormClosedListener(listener);
	}

	public void removeFormClosedListener(FormClosedListener listener) {
		this.eventHelper.removeFormClosedListener(listener);
	}

	@Override
	public void addActiveFormChangedListener(ActiveFormChangedListener listener) {
		this.eventHelper.addActiveFormChangedListener(listener);
	}

	@Override
	public void removeActiveFormChangedListener(ActiveFormChangedListener listener) {
		this.eventHelper.removeActiveFormChangedListener(listener);
	}

	/**
	 * 这个方法的实现很不好，并且使用到了一个很鸡肋的东西 WindowType
	 * 待有空来优化
	 *
	 * @param window
	 */
	private void refreshMenusAndToolbars(IForm window) {
		// TODO 待有空优化
		// 获取之前激活的窗口的类型
		WindowType beforeType = this.activatedChildFormType;
		if (window != null) {
			this.activatedChildFormType = window.getWindowType();
		} else {
			this.activatedChildFormType = WindowType.UNKNOWN;
		}

		// 如果窗口类型不一致，刷新子选项卡和工具条
		if (beforeType != this.activatedChildFormType) {
//			final FrameMenuManager frameMenuManager = (FrameMenuManager) Application.getActiveApplication().getMainFrame().getFrameMenuManager();
//			final ToolbarManager toolbarManager = (ToolbarManager) Application.getActiveApplication().getMainFrame().getToolbarManager();
			final RibbonManager ribbonManager = (RibbonManager) Application.getActiveApplication().getMainFrame().getRibbonManager();

			boolean needRefresh = false;
			// 如果之前存在子窗口，则需要移除原来的子菜单和工具条
			if (beforeType != WindowType.UNKNOWN) {
				// 移除原子窗体的子菜单
//				frameMenuManager.removeChildMenu(beforeType);
				ribbonManager.removeChildMenu(beforeType);
				// 移除原子窗体的子工具条
//				toolbarManager.removeChildToolbar(beforeType);

				needRefresh = true;
			}

			// 如果切换后存在子窗口，则需要添加子菜单和工具条
			if (!this.activatedChildFormType.equals(WindowType.UNKNOWN)) {
				// 激活新子窗体的子菜单
//				frameMenuManager.loadChildMenu(activatedChildFormType);
				ribbonManager.loadChildMenu(activatedChildFormType);
				// 激活新子窗体的子工具条
//				toolbarManager.loadChildToolbar(activatedChildFormType);

				needRefresh = true;
			}
			if (needRefresh) {
				ToolbarUIUtilities.updataToolbarsState();
//                frameMenuManager.getMenuBar().updateUI();
//				toolbarManager.getToolbarsContainer().repaint();
			}
		}
	}

	@Override
	public boolean isContain(IForm form) {
		if (form instanceof FormBaseChild) {
			return getPage((FormBaseChild) form) != null;
		} else {
			return false;
		}
	}
}
