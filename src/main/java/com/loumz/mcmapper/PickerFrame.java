/*
 * Copyright (c) 2010 William Shallum
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.loumz.mcmapper;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import com.jgoodies.forms.util.LayoutStyle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class PickerFrame extends Frame {

    private Label directoryLabel;

    private Button runButton;

    private Button pickButton;

    private String fileName;

    private Label pickLabel;

    private Label statusLabel;
    private Checkbox[] orientationCheckboxes = new Checkbox[8];
    private CheckboxGroup orientationCheckboxGroup;
    private MapOrientation[] checkboxOrientations;
    private Checkbox plainColoringCheckbox;
    private Checkbox heightCuedColoringCheckbox;

    public PickerFrame() {

        setTitle("MCMapper");

        LayoutStyle ls = LayoutStyle.getCurrent();

        ColumnSpec[] colSpecs = new ColumnSpec[]{
                new ColumnSpec(ColumnSpec.LEFT, ls.getDialogMarginX(), ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.RIGHT, Sizes.PREFERRED, ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.DEFAULT, ls.getLabelComponentPadX(), ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.DEFAULT, ls.getRelatedComponentsPadX(), ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.DEFAULT, ls.getRelatedComponentsPadX(), ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.DEFAULT, ls.getRelatedComponentsPadX(), ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.DEFAULT, ls.getRelatedComponentsPadX(), ColumnSpec.NO_GROW),
                new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, ColumnSpec.DEFAULT_GROW),
                new ColumnSpec(ColumnSpec.LEFT, ls.getDialogMarginX(), ColumnSpec.NO_GROW),
        };
        RowSpec[] rowSpecs = new RowSpec[]{
                new RowSpec(RowSpec.DEFAULT, ls.getDialogMarginY(), RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.PREFERRED, RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.bounded(ls.getUnrelatedComponentsPadY(), ls.getUnrelatedComponentsPadY(), Sizes.PREFERRED), RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.PREFERRED, RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.bounded(ls.getRelatedComponentsPadY(), ls.getRelatedComponentsPadY(), Sizes.PREFERRED), RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.PREFERRED, RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.bounded(ls.getUnrelatedComponentsPadY(), ls.getUnrelatedComponentsPadY(), Sizes.PREFERRED), RowSpec.DEFAULT_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.PREFERRED, RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.bounded(ls.getUnrelatedComponentsPadY(), ls.getUnrelatedComponentsPadY(), Sizes.PREFERRED), RowSpec.DEFAULT_GROW),
                new RowSpec(RowSpec.DEFAULT, Sizes.PREFERRED, RowSpec.NO_GROW),
                new RowSpec(RowSpec.DEFAULT, ls.getDialogMarginY(), RowSpec.NO_GROW),
        };
        this.setLayout(new FormLayout(colSpecs, rowSpecs));

        CellConstraints cc = new CellConstraints();
        this.add(pickLabel = new Label("Select level.dat"), cc.xy(2, 2));
        this.add(directoryLabel = new Label(), cc.xyw(4, 2, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        this.add(pickButton = new Button("Open..."), cc.xy(12, 2));

        this.add(new Label("Map Orientation"), cc.xy(2, 4));
        this.add(new Label("(up/right/down/left)"), cc.xy(2, 6));
        this.orientationCheckboxGroup = new CheckboxGroup();
        this.add(this.orientationCheckboxes[0] = new Checkbox("NESW (normal)", true, this.orientationCheckboxGroup), cc.xy(4, 4));
        this.add(this.orientationCheckboxes[1] = new Checkbox("WNES (+90)", false, this.orientationCheckboxGroup), cc.xy(6, 4));
        this.add(this.orientationCheckboxes[2] = new Checkbox("SWNE (+180)", false, this.orientationCheckboxGroup), cc.xy(8, 4));
        this.add(this.orientationCheckboxes[3] = new Checkbox("ESWN (+270)", false, this.orientationCheckboxGroup), cc.xy(10, 4));
        this.add(this.orientationCheckboxes[4] = new Checkbox("SENW (flip)", false, this.orientationCheckboxGroup), cc.xy(4, 6));
        this.add(this.orientationCheckboxes[5] = new Checkbox("WSEN (flip+90)", false, this.orientationCheckboxGroup), cc.xy(6, 6));
        this.add(this.orientationCheckboxes[6] = new Checkbox("NWSE (flip+180)", false, this.orientationCheckboxGroup), cc.xy(8, 6));
        this.add(this.orientationCheckboxes[7] = new Checkbox("ENWS (flip+270)", false, this.orientationCheckboxGroup), cc.xy(10, 6));
        this.checkboxOrientations = new MapOrientation[] { 
                MapOrientation.NESW, MapOrientation.WNES, MapOrientation.SWNE, MapOrientation.ESWN,
                MapOrientation.SENW, MapOrientation.WSEN, MapOrientation.NWSE, MapOrientation.ENWS
        };

        this.add(new Label("Coloring"), cc.xy(2,8));
        CheckboxGroup coloringCheckboxGroup = new CheckboxGroup();
        this.add(this.plainColoringCheckbox = new Checkbox("Plain", true, coloringCheckboxGroup), cc.xy(4, 8));
        this.add(this.heightCuedColoringCheckbox = new Checkbox("Cartograph-like", false, coloringCheckboxGroup), cc.xy(6, 8));

        this.add(statusLabel = new Label(), cc.xyw(4, 10, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        this.add(runButton = new Button("Run"), cc.xy(12, 10));

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });


        this.pickButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(PickerFrame.this, "Open level.dat", FileDialog.LOAD);
                String initialSavesDir = null;
                if (System.getProperty("os.name").startsWith("Mac")) {
                    initialSavesDir = System.getProperty("user.home") + "/Library/Application Support/minecraft/saves";
                }
                else if (System.getProperty("os.name").startsWith("Win")) {
                    initialSavesDir = System.getenv("APPDATA");
                    if (initialSavesDir != null) {
                        initialSavesDir += "\\minecraft\\saves";
                    }
                }
                else if (System.getProperty("os.name").equals("Linux")) {
                    initialSavesDir = System.getProperty("user.home") + ".minecraft/saves";
                }
                if (initialSavesDir != null) {
                    if (new File(initialSavesDir).exists()) {
                        fd.setDirectory(initialSavesDir);
                    }
                }
                fd.setVisible(true);
                String f = fd.getFile();
                String d = fd.getDirectory();
                if (f != null && d != null) {
                    File ff = new File(d, f);
                    File prev = null;
                    while (ff != null && !ff.getName().equals("saves")) {
                        prev = ff;
                        ff = ff.getParentFile();
                    }
                    if (ff != null && prev != null) {
                        PickerFrame.this.fileName = prev.getAbsolutePath();
                        PickerFrame.this.directoryLabel.setText(prev.getName());
                        PickerFrame.this.directoryLabel.setSize(PickerFrame.this.directoryLabel.getPreferredSize());
                    }
                }
            }
        });

        this.runButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (PickerFrame.this.fileName == null) {
                    return;
                }
                MapOrientation orientation = null;
                for (int i = 0; i < PickerFrame.this.orientationCheckboxes.length; i++) {
                    if (PickerFrame.this.orientationCheckboxes[i].getState()) {
                        orientation = PickerFrame.this.checkboxOrientations[i];
                    }
                }
                AbstractDrawMapAction dm = null;
                if (PickerFrame.this.plainColoringCheckbox.getState()) {
                    dm = new DrawMapAction(new File(PickerFrame.this.fileName, "level.dat"), PickerFrame.this, orientation);
                }
                else if (PickerFrame.this.heightCuedColoringCheckbox.getState()) {
                    dm = new HeightCuedDrawMapAction(new File(PickerFrame.this.fileName, "level.dat"), PickerFrame.this, orientation);
                }
                Thread t = new Thread(dm);
                t.start();
            }
        });

        this.pack();
        this.setMinimumSize(this.getPreferredSize());
    }

    public void setStatusText(String s) {
        this.statusLabel.setText(s);
    }
}
