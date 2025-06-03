// SPDX-License-Identifier: 0BSD
// SPDX-FileCopyrightText: The XZ for Java authors and contributors
// SPDX-FileContributor: Lasse Collin <lasse.collin@tukaani.org>

package org.tukaani.xz;

import org.tukaani.xz.lzma.LZMAEncoder;

class LZMA2Encoder extends LZMA2Coder implements FilterEncoder {
    private final LZMA2Options options;
    private final byte[] props = new byte[1];

    LZMA2Encoder(LZMA2Options options) {
        if (options.getPresetDict() != null)
            throw new IllegalArgumentException(
                    "XZ doesn't support a preset dictionary for now");

        if (options.getMode() == LZMA2Options.MODE_UNCOMPRESSED) {
            props[0] = (byte)0;
        } else {
            int d = Math.max(options.getDictSize(),
                             LZMA2Options.DICT_SIZE_MIN);
            props[0] = (byte)(LZMAEncoder.getDistSlot(d - 1) - 23);
        }

        // Make a private copy so that the caller is free to change its copy.
        this.options = (LZMA2Options)options.clone();
    }

    @Override
    public long getFilterID() {
        return FILTER_ID;
    }

    @Override
    public byte[] getFilterProps() {
        return props;
    }

    @Override
    public boolean supportsFlushing() {
        return true;
    }

    @Override
    public FinishableOutputStream getOutputStream(FinishableOutputStream out,
                                                  ArrayCache arrayCache) {
        return options.getOutputStream(out, arrayCache);
    }
}
