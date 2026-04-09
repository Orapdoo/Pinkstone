package net.caffeinemc.mods.sodium.client.util.iterator;

import java.util.NoSuchElementException;


public class ReversibleByteArrayIterator implements ByteIterator {
    private final byte[] elements;

    private final int step;

    private int currentIndex;
    private int remaining;

    public ReversibleByteArrayIterator(byte[] elements, int size, boolean reverse) {
        this.elements = elements;

        this.step = reverse ? -1 : 1;
        this.reset(size);
    }

    public ReversibleByteArrayIterator reset(int size) {
        this.remaining = size;
        this.currentIndex = this.step < 0 ? size - 1 : 0;
        return this;
    }

    @Override
    public boolean hasNext() {
        return this.remaining > 0;
    }

    @Override
    public int nextByteAsInt() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        int result = Byte.toUnsignedInt(this.elements[this.currentIndex]);

        this.currentIndex += this.step;
        this.remaining--;

        return result;
    }
}
