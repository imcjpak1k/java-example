package org.example.java8.stream;

import org.example.java8.util.VoBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    /**
     * 정렬....
     */
    @Test
    public void divdVoSort() {
        System.out.println(BigDecimal.TEN.compareTo(BigDecimal.ZERO));
        System.out.println(BigDecimal.ZERO.compareTo(BigDecimal.TEN));
        System.out.println("<< VO목록 정렬 >>");
        System.out.println("배당적용율과 괴리값이 가장 큰 VO를 먼저 먼저배당하도록 한다.");
        System.out.println("<정렬조건>");
        System.out.println(" 1. 배당율이 작은건");
        System.out.println(" 2. 배당괴리율(적용배당율-배당율)이 큰건");

        DivdVo v1 = VoBuilder.build(DivdVo::new)
                .with(DivdVo::setName, "길규환")
                .with(DivdVo::setHighPriority, false)
                .with(DivdVo::setDivdApRt, new BigDecimal("64.3"))    // 적용배당율
                .with(DivdVo::setDivdRt, new BigDecimal("55.6"))       // 실배당율
                .get();
        DivdVo v2 = VoBuilder.build(DivdVo::new)
                .with(DivdVo::setName, "이민아")
                .with(DivdVo::setHighPriority, false)
                .with(DivdVo::setDivdApRt, new BigDecimal("21.4"))
                .with(DivdVo::setDivdRt, new BigDecimal("22.2"))
                .get();
        DivdVo v3 = VoBuilder.build(DivdVo::new)
                .with(DivdVo::setName, "이규민")
                .with(DivdVo::setHighPriority, false)
                .with(DivdVo::setDivdApRt, new BigDecimal("14.3"))
                .with(DivdVo::setDivdRt, new BigDecimal("22.2"))
                .get();


        // 정렬순서1) 우선순위
//        Comparator<DivdVo> comparing1 = Comparator.comparing(DivdVo::isHighPriority);
        Comparator<DivdVo> comparing1 = Comparator.comparing(v->v.isHighPriority() ? 0 : 9);
        // 정렬순서2) 배당괴리울 오름차순
        Comparator<DivdVo> comparing2 = Comparator.comparing(v->v.getDivdApRt().subtract(v.divdRt));
        // 정렬순서3) 배당건수 오름차순
        Comparator<DivdVo> comparing3 = Comparator.comparing(DivdVo::getDivdRt);

//        // 조건1 + 조건2 ..음... 내림차순 정렬로 변경시 데이터가 달라질듯.. ㅡㅡ;;; 테스트 안해봄.. ㅋㅋ
//        Comparator<DivdVo> comparing =
//                Comparator.comparing(DivdVo::getDivdRt)
//                        .thenComparing(Comparator.comparing(v->v.getDivdApRt().subtract(v.getDivdRt())));

        Stream.of(v1,v2,v3)
//                .filter(c1 -> c1.getDivdApRt().compareTo(c1.getDivdRt()) != -1)
//                .sorted(Comparator.comparing(DivdVo::getDivdRt)
//                        .thenComparing(v->v.getDivdApRt().subtract(v.divdRt)).reversed()
//                )
                .sorted(comparing1.thenComparing(comparing2.reversed().thenComparing(comparing3)))
                .forEach(System.out::println);
                ;
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



    /**
     * 배당적용
     */
    class DivdVo {
        private String name;
        private boolean highPriority;
        private BigDecimal divdApRt;
        private BigDecimal divdRt;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getDivdApRt() {
            return divdApRt;
        }

        public void setDivdApRt(BigDecimal divdApRt) {
            this.divdApRt = divdApRt;
        }

        public BigDecimal getDivdRt() {
            return divdRt;
        }

        public void setDivdRt(BigDecimal divdRt) {
            this.divdRt = divdRt;
        }

        public boolean isHighPriority() {
            return highPriority;
        }

        public void setHighPriority(boolean highPriority) {
            this.highPriority = highPriority;
        }

        public BigDecimal getDivdRtDif() {
            // 배당율이 적용보다율보다 크거나 같으면 0으로...
            if(divdRt.compareTo(divdApRt) != -1) {
                return BigDecimal.ZERO;
            }
            return this.divdApRt.subtract(divdRt);
        }
        @Override
        public String toString() {
            return "name:"+ this.name +", divdApRt:"+ this.divdApRt +", divdRt:"+ divdRt + ",rtdiff:"+ divdApRt.subtract(divdRt);
        }
    }
}