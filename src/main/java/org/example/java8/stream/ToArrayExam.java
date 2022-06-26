package org.example.java8.stream;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * https://codechacha.com/ko/java8-convert-stream-to-array/
 * Stream객체를 Array객채로 변환
 */
public class ToArrayExam {
    public static void main(String[] str) {
        toStringArray();
        toIntegerArray();
        toIntArray();
    }

    /**
     * Stream객체를 StringArray객체로 변환
     */
    public static void toStringArray() {
        System.out.println("<< toStringArray >>");
//        List<String> list = stream.collect(Collectors.toList());
        Stream<String> stream = Stream.of("a", "b", "c", "d", "e");
        String[] strArray = stream
                        .map(s -> s.toUpperCase(Locale.ROOT))
                        .toArray(String[]::new);

        for(String str : strArray) {
            System.out.printf(" - %s\n", str);
        }
    }

    /**
     * Stream객체를 IntegerArray객채로 반환
     */
    public static void toIntegerArray() {
        System.out.println("<< toIntArray >>");
        Stream<String> stream = Stream.of("1", "2", "3", "4", "5");
        Integer[] integerArray = stream
                .map(x -> Integer.parseInt(x))
                .toArray(Integer[]::new);

        for(Integer i : integerArray) {
            System.out.printf(" - %s\n", i);
        }


        Stream<String> stream2 = Stream.of("1", "2", "3", "4", "5");
        Integer[] integerArray2 = stream2
                .mapToInt(x -> Integer.parseInt(x))
                .boxed()
                .toArray(Integer[]::new);

        for(Integer i : integerArray2) {
            System.out.printf(" boxed - %s\n", i);
        }
    }

    /**
     * Stream객체를 int배열객체로 반환
     */
    public static void toIntArray() {
        System.out.println("<< toIntegerArray >>");
        Stream<String> stream = Stream.of("1", "2", "3", "4", "5");
        int[] intArray = stream
                .mapToInt(x -> Integer.parseInt(x))
//                .boxed()
                .toArray();

        for(int i : intArray) {
            System.out.printf(" - %s\n", i);
        }

        IntStream intStream = IntStream.range(1, 10);
        int[] intArray2 = intStream
                    .map(x -> x * x)
                    .toArray();
        for(int i : intArray2) {
            System.out.printf(" - intStream %s\n", i);
        }
    }


//    public static void toStringArrays(@NotNull String i) {
//    }
//    public static void toStringArray() {
//    }
}
