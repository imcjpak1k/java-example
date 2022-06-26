package org.example.java8.stream;

import org.example.java8.stream.vo.ReportCardVo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * {@link Stream}을 이용한 계산
 * 참고사이트
 * https://madplay.github.io/post/java-streams-terminal-operations
 */
public class CalculationEx {
    public static void main (String[] args) {
        intSum();
        bigDecimalSum();
        voSum();
    }

    /**
     * {@link IntStream}을 이용한 계산
     * - 합계
     */
    public static void intSum() {
        System.out.println("<< int sum example >>");
        IntStream stream = IntStream.rangeClosed(1, 100);
        System.out.printf("total sum : %d\n", stream.sum());
    }

    /**
     * {@link Stream}의 자료 타입이 {@link BigDecimal}경우
     * - 합계
     */
    public static void bigDecimalSum() {
        System.out.println("<< bigdecimal sum example >>");
//        Stream<BigDecimal> stream = IntStream.rangeClosed(1, 100).mapToObj(i -> BigDecimal.valueOf(i));
//        Stream<BigDecimal> stream = IntStream.rangeClosed(1, 100).mapToObj(BigDecimal::valueOf);
        List<BigDecimal> list = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> BigDecimal.valueOf(i))
                .collect(Collectors.toList());

//        BigDecimal sum = stream.reduce(BigDecimal::add).get();
        BigDecimal sum = list
                .stream()
                .parallel()
                .reduce(BigDecimal::add)
                .get();

        System.out.printf("합계 : %s", sum);
    }

    /**
     * {@link ReportCardVo}의 합계 계산 및 순위
     */
    public static void voSum() {
        System.out.println("<< vo object sum example >>");
        List<ReportCardVo> list = getReportCards();

        AtomicInteger rank = new AtomicInteger(1);
        list.stream()
                .map(vo -> {
                    var sum = vo.getKor() + vo.getEng() + vo.getMath() + vo.getEtc();
                    var avg = sum / 4;
                    var level = "F";
                    if(avg >= 90) {
                        level = "A";
                    }
                    else if(avg >= 80) {
                        level = "B";
                    }
                    else if(avg >= 70) {
                        level = "C";
                    }
                    else if(avg >= 60) {
                        level = "D";
                    }

                    vo.setAverage( avg );
                    vo.setLevel(level);
                    return vo;
                })
                .sorted(Comparator.comparing(ReportCardVo::getAverage))     // 오름차순
//                .sorted(Comparator.comparing(ReportCardVo::getAverage).reversed())    // 내림차순
                .peek(vo -> {
                    // 동일한값인 경우에는 동일한 등수부여가 안된다......
                    vo.setRank( rank.getAndIncrement() );
                })
                .forEach(System.out::println);


        list.stream()
                .map(vo -> {
                    var sum = vo.getKor() + vo.getEng() + vo.getMath() + vo.getEtc();
                    var avg = sum / 4;
                    var level = "F";
                    if(avg >= 90) {
                        level = "A";
                    }
                    else if(avg >= 80) {
                        level = "B";
                    }
                    else if(avg >= 70) {
                        level = "C";
                    }
                    else if(avg >= 60) {
                        level = "D";
                    }

                    vo.setAverage( avg );
                    vo.setLevel(level);
                    return vo;
                });
    }

    /**
     * 성적표목록 반환
     * @return
     */
    public static List<ReportCardVo> getReportCards() {
        List<ReportCardVo> list = Arrays.asList(
                new ReportCardVo("홍길동", 90, 99, 80, 40)
                , new ReportCardVo("세종대왕", 99, 99,99,30)
                , new ReportCardVo("신사임당", 90, 40, 80, 99)
                , new ReportCardVo("이육사", 90, 50, 50, 99)
                , new ReportCardVo("김소월", 90, 88, 50, 99)
                , new ReportCardVo("이상", 90, 50, 50, 99)
                , new ReportCardVo("김구", 90, 50, 50, 99)
                , new ReportCardVo("윤봉길", 90, 50, 50, 99)
        );

        return list;
    }

}
