package org.example.java8.stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 참고사이트
 * https://www.baeldung.com/java-8-comparator-comparing
 *
 */
class StreamSortedExamTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * syntax : Comparator<T> comparing(
     *              Function<? super T, ? extends U> keyExtractor,
     *              Comparator<? super U> keyComparator
     *          )
     */
    @Test
    void intList() {
        System.out.printf("" +
                "\n<< 숫자정렬 >>" +
                "\n 1. 1~100 까지의 값에서 마지막 1자리에 값으로만 정렬한다." +
                "\n"
        );
        IntStream.range(1, 100)
                .mapToObj(String::valueOf)
                .sorted(Comparator.comparing(String::valueOf, (s1, s2) -> {
                    return s1.charAt(s1.length()-1) - s2.charAt(s2.length()-1);
                }))
                .forEach(System.out::println);
                ;
    }

    /**
     * syntax : Comparator<T> comparing(Function<? super T, ? extends U> keyExtractor)
     *
     * 문자열 정렬
     * 1. 하나의 항목으로 정렬
     * 2. n개의 항목을 and 조건으로 정렬
     */
    @Test
    void stringList() {
        System.out.printf("" +
                "\n<< 문자열 List 정렬 >>" +
                "\n 1. 문자열로 정렬(오름차순)" +
                "\n 2. 문자열의 길이로 정렬(오름차순)" +
                "\n"
        );
        List<String> strList = Arrays.asList("apple", "banana", "kick", "pineapple"
                , "avocado", "orange", "mango", "watermelon", "plum", "melon"
                , "grape fruit", "hallabong", "grape", "kiwi", "golden kiwi"
        );
        strList.stream()
                .sorted(Comparator.comparing(String::toString).thenComparing(String::length))
//                .sorted(Comparator.comparing(String::toString).reversed().thenComparing(String::length))
                .forEach(System.out::println);

        System.out.printf("" +
                "\n<< 문자열 List 정렬 >>" +
                "\n 1. 문자열의 길이로 정렬(내림차순)" +
                "\n 2. 문자열로 정렬(오름차순)" +
                "\n"
        );
        strList.stream()
                .sorted(Comparator.comparing(String::length).reversed().thenComparing(String::toString))
                .forEach(System.out::println);


    }

    /**
     * Vo List객체정렬
     * 문자열 List객체 정렬과 기본적으로 동일하다.
     * 단, vo객체의 어떤 속성(항목)으로 정렬할 것인가를 선택하는 부분만 조금 다를뿐이다.
     */
    @Test
    void voList() {
        System.out.printf("" +
                "\n<< Employee Vo List 정렬 >>" +
                "\n 1. 급여 정렬(내림차순)" +
                "\n 2. 나이 정렬(오름차순)" +
                "\n"
        );
        List<Employee> cityList = Arrays.asList(
                new Employee("홍길동", 35, 770)
                , new Employee("이순신", 36, 740)
                , new Employee("이육사", 37, 780)
                , new Employee("심사임당", 44, 800)
                , new Employee("이익", 46, 800)
                , new Employee("정약용", 48, 800)
                , new Employee("세종대왕", 49, 880)
                , new Employee("용인", 36, 880)
                , new Employee("진안", 48, 900)
        );

        cityList.stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed().thenComparing(Employee::getAge))
                .forEach(System.out::println);

        System.out.println("\n<< 정렬키를 지정 후 정렬키의 값으로 정렬 >>" +
                "\n - 인자1 : 비교할 요소" +
                "\n - 인자2 : 정렬키 (compare구현)(나이의 마지막숫자만 정렬)");
        cityList.stream()
//                .sorted(Comparator.comparing(Employee::getAge, (s1, s2)-> {
//                    return s1.compareTo(s2);
//                })
//                .sorted(Comparator.comparing(Employee::getAge, (s1, s2)-> {
//                            String str1 = s1.toString();
//                            String str2 = s2.toString();
//                            return str1.charAt(str1.length()-1) - str2.charAt(str2.length()-1);
//                })
                .sorted(Comparator.comparing(Employee::getSalary, (s1, s2)-> {
                    // 급여 100단위별로 정렬
                    return (s1.intValue()/100) - (s2.intValue()/100);
                }).thenComparing(Employee::getAge))
                .forEach(System.out::println);

    }


    class Employee {
        private String name;
        private int age;
        private long salary;
        Employee(String name, int age, long salary) {
            this.name = name;
            this.age = age;
            this.salary = salary;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getSalary() {
            return salary;
        }

        public void setSalary(long salary) {
            this.salary = salary;
        }

        @Override
        public String toString() {
            return String.format("이름:%s, 나이:%d, 급여:%d", name, age, salary);
        }
    }
}