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

import com.loumz.mcmapper.nbt.NbtByteArrayItem;
import com.loumz.mcmapper.nbt.NbtIntItem;
import com.loumz.mcmapper.nbt.NbtItem;
import com.loumz.mcmapper.nbt.NbtReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Chunk {
    private File file;
    private int x;
    private int z;
    private byte[] blockBytes;
    private byte[] skyLightBytes;
    private byte[] blockLightBytes;
    private boolean ok = true;

    public Chunk(File f) throws IOException {
        this.file = f;
        final Pattern p = Pattern.compile("^c\\.(-?[0-9a-z]+).(-?[0-9a-z]+)\\.dat$");
        Matcher m = p.matcher(f.getName());
        if (!m.matches()) {
            throw new IOException("File name " + f.getName() + " does not match pattern. Cannot get xz for sorting.");
        }
        this.x = LevelUtil.un36(m.group(1));
        this.z = LevelUtil.un36(m.group(2));
    }

    public void load() throws IOException {
        NbtReader r = new NbtReader(new FileInputStream(this.file));
        try {
            NbtItem item = null;
            this.blockBytes = null;

            item = r.readNext();
            while (item != null) {
                if (item.getClass() == NbtByteArrayItem.class) {
                    if ("Blocks".equals(item.getName())) {
                        this.blockBytes = ((NbtByteArrayItem) item).getValue();
                    }
                    else if ("BlockLight".equals(item.getName())) {
                        this.blockLightBytes = this.unpack(((NbtByteArrayItem) item).getValue());
                    }
                    else if ("SkyLight".equals(item.getName())) {
                        this.skyLightBytes = this.unpack(((NbtByteArrayItem) item).getValue());
                    }
                }
                else if (item.getClass() == NbtIntItem.class) {
                    if ("xPos".equals(item.getName())) {
                        int inFileX = ((NbtIntItem) item).getValue();
                        if (inFileX != this.x) {
                            throw new IOException("in-file x not equal to in-filename x");
                        }
                    }
                    else if ("xPos".equals(item.getName())) {
                        int inFileZ = ((NbtIntItem) item).getValue();
                        if (inFileZ != this.z) {
                            throw new IOException("in-file z not equal to in-filename z");
                        }
                    }
                }
                item = r.readNext();
            }
        }
        finally {
            r.close();
        }
    }

    public void unload() {
        this.blockBytes = null;
        this.blockLightBytes = null;
        this.skyLightBytes = null;
    }

    private byte[] unpack(byte[] value) {
        byte[] result = new byte[value.length * 2];
        for (int i = 0; i < value.length; i++) {
            result[i * 2] = (byte) (value[i] & 0xf);
            result[i * 2 + 1] = (byte) ((value[i] >> 4) & 0xf);
        }
        return result;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public byte getBlockAt(int x, int z, int y) {
        return this.blockBytes[x * 128 * 16 + z * 128 + y];
    }

    public byte getBlockLightAt(int x, int z, int y) {
        return this.blockLightBytes[x * 128 * 16 + z * 128 + y];
    }

    public byte getSkyLightAt(int x, int z, int y) {
        return this.skyLightBytes[x * 128 * 16 + z * 128 + y];
    }
}
