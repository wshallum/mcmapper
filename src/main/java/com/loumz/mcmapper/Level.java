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
import java.io.IOException;

public class Level {
    File baseFile;
    private boolean initialized;
    private Rectangle bounds;

    public Level(File baseFile) {
        this.baseFile = baseFile;
    }

    private void initialize() {
        if (this.initialized) {
            return;
        }
        this.bounds = LevelUtil.getLevelBounds(baseFile.getParentFile());
        this.initialized = true;
    }

    public Rectangle getBounds() {
        this.initialize();
        return this.bounds;
    }

    public Chunk getChunk(int x, int z) {
        this.initialize();
        int maxX = this.bounds.x + this.bounds.width;
        int maxZ = this.bounds.y + this.bounds.height;
        if (x < this.bounds.x || x > maxX) {
            return null;
        }
        if (z < this.bounds.y || z > maxZ) {
            return null;
        }
        try {
            return this.loadChunk(x, z);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Chunk loadChunk(int x, int z) throws IOException {
        String xDirName = LevelUtil.to36(((x % 64) + 64) % 64);
        String zDirName = LevelUtil.to36(((z % 64) + 64) % 64);
        String fileName = String.format("c.%s.%s.dat", LevelUtil.to36(x), LevelUtil.to36(z));
        File f = new File(this.baseFile.getParent(), xDirName);
        f = new File(f, zDirName);
        f = new File(f, fileName);
        if (!f.exists()) {
            return null;
        }
        Chunk c = new Chunk(f);
        // this.cacheChunk(x, z, c);
        return c;
    }
}
