package org.example.java8.util.spliterator;

import java.util.Spliterator;
import java.util.function.Consumer;

public class WordCounterSpliterator implements Spliterator<Character> {
    private final String string;
    private int currentChar = 0;

    public WordCounterSpliterator(String string) {
        this.string = string;
    }

    // 탐색 할 요소가 남아있다면 true 반환
    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        action.accept(string.charAt(currentChar++)); // 현재 문자열을 소비
        return currentChar < string.length(); // 소비할 문자가 남아있으면 true 반환
    }

    // 요소를 분할해서 Spliterator 생성
    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = string.length() - currentChar;
        if (currentSize < 10) return null;

        int toIndex = (currentSize >> 1) + currentChar;
        Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, toIndex));
        currentChar = toIndex;
        return spliterator;

//
//        // 파싱할 문자열의 중간을 분할 위치로 설정
//        for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
//            if (Character.isWhitespace(string.charAt(splitPos))) { // 공백 문자가 나올때
//                // 문자열을 분할 해 Spliterator 생성
//                Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
//                // 시작을 분할 위치로 설정
//                currentChar = splitPos;
//                return spliterator;
//            }
//        }
//        return null;
    }

    // 탐색해야 할 요소의 수
    @Override
    public long estimateSize() {
        return string.length() - currentChar;
    }

    // Spliterator 객체에 포함된 모든 특성값의 합을 반환
    @Override
    public int characteristics() {
        // ORDERED : 문자열의 순서가 유의미함
        // SIZED : estimatedSize 메서드의 반환값이 정확함
        // NONNULL : 문자열에는 null이 존재하지 않음
        // IMMUTABLE : 문자열 자체가 불변 클래스이므로 파싱하며 속성이 추가되지 않음
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }

}