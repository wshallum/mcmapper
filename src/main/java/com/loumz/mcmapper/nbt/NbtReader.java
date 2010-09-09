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

package com.loumz.mcmapper.nbt;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class NbtReader {

    private static class TagExpectation {
        public int type;
        public int count;

        public TagExpectation(int type, int count) {
            this.type = type;
            this.count = count;
        }
    }

    private DataInputStream inputStream;
    private ArrayList<TagExpectation> expectedTypeStack;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public NbtReader(InputStream is) throws IOException {
        this.inputStream = new DataInputStream(new GZIPInputStream(is));
        this.expectedTypeStack = new ArrayList<TagExpectation>(10);
        this.expectedTypeStack.add(new TagExpectation(-1, 0));
    }

    private boolean isExpectingNamedTag() {
        return this.getCurrentTagExpectation().type == -1;
    }

    private TagExpectation getCurrentTagExpectation() {
        return this.expectedTypeStack.get(this.expectedTypeStack.size() - 1);
    }

    private TagExpectation popTagExpectation() {
        int lastIndex = this.expectedTypeStack.size() - 1;
        TagExpectation result = this.expectedTypeStack.get(lastIndex);
        this.expectedTypeStack.remove(lastIndex);
        return result;
    }

    private void pushTagExpectation(int type, int count) {
        this.expectedTypeStack.add(new TagExpectation(type, count));
    }

    public void close() throws IOException {
        this.inputStream.close();
    }

    public NbtItem readNext() throws IOException {
        String name = null;
        int tagType;
        if (this.isExpectingNamedTag()) {
            tagType = this.inputStream.read();
            if (tagType < 0) {
                this.close();
                return null;
            }
            if (tagType != NbtItem.TAG_END) {
                name = this.readString();
            }
        }
        else {
            tagType = this.getCurrentTagExpectation().type;
            this.decrementNumberOfExpectedTags();
            if (this.getCurrentTagExpectation().count < 0) {
                this.popTagExpectation();
                return new NbtTagEndItem();
            }
        }
        switch (tagType) {
            case NbtItem.TAG_END:
                this.popTagExpectation();
                return new NbtTagEndItem();
            case NbtItem.TAG_BYTE:
                return new NbtByteItem(name, this.readByte());
            case NbtItem.TAG_SHORT:
                return new NbtShortItem(name, this.readShort());
            case NbtItem.TAG_INT:
                return new NbtIntItem(name, this.readInt());
            case NbtItem.TAG_LONG:
                return new NbtLongItem(name, this.readLong());
            case NbtItem.TAG_FLOAT:
                return new NbtFloatItem(name, this.readFloat());
            case NbtItem.TAG_DOUBLE:
                return new NbtDoubleItem(name, this.readDouble());
            case NbtItem.TAG_BYTE_ARRAY:
                int length = this.readInt();
                byte[] bytes = this.readBytes(length);
                return new NbtByteArrayItem(name, bytes);
            case NbtItem.TAG_STRING:
                return new NbtStringItem(name, this.readString());
            case NbtItem.TAG_LIST:
                byte type = this.readByte();
                int count = this.readInt();
                this.pushTagExpectation(type, count);
                return new NbtListTagItem(name, type, count);
            case NbtItem.TAG_COMPOUND:
                this.pushTagExpectation(-1, 0);
                return new NbtCompoundTagItem(name);
        }
        return null;
    }

    private double readDouble() throws IOException {
        return this.inputStream.readDouble();

    }

    private void decrementNumberOfExpectedTags() {

        TagExpectation te = this.getCurrentTagExpectation();
        if (te.type < 0) {
            throw new AssertionError("Unexpected decr w/ te type=" + te.type);
        }
        te.count--;
    }

    private float readFloat() throws IOException {
        return this.inputStream.readFloat();
    }

    private long readLong() throws IOException {
        return this.inputStream.readLong();
    }

    private int readInt() throws IOException {
        return this.inputStream.readInt();
    }

    private byte readByte() throws IOException {
        int b = this.inputStream.read();
        if (b < 0) {
            throw new EOFException("EOF reading byte");
        }
        return (byte) b;
    }

    private String readString() throws IOException {
        short count = this.readShort();
        if (count < 0) {
            // TODO
        }
        byte[] bytes = this.readBytes(count);
        return new String(bytes, UTF8);
    }

    private byte[] readBytes(int count) throws IOException {
        byte[] buffer = new byte[count];
        this.inputStream.readFully(buffer);
        return buffer;
    }

    private short readShort() throws IOException {
        return this.inputStream.readShort();
    }
}
