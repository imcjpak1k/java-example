/**
 * https://stackoverflow.com/questions/43182732/computing-a-ranking-with-java-8-stream-api
 */

package org.example.java8.stream.vo;

public interface RankVo<T> {
    int getRanking();
    T getItem();

    static <T> RankVo<T> factory(int ranking, T item) {
        return new RankVo<T>() {
            @Override
            public int getRanking() {
                return ranking;
            }

            @Override
            public T getItem() {
                return item;
            }
        };
    }
}
