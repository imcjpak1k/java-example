package org.example.java8.util.spliterator;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class LongSpliterator implements Spliterator<Long> {
    private List<Long> list;
    private int index;

    public LongSpliterator(List<Long> list) {
        this.list = list;
        this.index = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Long> action) {
        Long l = list.get(index++);

        action.accept(l);

        return list.size() > index;
    }

    @Override
    public Spliterator<Long> trySplit() {
        final int LIMITED_SIZE = 1_000;

        // 남은건수
        int remaining_size = list.size() - index;
        if(LIMITED_SIZE >= remaining_size) {
            return null;
        }

        // 남은건수/2
        int toIndex = (remaining_size >> 1) + index;
        List<Long> subList = list.subList(index, toIndex);

        // index 이동
        index = toIndex;

        return new LongSpliterator(subList);
    }

    @Override
    public long estimateSize() {
        return (long) list.size()-index;
    }

    @Override
    public int characteristics() {
        return SIZED | SUBSIZED | DISTINCT | NONNULL | IMMUTABLE;
    }
}
