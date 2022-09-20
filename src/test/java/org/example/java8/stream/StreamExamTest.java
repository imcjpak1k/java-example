package org.example.java8.stream;

import org.example.java8.stream.vo.Employee;
import org.example.java8.util.VoBuilder;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamExamTest {
    /**
     * 스트림의 모든 요소가 조건(predecate)을 만족하는지 여부를 반환
     * boolean  allMatch(Predicate<? super T> predicate)
     */
    @Test
    public void allMatch() {
        SecureRandom sr = new SecureRandom();
        System.out.println("<< 랜덤숫자 3개모두 홀수인지 체크 >>");
        boolean bool = sr.ints(3, 1, 100)
                .peek(System.out::println)
                .allMatch(n -> (n & 1) == 1)
        ;
        System.out.println("결과 : "+ bool);
    }

    /**
     * 스트림의 모든 요소 중 1개라도 조건(predecate)을 만족하는지 여부를 반환
     * boolean  anyMatch(Predicate<? super T> predicate)
     */
    @Test
    public void anyMatch() {
        SecureRandom sr = new SecureRandom();
        System.out.println("<< 랜덤숫자 3개 중 1개라도 홀수인지 체크 >>");
        boolean bool = sr.ints(3, 1, 100)
                .peek(System.out::println)
                .anyMatch(n -> (n & 1) == 1)
                ;
        System.out.println("결과 : "+ bool);
    }

    /**
     * Builder를 반환하며, 해당 빌더는 {@link java.util.List}와 비슷하게 사용할 수 있다.
     * static <T> Stream.Builder<T>     builder()
     */
    @Test
    public void builder() {
        Stream.Builder<Integer> builder = Stream.builder();

        // 추가방법1
        builder.accept(10);
        builder.accept(11);
        builder.accept(12);
        // 추가방법2
        builder.add(1)
                .add(2)
                .add(3)
                ;
        Stream<Integer> stream = builder.build();

        stream.forEach(System.out::println);


    }

    @Test
    public void collect() {
        SecureRandom secureRandom = new SecureRandom();

        System.out.println();
        System.out.println("<< 숫자를 List객체로 반환 >>");
        System.out.println(" - SecureRandom -> InsStream -> collect(List)");
//        secureRandom.ints(1, 10).limit(10).forEach(System.out::println);
        List<Integer> collect1 = secureRandom.ints(10, 1, 10)
                .boxed()
//                .peek(System.out::println)
//                .collect(Collectors.toList())
                .collect(ArrayList::new
                        , List::add
                        , List::addAll
                )
                ;

        System.out.println(collect1);


        System.out.println();
        System.out.println("<< 숫자를 하나의 문자열로 합치기 >>");
        System.out.println(" - SecureRandom -> InsStream -> collect(StringBuffer)");
        StringBuilder collect2 = secureRandom.ints(10, 1, 10)
                .collect(StringBuilder::new, (sb, n) -> sb.append(",").append(n), (sb1, sb2) -> sb1.append(sb2));
        System.out.println(collect2);


        System.out.println();
        System.out.println("<< 홀수, 짝수 값을 구분하여 Map으로 반환 >>");
        System.out.println(" - SecureRandom -> InsStream -> collect(Map<String, List<Integer>>)");
        HashMap<String, List> collect3 = secureRandom.ints(10, 1, 10)
                .boxed()
                .collect(HashMap::new, (map, n) -> {
                    String key = (n & 1) == 1 ? "홀수" : "짝수";
                    List<Integer> list = (List<Integer>) map.get(key);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(n);

                    map.put(key, list);
                }, HashMap::putAll);

        System.out.println(collect3);
    }

    @Test
    public void concat() {
        SecureRandom secureRandom = new SecureRandom();
        IntStream int_stream1 = secureRandom.ints(10, 1, 10);
        IntStream int_stream2 = secureRandom.ints(10, 11, 20);

        System.out.println();
        System.out.println(" SecureRandom -> ints -> concat");
        IntStream concat1 = IntStream.concat(int_stream1, int_stream2);
        concat1.forEach(System.out::println);


        System.out.println();
        System.out.println("SecureRandom -> ints -> boxed -> concat -> map(Integer -> String) -> collect(joining)");
        Stream<Integer> boxed1 = secureRandom.ints(10, 1, 10).boxed();
        Stream<Integer> boxed2 = secureRandom.ints(10, 11, 20).boxed();
        Stream<Integer> concat2 = Stream.concat(boxed1, boxed2);

        String collect2 = concat2.map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(collect2);

        System.out.println();
        System.out.println("concat -> map -> collect(joining)");
        Stream<Integer> concat3 = Stream.concat(Stream.of(1, 2, 3, 4, 5), Stream.of(91, 92, 94));
        String collect3 = concat3.map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(collect3);

    }

    @Test
    public void count() {
        SecureRandom secureRandom = new SecureRandom();
        System.out.println();
        System.out.println("SecureRandom -> inits -> count");
        long count = secureRandom.ints(10, 1, 100).count();
        System.out.println(count);
    }

    /**
     * 중복값을 제외
     * Object.equals(Object)로 중복을 제거함
     */
    @Test
    public void distinct() {
        SecureRandom secureRandom = new SecureRandom();
        System.out.println();

        System.out.println("<< Integer distinct >>");
        System.out.println("SecureRandom -> inits -> distinct -> count");
        long count = secureRandom.ints(20, 1, 20)
                .peek(n -> System.out.printf("%d\n", n))
                .distinct()
                .peek(n -> System.out.printf("추가 ==> %d\n", n))
                .count();
        System.out.println();
        System.out.println("중복제거건수 : "+count);


        String s1 = "1";
        String s1_copy = s1;
        String s2 = "2";
        String s2se = "2";

        System.out.println();
        System.out.println("<< 문자열 distinct >>");
        long count1 = Stream.of(s1, s1_copy, s2se)
                .peek(n -> System.out.printf("%s\n", n))
                .distinct()
                .peek(n -> System.out.printf("추가 ==> %s\n", n))
                .count();


        Employee emp1 =VoBuilder.build(Employee::new)
                .with(v -> v.setName("이문세"))
                .with(v -> v.setSalary(20_000))
                .with(Employee::setGender, "male")
                .with(Employee::setDepartment, "가수팀")
                .get();

        Employee emp2 = VoBuilder.build(Employee::new)
                .with(Employee::setName, "신혜철")
                .with(Employee::setGender, "female")
                .with(Employee::setSalary, 18_000)
                .with(Employee::setDepartment, "가수팀")
                .get();

        Employee emp2clone = emp2;

        Employee emp3 = VoBuilder.build(Employee::new)
                .with(Employee::setName, "루시")
                .with(Employee::setGender, "female")
                .with(Employee::setSalary, 10_000)
                .with(Employee::setDepartment, "가수팀")
                .get();
        Employee emp3se = VoBuilder.build(Employee::new)
                .with(Employee::setName, "루시")
                .with(Employee::setGender, "female")
                .with(Employee::setSalary, 10_000)
                .with(Employee::setDepartment, "가수팀")
                .get();

        // vo객체 인스턴스로 중복제거
        // Employee의 hashcode, equals 메소드를 오라버라이 해서 중복건수를 조정할 수 있음
        System.out.println();
        System.out.println("<< VO distinct >>");
        long count2 = Stream.of(emp1, emp2, emp2clone, emp3, emp3se)
                .peek(n -> System.out.printf("%s\n", n))
                .distinct()
                .peek(n -> System.out.printf("추가 ==> %s\n", n))
                .count();
    }

    /**
     * dropWhile은 조건에 만족한 요소들을 버린다고 하는데...
     * 버리려지는 놈이 없네..ㅡㅡ;;;;;
     */
    @Test
    public void dropWhile() {
        List<Integer> ints = Arrays.asList(0,1,9,2,8,3,7,4,6,5);

        System.out.println("숫자값 '5'보다 큰값은 버림 (정열 X)");
        List<Integer> collect1 = ints.stream()
//                .peek(n -> System.out.printf("%d > 5 : %b\n", n, n > 5))
                .dropWhile(n -> n>5)
                .collect(Collectors.toList());
        System.out.println(collect1);


        System.out.println("숫자값 '5'보다 큰값은 버림 (정열 Y)");
        List<Integer> collect2 = ints.stream()
                .sorted()
                .dropWhile(n -> (n&1) == 1)
                .collect(Collectors.toList());
        System.out.println(collect2);
    }

    /**
     * dropWhile와 반대로..
     * 하지만 안된다.. 왜?? ㅡㅡ;
     */
    @Test
    public void takeWhile() {
        List<Integer> ints = Arrays.asList(0,1,9,2,8,3,7,4,6,5);

        System.out.println("숫자값 '5'보다 큰값만 취한다 (정열 X)");
        List<Integer> collect1 = ints.stream()
//                .peek(n -> System.out.printf("%d > 5 : %b\n", n, n > 5))
                .takeWhile(n -> n>5)
                .collect(Collectors.toList());
        System.out.println(collect1);
    }

    @Test
    public void filter() {
        List<Integer> ints = Arrays.asList(0,1,9,2,8,3,7,4,6,5);
        List<Integer> collect1 = ints.stream()
                .filter(n -> n>5)
                .collect(Collectors.toList());

        System.out.println(collect1);
    }

    /**
     * 스트림의 첫번째 요소를 반환
     * 병렬처리를 하더라도 안정적인(일관된) 결과값을 반환한다.
     */
    @Test
    public void findFirst() {
//        List<String> strings = Arrays.asList("a1", "b1", "c1", "d1", "e1", "a2", "b2", "c2", "d2", "e2");
        List<String> strings = Arrays.asList("a1", "b1", "c1", "d1", "e1", "a2", "b2", "c2", "d2", "e2", "b", "bbb");

        String e1 = strings.stream()
                .filter(s -> s.startsWith("b"))
                .findFirst()
                .get();

        System.out.println("single stream : "+ e1);

        String e2 = strings.stream()
                .parallel()
                .filter(s -> s.startsWith("b"))
                .findFirst()
                .get();
        System.out.println("parallel stream : "+ e2);
    }

    /**
     * 스트림의 요소를 찾아 반환
     * 단, 병렬처리시에는 반환되는 요소가 달라질수 있음
     */
    @Test
    public void findAny() {
//        List<String> strings = Arrays.asList("a1", "b1", "c1", "d1", "e1", "a2", "b2", "c2", "d2", "e2", "b", "bbb");
        List<String> strings = Arrays.asList("a1", "b1", "c1", "d1", "e1", "a2", "b2");
        String e1 = strings.stream()
                .filter(s -> s.startsWith("b"))
                .findAny()
                .get();

        System.out.println("single stream : "+ e1);

        String e2 = strings.parallelStream()
                .filter(s -> s.startsWith("b"))
                .findAny()
                .get();
        System.out.println("parallel stream : "+ e2);
    }

    /**
     * forEachOrdered 이놈을 잘 모르겠지... ㅡㅡ;;
     * parallel에서 순서를 보장해줌.
     * 테스트시 주의사항
     * {@link java.io.PrintStream}객체의 print 메소드는 동기화되어 있는 메소드가 아니므로 정상적으로 출력이 안되는 문제가 있더라..
     * 꼭~~ println으로 테스트 해야함.
     * strings.stream().parallel().forEachOrdered(System.out::print);     // 병열처리시 값을 정상적으로 출력못할수 있음
     * strings.stream().parallel().forEachOrdered(System.out::println);
     */
    @Test
    public void forEachOrdered() {
        System.out.println();
//        List<String> list = Arrays.asList("a1", "b1", "c1", "d1", "e1", "a2", "b2", "c2", "d2", "e2", "b", "bbb");
        List<Integer> list = (List<Integer>) IntStream.range(1, 10).boxed().toList();
        System.out.println("<<not parallel - forEach>>");
        list.stream().forEach(System.out::println);
        System.out.println("<<not parallel - forEachOrdered>>");
        list.stream().forEachOrdered(System.out::println);
        System.out.println();

        System.out.println("<<parallel - forEach>>");
        list.stream().parallel().forEach(System.out::println);
        System.out.println();
        System.out.println("<<parallel - forEachOrdered>>");
        list.stream().parallel().forEachOrdered(System.out::println);
        System.out.println();
    }


    /**
     * Stream의 요소로 새로운 Stream을 생성하여 반환
     */
    @Test
    public void flatMap() {
        System.out.println("<<파일문자열 출력>>");
        Path path = Paths.get("/Users/cjpak/temp", "readme.txt");
        try(BufferedReader bufferedReader = Files.newBufferedReader(path);) {
            // 파일의 전체문자열 읽어오고
            Stream<String> lines = bufferedReader.lines();
            // 문자열(문장)을 하나의 문자로 Stream으로 변환 후 Stream을 반환
            Stream<String> charStream = lines.flatMap(s -> Arrays.stream(s.split("")));
            // 문자 Stram 출력
            charStream.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("<< 다중 스트림을 하나의 스트립으로 >>");

        Stream<Integer> s1 = IntStream.range(1, 10).boxed();
        Stream<Integer> s2 = IntStream.range(10, 20).boxed();
        Stream<Integer> s3 = IntStream.range(20, 30).boxed();

        Stream<Stream<Integer>> stream = Stream.of(s1,s2,s3);
        stream.flatMap(s -> s).forEach(System.out::println);
    }

    /**
     * 객체를 생성해서 반환
     */
    public void generate() {
        Stream<StringBuilder> sb_stream = Stream.generate(()-> new StringBuilder());
        System.out.println(sb_stream);
        Stream<List<String>> list_stream = Stream.generate(()->new ArrayList<>());
    }

    /**
     * 초기값(seed)를 파라미터로 무한하게 함수(UnaryOperator<T> f)를 호출
     * 무한하게 호출되는 문제를 해결하기 위해서 limit를 호출. (절대건수로 제한)
     * 또는 {@link java.util.function.Predicate}를 사용해서 무한반복에 빠지는 것을 방지하도록 한다. (누적된 결과값으로 제한)
     */
    @Test
    public void ieterate() {
        System.out.println("<<문자열>>");
        Stream.iterate("초기값", s-> String.format("<%s>", s))
                .limit(10)
                .forEach(System.out::println);

        System.out.println("<<1~10 표현>>");
        Stream.iterate(1, n -> n+1)
                .limit(10)
                .forEach(System.out::println);

        System.out.println("<<1+2+3+..+10 = 합>>");
//        String collect1 = Stream.iterate(1, n -> n + 1).limit(10).map(String::valueOf).collect(Collectors.joining(","));
//        System.out.println(collect1);
//        Integer sum = Stream.iterate(1, n -> n + 1)
//                .limit(10)
//                .mapToInt(Integer::valueOf)
//                .boxed()
//                .collect(Collectors.summingInt(Integer::intValue));

        Integer sum = Stream.iterate(1, n -> n + 1)
                .limit(10)
                .mapToInt(Integer::intValue)
                .sum();

        System.out.println(sum);

        System.out.println("<<2의 10승 계산>>");
        Stream.iterate(2, n -> n*2)
                .limit(10)
                .forEach(System.out::println);


        System.out.println("<<1~10 표현 (limit미사용) >>");
        Stream.iterate(1, n -> n<=10, n -> n+1)
                .forEach(System.out::println);

        // 음.. 결과값으로 제어할수 있구만..
        // 어떻게 응용하면 좋은가???
        System.out.println("<<2의 10승 계산 (limit미사용) >>");
        Stream.iterate(2, n -> n<=1024, n -> n*2)
                .forEach(System.out::println);

        System.out.println("<< Fibonacci >>");
        int fibonacci_sum = Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
                .peek(a -> System.out.printf("[%s][%s]\n", a[0], a[1]))
                .limit(10)
                .mapToInt(a -> a[0])
                .sum();
        System.out.println("Fibonacci Sum : "+ fibonacci_sum);
    }

    @Test
    public void max() {
        int max = IntStream.range(1, 10).boxed().max(Integer::compareTo).get();
        int min = IntStream.range(1, 10).boxed().min(Integer::compareTo).get();
        System.out.println("max : "+ max);
        System.out.println("min : "+ min);
    }

    /**
     * Stream의 요소에서 {@link java.util.function.Predicate}에 만족한 값이 없는경우 ture를 반환하며,
     * true를 반환한다는 말은 모든 요소들을 확인했다는 이야기이므로
     * 속도가 느리다라는 말이 된다.
     */
    @Test
    public void noneMatch() {
        boolean b1 = IntStream.range(1, 10).boxed().peek(System.out::println).noneMatch(n -> n > 10);
        System.out.println("n > 10 noneMatch : "+b1);
        boolean b2 = IntStream.range(1, 10).boxed().peek(System.out::println).noneMatch(n -> n < 10);
        System.out.println("n < 10 noneMatch : "+b2);
    }

    /**
     * 스트림의 모든 요소들을 반복해서 accumulator를 수행 후 결과를 반환
     * 단, 병렬처리에에는 주의해서 사용해야 함
     */
    @Test
    public void reduce() {
        List<String> strings = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
//        Optional<String> reduce0 = strings.stream().reduce(String::concat);
        System.out.println("문자열 합치기 :" + strings.stream().reduce(String::concat).get());
        System.out.println("문자열 합치기 :" + strings.stream().reduce((a,b)->a.concat(b)).get());

        Optional<BigDecimal> sum = IntStream.range(1, 11)
                .boxed()
                .map(BigDecimal::new)
                .reduce(BigDecimal::add);
        System.out.println("1~10 합계(초기값 0) \t==> 1 + 2 + .... + 10  : "+ sum.get());

        // 1~10합
        Optional<Integer> reduce1 = IntStream.range(1, 11)
                .boxed()
                .reduce((a, b) -> a + b);//                .ifPresent(System.out::println);
        System.out.println("1~10 합계(초기값 0) \t==> 1 + 2 + .... + 10  : "+ reduce1.get());


        Integer reduce2 = IntStream.range(1, 11)
                .boxed()
                .reduce(10, (a, b) -> a + b);
        System.out.println("1~10 합계(초기값 10) \t==> 10 + 1 + 2 + .... + 10  : "+ reduce2);

        // 합을 구할 때는 벙렬처리에 큰문제가 없지만, 다른 처리시에는 계산에 오류가 발생할 수 있음.
        Integer reduce3 = IntStream.range(1, 100)
                .boxed()
                .parallel()
                .reduce(10, (a, b) -> a + b);
        System.out.println("[parallel] 1~10 합계(초기값 10) \t==> 10 + 1 + 2 + .... + 10  : "+ reduce3);

        Integer reduce4 = IntStream.range(1, 100)
                .boxed()
                .parallel()
                .reduce(10, (a, b) -> a + b, (v1, v2) -> v1 + v2);
        System.out.println("[parallel] 1~10 합계(초기값 10) \t==> 10 + 1 + 2 + .... + 10  : "+ reduce4);



        Integer reduce5 = IntStream.range(1, 11)
                .boxed()
                .reduce(0, (a, b) -> a - b);
        System.out.println("1~10 합계 \t==> 1 - 2 - .... - 10  : "+ reduce5);

        // 병렬처리시에는 계산에 오류가 발생할 수 있으므로 주의해서 사용해야 함.
        // 병렬처리시에는 계산에 오류가 발생할 수 있으므로 주의해서 사용해야 함.
        // 병렬처리시에는 계산에 오류가 발생할 수 있으므로 주의해서 사용해야 함.
        Integer reduce6 = IntStream.range(1, 11)
                .boxed()
                .parallel()
                .reduce(0, (a, b) -> a - b);
        System.out.println("[parallel] 1~10 합계(초기값 0) \t==> 0 - 1 - 2 - .... - 10  : "+ reduce6);

        // 병렬처리시에는 다음과 같이 처리해야 함
        Integer reduce7 = IntStream.range(1, 11)
                .boxed()
                .parallel()
                .reduce(0, (a, b) -> a - b, (v1, v2) -> v1 + v2);
        System.out.println("[parallel] 1~10 합계(초기값 0) \t==> 0 - 1 - 2 - .... - 10  : "+ reduce7);
    }
}
