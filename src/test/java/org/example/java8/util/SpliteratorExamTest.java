package org.example.java8.util;

import org.example.java8.util.spliterator.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.*;

/**
 * 참고사이트
 * https://doohyun.tistory.com/42
 * https://devbksheen.tistory.com/entry/%EB%AA%A8%EB%8D%98-%EC%9E%90%EB%B0%94-Spliterator-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4%EB%9E%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80#Spliterator%--%EC%-D%B-%ED%--%B-%ED%-E%--%EC%-D%B-%EC%-A%A-%EB%-E%--%-F
 * 위 사이트의 글을 가져왔음.
 *
 * Spliterator 인터페이스란?
 * Spliterator는 분할할 수 있는 반복자라는 의미이다.
 * Iterator 처럼 Spliterator는 소스의 요소 탐색 기능을 제공한다는 점은 같지만 Spliterator는 병렬 작업에 특화되어 있다.
 * 커스텀 Spliterator를 꼭 구현해야 하는 건 아니지만 Spliterator가 어떻게 동작하는지 이해한다면 병렬 스트림 동작과 관련한 통찰력을 얻을 수 있다.
 *
 * java8은 컬렉션 프레임워크에 포함된 모든 자료구조에 사용할 수 있는 디폴트 Spliterator 구현을 제공한다.
 *
 * public interface Spliterator<T> {
 *     boolean tryAdvance(Consumer<? super T> action);
 *     Spliterator<T> trySplit();
 *     long estimateSize();
 *     int characteristics();
 *     ...
 * }
 * 여기서 T는 Spliterator에서 탐색하는 요소의 형식을 가리킨다.
 *
 * tryAdvance : 요소를 하나씩 소비하면서 탐색해야 할 요소가 남아있으면 true 반환
 * trySplit : 일부 요소를 분할해서 두 번째 Spliterator를 생성
 * estimateSize : 탐색해야 할 요소의 수 제공
 * characteristics : Spliterator 객체에 포함된 모든 특성값의 합을 반환
 *  - ORDERED       리스트처럼 요소에 정해진 순서가 있으므로 Spliterator는 해당 순서에 유의해서 수행한다.
 *  - DISTINCT      요소에 중복값이 없음을 의미 (a.equals(b) == fasle)
 *  - SORTED        요소들이 이미 정렬되어 있음을 의미
 *  - NONNULL       NULL요소가 없음을 의미
 *  - IMMUTABLE     요소를 탐색하는 동안 수정/삭제가 없음
 *  - CONCURRENT    동기화없이 여러 스레드에서 동시에 수정/삭제가 가능
 *  - SIZED         크기가 알려 소스(ex: Set)로 Spliterator를 생했으므로 estimateSize()는 정확한 값을 반환한다.
 *  - SUBSIZED      분할된 Spliterator 에 SIZED특성을 갖는다.
 *
 * List Spliterator : ORDERED, SIZED, SUBSIZED
 * Set Spliterator : DISTINCT, SIZED
 * IntStream.of Spliterator : IMMUTABLE, ORDERED, SIZED, SUBSIZED
 * IntStream.generate Spliterator : IMMUTABLE
 */
public class SpliteratorExamTest {
//    String strs = "요소를 하나씩 소비하면서 탐색해야 할 요소가 남아있으면 true 반환";
//    String strs = "하늘과 바람과 별과 시";
//    String strs = "하늘과 바람";
    String strs = "ABCDEFGHIJKLMNOPQRSTUVWXY-ABCDEFGHIJKLMNOPQRSTUVWXY-ABCDEFGHIJKLMNOPQRSTUVWXY-ABCDEFGHIJKLMNOPQRSTUVWXY";
//    String strs = "strs.chars();";



    /**
     * 1~1,000,000 합계
     * 결과값
     * index[0] 결과값 : 499999500000
     * index[1] 결과값 : 499999500000
     * index[2] 결과값 : 499999500000
     * index[3] 결과값 : 499999500000
     * index[4] 결과값 : 499999500000
     */
    @Test
    public void single_foreach_sum() {
        List<Long> longs = LongStream.range(1, 1_000_000).boxed().toList();
        IntStream.range(0, 5)
                .forEach(n-> {
//                    System.out.println(String.format("index[%d] 결과값 : %d", n, foreach_sum(1_000_000)));
                    System.out.println(String.format("index[%d] 결과값 : %d", n, foreach_sum(longs)));
                });
    }

    /**
     * 1~1,000,000 합계
     * 동일한 작업을 여러번 수행하였으나 결과값이 다르다.
     *
     *
     * 결과값
     * index[0] 결과값 : 50338353342
     * index[1] 결과값 : 36336505963
     * index[2] 결과값 : 71781898906
     * index[3] 결과값 : 198247785403
     * index[4] 결과값 : 174037698241
     */
    @Test
    public void parallel_foreach_sum() {
        List<Long> longs = LongStream.range(1, 1_000_000).boxed().toList();
        IntStream.range(0, 5)
                .forEach(n-> {
//                    System.out.println(String.format("index[%d] 결과값 : %d", n, foreach_sum_parallel(1_000_000)));
//                    System.out.println(String.format("index[%d] 결과값 : %d", n, sum_parallel_spliterator(1_000_000)));
                    System.out.println(String.format("index[%d] 결과값 : %d", n, sum_parallel_spliterator(longs)));
                });
    }


    /**
     * 싱글처리
     *
     * 결과값
     * index[0] 결과값 : 499999500000
     * index[1] 결과값 : 499999500000
     * index[2] 결과값 : 499999500000
     * index[3] 결과값 : 499999500000
     * index[4] 결과값 : 499999500000
     *
     * @param longs
     * @return
     */
    public long foreach_sum(List<Long> longs) {
        NumberSum longSum = new NumberSum();
        longs.forEach(longSum::add);

        return longSum.getValue();
    }

    /**
     * 병렬처리로 합계구하기
     *
     * {@link NumberSum}객체에 다수의 스레드가 동시에 접근하여 발생하는 문제(데이터 레이스)
     *
     * 결과값
     * index[0] 결과값 : 50338353342
     * index[1] 결과값 : 36336505963
     * index[2] 결과값 : 71781898906
     * index[3] 결과값 : 198247785403
     * index[4] 결과값 : 174037698241
     * @param n
     * @return
     */
    public long foreach_sum_parallel(long n) {
        NumberSum accumulator = new NumberSum();
        LongStream.range(1, n)
                .parallel()
                .forEach(accumulator::add);

        return accumulator.getValue();
    }

    /**
     * 병렬처리
     * reduce에 사용하는 객체({@link NumberSum)}를 잘 만들어야 정상적인 결과가 나온다.. ㅡㅡ;;;
     * 객체의 변수에 값을 누적하게 되면 다수의 스레드가 동시접근하여 정상적인 계산이 수행되지 않는다...ㅠㅠ
     * 이런 거지같은 경우를 '데이터레이스'라고 한다.
     *
     * index[0] 결과값 : 499999500000
     * index[1] 결과값 : 499999500000
     * index[2] 결과값 : 499999500000
     * index[3] 결과값 : 499999500000
     * index[4] 결과값 : 499999500000
     *
     * @param longs
     * @return
     */
    public long sum_parallel_spliterator(List<Long> longs) {
        // Spliterator인터페이스는 구현해서 하는방법
//        NumberSum numberSum = StreamSupport.stream(new LongSpliterator(longs), true)
//                .reduce(new NumberSum(0), NumberSum::add_accumulator, NumberSum::add_combine);
//        return numberSum.getValue();

        // 숫자의 경우 Spliterators util를 사용해서 생성가능하다.
//        Spliterator<Long> spliterator = Spliterators.spliterator(longs, longs.spliterator().characteristics());
        // 이렇게도 생성하고~~
        Spliterator.OfLong spliterator = Spliterators.spliterator(longs.stream().mapToLong(Long::longValue).toArray(), Spliterator.SIZED);
        NumberSum numberSum = StreamSupport.stream(spliterator, true)
                .reduce(new NumberSum(0), NumberSum::add_accumulator, NumberSum::add_combine);
        return numberSum.getValue();


    }







    /**
     * non paraller stream
     */
    @Test
    public void nonParaller() {
        // 알파벳
//        String collect = IntStream.range(65, 65+26).mapToObj(Character::toString).collect(Collectors.joining());
//        System.out.println(collect);

        long count1 = strs.chars().filter(Character::isAlphabetic).count();
        long count2 = strs.chars().parallel().filter(Character::isAlphabetic).count();
        System.out.println(count1);
        System.out.println(count2);

//        AtomicInteger ai = new AtomicInteger();
//        strs.chars().filter(Character::isAlphabetic).reduce("", String::concat);
        String reduce1 = Arrays.stream(strs.split("")).dropWhile(String::isBlank).reduce("", String::concat);
        String reduce2 = Arrays.stream(strs.split("")).parallel().dropWhile(String::isBlank).reduce("", String::concat);
        System.out.println(reduce1);
        System.out.println(reduce2);


        // 요소가 primitive type인 경우에는 병렬처리하여도 문제가 없음


        System.out.println();
        System.out.println("<< String.concat : nonParaller >>");
        String string = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
                .map(String::valueOf)
                .reduce("", String::concat, String::concat);
        System.out.println(string);


        System.out.println();
        System.out.println("<< StringBuilder.append : nonParaller >>");
        StringBuilder stringBuilder  = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
//                .map(String::valueOf)
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append);
        System.out.println(stringBuilder);


        System.out.println();
        System.out.println("<< StringBuilder.append : nonParaller >>");
        StringBuffer stringBuffer  = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
//                .map(String::valueOf)
                .reduce(new StringBuffer(), StringBuffer::append, StringBuffer::append);
        System.out.println(stringBuffer);


        System.out.println();
        System.out.println("<< WordConcat.concat >>");
        WordConcat wordConcat = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
                .reduce(new WordConcat(), WordConcat::concat, WordConcat::combine);
        System.out.println(wordConcat.toString());


        System.out.println();
        System.out.println("<< WordCounter.accumulate >>");
        WordCounter wordCounter = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
                .reduce(new WordCounter(0 ,true), WordCounter::accumulate, WordCounter::combine);
        System.out.println(wordCounter.getCounter());

    }

    /**
     * 병렬처리시 다음과 같은 문제가 있음
     *  - ArrayIndexOutOfBoundsException 이 발생할 수 있음
     *  - 정상적으로 결과값이 반환되지 않음.
     *    ex)
     *    입력 : 하늘과 바람과 별과 시
     *    출력 : 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과  별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과  별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과  별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과  별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과  별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과  별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과  별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바 별 별과 별 별과과시람하  별 별과 별 별과과시람하 바늘과
     *
     * 위와 같은 오류가 발생하는 이유는 StringBuilder객체를 통해서 처리하게되면 하나의 인스턴스로 모든 스레드가 공유하고
     * 그로인해 데이터레이스 발생...
     * 이건 해결불가~~~~
     * 이건 해결불가~~~~
     * 이건 해결불가~~~~
     * 이건 해결불가~~~~
     */
    @Test
    public void pallerStringBuilder() {
        String strs = "하늘과 바람과 별과 시";
        System.out.println();
        System.out.println("<< StringBuilder.append : paraller >>");
        StringBuilder stringBuilder  = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
                .parallel()
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append);
        System.out.println(stringBuilder);


        System.out.println();
        System.out.println("<< StringBuilder.append : paraller >>");
        List<Character> characters = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
                .toList();

        StringBuilder stringBuilder1 = characters.parallelStream()
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append);
        System.out.println(stringBuilder1);
    }

    @Test
    public void parallerSum() {
        int toIndex = 1_000_000;
        Integer sum = IntStream.range(1, toIndex).boxed().reduce(0, Integer::sum, Integer::sum);
        System.out.println("SingleThread : 1+....+99="+ sum);

        Integer sum1 = IntStream.range(1, toIndex).boxed().reduce(0, (a,b)-> a-b, (a,b)-> a-b);
        System.out.println("SingleThread : 1000-1-....-99="+ sum1);
//        assert sum1 == (1_000 - sum);

        Integer sum2 = IntStream.range(1, toIndex).boxed().parallel().reduce(1_000, (a,b)-> a-b, (a,b)-> a-b);
        System.out.println("MultiThread : 1000-1-....-99="+ sum2);
//        assert sum2 == (1000 - sum);        // error 일치하지 않음

        List<Long> integers = LongStream.range(1, toIndex).boxed().toList();
        Stream<Long> stream = StreamSupport.stream(new LongSpliterator(integers), true);
        NumberSum numberSum = stream.reduce(new NumberSum(0), NumberSum::sub_accumulator, NumberSum::sub_combine);
        System.out.println("MultiThread : 1000 - 1-....-99="+ numberSum.getValue());
//        assert sum3 == (1000 - sum);
    }


    /**
     * {@link StringBuilder}, {@link StringBuffer}를 사용해서 문자열을 합칠려니까 안되네.. ㅡㅡ;;;
     * reduce로 문자열을 합치고하 할때에는 primitive type(String)객체가 아닌 일반 객체의 경우에는 정상적이 결과가 안나옴.. ㅡㅡ;;;
     * 뭐가문제일까???
     * 뭐가문제일까???
     * 뭐가문제일까???
     * 뭐가문제일까???
     * 뭐가문제일까???
     *
     * Collect를 사용하면 굳이 {@link Spliterator}를 사용하지 않고도 병렬로 처리가 가능하다..
     * 병렬처리시 reduce에서 사용하는 값(객체)가 데이터레이스로 문제가 발생할 수 있으므로
     * 객체가 공유되지 않도록 만들어야 한다.... 라고 결론이 나온다.. ㅡㅡ;;
     * 음...
     *
     */
    @Test
    public void pallerStringBuilderSpliterator() throws InterruptedException {

        List<Character> characters = IntStream.range(0, strs.length())
                .mapToObj(strs::charAt)
                .toList();


        System.out.println("원래문자 : "+ strs);

        String char_join = characters.stream().parallel().map(String::valueOf).collect(Collectors.joining());
        System.out.println("string parallel join : "+ char_join);
        String char_reduce = characters.stream().parallel().map(String::valueOf).reduce("", String::concat, String::concat);
        System.out.println("string parallel reduce : "+ char_reduce);

        WordConcat wordConcat1 = characters.stream().parallel().reduce(new WordConcat(), WordConcat::concat, WordConcat::combine);
        System.out.println("parallel wordConcat : " + wordConcat1);

        System.out.println();

        // Spliterator stream 생성
        Stream<Character> stream = StreamSupport.stream(new CharacterSpliterator(characters), true);
        System.out.println("Spliterator Paraller : "+ stream.isParallel());

        // word concat
        WordConcat wordConcat2 = stream.reduce(new WordConcat(), WordConcat::concat, WordConcat::combine);
        System.out.println("Spliterator wordConcat : " + wordConcat2);


//        // character sum
//        Integer character_sum = stream.reduce(0, Integer::sum, Integer::sum);
//        System.out.println("char sum : "+ character_sum);
//        assert sum1 == character_sum;

        // 문자열 합치기
//        String string = stream.map(String::valueOf).collect(Collectors.joining());
//        System.out.println("stream Spliterator collect join : "+ string);


        // reduce 작업시 값이 뻥튀기 된다... 왜??
        // StringBuffer, StringBuilder 객들을 사용해서 발생하는 문제인가?.??
        // 숫자의 합계를 구하거나 그런경우에는 문제가 없다..
//        StringBuilder stringBuilder = stream.reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append);
//        System.out.println("문자열길이 : "+ stringBuilder.length());

    }

    @Test
    public void parallerWordCounter() {

        WordCounter wordCounter = IntStream.range(0, strs.length())
                .parallel()
                .mapToObj(strs::charAt)
                .reduce(new WordCounter(0 ,true), WordCounter::accumulate, WordCounter::combine);

        System.out.println();
        System.out.println("<< WordCounter.accumulate : paraller >>");
        System.out.println(wordCounter.getCounter());

//        Spliterator<Character> spliterator = new WordCounterSpliterator(strs);
        Stream<Character> stream = StreamSupport.stream(new WordCounterSpliterator(strs), true);
        WordCounter wordCounter1 = stream.reduce(new WordCounter(0, true), WordCounter::accumulate, WordCounter::combine);
        System.out.println();
        System.out.println("<< WordCounter.accumulate : paraller >>");
        System.out.println(wordCounter1.getCounter());

    }

}
