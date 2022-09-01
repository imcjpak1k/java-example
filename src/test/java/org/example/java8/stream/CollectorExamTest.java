package org.example.java8.stream;

import org.example.java8.stream.vo.Employee;
import org.example.java8.util.VoBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/stream/Collectors.html
 *
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
     *  <R> R collect(     java.util.function.Supplier<R> supplier,
     *     java.util.function.ObjIntConsumer<R> accumulator,
     *     java.util.function.BiConsumer<R, R> combiner )
     */
    @Test
    void collect() {
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

    /**
     * static <T> Collector<T,?,Double>     averagingDouble(ToDoubleFunction<? super T> mapper)
     * static <T> Collector<T,?,Double>     averagingInt(ToIntFunction<? super T> mapper)
     * static <T> Collector<T,?,Double>     averagingLong(ToLongFunction<? super T> mapper)
     */
    @Test
    public void averagingDouble() {

        // 직원목록
        List<Employee> employees = getEmployees();

        System.out.println("<< 전직원 급여평균 >>");
        System.out.println(" - averagingDouble");
        Collector<Employee, ?, Double> collector1
                = Collectors.averagingDouble(e -> e.getSalary());

        Double collect1 = employees.stream().collect(collector1);
        System.out.println("전직원 급여평균 : " + collect1);


        System.out.println("<< 부서별 평균급여 >>");
        System.out.println(" - groupingBy -> averagingDouble");
        Collector<Employee, ?, Map<String, Double>> collector2
                = Collectors.groupingBy(Employee::getDepartment, Collectors.averagingLong(Employee::getSalary));

        // 부서별-평균
        Map<String, Double> departmentAvg = employees.stream().collect(collector2);
        System.out.println(departmentAvg);
    }


    /**
     * static <T,A,R,RR> Collector<T,A,RR>      collectingAndThen(Collector<T,A,R> downstream, Function<R,RR> finisher)
     */
    @Test
    public void collectingAndThen() {
        List<Employee> employees = getEmployees();

        // 이름을 List객체로 만든 후 불변객체로 변경
        System.out.println(" - collectingAndThen -> mapping -> unmodifiableList");
        Collector<Employee, Object, List<String>> collector1
                = Collectors.collectingAndThen(Collectors.mapping(Employee::getName, Collectors.toList()), Collections::unmodifiableList);

        List<String> collect1 = employees.stream().collect(collector1);
        System.out.println(collect1);

        // 이름을 List객체로 받고 객체의 toString을 호출함
        System.out.println(" - collectingAndThen -> mapping -> toString");
        Collector<Employee, Object, String> collector2
                = Collectors.collectingAndThen(Collectors.mapping(Employee::getName, Collectors.toList()), Collection::toString);
        String collect2 = employees.stream().collect(collector2);
        System.out.println(collect2);

        // 이름목록을 생성 후 하나의 문자열로 반환
        System.out.println(" - collectingAndThen -> mapping -> joining");
        Collector<Employee, Object, String> collector3
                = Collectors.collectingAndThen(Collectors.mapping(Employee::getName, Collectors.toUnmodifiableList()), list -> {
//                = Collectors.collectingAndThen(Collectors.mapping(e -> String.format("%s(길이:%d)", e.getName(), e.getName().length()), Collectors.toUnmodifiableList()), list -> {
            return list.stream().collect(Collectors.joining(" | "));
        });

        String collect3 = employees.stream().collect(collector3);
        System.out.println(collect3);


        // todo 활용할수 있는 많은 방법이 있을거 같으나 생각이 안난다.. ㅡㅡ;;;;
    }

    /**
     * static <T> Collector<T,?,Long>       counting()
     * static <T> Collector<T,?,List<T>>    toList()
     * static <T,A,R> Collector<T,?,R>      filtering(Predicate<? super T> predicate, Collector<? super T,A,R> downstream)
     */
    @Test
    public void counting() {
        System.out.println("<< counting >>");
        List<Employee> employees = getEmployees();

        // filter
        Predicate<Employee> filtering = employee -> employee.getSalary() > 15_000;


        System.out.println();
        System.out.println("<< 전체건수 >>");
        System.out.println(" - counting");
        Long collect = employees.stream().collect(Collectors.counting());
        System.out.println(collect);

        Long collect0 = employees.stream().collect(Collectors.reducing(0L, e -> 1L, Long::sum));
        System.out.println(collect0);


        System.out.println();
        System.out.println("<< 급여가 15,000 초과건수 >>");
        System.out.println(" - stream.filter -> counting");
        Long collect1 = employees.stream().filter(filtering).collect(Collectors.counting());
        System.out.println(collect1);

        System.out.println();
        System.out.println("<< 급여가 15,000 초과건수 >>");
        System.out.println(" - filter -> counting");

        Long collect2 = employees.stream().collect(Collectors.filtering(filtering, Collectors.counting()));
        System.out.println(collect2);



        System.out.println();
        System.out.println("<< 부서별-급여 15,000 초과직원 수 >>");
        System.out.println(" - groupingBy -> filter -> counting");
        Collector<Employee, ?, Map<String, Long>> collector3
                = Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.filtering(filtering, Collectors.counting()));
        Map<String, Long> collect3 = employees.stream().collect(collector3);

        collect3.forEach((k, v)-> {
            System.out.printf("  - %s ==> %s\n", k, v);
        });

        System.out.println();
        System.out.println("<< 부서별-급여 15,000 초과직원 수 >>");
        System.out.println(" - groupingBy -> collectingAndThen -> filter -> counting");
        // 예) 전체중 몇명...==> 가수팀 3/5
        Collector<Employee, ?, Map<String, List<Employee>>> collector4
                = Collectors.groupingBy(Employee::getDepartment, Collectors.toList());

        Collector<Employee, ?, Map<String, String>> employeeMapCollector = Collectors.collectingAndThen(collector4, map -> {
            Map<String, String> return_map = new LinkedHashMap<>();
            map.keySet().forEach(key -> {
                System.out.println(key);

                List<Employee> list = map.get(key);
                long tot_count = list.size();
                long count = list.stream()
                        .filter(filtering)
                        .peek(System.out::println)
                        .count();

                return_map.put(key, String.format("%d/%d (명)", count, tot_count));


            });
            return return_map;
        });

        Map<String, String> collect4 = employees.stream().collect(employeeMapCollector);
        System.out.println(collect4);
    }

    /**
     * static <T,A,R> Collector<T,?,R>      filtering(Predicate<? super T> predicate, Collector<? super T,A,R> downstream)
     * 성능을 위해서는 filtering을 먼저하는 것이 좋을 것으로 생각된다.
     */
    @Test
    public void filtering() {
        System.out.println("<< filtering >>");

        List<Employee> employees = getEmployees();
        // filter
        Predicate<Employee> filtering = employee -> employee.getSalary() > 15_000;

        System.out.println("<< 부서별 급여요약 (15,000이상) >>");
        System.out.println(" - filtering(급여) -> groupingBy(부서) -> summarizingInt(급여)");

        Collector<Employee, ?, Map<String, IntSummaryStatistics>> collector1
                = Collectors.filtering(filtering, Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.summarizingInt(Employee::getSalary)));

        Map<String, IntSummaryStatistics> collect1 = employees.stream()
//                .peek(System.out::println)
                .collect(collector1);

        System.out.println();
        collect1.forEach((k, v) -> {
            System.out.printf(" %s ==> %s\n", k, v);
        });



        System.out.println();
        System.out.println("<< 부서별-직원(Employee)목록 (15,000이상) >>");
        System.out.println(" - filtering(급여) -> groupingBy(부서) -> toList(이름)");
        Collector<Employee, ?, Map<String, List<Employee>>> collector2
//                = Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.filtering(filtering, Collectors.toList()));
                = Collectors.filtering(filtering, Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.toList()));

        Map<String, List<Employee>> collect2 = employees.stream().collect(collector2);

        collect2.forEach((k, v) -> {
            System.out.println(k);
            v.forEach(e -> System.out.printf(" - %s \n", e));
        });



        System.out.println();
        System.out.println("<< 부서별-직원(이름) 목록(15,000이상) >>");
        System.out.println(" - filtering(급여) -> groupingBy(부서) -> mapping(이름) -> toList(이름)");

        Collector<Employee, ?, Map<String, List<String>>> employeeMapCollector3
//                = Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.filtering(filtering, Collectors.mapping(Employee::getName, Collectors.toList())));
                = Collectors.filtering(filtering, Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.mapping(Employee::getName, Collectors.toList())));

        Map<String, List<String>> collect3 = employees.stream().collect(employeeMapCollector3);
        collect3.forEach((k, v) -> {
            System.out.println(k);
            v.forEach(s -> System.out.printf(" - %s \n", s));
        });

        System.out.println();
        System.out.println("<< 직원이름(문자열) (15,000이상) >>");
        System.out.println(" - filtering(급여) -> mapping(이름) -> joining(이름, ',')");
        Collector<Employee, ?, String> filtering4
                = Collectors.filtering(filtering, Collectors.mapping(Employee::getName, Collectors.joining(",")));

        String collect4 = employees.stream().collect(filtering4);
        System.out.println( " - "+ collect4);

        System.out.println();
        System.out.println("<< 부서별-직원이름(문자열) (15,000이상) >>");
        System.out.println(" - filtering(급여) -> groupingBy(부서) -> mapping(이름) -> joining(이름)");
        Collector<Employee, ?, Map<String, String>> filtering5
                = Collectors.filtering(filtering
                        , Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new
                            , Collectors.mapping(Employee::getName, Collectors.joining(","))));

        Map<String, String> collect5 = employees.stream().collect(filtering5);
        collect5.forEach((k, v) -> {
            System.out.printf(" %s ==> %s \n", k, v);
        });
    }


    /**
     * static <T,U,A,R> Collector<T,?,R>    flatMapping(Function<? super T,? extends Stream<? extends U>> mapper, Collector<? super U,A,R> downstream)
     */
    @Test
    public void flatMapping() {
        System.out.println("<< flatMapping >>");
        List<Employee> employees = getEmployees();
        List<List<Employee>> lists = Arrays.asList(getEmployees(), getEmployees(), getEmployees());

        System.out.println();
        System.out.println("<< 중첩리스트를 하나의 목록으로 >>");
        System.out.println(" - flatMapping -> toList");
        List<Employee> collect1 = lists.stream().collect(Collectors.flatMapping(List::stream, Collectors.toList()));
        collect1.forEach(System.out::println);

//        List<Employee> collect3 = lists.stream().collect(Collectors.flatMapping(List::stream, Collectors.toMap());


        System.out.println();
        System.out.println(" - flatMapping -> toList -> sorted");
//        List<Employee> collect2 = lists.stream().collect(Collectors.flatMapping(e -> e.stream().sorted(Comparator.comparing(Employee::getName)), Collectors.toList()));
        List<Employee> collect2 = lists.stream()
                .collect(Collectors.flatMapping(List::stream, Collectors.toList()))
                .stream().sorted(Comparator.comparing(Employee::getName))
                .toList();
        collect2.forEach(System.out::println);

        System.out.println();
        System.out.println(" - flatMapping -> toList -> grouping");
        Map<Integer, List<Employee>> collect3 = lists.stream()
                .collect(Collectors.groupingBy(Collection::size, Collectors.flatMapping(ele -> ele.stream().filter(e -> e.getSalary() > 15_000), Collectors.toList())));
        System.out.println(collect3);


        System.out.println(" - flatMapping -> filter -> toList");
        List<Employee> collect4 = lists.stream()
                .collect(Collectors.flatMapping(l -> l.stream().filter(e -> e.getSalary() > 19_000), Collectors.toList()));
        collect4.stream().forEach(System.out::println);

        System.out.println(" - flatMap -> filter -> toList");
        List<Employee> employees1 = lists.stream().flatMap(l -> l.stream().filter(e -> e.getSalary() > 19_000)).toList();
        employees1.forEach(System.out::println);

        // todo flatMapping는 어떻게 응용해서 사용해야 할까??
    }

    /**
     * static <T> Collector<T,?,List<T>>    toList()
     */
    @Test
    public void toList() {
        System.out.println("<< toList() >>");
        List<Employee> employees = getEmployees();

        // filter
        Predicate<Employee> predicate = employee -> employee.getSalary() > 15_000;

        System.out.println("<< 직원급여가 15,000 초과인 사람목록 >>");
        System.out.println("filter -> toList");
        List<Employee> collect1 = employees.stream()
                .filter(e->e.getSalary() > 15_000)
                .collect(Collectors.toList());

        collect1.forEach(System.out::println);

        String str = "a,b,c,d,e,f,g";
        System.out.println("<< 문자열을 목록으로 변경 >>");
        System.out.println("split -> Stream.toList");
        Arrays.stream(str.split(",")).toList()
                .forEach(System.out::println);


        System.out.println("<< 문자열을 목록으로 변경 >>");
        System.out.println("" +
                " -> Collectors.toList");
        Arrays.stream(str.split(",")).collect(Collectors.toList())
                .forEach(System.out::println);

    }



    /**
     * https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/stream/Collectors.html
     * groupingBy
     * - static <T,K> Collector<T,?,Map<K,List<T>               groupingBy(Function<? super T,? extends K> classifier)
     * - static <T,K,A,D> Collector<T,?,Map<K,D>>               groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
     * - static <T,K,D,A,M extends Map<K,D>> Collector<T,?,M>   groupingBy(Function<? super T,? extends K> classifier, Supplier<M> mapFactory, Collector<? super T,A,D> downstream)
     * - static <T> Collector<T,?,Optional<T>>                  maxBy(Comparator<? super T> comparator)
     * - static <T> Collector<T,?,Optional<T>>                  minBy(Comparator<? super T> comparator)
     */
    @Test
    public void groupingBy() {
        System.out.println("<< groupingBy >>");
        // 직원목록
        List<Employee> employees = getEmployees();


        // groupingBy(Function<? super T,? extends K> classifier)
        // groupingBy(Function<? super T,? extends K> classifier)
        // groupingBy(Function<? super T,? extends K> classifier)
        System.out.println("<< 부서별 ==> 직원목록 >>");
        Collector<Employee, ?, Map<String, List<Employee>>> collector1
                = Collectors.groupingBy(Employee::getDepartment);

        Map<String, List<Employee>> collect1 = employees.stream().collect(collector1);
        collect1.forEach((key, entry) -> {
            System.out.printf("key: %s, entry.toString() : %s \n", key, entry);
        });


        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        // maxBy(Comparator<? super T> comparator)
        System.out.println();
        System.out.println("<< 성별 고액급여(최대값) => 직원 >>");
        Collector<Employee, ?, Map<String, Optional<Employee>>> collector2
                = Collectors.groupingBy(Employee::getGender, Collectors.maxBy(Comparator.comparing(Employee::getSalary)));
//                = Collectors.groupingBy(Employee::getGender, Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)));

        Map<String, Optional<Employee>> collect2 = employees.stream().collect(collector2);
        collect2.forEach((key, entry) -> {
            System.out.printf("key: %s, entry.toString() : %s \n", key, entry);
        });

//        Collector<Employee, ?, Map<String, Set<Object>>> employeeMapCollector = Collectors.groupingBy(Employee::getGender, Collectors.toSet());

        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        System.out.println();
        System.out.println("<< 부서별 + 성별 ==> 직원목록 >>");
        Collector<Employee, ?, Map<String, Map<String, List<Employee>>>> collector3
                = Collectors.groupingBy(Employee::getDepartment, Collectors.groupingBy(Employee::getGender));

        Map<String, Map<String, List<Employee>>> collect3 = employees.stream().collect(collector3);
        collect3.forEach((key, entry) -> {
            System.out.printf("key: %s, entry.toString() : %s \n", key, entry);
        });

//        Collection<Map<String, List<Employee>>> collectr = employees.stream().collect(Collectors.collectingAndThen(collector3, e -> e.values()));

        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        System.out.println();
        System.out.println("<< groupingBy: 부서별 + 성별 ==> summarizing >>");
        Collector<Employee, ?, Map<String, Map<String, IntSummaryStatistics>>> collector4
                = Collectors.groupingBy(Employee::getDepartment, Collectors.groupingBy(Employee::getGender, Collectors.summarizingInt(Employee::getSalary)));

        Map<String, Map<String, IntSummaryStatistics>> collect4 = employees.stream().collect(collector4);
        collect4.forEach((key, entry) -> {
            System.out.printf("key: %s, entry.toString() : %s \n", key, entry);
        });

        // groupingBy(Function<? super T,? extends K> classifier, Supplier<M> mapFactory, Collector<? super T,A,D> downstream)
        // groupingBy(Function<? super T,? extends K> classifier, Supplier<M> mapFactory, Collector<? super T,A,D> downstream)
        // groupingBy(Function<? super T,? extends K> classifier, Supplier<M> mapFactory, Collector<? super T,A,D> downstream)

        System.out.println();
        System.out.println("<< groupingBy: 부서별 + 성별 ==> summarizing (부서명 정렬, LinkedHashMap으로 변경) >>");
        Collector<Employee, ?, Map<String, Map<String, IntSummaryStatistics>>> collector5
                = Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.groupingBy(Employee::getGender, Collectors.summarizingInt(Employee::getSalary)));

        Map<String, Map<String, IntSummaryStatistics>> collect5 = employees.stream().collect(collector5);
        collect5.forEach((key, entry) -> {
            System.out.printf("key: %s, entry.toString() : %s \n", key, entry);
        });
    }


    public void toCollection() {
        System.out.println("<< toCollection >>");

    }

    public void toSet() {
        List<Employee> employees = getEmployees();
        Set<Employee> collect = employees.stream().collect(Collectors.toSet());

    }

    public void toMap() {
        List<Employee> employees = getEmployees();

    }


    /**
     * summarizingInt를 사용하면 부서별 합계, 평균, 건수, 최소값, 최대값을 한번에 구할 수 있다.
     */
    @Test
    public void summarizingInt() {
        System.out.println("<< summarizingInt: 부서별 급여합계 >>");

        // 직원목록
        List<Employee> employees = getEmployees();

        Collector<Employee, ?, IntSummaryStatistics> employeeIntSummaryStatisticsCollector = Collectors.summarizingInt(Employee::getSalary);

        // 부서별-급여합계, 급여평균, 최소급여, 최대급여, 건수
        Map<String, IntSummaryStatistics> departmentSum = employees.stream()
//                .collect(Collectors.groupingBy(Employee::getDepartment, employeeIntSummaryStatisticsCollector));
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.summarizingInt(Employee::getSalary)));
        System.out.println(departmentSum);

        departmentSum.forEach((key,entry) -> {
            System.out.printf("\n 부서명(%s), 사원수(%s), 급여합계(%s), 급여평균:(%s), 최소급여:(%s) 최대급여:(%s)"
                    , key
                    , entry.getCount(), entry.getSum(), entry.getAverage(), entry.getMin(), entry.getMax());
        });

    }

    /**
     * - static <T> Collector<T,?,Optional<T>>  maxBy(Comparator<? super T> comparator)
     * - static <T> Collector<T,?,Optional<T>>  minBy(Comparator<? super T> comparator)
     */
    @Test
    public void maxBy() {
        // 직원목록
        List<Employee> employees = getEmployees();

        // maxBy(Comparator<? super T> comparator)
        System.out.println("<< 급여(최대값) >>");
        Collector<Employee, ?, Optional<Employee>> collector1
                = Collectors.maxBy(Comparator.comparingInt(Employee::getSalary));

        Optional<Employee> collect1 = employees.stream().collect(collector1);
        System.out.println(collect1);

        System.out.println("<< 급여(최소값) >>");
        Collector<Employee, ?, Optional<Employee>> collector2
                = Collectors.minBy(Comparator.comparingInt(Employee::getSalary));

        Optional<Employee> collect2 = employees.stream().collect(collector2);
        System.out.println(collect2);


        // groupingBy(Function<? super T,? extends K> classifier, Collector<? super T,A,D> downstream)
        // maxBy(Comparator<? super T> comparator)
        System.out.println("<< groupingBy: 성별 ==> 급여(최대값) >>");
        Collector<Employee, ?, Map<String, Optional<Employee>>> collector3
                = Collectors.groupingBy(Employee::getGender, Collectors.maxBy(Comparator.comparing(Employee::getSalary)));
//                = Collectors.groupingBy(Employee::getGender, Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)));

        Map<String, Optional<Employee>> collect3 = employees.stream().collect(collector3);
        collect3.forEach((key, entry) -> {
            System.out.printf("key: %s, entry.toString() : %s \n", key, entry);
        });
    }

    /**
     * static <T> Collector<T,?,Map<Boolean,List<T>>>   partitioningBy(Predicate<? super T> predicate)
     * static <T,D,A> Collector<T,?,Map<Boolean,D>>     partitioningBy(Predicate<? super T> predicate, Collector<? super T,A,D> downstream
     */
    @Test
    public void partitioningBy() {
        System.out.println("<< partitioningBy >>");

        List<Employee> employees = getEmployees();
        System.out.println("<< 급여 19,000 이상(true), 미만(false)을 그룹으로 직원정보 분류 >>");
        Map<Boolean, List<Employee>> collect1 = employees.stream()
                .collect(Collectors.partitioningBy(employee -> employee.getSalary() >= 19_000));
        collect1.forEach((bool, entry) -> {
            System.out.printf("그릅 : %b\n", bool);
            entry.stream()
                    .sorted(Comparator.comparingLong(Employee::getSalary))
                    .forEach(System.out::println);
        });


        System.out.println();
        System.out.println("<< 급여 19,000 이상(true), 미만(false)을 그룹으로 직원정보 분류 및 부서별 직원목록 >>");
        System.out.println(" - groupingBy -> groupingBy 를 이용하면 동일하게 구현가능해 보입니다.");
        System.out.println(" - partitioningBy -> groupingBy");
        Map<Boolean, Map<String, List<Employee>>> collect2 = employees.stream()
                .collect(Collectors.partitioningBy(employee -> employee.getSalary() >= 19_000, Collectors.groupingBy(Employee::getDepartment)));

        collect2.forEach((bool, entry) -> {
            System.out.printf("그릅 : %b\n", bool);
//            System.out.println(entry);
            entry.forEach((key, part_employees) -> {
                System.out.printf("부서 : %s\n", key);
                part_employees.stream()
                        .sorted(Comparator.comparingLong(Employee::getSalary))
                        .forEach(System.out::println);

            });
        });

        System.out.println();
        System.out.println("<< 급여 19,000 이상(true), 미만(false)을 그룹으로 직원정보 분류 및 부서별 직원목록 >>");
        System.out.println(" - groupingBy -> groupingBy (위의 partitioningBy를 동일결과)");
        Map<Boolean, Map<String, List<Employee>>> collect3 = employees.stream()
                .collect(Collectors.groupingBy(employee -> employee.getSalary() >= 19_000, Collectors.groupingBy(Employee::getDepartment)));

        collect3.forEach((bool, entry) -> {
            System.out.printf("그릅 : %b\n", bool);
            entry.forEach((department, department_employees) -> {
                System.out.printf("부서 : %s\n", department);
                department_employees.stream()
                        .sorted(Comparator.comparingLong(Employee::getSalary))
                        .forEach(System.out::println);
            });
        });
    }

    /**
     * static <T,U,A,R> Collector<T,?,R>    mapping(Function<? super T,? extends U> mapper, Collector<? super U,A,R> downstream)
     */
    @Test
    public void mapping() {
        // 직원목록
        List<Employee> employees = getEmployees();

        // mapping(Function<? super T,? extends U> mapper, Collector<? super U,A,R> downstream)
        System.out.println("<< 직원명 ==> 문자열로 합치기 >>");
        Collector<Employee, ?, String> collector1
                = Collectors.mapping(Employee::getName, Collectors.joining(","));

        String collect1 = employees.stream().collect(collector1);
        System.out.println(collect1);


        System.out.println("<< 직원명 ==> List반환 >>");
        Collector<Employee, ?, List<String>> collector2
                = Collectors.mapping(Employee::getName, Collectors.toList());
        List<String> collect2 = employees.stream().collect(collector2);
        System.out.println(collect2);

        System.out.println("<< 직원명 ==> Set반환(중복되는 이름은 제거됨) >>");
        Collector<Employee, ?, Set<String>> collector3
                = Collectors.mapping(Employee::getName, Collectors.toSet());
        Set<String> collect3 = employees.stream().collect(collector3);
        System.out.println(collect3);
        // todo mapping 더 생각해보자...

    }

    /**
     * static <T> Collector<T,?,Optional<T>>    reducing(BinaryOperator<T> op)
     * static <T> Collector<T,?,T>              reducing(T identity, BinaryOperator<T> op)
     * static <T,U> Collector<T,?,U>            reducing(U identity, Function<? super T,? extends U> mapper, BinaryOperator<U> op)
     */
    @Test
    public void reducing() {
        System.out.println("<< reducing >>");
        List<Employee> employees = getEmployees();

        // static <T> Collector<T,?,Optional<T>>    reducing(BinaryOperator<T> op)
        // static <T> Collector<T,?,Optional<T>>    reducing(BinaryOperator<T> op)
        // static <T> Collector<T,?,Optional<T>>    reducing(BinaryOperator<T> op)
        System.out.println();
        System.out.println("<< 급여가 제일 적은사람 >>");
        Optional<Employee> collect1 = employees.stream().collect(Collectors.reducing((a, b) -> {
            return a.getSalary() < b.getSalary() ? a : b;
        }));
        System.out.println(collect1);

        System.out.println();
        System.out.println("<< 급여합계 >>");
        Optional<Integer> collect2 = employees.stream().map(Employee::getSalary).collect(Collectors.reducing((a, b) -> a + b));
        System.out.println(collect2);

        // static <T> Collector<T,?,T>              reducing(T identity, BinaryOperator<T> op)
        // static <T> Collector<T,?,T>              reducing(T identity, BinaryOperator<T> op)
        // static <T> Collector<T,?,T>              reducing(T identity, BinaryOperator<T> op)
        System.out.println();
        System.out.println("<< 합계(급여) 이름문자열 (그룹함수 비슷하게...) >>");
        Employee collect3 = employees.stream().collect(Collectors.reducing(new Employee(), (e1, e2) -> {
            if (e1.getSalary() == null) {
                e1.copy(e2);
                return e1;
            }

            e1.setSalary(e1.getSalary() + e2.getSalary());
            e1.setName(e1.getName() + "," + e2.getName());
            e1.setDepartment(null);

            return e1;

        }));

        System.out.println(collect3);

        // static <T,U> Collector<T,?,U>            reducing(U identity, Function<? super T,? extends U> mapper, BinaryOperator<U> op)
        // static <T,U> Collector<T,?,U>            reducing(U identity, Function<? super T,? extends U> mapper, BinaryOperator<U> op)
        // static <T,U> Collector<T,?,U>            reducing(U identity, Function<? super T,? extends U> mapper, BinaryOperator<U> op)
        System.out.println();
        System.out.println("<< 이름합치기 >>");
        String collect4 = employees.stream().collect(Collectors.reducing("", Employee::getName, (a, b) -> a + "," + b));
        System.out.println(collect4);


        System.out.println();
        System.out.println("<< 합계 >>");
//        Integer collect5 = employees.stream().collect(Collectors.reducing(0, e -> e.getSalary(), (a,b) -> a+b));
        Integer collect5 = employees.stream().collect(Collectors.reducing(0, Employee::getSalary, Integer::sum));
        System.out.println(collect5);


        System.out.println();
        String collect6 = employees.stream().collect(Collectors.reducing("", Employee::getDepartment, BinaryOperator.maxBy(Comparator.comparing(String::length))));
        System.out.println(collect6);

    }

    @Test
    public void teeing() {
        System.out.println("<< teeing >>");
        List<Employee> employees = getEmployees();


        System.out.println();
        System.out.println("<< 초대값, 최소값을 구해 Map으로 반환 >>");
        Collector<Employee, ?, Optional<Employee>> collector_max = Collectors.maxBy(Comparator.comparing(Employee::getSalary));
        Collector<Employee, ?, Optional<Employee>> collector_min = Collectors.minBy(Comparator.comparing(Employee::getSalary));
        Collector<Employee, ?, Map<String, Employee>> teeing1 = Collectors.teeing(collector_max, collector_min, (max, min) -> {
            Map<String, Employee> result = new HashMap();
            result.put("MAX", max.get());
            result.put("MIN", min.get());
            return result;
        });

        Map<String, Employee> collect1 = employees.stream().collect(teeing1);
        collect1.forEach((k, v) -> {
            System.out.printf("%s ==> %s \n", k, v);
        });


        System.out.println();
        System.out.println("<< 초대값, 최소값을 구해 List 으로 반환 >>");
        List<Employee> collect2 = employees.stream().collect(Collectors.teeing(collector_max, collector_min, (max, min) -> {
            return Arrays.asList(max.get(), min.get());
        }));
        collect2.forEach(System.out::println);

    }

    public List<Employee> getEmployees() {
        return Arrays.asList(
                VoBuilder.build(Employee::new)
                        .with(v -> v.setName("이문세"))
                        .with(v -> v.setSalary(20_000))
                        .with(Employee::setGender, "male")
                        .with(Employee::setDepartment, "가수팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "신혜철")
                        .with(Employee::setGender, "female")
                        .with(Employee::setSalary, 18_000)
                        .with(Employee::setDepartment, "가수팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "루시")
                        .with(Employee::setGender, "female")
                        .with(Employee::setSalary, 10_000)
                        .with(Employee::setDepartment, "가수팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "아이유")
                        .with(Employee::setGender, "male")
                        .with(Employee::setSalary, 21_000)
                        .with(Employee::setDepartment, "가수팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "김태우")
                        .with(Employee::setSalary, 13_000)
                        .with(Employee::setGender, "male")
                        .with(Employee::setDepartment, "가수팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "이미자")
                        .with(Employee::setSalary, 12_100)
                        .with(Employee::setGender, "female")
                        .with(Employee::setDepartment, "가수팀")
                        .get()


                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "이성균")
                        .with(Employee::setGender, "female")
                        .with(Employee::setSalary, 15_000)
                        .with(Employee::setDepartment, "연기팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "김혜자")
                        .with(Employee::setGender, "female")
                        .with(Employee::setSalary, 20_000)
                        .with(Employee::setDepartment, "연기팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "독고영재")
                        .with(Employee::setSalary, 15_100)
                        .with(Employee::setGender, "male")
                        .with(Employee::setDepartment, "연기팀")
                        .get()

                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "김순자")
                        .with(Employee::setGender, "female")
                        .with(Employee::setSalary, 17_550)
                        .with(Employee::setDepartment, "연기팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "안성기")
                        .with(Employee::setSalary, 16_500)
                        .with(Employee::setGender, "female")
                        .with(Employee::setDepartment, "연기팀")
                        .get()
                , VoBuilder.build(Employee::new)
                        .with(Employee::setName, "장동건")
                        .with(Employee::setSalary, 19_000)
                        .with(Employee::setGender, "male")
                        .with(Employee::setDepartment, "연기팀")
                        .get()

                );
    }

}