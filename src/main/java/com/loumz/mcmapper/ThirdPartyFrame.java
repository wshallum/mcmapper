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

public class ThirdPartyFrame extends Frame {
    public ThirdPartyFrame() {
        setTitle("Third-party Copyrights");
        URL url = getClass().getResource("THIRD-PARTY-LICENSES");
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
        setSize(400, 500);
        setLayout(new BorderLayout());
        TextArea a = new TextArea(licenseText);
        a.setEditable(false);
        add(a, BorderLayout.CENTER);
        Panel p = new Panel();
        Button ok = new Button("OK");
        p.add(ok);
        add(p, BorderLayout.SOUTH);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ThirdPartyFrame.this.setVisible(false);
                ThirdPartyFrame.this.dispose();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ThirdPartyFrame.this.setVisible(false);
                ThirdPartyFrame.this.dispose();
            }
        });
        this.pack();        
    }
}
