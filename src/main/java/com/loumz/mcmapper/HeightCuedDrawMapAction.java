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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class HeightCuedDrawMapAction extends DrawMapAction {
    final int[] clear = new int[16 * 16 * 4];
    public HeightCuedDrawMapAction(File baseFile, PickerFrame frame, MapOrientation orientation) {
        super(baseFile, frame, orientation);
        Arrays.fill(clear, 255);
    }

    @Override
    protected Callable<Object> createCallable(final Chunk chunk, final BufferedImage bufferedImage) {
        return new Callable<Object>() {
            public Object call() throws Exception {
                WritableRaster rs = bufferedImage.getRaster();
                rs.setPixels(0, 0, 16, 16, clear);
                chunk.load();
                try {
                    int[] rgbValues = new int[4];
                    int[] currentValues = new int[4];
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            Arrays.fill(currentValues, 0);
                            for (int y = 127; y >= 0; y--) {
                                byte b = chunk.getBlockAt(x, z, y);
                                if (b == 0) {
                                    continue;
                                }
                                LevelUtil.getRgbaValues(b, rgbValues);
                                if (isFiery(b)) {
                                    blend(currentValues, rgbValues, 128);
                                }
                                else {
                                    int l = getLighting(chunk, x, z, y + 1) * 4;
                                    int brightness = Math.min((int) (0.8 * l + 0.5 * y), 128);
                                    if (isWatery(b)) {
                                        byte blockAbove = (y == 127) ? 0 : chunk.getBlockAt(x, z, y + 1);
                                        if (blockAbove == 0) {
                                            rgbValues[3] = 128;
                                            blend(currentValues, rgbValues, brightness);
                                        }
                                    }
                                    else {
                                        blend(currentValues, rgbValues, brightness);
                                    }
                                }
                                if (currentValues[3] >= 255) {
                                    break;
                                }
                            }

                            int imageX = orientation.isMinecraftXImageX() ? x : z;
                            int imageY = orientation.isMinecraftXImageX() ? z : x;
                            if (!orientation.isRightPositive()) {
                                imageX = 15 - imageX;
                            }
                            if (!orientation.isDownPositive()) {
                                imageY = 15 - imageY;
                            }

                            rs.setPixel(imageX, imageY, currentValues);
                        }
                    }

                }
                finally {
                    chunk.unload();
                }
                return null;
            }
        };
    }

    /**
     * Blend A over B and store into A.
     * @param a array A (R/G/B/A)
     * @param b array B (R/G/B/A)
     * @param brightness used to scale R/G/B values of b. 128 = full values, 0 = 0.
     */
    protected void blend(int[] a, int[] b, int brightness) {
        float alphaA = a[3] / 255.0f;
        float alphaB = b[3] / 255.0f;
        float alphaResult = alphaA + alphaB * (1 - alphaA);
        float brightnessMultiplier = brightness / 128.0f;
        a[0] = (int) ((a[0] * alphaA + b[0] * brightnessMultiplier * alphaB * (1 - alphaA)) / alphaResult);
        a[1] = (int) ((a[1] * alphaA + b[1] * brightnessMultiplier * alphaB * (1 - alphaA)) / alphaResult);
        a[2] = (int) ((a[2] * alphaA + b[2] * brightnessMultiplier * alphaB * (1 - alphaA)) / alphaResult);
        a[3] = (int) (alphaResult * 255);
    }

    protected boolean isWatery(byte b) {
        switch (b) {
            case 8:
            case 9:
            case 79:
                return true;
            default:
                return false;
        }
    }

    protected boolean isFiery(byte b) {
        switch (b) {
            case 10:
            case 11:
            case 50:
            case 51:
            case 76:
                return true;
            default:
                return false;
        }
    }

    protected byte getLighting(Chunk chunk, int x, int z, int y) {
        if (y > 127) {
            return 16;
        }
        byte s = chunk.getSkyLightAt(x, z, y);
        byte b = chunk.getBlockLightAt(x, z, y);
        return (byte) (Math.max(s, b) + 1);
    }
}
