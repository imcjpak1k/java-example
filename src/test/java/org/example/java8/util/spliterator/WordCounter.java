package org.example.java8.util.spliterator;

public class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(char c) {
            if (Character.isWhitespace(c)) { // 공백 문자일때
                // 앞 문자열이 공백이 아니면 lastSpace를 true로 변경
                return lastSpace ? this : new WordCounter(counter, true);
            } else { // 공백 문자가 아닐때
                // 앞 문자열이 공백이면 counter + 1
                return lastSpace ? new WordCounter(counter + 1, false) : this;
            }
        }

        // 두 WordCounter의 counter값을 더한다.
        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
}
