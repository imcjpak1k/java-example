package org.example.java8.stream;



import org.example.java8.stream.vo.ReportCardVo;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class SortExam {
    public static void main(String[] args) {
        stringSort();
        integerSort();
        voSort();
        rankVoSort01();
        rankVoSort02();
        rankVoSort03();
    }



    public static void stringSort() {
        System.out.println("<< 문자열목록 정렬 >>");
    }

    public static void integerSort() {
        System.out.println("<< 숫자목록 정렬 >>");
    }

    public static void voSort() {
        System.out.println("<< VO목록 정렬 >>");
    }

    /**
     * https://stackoverflow.com/questions/43182732/computing-a-ranking-with-java-8-stream-api
     * 문자열에서 단어를 분리하여 문자열의 길이가 긴것부터 내림차순으로 정렬해서 순위를 보여준다.
     *
     */
    public static void rankVoSort01() {
        System.out.println("<< 문자열 정렬 및 순위 매기기(vo사용) : Basic procedure >>");
        String paragraph = "A common problem when processing data is to build  a ranking of items based on some property";
        // 단어를 공백으로 분리해서 스트림객체를 생성한다.
        Stream<String> words = Stream.of(paragraph.split(" "));

        Function<String,Integer> propertyExtractor = String::length;
        Comparator<Integer> propertyComparator = Comparator.reverseOrder();
        SortedMap<Integer,List<String>> ranking = new TreeMap<>();

        words
                .sorted(Comparator.comparing(propertyExtractor, propertyComparator))
                .forEach(item -> {
                    // 문자열 길이
                    Integer property = propertyExtractor.apply(item);
                    if(ranking.isEmpty()) {
                        ranking.put(1, new LinkedList<>());
                    }
                    else {
                        Integer rank = ranking.lastKey();
                        List<String> items = ranking.get(rank);
                        if(!property.equals(propertyExtractor.apply(items.get(0)))) {
//                            System.out.println(rank +" , "+ items.size());
                            ranking.put(rank + items.size(), new LinkedList<>());
                        }
                    }

//                    System.out.println(item);
                    ranking.get(ranking.lastKey()).add(item);
                });


        System.out.println(ranking);

    }

    /**
     * https://stackoverflow.com/questions/43182732/computing-a-ranking-with-java-8-stream-api
     * 문자열에서 단어를 분리하여 문자열의 길이가 긴것부터 내림차순으로 정렬해서 순위를 보여준다.
     * 1. 문자열의 길이로 내림차순 정렬을 한다.
     * 2. {@link TreeMap}에 순위면 문자열의 {@link List}를 저장한다.
     * 3. 병렬로 처리된 {@link TreeMap}의 내용은 병합한다.
     */
    public static void rankVoSort02() {
        System.out.println("<< 문자열 정렬 및 순위 매기기(vo사용) : Usage of collector >>");
        String paragraph = "A common problem when processing data is to build  a ranking of items based on some property";
        // 단어를 공백으로 분리해서 스트림객체를 생성한다.
        Stream<String> words = Stream.of(paragraph.split(" "));

        Function<String,Integer> propertyExtractor = String::length;
        Comparator<Integer> propertyComparator = Comparator.reverseOrder();
        SortedMap<Integer,List<String>> ranking =
                words.parallel()
                        .sorted(Comparator.comparing(propertyExtractor, propertyComparator))
                        .collect(TreeMap::new,         // supplier
                                (rank, item) -> {   // accumulator
//                                    System.out.println("accumulator : "+ item);
                                    Integer property = propertyExtractor.apply(item);
                                    if(rank.isEmpty()){
                                        rank.put(1,new LinkedList<String>());
                                    }else{
                                        Integer r = rank.lastKey();
                                        List<String> items = rank.get(r);
                                        Integer prevProp = propertyExtractor.apply(items.get(0));
                                        if(! property.equals(prevProp)) {
                                            rank.put(r+items.size(), new LinkedList<String>());
                                        }
                                    }
                                    rank.get(rank.lastKey()).add(item);
                                },
                                (rank1, rank2) -> {     // combiner
                                    int lastRanking = rank1.lastKey();
                                    int offset = lastRanking + rank1.get(lastRanking).size()-1;
//                                    System.out.println("combiner lastRanking="+ lastRanking +", offset="+ offset);
//                                    System.out.println(rank1.get(lastRanking));
                                    if( propertyExtractor.apply(rank1.get(lastRanking).get(0))
                                            == propertyExtractor.apply(rank2.get(rank2.firstKey()).get(0)) ){
                                        rank1.get(lastRanking).addAll(rank2.get(rank2.firstKey()));
                                        rank2.remove(rank2.firstKey());
                                    }

                                    rank2.forEach((r,items) -> {
//                                        System.out.println(items);
                                        rank1.put(offset+r, items);}
                                    );
                                }
                        );

        System.out.println(ranking);


    }

    /**
     * https://stackoverflow.com/questions/43182732/computing-a-ranking-with-java-8-stream-api
     * 문자열에서 단어를 분리하여 문자열의 길이가 긴것부터 내림차순으로 정렬해서 순위를 보여준다.
     */
    public static void rankVoSort03() {
        System.out.println("<< 문자열 정렬 및 순위 매기기(vo사용) : Grouping by ranking property >>");
        String paragraph = "A common problem when processing data is to build  a ranking of items based on some property";
        // 단어를 공백으로 분리해서 스트림객체를 생성한다.
        Stream<String> words = Stream.of(paragraph.split(" "));

        Function<String,Integer> propertyExtractor = String::length;
        Comparator<Integer> propertyComparator = Comparator.reverseOrder();

        // combiner
        BiConsumer<SortedMap<Integer,List<String>>,
                SortedMap<Integer,List<String>>> combiner = (rank1,rank2) -> {
                    int lastRanking = rank1.lastKey();
                    int offset = lastRanking + rank1.get(lastRanking).size()-1;
                    if( propertyExtractor.apply(rank1.get(lastRanking).get(0))
                            == propertyExtractor.apply(rank2.get(rank2.firstKey()).get(0)) ){
                        rank1.get(lastRanking).addAll(rank2.get(rank2.firstKey()));
                        rank2.remove(rank2.firstKey());
                    }
                    rank2.forEach((r,items) -> {rank1.put(offset+r, items);} );
                };

        SortedMap<Integer,List<String>> ranking =
                words.collect(
                        collectingAndThen(
                                groupingBy(propertyExtractor,
                                        ()->new TreeMap<>(propertyComparator),
                                        toList()
                                ),
                                map -> map.entrySet().stream().collect(
                                        TreeMap::new,
                                        (rank,entry) ->
                                                rank.put(rank.isEmpty()?1:rank.lastKey()+
                                                                rank.get(rank.lastKey()).size(),
                                                        entry.getValue()
                                                ),
                                        combiner
                                )
                        )
                );


        System.out.println(ranking);


    }

    public static List<ReportCardVo> getReportCards() {
//        List<ReportCardVo> list = Arrays.asList(
//                new ReportCardVo("홍길동", 90, 99, 80, 40)
//                , new ReportCardVo("세종대왕", 99, 99,99,30)
//                , new ReportCardVo("신사임당", 90, 40, 80, 99)
//                , new ReportCardVo("이육사", 90, 50, 50, 99)
//                , new ReportCardVo("김소월", 90, 88, 50, 99)
//                , new ReportCardVo("이상", 90, 50, 50, 99)
//                , new ReportCardVo("김구", 90, 50, 50, 99)
//                , new ReportCardVo("윤봉길", 90, 50, 50, 99)
//        );
//
//        return list;
        return Arrays.asList(
                new ReportCardVo("홍길동", 90, 99, 80, 40)
                , new ReportCardVo("세종대왕", 99, 99,99,30)
                , new ReportCardVo("신사임당", 90, 40, 80, 99)
                , new ReportCardVo("이육사", 90, 50, 50, 99)
                , new ReportCardVo("김소월", 90, 88, 50, 99)
                , new ReportCardVo("이상", 90, 50, 50, 99)
                , new ReportCardVo("김구", 90, 50, 50, 99)
                , new ReportCardVo("윤봉길", 90, 50, 50, 99)
        );
    }
}
