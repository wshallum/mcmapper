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
import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelUtil {
    public static Rectangle getLevelBounds(File topDir) {
        FileFilter dirLevelFilter = new FileFilter() {

            public boolean accept(File pathname) {
                if (!pathname.isDirectory()) {
                    return false;
                }
                String name = pathname.getName();
                if (name.length() > 2) {
                    return false;
                }
                char firstChar = name.charAt(0);
                char secondChar = name.length() == 2 ? name.charAt(1) : 0;
                if (name.length() == 2) {
                    return firstChar == '1' && (((secondChar >= '0') && (secondChar <= '9')) || ((secondChar >= 'a') && (secondChar <= 's')));
                }
                else {
                    return ((firstChar >= '0') && (firstChar <= '9')) || ((firstChar >= 'a') && (firstChar <= 'z'));
                }
            }
        };
        final Pattern p = Pattern.compile("^c\\.(-?[0-9a-z]+).(-?[0-9a-z]+)\\.dat$");
        final Rectangle result = new Rectangle(0, 0, 0, 0);
        FileFilter levelFilter = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
                }
                String name = pathname.getName();
                Matcher m = p.matcher(name);
                if (m.matches()) {
                    String x36 = m.group(1);
                    String z36 = m.group(2);
                    int x = un36(x36);
                    int z = un36(z36);
                    if (x < result.x) {
                        result.x = x;
                    }
                    if (x > result.width) {
                        result.width = x;
                    }
                    if (z < result.y) {
                        result.y = z;
                    }
                    if (z > result.height) {
                        result.height = z;
                    }
                    return true;
                }
                return false;
            }
        };

        File[] firstLevelFiles = topDir.listFiles(dirLevelFilter);
        for (File firstLevelDir : firstLevelFiles) {
            File[] secondLevelFiles = firstLevelDir.listFiles(dirLevelFilter);
            for (File secondLevelDir : secondLevelFiles) {
                File[] levelChunks = secondLevelDir.listFiles(levelFilter);
            }
        }
        result.width -= result.x;
        result.height -= result.y;
        return result;
    }

    public static int un36(String n36) {
        int mul = 1;
        if (n36.startsWith("-")) {
            mul = -1;
            n36 = n36.substring(1);
        }
        int n = 0;
        char[] ch = n36.toCharArray();
        for (char aCh : ch) {
            n *= 36;
            if (aCh >= '0' && aCh <= '9') {
                n += (aCh - '0');
            }
            else if (aCh >= 'a' && aCh <= 'z') {
                n += 10 + (aCh - 'a');
            }
        }
        return mul * n;
    }

    public static String to36(int n) {
        return Integer.toString(n, 36);
    }

    private static int[] RGBA_VALUES = new int[]{
            255, 255, 255, 0,
            120, 120, 120, 255,
            117, 176, 73, 255,
            134, 96, 67, 255,
            115, 115, 115, 255,
            157, 128, 79, 255,
            120, 120, 120, 0,
            84, 84, 84, 255,
            38, 92, 255, 51,
            38, 92, 255, 51,
            255, 90, 0, 255,
            255, 90, 0, 255,
            218, 210, 158, 255,
            136, 126, 126, 255,
            143, 140, 125, 255,
            136, 130, 127, 255,
            115, 115, 115, 255,
            102, 81, 51, 255,
            60, 192, 41, 100,
            0, 0, 0, 0,
            255, 255, 255, 64,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            222, 222, 222, 255,
            0, 0, 0, 0,
            255, 255, 0, 255,
            255, 0, 0, 255,
            0, 0, 0, 0,
            0, 0, 0, 0,
            231, 165, 45, 255,
            191, 191, 191, 255,
            200, 200, 200, 255,
            200, 200, 200, 255,
            170, 86, 62, 255,
            160, 83, 65, 255,
            0, 0, 0, 0,
            115, 115, 115, 255,
            26, 11, 43, 255,
            245, 220, 50, 200,
            255, 170, 30, 200,
            0, 0, 0, 0,
            157, 128, 79, 255,
            125, 91, 38, 255,
            0, 0, 0, 0,
            129, 140, 143, 255,
            45, 166, 152, 255,
            114, 88, 56, 255,
            146, 192, 0, 255,
            95, 58, 30, 255,
            96, 96, 96, 255,
            96, 96, 96, 255,
            111, 91, 54, 255,
            136, 109, 67, 255,
            181, 140, 64, 32,
            150, 134, 102, 180,
            115, 115, 115, 255,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            191, 191, 191, 255,
            0, 0, 0, 0,
            131, 107, 107, 255,
            131, 107, 107, 255,
            181, 140, 64, 32,
            255, 0, 0, 200,
            0, 0, 0, 0,
            255, 255, 255, 255,
            83, 113, 163, 51,
            250, 250, 250, 255,
            25, 120, 25, 255,
            151, 157, 169, 255,
            100, 67, 50, 255,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
    };


    public static void getRgbaValues(byte b, int[] rgbValues) {
        int ofs = b * 4;
        if (ofs >= RGBA_VALUES.length) {
            ofs = 0;
        }
        System.arraycopy(RGBA_VALUES, ofs, rgbValues, 0, 4);
    }
}
