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

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.Sanselan;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrawMapAction extends AbstractDrawMapAction implements Comparator<Chunk> {
    protected ArrayList<Chunk> chunks;
    protected Rectangle bounds;
    protected int chunkCursor;
    protected BufferedImage compositeCanvas;
    protected MapOrientation orientation;

    public DrawMapAction(File baseFile, PickerFrame frame, MapOrientation orientation) {
        super(frame, baseFile);
        this.orientation = orientation;
    }

    @Override
    protected ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(3);
    }

    @Override
    protected void writeAndDisplayImage() throws Exception {
        Graphics2D g2 = this.compositeCanvas.createGraphics();
        g2.setPaint(Color.white);
        Rectangle rect = new Rectangle(0, this.compositeCanvas.getHeight() - 150, this.compositeCanvas.getWidth(), 150);
        g2.fill(rect);
        g2.clip(rect);
        AffineTransform at = g2.getTransform();
        g2.translate(rect.x, rect.y);
        Font mono = new Font(Font.DIALOG, Font.BOLD, 12);
        g2.setFont(mono);
        g2.setPaint(Color.black);
        Date dt = new Date();
        DateFormat d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String worldDirName = this.baseFile.getParentFile().getName();
        g2.drawString(String.format("MCMapper - %s - finished %s", worldDirName, d.format(dt)), 20, 20);
        g2.drawString("Orientation (up/right/down/left): " + this.orientation.toString(), 20, 60);
        g2.setTransform(at);
        g2.dispose();
        File outputFile = new File(this.baseFile.getParentFile(), "level.png");
        Sanselan.writeImage(this.compositeCanvas, outputFile, ImageFormat.IMAGE_FORMAT_PNG, null);
        setStatus("OK");
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(outputFile);
            }
            catch (IOException e) {
                setStatus("OK - Please open " + outputFile.getAbsolutePath());
            }
        }
    }

    /**
     * Creates canvas used for compositing the chunk images.
     */
    @Override
    protected void createCompositeCanvas() {
        this.compositeCanvas = new BufferedImage(
                16 * (this.orientation.isMinecraftXImageX() ? this.bounds.width : this.bounds.height),
                16 * (this.orientation.isMinecraftXImageX() ? this.bounds.height : this.bounds.width) + 150, 
                BufferedImage.TYPE_INT_ARGB_PRE);
    }

    /**
     * Creates callable object that renders the chunk into the buffered image.
     *
     * @param chunk         the chunk to render
     * @param bufferedImage the buffered image to render to
     * @return the callable
     */
    @Override
    protected Callable<Object> createCallable(final Chunk chunk, final BufferedImage bufferedImage) {
        return new Callable<Object>() {
            public Object call() throws Exception {
                chunk.load();
                try {
                    WritableRaster rs = bufferedImage.getRaster();
                    rs.setPixels(0, 0, 16, 16, new int[16 * 16 * 4]);
                    int[] rgbValues = new int[4];
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 127; y >= 0; y--) {
                                byte b = chunk.getBlockAt(x, z, y);
                                if (b != 0) {
                                    LevelUtil.getRgbaValues(b, rgbValues);
                                    int imageX = orientation.isMinecraftXImageX() ? x : z;
                                    int imageY = orientation.isMinecraftXImageX() ? z : x;
                                    if (!orientation.isRightPositive()) {
                                        imageX = 15 - imageX;
                                    }
                                    if (!orientation.isDownPositive()) {
                                        imageY = 15 - imageY;
                                    }
                                    rs.setPixel(imageX, imageY, rgbValues);
                                    break;
                                }
                            }
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
     * Creates image used for one chunk.
     *
     * @return BufferedImage
     */
    @Override
    protected BufferedImage createBufferedImage() {
        return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB_PRE);
    }


    public int compare(Chunk o1, Chunk o2) {
        // sort by image-y, image-x
        int dx = o1.getX() - o2.getX();
        int dz = o1.getZ() - o2.getZ();

        int dimgx = 0;
        int dimgy = 0;

        if (this.orientation.isMinecraftXImageX()) {
            dimgx = dx;
            dimgy = dz;
        }
        else {
            dimgx = dz;
            dimgy = dx;
        }

        if (!this.orientation.isDownPositive()) {
            dimgy = -dimgy;
        }
        if (!this.orientation.isRightPositive()) {
            dimgx = -dimgx;
        }
        if (dimgy != 0) {
            return dimgy;
        }
        return dimgx;
    }

    @Override
    protected void getNextBatch(List<Chunk> nextBatch) {
        nextBatch.clear();
        if (!hasChunks()) {
            return;
        }
        Chunk c = this.chunks.get(this.chunkCursor);
        int x = c.getX();
        nextBatch.add(c);
        this.chunkCursor++;
        for (; this.chunkCursor < this.chunks.size(); this.chunkCursor++) {
            c = this.chunks.get(this.chunkCursor);
            if (c.getX() == x) {
                nextBatch.add(c);
                if (nextBatch.size() >= this.getMaxBatchSize()) {
                    this.chunkCursor++;
                    return;
                }
            }
            else {
                return;
            }
        }
    }

    @Override
    protected int getMaxBatchSize() {
        return 10;
    }

    @Override
    protected final boolean hasChunks() {
        return this.chunkCursor < this.chunks.size();
    }

    @Override
    protected void sortChunks() {
        // sort chunks.
        Collections.sort(this.chunks, this);
        this.chunkCursor = 0;
    }

    /**
     * Gathers chunk names. Also sets bounds based on chunk name.
     */
    @Override
    protected void gatherChunks() {
        this.chunks = new ArrayList<Chunk>();
        final FileFilter dirLevelFilter = new FileFilter() {

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
        final FileFilter levelFilter = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
                }
                String name = pathname.getName();
                Matcher m = p.matcher(name);
                if (m.matches()) {
                    return true;
                }
                return false;
            }
        };

        final File[] firstLevelFiles = this.baseFile.getParentFile().listFiles(dirLevelFilter);
        // split firstLevelFiles into 2 and spawn 2 threads for I/O
        int half = firstLevelFiles.length / 2;
        int rest = firstLevelFiles.length - half;
        File[][] ff = new File[][]{
                new File[half],
                new File[rest],
        };
        System.arraycopy(firstLevelFiles, 0, ff[0], 0, half);
        System.arraycopy(firstLevelFiles, half, ff[1], 0, rest);
        Future<Object[]>[] futures = new Future[ff.length];
        for (int i = 0; i < ff.length; i++) {
            final File[] files = ff[i];
            futures[i] = this.executor.submit(new Callable<Object[]>() {
                public Object[] call() throws Exception {
                    int minX = 0;
                    int minZ = 0;
                    int maxX = 0;
                    int maxZ = 0;

                    ArrayList<Chunk> threadChunks = new ArrayList<Chunk>();

                    for (File firstLevelDir : files) {
                        File[] secondLevelFiles = firstLevelDir.listFiles(dirLevelFilter);
                        for (File secondLevelDir : secondLevelFiles) {
                            File[] levelChunks = secondLevelDir.listFiles(levelFilter);
                            for (File f : levelChunks) {
                                Chunk c = null;
                                try {
                                    c = new Chunk(f);
                                    minX = Math.min(minX, c.getX());
                                    minZ = Math.min(minZ, c.getZ());
                                    maxX = Math.max(maxX, c.getX());
                                    maxZ = Math.max(maxZ, c.getZ());
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    c = null;
                                }
                                if (c != null) {
                                    threadChunks.add(c);
                                }
                            }
                        }
                    }
                    return new Object[]{threadChunks, Integer.valueOf(minX), Integer.valueOf(minZ), Integer.valueOf(maxX), Integer.valueOf(maxZ)};
                }
            });
        }
        int minX = 0;
        int minZ = 0;
        int maxX = 0;
        int maxZ = 0;
        for (int i = 0; i < futures.length; i++) {
            try {
                Object[] objs = futures[i].get();
                this.chunks.addAll((List<Chunk>) objs[0]);
                minX = Math.min(minX, ((Integer) objs[1]).intValue());
                minZ = Math.min(minZ, ((Integer) objs[2]).intValue());
                maxX = Math.max(maxX, ((Integer) objs[3]).intValue());
                maxZ = Math.max(maxZ, ((Integer) objs[4]).intValue());
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Rectangle result = new Rectangle(minX, minZ, maxX - minX + 1, maxZ - minZ + 1);
        this.bounds = result;
    }

    @Override
    protected int getTotalChunks() {
        return this.chunks.size();
    }

    protected void renderBatchResult(List<Chunk> chunks, List<BufferedImage> images) {

        int h = 16 * this.bounds.height;
        Graphics2D g = this.compositeCanvas.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = chunks.get(i);
            int absX = -this.bounds.x + chunk.getX();
            int absZ = -this.bounds.y + chunk.getZ();

            int imgXTile; int imgYTile;
            int maxImgXTile, maxImgYTile;

            if (this.orientation.isMinecraftXImageX()) {
                imgXTile = absX;
                imgYTile = absZ;
                maxImgXTile = this.bounds.width - 1;
                maxImgYTile = this.bounds.height - 1;
            }
            else {
                imgXTile = absZ;
                imgYTile = absX;
                maxImgXTile = this.bounds.height - 1;
                maxImgYTile = this.bounds.width - 1;
            }

            if (!this.orientation.isDownPositive()) {
                imgYTile = maxImgYTile - imgYTile;
            }
            if (!this.orientation.isRightPositive()) {
                imgXTile = maxImgXTile - imgXTile;
            }

            g.drawImage(images.get(i), imgXTile * 16, imgYTile * 16, null);
        }
        g.dispose();
    }


}
