package com.supermap.desktop.ui.controls.comboBox;

/**
 * @author XiaJT
 */

import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


public class SearchBoxModel<T> extends AbstractListModel<T>
		implements MutableComboBoxModel<T>, KeyListener, ItemListener {
	private SearchItemValueGetter searchItemValueGetter;
	private ArrayList<T> data = new ArrayList<>();
	protected ArrayList<T> searchData = new ArrayList<>();
	private T selection;
	protected JComboBox<T> comboBox;
	private ComboBoxEditor comboBoxEditor;
	private boolean isKeyReleased = false;


	public SearchBoxModel(JComboBox<T> comboBox, SearchItemValueGetter searchItemValueGetter) {
		this.comboBox = comboBox;
		this.searchItemValueGetter = searchItemValueGetter == null ? new SearchItemValueGetter() {
			@Override
			public String getSearchString(Object item) {
				return item.toString();
			}
		} : searchItemValueGetter;
		comboBoxEditor = comboBox.getEditor();
		//here we add the key listener to the text field that the combobox is wrapped around
		comboBoxEditor.getEditorComponent().addKeyListener(this);
	}

	public SearchBoxModel(JSearchComboBox<T> comboBox) {
		this(comboBox, null);
	}

	public void setSearchItemValueGetter(SearchItemValueGetter searchItemValueGetter) {
		this.searchItemValueGetter = searchItemValueGetter;
	}

	public void updateModel(String inputString) {
		resetSearchData();
//lets find any items which start with the string the user typed, and add it to the popup list
//here you would usually get your items from a database, or some other storage...
		if (!StringUtilities.isNullOrEmpty(inputString)) {
			for (int i = searchData.size() - 1; i >= 0; i--) {
				String searchString = searchItemValueGetter.getSearchString(searchData.get(i));
				if (searchString != null && !StringUtilities.isContain(searchString, inputString)) {
					searchData.remove(i);
				}
			}
		}
//		super.fireContentsChanged(this, 0, searchData.size());

//this is a hack to get around redraw problems when changing the lsit length of the displayed popups
		if (comboBox.isPopupVisible()) {
			comboBox.hidePopup();
		}
		if (searchData.size() > 0) {
			comboBox.showPopup();
		}
//		if (searchData.size() != 0)
//			comboBox.setSelectedIndex(0);
	}


	private void resetSearchData() {
		searchData.clear();
		searchData.addAll(data);
	}

	public int getSize() {
		return searchData.size();
	}

	public T getElementAt(int index) {
		if (index >= searchData.size()) {
			return null;
		}
		return searchData.get(index);
	}

	public void setSelectedItem(Object anItem) {
		selection = (T) anItem;
		fireContentsChanged(this, -1, -1);
	}

	public Object getSelectedItem() {
		return selection;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		isKeyReleased = true;
	}

	public void keyReleased(KeyEvent e) {
		isKeyReleased = true;
		if (e.isControlDown() || e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			return;
		}
		String str = comboBoxEditor.getItem().toString();
		JTextField jtf = (JTextField) comboBoxEditor.getEditorComponent();
		int currentPos = jtf.getCaretPosition();

		if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
			if (e.getKeyCode() != KeyEvent.VK_ENTER) {
//				comboBoxEditor.setItem(str);
				jtf.setCaretPosition(currentPos);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (getSelectedIndex() != -1) {
				comboBox.setSelectedIndex(comboBox.getSelectedIndex());
			} else {
//				comboBox.setSelectedItem(str);
			}
		} else {
			updateModel(str);
//			comboBoxEditor.setItem(str);
//			jtf.setCaretPosition(currentPos);
		}
		isKeyReleased = false;
	}

	public void itemStateChanged(ItemEvent e) {
		comboBoxEditor.setItem(e.getItem().toString());
		comboBox.setSelectedItem(e.getItem());
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		super.addListDataListener(l);
	}

	@Override
	public void addElement(Object item) {
		data.add((T) item);
		resetSearchData();
	}

	@Override
	public void removeElement(Object obj) {
		data.remove(obj);
		resetSearchData();
	}

	@Override
	public void insertElementAt(Object item, int index) {
		data.add(index, (T) item);
		resetSearchData();
	}

	@Override
	public void removeElementAt(int index) {
		data.remove(index);
		resetSearchData();
	}

	public int getSelectedIndex() {
		try {
			if (!comboBox.isPopupVisible() && !isKeyReleased) {
				resetSearchData();
				fireContentsChanged(this, 0, searchData.size());
			}
			String str = comboBoxEditor.getItem().toString();
			for (int i = 0; i < searchData.size(); i++) {
				String searchString = searchItemValueGetter.getSearchString(searchData.get(i));
				if (!StringUtilities.isNullOrEmpty(searchString) && searchString.equals(str)) {
					return i;
				}
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

	public boolean isSearch() {
		return searchData.size() != data.size();
	}

	public void addSearchResult(T resultData) {
		searchData.add(resultData);
//		super.fireContentsChanged(this, searchData.size() - 1, searchData.size());
	}
}
