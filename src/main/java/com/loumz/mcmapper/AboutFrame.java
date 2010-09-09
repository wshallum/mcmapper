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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class AboutFrame extends Frame {
    public AboutFrame() {
        setTitle("About");
        setSize(400, 500);
        URL url = getClass().getResource("LICENSE");
        String licenseText = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            char[] c = new char[32768];
            int cread = br.read(c, 0, 32768);
            licenseText = new String(c, 0, cread);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Label copyrightLabel = new Label("MCMapper");
        copyrightLabel.setAlignment(Label.CENTER);
        setLayout(new BorderLayout());
        TextArea t = new TextArea(licenseText);
        t.setEditable(false);
        add(t, BorderLayout.CENTER);
        Button ok = new Button("OK");
        Button thirdParty = new Button("Third Party Copyrights...");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutFrame.this.setVisible(false);
                AboutFrame.this.dispose();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AboutFrame.this.setVisible(false);
                AboutFrame.this.dispose();
            }
        });
        thirdParty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ThirdPartyFrame().setVisible(true);
            }
        });
        Panel p = new Panel();
        p.add(thirdParty);
        p.add(ok);
        add(copyrightLabel, BorderLayout.NORTH);
        add(p, BorderLayout.SOUTH);
        this.pack();
    }
}
