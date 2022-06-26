/**
 * 자료출처
 * https://stackoverflow.com/questions/43182732/computing-a-ranking-with-java-8-stream-api
 */
package org.example.java8.stream.vo;

public class Rank<T> {
    protected int ranking;
    protected T item;
    protected Rank(int r, T i){
        ranking = r;
        item = i;
    }

    public int getRanking() {
        return ranking;
    }

    public T getItem() {
        return item;
    }

    static <T> Rank<T> factory(int ranking, T item) {
        return new Rank<T>(ranking, item) {
            public int getRanking() {
                return ranking;
            }

            public T getItem() {
                return item;
            }
        };
    }
}
