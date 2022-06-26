package org.example.java8.stream.vo;


public class ReportCardVo {
    private String name;
    private String level;
    private int rank;
    private int kor;
    private int eng;
    private int math;
    private int etc;
    private int average;

    public ReportCardVo(String name, int kor, int eng, int math, int etc) {
        this.name = name;
        this.kor = kor;
        this.eng = eng;
        this.math = math;
        this.etc = etc;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getKor() {
        return kor;
    }

    public void setKor(int kor) {
        this.kor = kor;
    }

    public int getEng() {
        return eng;
    }

    public void setEng(int eng) {
        this.eng = eng;
    }

    public int getMath() {
        return math;
    }

    public void setMath(int math) {
        this.math = math;
    }

    public int getEtc() {
        return etc;
    }

    public void setEtc(int etc) {
        this.etc = etc;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return "ReportCard{" +
                "name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", rank=" + rank +
                ", kor=" + kor +
                ", eng=" + eng +
                ", math=" + math +
                ", etc=" + etc +
                ", average=" + average +
                '}';
    }
}
