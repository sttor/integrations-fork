// SPDX-License-Identifier: 0BSD
// SPDX-FileCopyrightText: The XZ for Java authors and contributors
// SPDX-FileContributor: Lasse Collin <lasse.collin@tukaani.org>
// SPDX-FileContributor: Igor Pavlov <https://7-zip.org/>

package org.tukaani.xz.simple;

// BCJ filter for little endian ARM instructions
public final class ARM implements SimpleFilter {
    private final boolean isEncoder;
    private int pos;

    public ARM(boolean isEncoder, int startPos) {
        this.isEncoder = isEncoder;
        pos = startPos + 8;
    }

    @Override
    public int code(byte[] buf, int off, int len) {
        int end = off + len - 4;
        int i;

        for (i = off; i <= end; i += 4) {
            if ((buf[i + 3] & 0xFF) == 0xEB) {
                int src = ((buf[i + 2] & 0xFF) << 16)
                          | ((buf[i + 1] & 0xFF) << 8)
                          | (buf[i] & 0xFF);
                src <<= 2;

                int dest;
                if (isEncoder)
                    dest = src + (pos + i - off);
                else
                    dest = src - (pos + i - off);

                dest >>>= 2;
                buf[i + 2] = (byte)(dest >>> 16);
                buf[i + 1] = (byte)(dest >>> 8);
                buf[i] = (byte)dest;
            }
        }

        i -= off;
        pos += i;
        return i;
    }
}
