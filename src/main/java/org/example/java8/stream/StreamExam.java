package org.example.java8.stream;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 스트림객체를 생성하는 다양한 방법을 찾아보자...
 * - Array
 * - String
 * - List
 * - iterate
 * - generate
 * - range
 * - builder
 * - SecureRandom.ints
 * - BufferedReader
 *
 * 참고사이트
 * https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/stream/BaseStream.html#onClose(java.lang.Runnable)
 * https://www.logicbig.com/how-to/code-snippets/jcode-java-8-streams-stream-onclose.html
 * https://www.tabnine.com/code/java/methods/java.util.stream.Stream/onClose
 *
 * https://futurecreator.github.io/2018/08/26/java-8-streams/
 */
public class StreamExam {
    public static void main(String[] str) {
        arrayExam();
        builderExam();
        rangeExam();
        generateExam();
        iterateExam();
        secureRandom_ints();
        onCloseExam1();
        onCloseExam2();
        fileReader();
    }

    /**
     * {@link Stream}객체생성
     * 참고사이트
     * https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/Random.html
     * https://www.tabnine.com/code/java/methods/java.security.SecureRandom/ints
     */
    private static void arrayExam() {
        System.out.println("<< StringArray to Stream >>");
        String[] strs = new String[]{"a", "b", "c", "d", "e",  "f"};
        Arrays.stream(strs)
                .map(String::toUpperCase)
                .forEach(System.out::println);

        System.out.println("<< Stream 생성 - String to split >>");
        // array to string
        String str = Arrays.stream(strs).collect(Collectors.joining());
        // string -> char -> toString
        str.chars()
                .mapToObj(Character::toString)
                .forEach(System.out::println);
    }

    public static void rangeExam() {
        System.out.println("<< IntStream.range >>");
        IntStream.range(0, 5)
                .map(n -> n * 2)
                .forEach(System.out::println);
    }

    /**
     * Stream.builder
     */
    public static void builderExam() {
        System.out.println("<< Stream.builder >>");
        Stream.<String>builder()
                .add("a")
                .add("b")
                .add("c")
                .build()
                .forEach(System.out::println);
    }

    /**
     * Stream.iterate
     */
    public static void iterateExam() {
        System.out.println("<< Stream.iterate >>");
        Stream.iterate(100, x-> x + 1)
                .limit(5)
                .forEach(System.out::println);
    }

    /**
     * Stream.generate
     */
    public static void generateExam() {
        System.out.println("<< Stream.generate #1 >>");
        Stream.generate(() -> 100)
                .limit(5)
                .forEach(System.out::println);

        System.out.println("<< Stream.generate #2 >>");
        Stream.generate(() -> {
                    // SecureRandom 객체는 매번 생성되므로 효율적이지 못하다.
                    // 이부분은 SecureRandom객체의 ints를 사용하는 것이 좋을듯 한다.
                    SecureRandom sr = new SecureRandom();
                    sr.setSeed(System.currentTimeMillis());
                    return sr.nextInt(20);  // 0~20
                })
                .limit(5)
                .forEach(System.out::println);
    }

    /**
     * SecureRandom.ints Example
     */
    public static void secureRandom_ints() {
        System.out.println("<< SecureRandom.ints #1 >>");
        SecureRandom sr = new SecureRandom();
//        sr.setSeed(System.currentTimeMillis());
        sr.setSeed(3);
        sr.ints(5)
                .forEach(System.out::println);

        System.out.println("<< SecureRandom.ints #2 >>");
        // 0 ~ 9 랜덤하게 숫자를 5개생성
        sr.ints(0,10)
                .limit(5)
                .forEach(System.out::println);

        System.out.println("<< SecureRandom.ints #3 >>");
        // A~Z까지 5개의 랜덤숫자생성
        sr.ints(5, 'A', 'Z'+1)
                .mapToObj(Character::toString)
//                .toList()
                .forEach(System.out::println);
    }


    /**
     * onClose는 스트림객체를 닫을(close호출) 때 호출된다.
     */
    public static void onCloseExam1() {
        System.out.println("<< Example #1 >>");
//        List<String> list = Arrays.asList("a", "b", "c", "d");
        IntStream intStream = IntStream.range(1, 10);
        // onClose 선언
        intStream.onClose(()->{
            System.out.println("스트림객체 닫기 중...");
        });

        // stream
        String[] strArray = intStream
                .filter(n -> (n & 1) == 1)      // 홀수
                .mapToObj(n -> String.format("%05d", n))
                .toArray(String[]::new);

        for(String str : strArray) {
            System.out.println("- "+ str);
        }

        System.out.println("스트림객체 닫기 호출");
        intStream.close();  // onClose의 구현체 실행
        System.out.println("스트림객체 닫기 완료");
    }

    /**
     * Stream객체를 닫을때 AtomicInteger의 값을 증가시킨다.
     * Stream종료 작업 후 수행하고자 할때 사용하면 좋을것으로 보인다.
     * 단점은 close를 호출해줘야 한다는것만 빼면...
     */
    public static void onCloseExam2() {
        System.out.println("<< Example #2 >>");
        AtomicInteger atomicInteger = new AtomicInteger();
        Stream<String> stream1 = Stream.of("first stream value").onClose(atomicInteger::incrementAndGet);
        Stream<String> stream2 = Stream.of("second stream value").onClose(atomicInteger::incrementAndGet);
        Stream<String> stream = Stream.concat(stream1, stream2);
        stream1.close();
        stream2.close();

        System.out.println("Stream Close Count : "+ atomicInteger.get());
        stream.forEach(System.out::println);
    }



    /**
     * 파일객체에서 stream사용시 onclose사용예제
     * https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/io/BufferedReader.html
     */
    public static void fileReader() {
        System.out.println("<< FileReader >>");
        Path path = Paths.get("/Users/cjpak/temp", "readme.txt");
        List<String> lineList = lines(path);
        lineList.forEach(str -> {
            System.out.println(str);
        });
    }

    /**
     * https://velog.io/@developerjun0615/Spring-Intellij-%EC%8B%A4%ED%96%89%EC%8B%9C-finished-with-non-zero-exit-value-1-%EC%98%A4%EB%A5%98
     * @param path
     * @return
     */
    public static List<String> lines(Path path) {
        System.out.println("<< BufferedReader.lines >>");
        try(FileReader fileReader = new FileReader(path.toFile())) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return bufferedReader
                    .lines()
                    .toList()
                    ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
