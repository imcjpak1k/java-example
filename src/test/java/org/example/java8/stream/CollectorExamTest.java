package org.example.java8.stream;

import org.example.java8.stream.vo.Employee;
import org.example.java8.util.VoBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Interface Collector<T,A,R>
 * Type Parameters:
 * T - the type of input elements to the reduction operation
 * A - the mutable accumulation type of the reduction operation (often hidden as an implementation detail)
 *
 * 해당 인터페이스는 스트림의 모든 요소를 하나의 객체(컨테이너)에 모으는 역할을 한다.
 * 기본적으로 supplier와 accumulator만 있으면 될거 같으나
 * 병렬로 처리시 각각의 병철처리한 결과(컨테이너)를 하나로 모으는 처리가 필요하는데
 * 해당역할을 하는 것이 combiner역할이다.
 *
 *
 * Collector interface는 4가지 함수를 지정하여 사용한다.
 *  - supplier() 결과값을 담는 컨테이너생성
 *  - accumulator() 컨테이너에 결과값을 누적
 *  - combiner() 2개이상의 컨테이너결과값을 하나로 결합
 *  - finisher() 최종결과값 수행
 */
class CollectorExamTest {
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Collector 인터페이스는 4개의 함수를 다음과 같은 순서로 구현되나 보다
     */
    @Test
    void exam() {
        System.out.println("<< Collector Interface는 다음과 같은 과정으로 처리된다. >>");

        // Supplier
        Supplier<StringBuilder> supplier = () -> new StringBuilder();

        // Accumulator
        BiConsumer<StringBuilder, Integer> accumulator = (sb, n) -> {
            if(!sb.isEmpty()) {
                sb.append("+");
            }
            sb.append(n);
        };

        // Combiner
        BinaryOperator<StringBuilder> combiner = (a, b) -> {
            return a.append("+").append(b);
        };

        StringBuilder sb1 = supplier.get();
        accumulator.accept(sb1, 1);
        accumulator.accept(sb1, 2);
        accumulator.accept(sb1, 3);
        accumulator.accept(sb1, 4);

        StringBuilder sb2 = supplier.get();
        accumulator.accept(sb2, 10);
        accumulator.accept(sb2, 20);
        accumulator.accept(sb2, 30);

        System.out.println(sb1);
        System.out.println(sb2);


        // finisher
        Function<StringBuilder, StringBuilder> finisher = (s) -> s.insert(0, "finisher : >> ");

        System.out.println("Combiner 결과값");
        System.out.println(finisher.apply(combiner.apply(sb1, sb2)));
    }

    /**
     * {@link StringBuilder}
     */
    @Test
    void exam1() {
        System.out.println("<< Collector Exam#1  - StringBuilder >>");
        System.out.println("숫자값 사이에 특수문자(+) 입력");


        StringBuilder collect = IntStream.range(1, 10)
                .parallel()
                .collect(
                    () -> new StringBuilder(),
                    (c, e) -> c.append(c.isEmpty() ? e : "+" + e ),
                    (c1, c2) -> c1.append("+").append(c2)
                );

        System.out.println("결과값 : "+ collect);
    }

    /**
     * {@link ArrayList}
     */
    @Test
    void exam2() {
        System.out.println("<< Collector Exam#2  - ArrayList >>");

        System.out.println("사용방법 #1");
        // 그냥사용

        IntStream.range(1, 10)
                .parallel()
                .filter(e -> (e & 1) == 1)
                .mapToObj(e -> String.format("[%s]", e))
                .collect(
                        () -> new ArrayList(),
                        (c, e) -> c.add(e),
                        (c1, c2) -> c1.addAll(c2)
                )
                .forEach(System.out::println);


        System.out.println("사용방법 #2 : 간결하게");
        // 표현식으로 좀더 간결하게..
        IntStream.range(20, 30)
                .parallel()
                .filter(e -> (e & 1) == 1)
                .mapToObj(e -> String.format("[%s]", e))
                .collect(
                        ArrayList::new,
                        List::add,
                        List::addAll
                )
                .forEach(System.out::println);


        System.out.println("사용방법 #3 : Collectors.toList()를 사용해서 더 간결하게");
        // 위의 소스를 구현해둔 메소드를 사용 :: Collectors.toList()
        IntStream.range(40, 50)
                .parallel()
                .filter(e -> (e & 1) == 1)
                .mapToObj(e -> String.format("[%s]", e))
//                .collect(Collectors.toList())
                .collect(Collectors.toCollection(ArrayList::new))
                .forEach(System.out::println);
    }

    @Test
    public void exam3() {
        Employee e1 = VoBuilder.build(Employee::new)
                .with(v -> v.setName("test1"))
                .with(v -> v.setSalary(10_000))
                .with(Employee::setDepartment, "보험코어개발팀 #1")
                .get();

        Employee e2 = VoBuilder.build(Employee::new)
                .with(Employee::setName, "test2")
                .with(Employee::setSalary, 12_000)
                .with(Employee::setDepartment, "보험코어개발팀 #1")
                .get();

        Employee e3 = VoBuilder.build(Employee::new)
                .with(Employee::setName, "test3")
                .with(Employee::setSalary, 12_000)
                .with(Employee::setDepartment, "보험코어개발팀 #2")
                .get();

//        Collector<Employee, ?, IntSummaryStatistics> employeeIntSummaryStatisticsCollector = Collectors.summarizingInt(Employee::getSalary);
        Map<String, IntSummaryStatistics> collect = Stream.of(e1, e2, e3)
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.summarizingInt(Employee::getSalary)));
        System.out.println(collect);
    }

}