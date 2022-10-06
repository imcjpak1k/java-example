package org.example.java8.util.spliterator;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class CharacterSpliterator implements Spliterator<Character> {
    private final int LIMITED_SPLITE_SIZE = 10;
    private int currentIndex = 0;
    private List<Character> characters;


    public CharacterSpliterator(String string) {

        this.characters = IntStream.range(0, string.length())
                .mapToObj(string::charAt)
                .toList();
    }
    public CharacterSpliterator(List<Character> characters) {
        this.characters = characters;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        Character character = characters.get(currentIndex++);

        // currentIndex의 값 처리 및 증가
        action.accept( character );

        return characters.size() > currentIndex;
    }

    @Override
    public Spliterator<Character> trySplit() {
        // 남은크기
        int current_size = (int)estimateSize();
        if(current_size <= LIMITED_SPLITE_SIZE) {
            return null;
        }

        // 남아있는 요소의 절반을 분할해서 반환
        int toIndex = currentIndex + (current_size >> 1);
        CharacterSpliterator spliterator = new CharacterSpliterator(characters.subList(currentIndex, toIndex));
        // 위치변경(반환한 객체의 크기를 적용함)
        currentIndex = toIndex;

        return spliterator;
    }

    @Override
    public long estimateSize() {
        return characters.size() - currentIndex;
    }

    /**
     *
     * @return
     */
    @Override
    public int characteristics() {
        return ORDERED | NONNULL | SIZED | SUBSIZED | IMMUTABLE | CONCURRENT;
    }
}
