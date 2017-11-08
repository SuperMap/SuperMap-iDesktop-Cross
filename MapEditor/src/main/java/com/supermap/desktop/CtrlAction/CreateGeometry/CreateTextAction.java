package com.supermap.desktop.CtrlAction.CreateGeometry;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.mapeditor.MapEditorProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.MapUtilities;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapClosedEvent;
import com.supermap.mapping.MapClosedListener;
import com.supermap.ui.Action;
import com.supermap.ui.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// @formatter:off

/**
 * 封装 MapControl 上文本绘制的操作
 * 1：Shift + Enter 换行，Enter 提交编辑。
 * 2：目前的 Java 组件，MapControl 在 Action.CreateText 的时候，无法选中已有的文本对象进行绘制。
 * 3：组件提供的文本绘制思路是，在 TrackedListener 的时候，取出  GeoText，设置完内容，执行完 TrackedListener 的回调方法之后，
 * 会将这个 GeoText 添加到地图。但是这样的思路需要阻塞方法，等待文本输入，实在太不易用。
 * 4：尝试过但放弃的方案。
 * 这里自行实现思路，使用 CreateText，在 TrackedListener 中设置一个任意非空的 GeoText，然后触发 GeometryAdded，
 * 在 GeometryAdded 中取出 id 以及对应 Layer 的 Recordset，然后修改。
 * 放弃原因：因为只有在 Tracked 里添加一个非空文本才会触发 GeometryAdded，才能在 GeometryAdded 里记录新增文本对象的 id，
 * 然后在 TextField 中完成编辑。这样的过程中，文本对象有一个属性文本内容，在 TextField 编辑时，无法完全隐藏或者遮盖这个文本内容。
 * 5：最终方案。
 *
 * @author highsad
 */
// @formatter:on
public class CreateTextAction {

    private static final int DEFAULT_INPUT_HEIGHT = 45;
    private static final float DEFAULT_INPUT_FONT_SIZE = 25.0f;

    private JTextArea textFieldInput = new JTextArea();
    private MapControl mapControl;
    private LayoutManager preLayout;
    private GeoText editingGeoText; // 用来记录每一次点击获取到的 GeoText，当鼠标在另一个位置点击再次出发 Tracked 的时候，将编辑的数据保存

    private TrackedListener trackedListener = new TrackedListener() {

        @Override
        public void tracked(TrackedEvent arg0) {
            mapControlTracked(arg0);

        }
    };
    private ActionChangedListener actionChangedListener = new ActionChangedListener() {

        @Override
        public void actionChanged(ActionChangedEvent arg0) {

            abstractActionListener(arg0);
        }
    };
    private MapClosedListener mapClosedListener = new MapClosedListener() {
        @Override
        public void mapClosed(MapClosedEvent mapClosedEvent) {
            commitEditing();
            endAction();
            mapClosedEvent.getMap().removeMapClosedListener(this);
        }
    };

    private void abstractActionListener(ActionChangedEvent arg0) {
        if (arg0.getOldAction() == Action.CREATETEXT) {

            // 结束文本对象绘制，绘制过程中，按住中键会切换为漫游，此时不希望结束绘制，而是提交当前编辑，漫游结束之后继续绘制
            if (arg0.getNewAction() != Action.PAN) {
                endAction();
            } else {
                commitEditing();
            }
        } else if (arg0.getOldAction() == Action.PAN && arg0.getNewAction() != Action.CREATETEXT) {

            // 在漫游状态，改变为其他 Action，触发这个事件，表明在绘制中进行的漫游，如果切换为CreateText 之外的 Action，那么就结束绘制
            endAction();
        } else if (arg0.getNewAction() == Action.CREATETEXT) {

            // 开始文本对象绘制
            startAction();
        }
    }

    /**
     * Shift + Enter 换行，Enter 结束编辑
     */
    private KeyListener keyListener = new TextActionKeyListener();

    public CreateTextAction() {
        this.textFieldInput.setBorder(null);
        this.textFieldInput.setSize(new Dimension(25, DEFAULT_INPUT_HEIGHT));
        this.textFieldInput.setFont(this.textFieldInput.getFont().deriveFont(Font.PLAIN, (float) DEFAULT_INPUT_FONT_SIZE));
        this.textFieldInput.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                resizeInput();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                resizeInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // nothing
            }
        });
    }

    /**
     * 开始文本绘制
     */
    public void Start(MapControl mapControl) {
        if (this.mapControl != null && this.mapControl != mapControl) {
            endAction();
        }

        this.mapControl = mapControl;
        // 先移除监听，避免在 CreateAction 状态下，多次点击导致多次注册监听
        this.mapControl.removeActionChangedListener(this.actionChangedListener);
        // 添加监听，再设置 Action，才能在 ActionChanged 里处理
        this.mapControl.addActionChangedListener(this.actionChangedListener);

        if (this.mapControl.getAction() != Action.CREATETEXT) {
            this.mapControl.setAction(Action.CREATETEXT);
            // 激活当前窗口以响应按键。
            this.mapControl.requestFocusInWindow();
        } else {
            this.mapControl.setAction(Action.SELECT2);
        }
        Application.getActiveApplication().getOutput().output(MapEditorProperties.getString("String_CreateTextAction_Tip"));
    }

    private void startAction() {
        this.preLayout = this.mapControl.getLayout();
        this.mapControl.setLayout(null);
        this.mapControl.add(this.textFieldInput);
        this.mapControl.addTrackedListener(this.trackedListener);
        this.mapControl.getMap().addMapClosedListener(this.mapClosedListener);
        this.textFieldInput.addKeyListener(this.keyListener);
    }

    private void endAction() {
        if (!this.textFieldInput.getText().isEmpty()) {
            commitEditing();
        }

        reset();
    }

    private void cancel() {
        reset();
        this.mapControl.setAction(Action.SELECT2);
    }

    /**
     * 还原 mapControl 至原状态
     */
    private void reset() {
        this.textFieldInput.removeKeyListener(this.keyListener);
        this.mapControl.remove(this.textFieldInput);
        this.mapControl.setLayout(this.preLayout);
        this.mapControl.removeTrackedListener(this.trackedListener);
        this.mapControl.removeActionChangedListener(this.actionChangedListener);
    }

    private void mapControlTracked(TrackedEvent e) {
        commitEditing();

        // 当地图在经纬坐标下时，鼠标点击位置在经纬范围之外，不做任何事情并输出提示
        if (this.mapControl.getMap().getPrjCoordSys() != null
                && this.mapControl.getMap().getPrjCoordSys().getType() == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE
                && e.getGeometry() != null
                && (e.getGeometry().getInnerPoint().getX() > 180 || e.getGeometry().getInnerPoint().getX() < -180
                || e.getGeometry().getInnerPoint().getY() > 90 || e.getGeometry().getInnerPoint().getY() < -90)) {
            // 什么都不做，输出提示信息的工作已经由 FormMap 全权负责
        } else {
            this.editingGeoText = (GeoText) e.getGeometry();
            this.editingGeoText.getTextStyle().setSizeFixed(false);
            // DEFAULT_INPUT_HEIGHT / 2 是一个经验值，使得不固定大小的时候，最后绘制到地图上的文本大小与输入的时候基本一致
            // 绘制时暂时设置为宋体，windows下能支持，linux下暂不支持
            this.editingGeoText.getTextStyle().setFontName(CoreProperties.getString("String_Font"));
            if (DEFAULT_INPUT_HEIGHT / 2 * MapUtilities.pixelLength(this.mapControl) > 0) {
                this.editingGeoText.getTextStyle().setFontHeight(DEFAULT_INPUT_HEIGHT / 2 * MapUtilities.pixelLength(this.mapControl));
            }
            // 获取 GeoText 的位置，将文本编辑控件显示到那个位置
            Point2D centerPointMap = this.editingGeoText.getInnerPoint();
            Point inputLocation = this.mapControl.getMap().mapToPixel(centerPointMap);

            this.textFieldInput.setLocation(inputLocation);
            this.textFieldInput.setVisible(true);
            this.textFieldInput.requestFocus();
        }
    }

    private void commitEditing() {
        Recordset recordset = null;

        try {
            IForm activeForm = Application.getActiveApplication().getActiveForm();

            recordset = finishCommit(recordset, activeForm);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            // Action结束之前，编辑提交之后，下一次编辑开始之前，隐藏编辑控件
            this.textFieldInput.setText("");
            this.textFieldInput.setVisible(false);
            if (this.editingGeoText != null) {
                this.editingGeoText.dispose();
            }
            if (recordset != null) {
                recordset.dispose();
            }
        }
    }

    private Recordset finishCommit(Recordset recordset, IForm activeForm) {
        if (activeForm instanceof IFormMap) {
            Layer activeEditableLayer = ((IFormMap) activeForm).getMapControl().getActiveEditableLayer();

            if (activeEditableLayer.getDataset() instanceof DatasetVector
                    && (activeEditableLayer.getDataset().getType() == DatasetType.TEXT || activeEditableLayer.getDataset().getType() == DatasetType.CAD)) {
                recordset = ((DatasetVector) activeEditableLayer.getDataset()).getRecordset(false, CursorType.DYNAMIC);
                // 表明有待提交的编辑
                if (this.editingGeoText != null) {
                    String text = this.textFieldInput.getText();
                    if (!StringUtilities.isNullOrEmpty(text)) {
                        TextPart textPart = new TextPart(text, this.editingGeoText.getPart(0).getAnchorPoint());
                        this.editingGeoText.setPart(0, textPart);

                        // 文本默认风格设置 2017.1.13 李逍遥 part7   共计part9

                        IFormMap formMap=((IFormMap) activeForm);
                        if (formMap.getDefaultTextStyle()!=null){
                            this.editingGeoText.setTextStyle(formMap.getDefaultTextStyle());
                            //this.editingGeoText.getTextStyle().setSizeFixed(formMap.getDefaultTextStyle().isSizeFixed());
                        }
                        if (Double.compare(formMap.getDefaultTextRotationAngle(),0)!=0){
                            this.editingGeoText.getPart(0).setRotation(formMap.getDefaultTextRotationAngle());
                        }

                        recordset.addNew(this.editingGeoText);
                        recordset.update();
                        mapControl.getEditHistory().add(EditType.ADDNEW, recordset, true);
                        mapControl.getEditHistory().batchEnd();
                        // 选中新添加的文本对象
                        recordset.moveLast();
                        activeEditableLayer.getSelection().clear();
                        activeEditableLayer.getSelection().add(recordset.getID());
                        this.mapControl.getMap().refresh();
                    }
                }
            }
        }
        return recordset;
    }

    /**
     * 计算输入的文本长度，动态调整输入框的大小
     */
    private void resizeInput() {
        String text = this.textFieldInput.getText();
        String[] lines = text.split(System.lineSeparator());
        FontMetrics fontMetrics = this.textFieldInput.getFontMetrics(this.textFieldInput.getFont());

        int textWidth = 0;
        for (int i = 0; i < lines.length; i++) {
            int lineWidth = fontMetrics.stringWidth(lines[i]);
            textWidth = Math.max(textWidth, lineWidth);
        }

        // 有一个换行符就增加一行的高度
        int lineCount = 1 + getLineSeparatorCount(text);
        int textHeight = fontMetrics.getHeight() * lineCount + 5 * (lineCount - 1);
        this.textFieldInput.setSize(new Dimension(textWidth + 5, textHeight));
    }

    private int getLineSeparatorCount(String text) {
        int lineSeparatorCount = 0;

        int fromIndex = 0;
        while (text.indexOf(System.lineSeparator(), fromIndex) >= 0) {
            fromIndex = text.indexOf(System.lineSeparator(), fromIndex) + 1;
            lineSeparatorCount++;
        }
        return lineSeparatorCount;
    }

    private class TextActionKeyListener extends KeyAdapter {

        private List<Integer> pressedKeys = new ArrayList<Integer>();
        private Timer timer = null;

        public TextActionKeyListener() {

            // 延迟 0.5S 从按下的按键中移除释放的 Shift 按键
            timer = new Timer(500, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    removeKey(KeyEvent.VK_SHIFT);
                }
            });
            timer.setRepeats(false);
        }

        /**
         * Invoked when a key has been pressed.
         */
        public void keyPressed(KeyEvent e) {

            // 如果按 Shift 太快，导致上一次的 Shift 释放 Timer 还正在运行，就强制停止 Timer
            if (e.getKeyCode() == KeyEvent.VK_SHIFT && this.timer.isRunning()) {
                this.timer.stop();
            }

            if (!pressedKeys.contains(e.getKeyCode())) {
                pressedKeys.add(new Integer(e.getKeyCode()));
            }
	        // 如果按下的只有 enter，那么就在释放按键的时候结束编辑
	        if (pressedKeys.size() == 1 && pressedKeys.get(0) == KeyEvent.VK_ENTER && e.getKeyCode() == KeyEvent.VK_ENTER) {
		        commitEditing();
	        }
        }

        /**
         * Invoked when a key has been released.
         */
        public void keyReleased(final KeyEvent e) {

            try {
                  // 如果按下的只有 enter，那么就在释放按键的时候结束编辑
//                if (pressedKeys.size() == 1 && pressedKeys.get(0) == KeyEvent.VK_ENTER && e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    commitEditing();
//                }

                // 如果按下的第一个按键是 Shift，第二个按键是 Enter，并且释放的按键也是 Enter，那么就换行
                if (pressedKeys.size() == 2 && pressedKeys.get(0) == KeyEvent.VK_SHIFT && pressedKeys.get(1) == KeyEvent.VK_ENTER
                        && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    CreateTextAction.this.textFieldInput.setText(CreateTextAction.this.textFieldInput.getText() + System.lineSeparator());
                }

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    timer.start();
                } else {
                    removeKey(e.getKeyCode());
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    // 释放 enter 键的时候，如果 timer 还在运行，则强制结束 timer，并移除可能存在 Shift 记录
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                        removeKey(KeyEvent.VK_SHIFT);
                    }
                }

                // ESC 结束编辑
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && CreateTextAction.this.mapControl != null) {
                    cancel();
                }
            } catch (Exception e2) {
                Application.getActiveApplication().getOutput().output(e2);
            }
        }

        private void removeKey(int keyCode) {
            if (pressedKeys.contains(keyCode)) {
                pressedKeys.remove(new Integer(keyCode));
            }
        }
    }
}
