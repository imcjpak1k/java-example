package org.example.java8.stream;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Syntax :
 *  <R> Stream<R> flatMap(Function<? super T,? extends Stream<? extends R>> mapper)
 *
 *  파라미터로 {@link java.util.function.Function}를 받아서 {@link java.util.stream.Stream}객체를 반한하도록 되어있는 구조로
 *  파라미터 Stream<T>를 받아 Stream<R>을 반한하면 되며,
 *  여기에서 T는 어떤객체여도 된다
 *
 *  파라미터 T는 String, Array, List, Stream, Object.... 어떤것이 되어서 상관이 없다
 *  예제)
 *  - Stream<Array>, Stream<List>, Stream<String>......
 */
public class FlatMapEx {
    public static void main(String[] strings) {


        stringArrayCombineEx1();
        stringArrayComnbineEx2();
        intArrayCombineEx1();
    }

    /**
     * 문자열배열을 하나의 Stream으로 병합
     */
    private static void stringArrayCombineEx1() {
        System.out.println("<< N개의 배열을 하나의 문자열 Stream으로 병합 >>");
        String[] strArray1 = {"가나", "다라"};
        String[] strArray2 = {"ab", "cd", "ef"};
        // 문자열배열의 스트림생성
        Stream<String[]> streamArray = Stream.of(strArray1, strArray2);
        streamArray
                .peek(array -> {
                    System.out.println(array);
                })
                // array -> stream #1
                .flatMap(array -> {
                    return Stream.of(array);
                })
                // aray -> stream #2
//                .flatMap(Stream::of)
                .forEach(System.out::println)
        ;
    }

    /**
     * 문자열배열을 Stream으로 병합 후 값을 변경한다.
     */
    private static void stringArrayComnbineEx2() {
        System.out.println("<< N개의 배열을 하나의 문자열 Stream으로 병합(값변경을 동시에 진행) >>");
        String[] strArray1 = {"가나", "다라"};
        String[] strArray2 = {"ab", "cd", "ef"};
        // 문자열배열의 스트림생성
        Stream<String[]> streamArray = Stream.of(strArray1, strArray2);
        streamArray
                .peek(array -> {
                    System.out.println(array);
                })
                .flatMap(array -> {
                    return Stream.of(array)
                            .map(s -> String.format("#%s", s))
                            ;
                })
                .forEach(System.out::println)
        ;
    }

    /**
     * int array를 병합 후 숫자를 한글로 변경하여 출력
     * int array -> {@link IntStream} -> flatMap (IntStream -> mapToObj)
     */
    private static void intArrayCombineEx1() {
        System.out.println("<< n개의 int array를 병합 및 데이터 변경 >>");

        String[] 숫자 = {"영","일","이","삼", "사", "오", "육","칠","팔","구"};
        String[] 단위 = {"","십","백","천", "만", "십만", "백만","천만","억","십억","백억","천억","조","십조"};
        int[] a1 = {1,2,3,4,5};
        int[] a2 = {6,7,8,9,0};

//        Stream<Integer> stream = IntStream.of(a1).mapToObj(Integer::valueOf);
//        Stream<Integer> stream = Stream.of(IntStream.of(a1), IntStream.of(a2))
//                .flatMap(ints -> ints.mapToObj(Integer::valueOf))
//                ;

        // 단순병합
        Stream<Integer> stream = Stream.of(a1,a2)
                .flatMap(ints -> IntStream.of(ints).mapToObj(Integer::valueOf))
                ;
        stream.forEach(System.out::println);

        // 병합 후 값 변환
        Stream.of(IntStream.of(a1), IntStream.of(a2))
//                .flatMap(ints -> ints.mapToObj(i -> 숫자[i]))
                .flatMap(ints -> ints.mapToObj(i -> {
                    /// 음.....
                    return 숫자[i];
                }))
                .forEach(System.out::println);
        ;

    }
}
