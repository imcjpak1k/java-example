package org.example.java8.function;

import java.util.function.BiConsumer;

/**
 * {@link BiConsumer}는 두개의 파라미터를 받아서 처리하며, 결과값을 리턴하지 않는다.
 * - accept     전달받은 파라미터를 처리하며, 반환값 없음
 * - andThan    전달받은 파라미터로 {@link BiConsumer}를 연결한 순차적으로 호출한다.
 */
public class BiConsumerEx {
    public static void main(String[] args) {
        ex1();
    }

    /**
     * 기본 api사용
     *
     */
    public static void ex1() {
        System.out.println("<< accept >>");
        // 2가지 타입 넘김
        BiConsumer<String, Integer> bi01 = (fmt, n) -> {
          System.out.printf(fmt, n);
        };

        bi01.accept("accpet :: 이번에 수익률이 %d%%를 기록했어~~ ^^\n", 100);

        // 곱하기
        BiConsumer<Integer, Integer> multiply = (n1, n2) -> {
            System.out.printf("- 곱하기 : %d * %d = %d\n", n1, n2, n1*n2);
        };
        multiply.accept(2, 5);
        multiply.accept(2, 6);
        multiply.accept(2, 7);

        // 더하기
        BiConsumer<Integer, Integer> plus = (n1, n2) -> {
            System.out.printf("- 더하기 : %d + %d = %d\n", n1, n2, n1+n2);
        };
        plus.accept(2,5);
        plus.accept(2,6);
        plus.accept(2,7);
        plus.accept(2,8);

        System.out.println("<< andThen >>");
        System.out.println(" >> 곱하기 -> 더하기 -> 곱하기 -> 더하기");
        multiply.andThen(plus).andThen(multiply).andThen(plus).accept(2,6);
    }
}
