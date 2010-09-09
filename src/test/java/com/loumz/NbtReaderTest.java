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

package com.loumz;

import com.loumz.mcmapper.nbt.*;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class NbtReaderTest extends TestCase {

    // test cases from http://www.minecraft.net/docs/NBT.txt

    private static final byte[] TEST_NBT = new byte[]{
            (byte) 0x1f, (byte) 0x8b, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xe3, (byte) 0x62, (byte) 0xe0, (byte) 0xce, (byte) 0x48, (byte) 0xcd,
            (byte) 0xc9, (byte) 0xc9, (byte) 0x57, (byte) 0x28, (byte) 0xcf, (byte) 0x2f, (byte) 0xca, (byte) 0x49, (byte) 0xe1, (byte) 0x60, (byte) 0x60, (byte) 0xc9, (byte) 0x4b, (byte) 0xcc, (byte) 0x4d, (byte) 0x65,
            (byte) 0xe0, (byte) 0x74, (byte) 0x4a, (byte) 0xcc, (byte) 0x4b, (byte) 0xcc, (byte) 0x2b, (byte) 0x4a, (byte) 0xcc, (byte) 0x4d, (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x77, (byte) 0xda, (byte) 0x5c,
            (byte) 0x3a, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };

    private static final byte[] BIGTEST_NBT = new byte[]{
            (byte) 0x1f, (byte) 0x8b, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xed, (byte) 0x54,
            (byte) 0xcf, (byte) 0x4f, (byte) 0x1a, (byte) 0x41, (byte) 0x14, (byte) 0x7e, (byte) 0xc2, (byte) 0x02, (byte) 0xcb, (byte) 0x96, (byte) 0x82, (byte) 0xb1,
            (byte) 0xc4, (byte) 0x10, (byte) 0x63, (byte) 0xcc, (byte) 0xab, (byte) 0xb5, (byte) 0x84, (byte) 0xa5, (byte) 0xdb, (byte) 0xcd, (byte) 0x42, (byte) 0x11,
            (byte) 0x89, (byte) 0xb1, (byte) 0x88, (byte) 0x16, (byte) 0x2c, (byte) 0x9a, (byte) 0x0d, (byte) 0x1a, (byte) 0xd8, (byte) 0xa8, (byte) 0x31, (byte) 0x86,
            (byte) 0xb8, (byte) 0x2b, (byte) 0xc3, (byte) 0x82, (byte) 0x2e, (byte) 0xbb, (byte) 0x66, (byte) 0x77, (byte) 0xb0, (byte) 0xf1, (byte) 0xd4, (byte) 0x4b,
            (byte) 0x7b, (byte) 0x6c, (byte) 0x7a, (byte) 0xeb, (byte) 0x3f, (byte) 0xd3, (byte) 0x23, (byte) 0x7f, (byte) 0x43, (byte) 0xcf, (byte) 0xbd, (byte) 0xf6,
            (byte) 0xbf, (byte) 0xa0, (byte) 0xc3, (byte) 0x2f, (byte) 0x7b, (byte) 0x69, (byte) 0xcf, (byte) 0xbd, (byte) 0xf0, (byte) 0x32, (byte) 0xc9, (byte) 0xf7,
            (byte) 0xe6, (byte) 0xbd, (byte) 0x6f, (byte) 0xe6, (byte) 0x7b, (byte) 0x6f, (byte) 0x26, (byte) 0x79, (byte) 0x02, (byte) 0x04, (byte) 0x54, (byte) 0x72,
            (byte) 0x4f, (byte) 0x2c, (byte) 0x0e, (byte) 0x78, (byte) 0xcb, (byte) 0xb1, (byte) 0x4d, (byte) 0x8d, (byte) 0x78, (byte) 0xf4, (byte) 0xe3, (byte) 0x70,
            (byte) 0x62, (byte) 0x3e, (byte) 0x08, (byte) 0x7b, (byte) 0x1d, (byte) 0xc7, (byte) 0xa5, (byte) 0x93, (byte) 0x18, (byte) 0x0f, (byte) 0x82, (byte) 0x47,
            (byte) 0xdd, (byte) 0xee, (byte) 0x84, (byte) 0x02, (byte) 0x62, (byte) 0xb5, (byte) 0xa2, (byte) 0xaa, (byte) 0xc7, (byte) 0x78, (byte) 0x76, (byte) 0x5c,
            (byte) 0x57, (byte) 0xcb, (byte) 0xa8, (byte) 0x55, (byte) 0x0f, (byte) 0x1b, (byte) 0xc8, (byte) 0xd6, (byte) 0x1e, (byte) 0x6a, (byte) 0x95, (byte) 0x86,
            (byte) 0x86, (byte) 0x0d, (byte) 0xad, (byte) 0x7e, (byte) 0x58, (byte) 0x7b, (byte) 0x8f, (byte) 0x83, (byte) 0xcf, (byte) 0x83, (byte) 0x4f, (byte) 0x83,
            (byte) 0x6f, (byte) 0xcf, (byte) 0x03, (byte) 0x10, (byte) 0x6e, (byte) 0x5b, (byte) 0x8e, (byte) 0x3e, (byte) 0xbe, (byte) 0xa5, (byte) 0x38, (byte) 0x4c,
            (byte) 0x64, (byte) 0xfd, (byte) 0x10, (byte) 0xea, (byte) 0xda, (byte) 0x74, (byte) 0xa6, (byte) 0x23, (byte) 0x40, (byte) 0xdc, (byte) 0x66, (byte) 0x2e,
            (byte) 0x69, (byte) 0xe1, (byte) 0xb5, (byte) 0xd3, (byte) 0xbb, (byte) 0x73, (byte) 0xfa, (byte) 0x76, (byte) 0x0b, (byte) 0x29, (byte) 0xdb, (byte) 0x0b,
            (byte) 0xe0, (byte) 0xef, (byte) 0xe8, (byte) 0x3d, (byte) 0x1e, (byte) 0x38, (byte) 0x5b, (byte) 0xef, (byte) 0x11, (byte) 0x08, (byte) 0x56, (byte) 0xf5,
            (byte) 0xde, (byte) 0x5d, (byte) 0xdf, (byte) 0x0b, (byte) 0x40, (byte) 0xe0, (byte) 0x5e, (byte) 0xb7, (byte) 0xfa, (byte) 0x64, (byte) 0xb7, (byte) 0x04,
            (byte) 0x00, (byte) 0x8c, (byte) 0x41, (byte) 0x4c, (byte) 0x73, (byte) 0xc6, (byte) 0x08, (byte) 0x55, (byte) 0x4c, (byte) 0xd3, (byte) 0x20, (byte) 0x2e,
            (byte) 0x7d, (byte) 0xa4, (byte) 0xc0, (byte) 0xc8, (byte) 0xc2, (byte) 0x10, (byte) 0xb3, (byte) 0xba, (byte) 0xde, (byte) 0x58, (byte) 0x0b, (byte) 0x53,
            (byte) 0xa3, (byte) 0xee, (byte) 0x44, (byte) 0x8e, (byte) 0x45, (byte) 0x03, (byte) 0x30, (byte) 0xb1, (byte) 0x27, (byte) 0x53, (byte) 0x8c, (byte) 0x4c,
            (byte) 0xf1, (byte) 0xe9, (byte) 0x14, (byte) 0xa3, (byte) 0x53, (byte) 0x8c, (byte) 0x85, (byte) 0xe1, (byte) 0xd9, (byte) 0x9f, (byte) 0xe3, (byte) 0xb3,
            (byte) 0xf2, (byte) 0x44, (byte) 0x81, (byte) 0xa5, (byte) 0x7c, (byte) 0x33, (byte) 0xdd, (byte) 0xd8, (byte) 0xbb, (byte) 0xc7, (byte) 0xaa, (byte) 0x75,
            (byte) 0x13, (byte) 0x5f, (byte) 0x28, (byte) 0x1c, (byte) 0x08, (byte) 0xd7, (byte) 0x2e, (byte) 0xd1, (byte) 0x59, (byte) 0x3f, (byte) 0xaf, (byte) 0x1d,
            (byte) 0x1b, (byte) 0x60, (byte) 0x21, (byte) 0x59, (byte) 0xdf, (byte) 0xfa, (byte) 0xf1, (byte) 0x05, (byte) 0xfe, (byte) 0xc1, (byte) 0xce, (byte) 0xfc,
            (byte) 0x9d, (byte) 0xbd, (byte) 0x00, (byte) 0xbc, (byte) 0xf1, (byte) 0x40, (byte) 0xc9, (byte) 0xf8, (byte) 0x85, (byte) 0x42, (byte) 0x40, (byte) 0x46,
            (byte) 0xfe, (byte) 0x9e, (byte) 0xeb, (byte) 0xea, (byte) 0x0f, (byte) 0x93, (byte) 0x3a, (byte) 0x68, (byte) 0x87, (byte) 0x60, (byte) 0xbb, (byte) 0xeb,
            (byte) 0x32, (byte) 0x37, (byte) 0xa3, (byte) 0x28, (byte) 0x0a, (byte) 0x8e, (byte) 0xbb, (byte) 0xf5, (byte) 0xd0, (byte) 0x69, (byte) 0x63, (byte) 0xca,
            (byte) 0x4e, (byte) 0xdb, (byte) 0xe9, (byte) 0xec, (byte) 0xe6, (byte) 0xe6, (byte) 0x2b, (byte) 0x3b, (byte) 0xbd, (byte) 0x25, (byte) 0xbe, (byte) 0x64,
            (byte) 0x49, (byte) 0x09, (byte) 0x3d, (byte) 0xaa, (byte) 0xbb, (byte) 0x94, (byte) 0xfd, (byte) 0x18, (byte) 0x7e, (byte) 0xe8, (byte) 0xd2, (byte) 0x0e,
            (byte) 0xda, (byte) 0x6f, (byte) 0x15, (byte) 0x4c, (byte) 0xb1, (byte) 0x68, (byte) 0x3e, (byte) 0x2b, (byte) 0xe1, (byte) 0x9b, (byte) 0x9c, (byte) 0x84,
            (byte) 0x99, (byte) 0xbc, (byte) 0x84, (byte) 0x05, (byte) 0x09, (byte) 0x65, (byte) 0x59, (byte) 0x16, (byte) 0x45, (byte) 0x00, (byte) 0xff, (byte) 0x2f,
            (byte) 0x28, (byte) 0xae, (byte) 0x2f, (byte) 0xf2, (byte) 0xc2, (byte) 0xb2, (byte) 0xa4, (byte) 0x2e, (byte) 0x1d, (byte) 0x20, (byte) 0x77, (byte) 0x5a,
            (byte) 0x3b, (byte) 0xb9, (byte) 0x8c, (byte) 0xca, (byte) 0xe7, (byte) 0x29, (byte) 0xdf, (byte) 0x51, (byte) 0x41, (byte) 0xc9, (byte) 0x16, (byte) 0xb5,
            (byte) 0xc5, (byte) 0x6d, (byte) 0xa1, (byte) 0x2a, (byte) 0xad, (byte) 0x2c, (byte) 0xc5, (byte) 0x31, (byte) 0x7f, (byte) 0xba, (byte) 0x7a, (byte) 0x92,
            (byte) 0x8e, (byte) 0x5e, (byte) 0x9d, (byte) 0x5f, (byte) 0xf8, (byte) 0x12, (byte) 0x05, (byte) 0x23, (byte) 0x1b, (byte) 0xd1, (byte) 0xf6, (byte) 0xb7,
            (byte) 0x77, (byte) 0xaa, (byte) 0xcd, (byte) 0x95, (byte) 0x72, (byte) 0xbc, (byte) 0x9e, (byte) 0xdf, (byte) 0x58, (byte) 0x5d, (byte) 0x4b, (byte) 0x97,
            (byte) 0xae, (byte) 0x92, (byte) 0x17, (byte) 0xb9, (byte) 0x44, (byte) 0xd0, (byte) 0x80, (byte) 0xc8, (byte) 0xfa, (byte) 0x3e, (byte) 0xbf, (byte) 0xb3,
            (byte) 0xdc, (byte) 0x54, (byte) 0xcb, (byte) 0x07, (byte) 0x75, (byte) 0x6e, (byte) 0xa3, (byte) 0xb6, (byte) 0x76, (byte) 0x59, (byte) 0x92, (byte) 0x93,
            (byte) 0xa9, (byte) 0xdc, (byte) 0x51, (byte) 0x50, (byte) 0x99, (byte) 0x6b, (byte) 0xcc, (byte) 0x35, (byte) 0xe6, (byte) 0x1a, (byte) 0xff, (byte) 0x57,
            (byte) 0x23, (byte) 0x08, (byte) 0x42, (byte) 0xcb, (byte) 0xe9, (byte) 0x1b, (byte) 0xd6, (byte) 0x78, (byte) 0xc2, (byte) 0xec, (byte) 0xfe, (byte) 0xfc,
            (byte) 0x7a, (byte) 0xfb, (byte) 0x7d, (byte) 0x78, (byte) 0xd3, (byte) 0x84, (byte) 0xdf, (byte) 0xd4, (byte) 0xf2, (byte) 0xa4, (byte) 0xfb, (byte) 0x08,
            (byte) 0x06, (byte) 0x00, (byte) 0x00
    };

    public void testNbt1() {
        ByteArrayInputStream bais = new ByteArrayInputStream(TEST_NBT);
        try {
            NbtReader reader = new NbtReader(bais);
            NbtItem item;
            item = reader.readNext();
            assertSame(NbtCompoundTagItem.class, item.getClass());
            assertEquals("hello world", item.getName());
            item = reader.readNext();
            assertSame(NbtStringItem.class, item.getClass());
            assertEquals("name", item.getName());
            assertEquals("Bananrama", ((NbtStringItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());
            try {
                item = reader.readNext();
                assertNull(item);
            }
            catch (IOException e) {
                fail();
            }
        }
        catch (IOException e) {
            fail("IOException");
        }
    }

    public void testNbt2() {
        ByteArrayInputStream bais = new ByteArrayInputStream(BIGTEST_NBT);
        try {
            NbtReader reader = new NbtReader(bais);
            NbtItem item;
            item = reader.readNext();
            assertSame(NbtCompoundTagItem.class, item.getClass());
            assertEquals("Level", item.getName());
            item = reader.readNext();
            assertSame(NbtLongItem.class, item.getClass());
            assertEquals("longTest", item.getName());
            assertEquals(9223372036854775807L, ((NbtLongItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtShortItem.class, item.getClass());
            assertEquals("shortTest", item.getName());
            assertEquals((short) 32767, ((NbtShortItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtStringItem.class, item.getClass());
            assertEquals("stringTest", item.getName());
            assertEquals("HELLO WORLD THIS IS A TEST STRING ÅÄÖ!", ((NbtStringItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtFloatItem.class, item.getClass());
            assertEquals("floatTest", item.getName());
            assertEquals(0.49823147f, ((NbtFloatItem) item).getValue(), 0.0000001f);
            item = reader.readNext();
            assertSame(NbtIntItem.class, item.getClass());
            assertEquals("intTest", item.getName());
            assertEquals(2147483647, ((NbtIntItem) item).getValue());

            // begin nested compound
            item = reader.readNext();
            assertSame(NbtCompoundTagItem.class, item.getClass());
            assertEquals("nested compound test", item.getName());

            item = reader.readNext();
            assertSame(NbtCompoundTagItem.class, item.getClass());
            assertEquals("ham", item.getName());
            item = reader.readNext();
            assertSame(NbtStringItem.class, item.getClass());
            assertEquals("name", item.getName());
            assertEquals("Hampus", ((NbtStringItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtFloatItem.class, item.getClass());
            assertEquals("value", item.getName());
            assertEquals(0.75f, ((NbtFloatItem) item).getValue(), 0.001f);
            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());

            item = reader.readNext();
            assertSame(NbtCompoundTagItem.class, item.getClass());
            assertEquals("egg", item.getName());
            item = reader.readNext();
            assertSame(NbtStringItem.class, item.getClass());
            assertEquals("name", item.getName());
            assertEquals("Eggbert", ((NbtStringItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtFloatItem.class, item.getClass());
            assertEquals("value", item.getName());
            assertEquals(0.5f, ((NbtFloatItem) item).getValue(), 0.001f);
            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());

            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());
            // end nested compound

            item = reader.readNext();
            assertSame(NbtListTagItem.class, item.getClass());
            assertEquals("listTest (long)", item.getName());
            assertEquals(NbtItem.TAG_LONG, ((NbtListTagItem) item).getTagType());
            assertEquals(5, ((NbtListTagItem) item).getLength());

            for (int i = 11; i <= 15; i++) {
                item = reader.readNext();
                assertSame(NbtLongItem.class, item.getClass());
                assertSame(null, item.getName());
                assertEquals(i, ((NbtLongItem) item).getValue());
            }

            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());
            // end list of long tags

            item = reader.readNext();
            assertSame(NbtListTagItem.class, item.getClass());
            assertEquals("listTest (compound)", item.getName());
            assertEquals(NbtItem.TAG_COMPOUND, ((NbtListTagItem) item).getTagType());
            assertEquals(2, ((NbtListTagItem) item).getLength());

            // nested compound #0
            item = reader.readNext();
            assertSame(NbtCompoundTagItem.class, item.getClass());
            assertSame(null, item.getName());
            item = reader.readNext();
            assertSame(NbtStringItem.class, item.getClass());
            assertEquals("name", item.getName());
            assertEquals("Compound tag #0", ((NbtStringItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtLongItem.class, item.getClass());
            assertEquals("created-on", item.getName());
            assertEquals(1264099775885L, ((NbtLongItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());

            // nested compound #1
            item = reader.readNext();
            assertSame(NbtCompoundTagItem.class, item.getClass());
            assertSame(null, item.getName());
            item = reader.readNext();
            assertSame(NbtStringItem.class, item.getClass());
            assertEquals("name", item.getName());
            assertEquals("Compound tag #1", ((NbtStringItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtLongItem.class, item.getClass());
            assertEquals("created-on", item.getName());
            assertEquals(1264099775885L, ((NbtLongItem) item).getValue());
            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());

            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());
            // end list of compound tags

            item = reader.readNext();
            assertSame(NbtByteItem.class, item.getClass());
            assertEquals("byteTest", item.getName());
            assertEquals(127, ((NbtByteItem) item).getValue());

            item = reader.readNext();
            assertSame(NbtByteArrayItem.class, item.getClass());
            assertEquals("byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))", item.getName());
            assertEquals(1000, ((NbtByteArrayItem) item).getValue().length);
            for (int i = 0; i < ((NbtByteArrayItem) item).getValue().length; i++) {
                int chk = (i * i * 255 + i * 7) % 100;
                assertEquals(chk, ((NbtByteArrayItem) item).getValue()[i]);
            }

            item = reader.readNext();
            assertSame(NbtDoubleItem.class, item.getClass());
            assertEquals("doubleTest", item.getName());
            assertEquals(0.4931287132182315, ((NbtDoubleItem) item).getValue(), 0.00000000000000001);

            item = reader.readNext();
            assertSame(NbtTagEndItem.class, item.getClass());
        }
        catch (IOException e) {
            fail("IOException" + e);
        }
    }
}
