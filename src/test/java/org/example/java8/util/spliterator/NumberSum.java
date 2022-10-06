package org.example.java8.util.spliterator;

public class NumberSum {
    private long value = 0;

    public NumberSum() {}
    public NumberSum(long l) {
        value = l;
    }
    public void add(long n) {
        value += n;
    }

    public NumberSum add_accumulator(long n) {
        return new NumberSum(this.getValue() + n);
    }

    public NumberSum sub_accumulator(long n) {
        return new NumberSum(this.getValue() - n);
    }

    public NumberSum add_combine(NumberSum longSum) {
        return new NumberSum(longSum.getValue() + this.getValue());
    }
    public NumberSum sub_combine(NumberSum longSum) {
        return new NumberSum(longSum.getValue() - this.getValue());
    }

    public long getValue() {
        return value;
    }
}
