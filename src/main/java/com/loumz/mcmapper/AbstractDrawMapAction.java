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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AbstractDrawMapAction implements Runnable {
    protected File baseFile;
    protected PickerFrame frame;
    protected ExecutorService executor;

    public AbstractDrawMapAction(PickerFrame frame, File baseFile) {
        this.frame = frame;
        this.baseFile = baseFile;
        this.executor = createExecutor();
    }

    /**
     * Returns the executor to be used by this class to execute actions in multiple threads.
     *
     * @return the executor to be used.
     */
    protected abstract ExecutorService createExecutor();

    /**
     * Set status message.
     *
     * @param s the status message to display
     */
    protected void setStatus(final String s) {
        if (frame != null) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    frame.setStatusText(s);
                }
            });
        }
    }

    /**
     * Writes the composite image to disk and displays it.
     *
     * @throws Exception if it fails.
     */
    protected abstract void writeAndDisplayImage() throws Exception;

    /**
     * Creates the canvas to composite on.
     */
    protected abstract void createCompositeCanvas();

    /**
     * Create callable that will render the chunk to the BufferedImage.
     *
     * @param chunk         the chunk to render
     * @param bufferedImage the image to render to
     * @return the callable
     */
    protected abstract Callable<Object> createCallable(Chunk chunk, BufferedImage bufferedImage);

    /**
     * Create a single BufferedImage for a chunk.
     *
     * @return the BufferedImage
     */
    protected abstract BufferedImage createBufferedImage();

    /**
     * Sort chunks for batching & rendering.
     */
    protected abstract void sortChunks();

    /**
     * Get chunks from the fs.
     */
    protected abstract void gatherChunks();

    public void run() {
        try {

            setStatus("Gathering chunks...");
            gatherChunks();
            createCompositeCanvas();
            setStatus("Sorting chunks...");
            sortChunks();
            java.util.List<BufferedImage> batchImages = new ArrayList<BufferedImage>();
            java.util.List<Chunk> nextBatch = new ArrayList<Chunk>();
            int totalChunks = getTotalChunks();
            int chunksRendered = 0;
            while (hasChunks()) {
                getNextBatch(nextBatch);
                setStatus("Rendering... " + chunksRendered + "/" + totalChunks);
                renderChunkBatch(nextBatch, batchImages);
                chunksRendered += nextBatch.size();
                renderBatchResult(nextBatch, batchImages);
            }
            setStatus("Writing image...");
            writeAndDisplayImage();
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (frame != null) {
                setStatus("Exception: " + e.toString());
            }
        }
    }

    /**
     * Get total number of chunks. Used for progress display.
     *
     * @return the number of chunks.
     */
    protected abstract int getTotalChunks();

    /**
     * Render the result onto the composite image.
     *
     * @param chunks the chunks that were rendered
     * @param images the images resulting from rendering the chunks
     */
    protected abstract void renderBatchResult(java.util.List<Chunk> chunks, java.util.List<BufferedImage> images);

    protected final void renderChunkBatch(java.util.List<Chunk> batch, java.util.List<BufferedImage> batchImages) {
        java.util.List<Callable<Object>> callables = new ArrayList<Callable<Object>>(batch.size());
        while (batchImages.size() < batch.size()) {
            batchImages.add(createBufferedImage());
        }
        for (int i = 0; i < batch.size(); i++) {
            callables.add(createCallable(batch.get(i), batchImages.get(i)));
        }
        try {
            java.util.List<Future<Object>> futures = this.executor.invokeAll(callables);
            for (Future<Object> f : futures) {
                if (!f.isDone()) {
                    System.err.println("oops!");
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the next batch of chunks. A batch of chunks can be rendered and composited together without any
     * precedence problems.
     *
     * @param nextBatch list to fill in with the next batch of chunks
     */
    protected abstract void getNextBatch(java.util.List<Chunk> nextBatch);

    /**
     * Get maximum number of chunks to be rendered at one time.
     * This also controls the number of chunks and BufferedImages loaded at one time.
     *
     * @return the maximum batch size.
     */
    protected abstract int getMaxBatchSize();

    protected abstract boolean hasChunks();
}
