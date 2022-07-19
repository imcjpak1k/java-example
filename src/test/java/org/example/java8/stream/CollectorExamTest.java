package org.example.java8.stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

/**
 * Interface Collector<T,A,R>
 * Type Parameters:
 * T - the type of input elements to the reduction operation
 * A - the mutable accumulation type of the reduction operation (often hidden as an implementation detail)
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
    void ex() {
        System.out.println("<< 기본과정을 풀어서 해보자... >>");

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

//        combiner.apply(sb1, sb2);

        // finisher
        System.out.println("Combiner 결과값");
        System.out.println(combiner.apply(sb1, sb2));
    }
}