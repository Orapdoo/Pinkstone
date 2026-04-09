package net.caffeinemc.mods.sodium.client.util.iterator;

import java.util.NoSuchElementException;

public class ByteArrayIterator implements ByteIterator {
    private final byte[] elements;
    private int lastIndex;

    private int index;

    public ByteArrayIterator(byte[] elements, int lastIndex) {
        this.elements = elements;
        this.reset(lastIndex);
    }

    public ByteArrayIterator reset(int lastIndex) {
        this.lastIndex = lastIndex;
        this.index = 0;
        return this;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.lastIndex;
    }

    @Override
    public int nextByteAsInt() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        return Byte.toUnsignedInt(this.elements[this.index++]);
    }
}
