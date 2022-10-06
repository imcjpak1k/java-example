package org.example.java8.util.spliterator;

import org.jetbrains.annotations.NotNull;

public class WordConcat {
    private final String text;
    public WordConcat() {
        text = "";
    }
    public WordConcat(String str) {
        this.text = str;
    }

    private WordConcat concat(@NotNull String str) {
//        System.out.println(this.hashCode());
        // 새로운 객체반환(스레드 공유현상 제거)
        return new WordConcat(this.text.concat(str));
    }

    public WordConcat concat(@NotNull Character character) {
//        System.out.println(this.hashCode());
        // 새로운 객체반환(스레드 공유현상 제거)
        return new WordConcat(this.text.concat(character.toString()));
    }
    public WordConcat combine(@NotNull WordConcat wordConcat) {
        return new WordConcat(this.text.concat(wordConcat.toString()));
    }

    @Override
    public String toString() {
        return this.text;
    }
}
