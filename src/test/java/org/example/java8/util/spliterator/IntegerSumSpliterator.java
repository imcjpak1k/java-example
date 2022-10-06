package org.example.java8.util.spliterator;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class IntegerSumSpliterator implements Spliterator<Integer> {
    private static final int LIMITED_SPLIT_SIZE = 500;
    private List<Integer> integerList;
    private Integer currentIndex = 0;
    
    public IntegerSumSpliterator(List<Integer> integerList){
        this.integerList = integerList;
    }
    
    @Override
    public boolean tryAdvance(Consumer<? super Integer> action) {
        action.accept(integerList.get(currentIndex++));
        return currentIndex < integerList.size();
    }

    @Override
    public Spliterator<Integer> trySplit() {
        Integer currentSize = integerList.size() - currentIndex;
        if (currentSize <= LIMITED_SPLIT_SIZE) {
            // 잘라진 사이즈가 자르지 말아야할 최소사이즈보다 작다면 null 을 출력.
            return null;
        }
        else {
            // 할 일을 절반씩 잘라줍시다.
            Integer splitTargetSize = currentIndex + currentSize/2;
            List<Integer> subList = integerList.subList(currentIndex, splitTargetSize);
            currentIndex = splitTargetSize;
            return new IntegerSumSpliterator(subList);
        }
    }

    @Override
    public long estimateSize() {
        return integerList.size() - currentIndex;
    }

    @Override
    public int characteristics() {
        return Spliterator.DISTINCT + Spliterator.IMMUTABLE + Spliterator.CONCURRENT + Spliterator.SIZED + Spliterator.SUBSIZED;
    }
}
