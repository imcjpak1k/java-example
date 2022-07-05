package org.example.java8.function;

import java.util.function.Function;

/**
 * Function Interface Example
 * {@link java.util.function.Function}
 * {@link java.util.function.BiFunction}
 */
public class FunctionExam {
    public static void main(String[] str) {
        functionEx();
    }


    /**
     * {@link java.util.function.Function} interface 예제
     * Interface Function<T,R>
     * 입력값 1개( T ) 입력받아 결과값 1개 ( R ) 을 반환하는 interface이며,
     * 입력값과 출력값의 데이터 타입은 같을 필요는 없다.️
     *
     * default <V> Function<V,R> compose(Function<? super V,? extends T> before)
     * function을 chaining하여 호출하는 기능
     */
    public static void functionEx() {
        System.out.println("<< Function Interface >>");

        // 숫자값을 입력받아 문자열을 반환한다.
        Function<Integer, String> convertFn = x -> String.format("입력값 : %d", x);
        System.out.println( convertFn.apply(40) );

        // 2개의 function인터페이스를 연결해서 수행
        // 1번 fnction을 수행 후 결과값을 2번째 function으로 전달하여 결과값을 반환한다.
        // 여러개의 function을 열결하여 호출한다. Chain of Responsibility 패턴인듯...
        Function<Integer, String> fnc1st = n -> String.format("❤%d❤", n);
        Function<String, String> fnc2nd = s -> String.format("★%s★", s);
        Function<String, String> fnc3th = s -> String.format("<<%s>>", s);
        System.out.println(fnc2nd.compose(fnc1st).apply(100));
        System.out.println(fnc3th.compose(fnc2nd.compose(fnc1st)).apply(100));
    }
}
