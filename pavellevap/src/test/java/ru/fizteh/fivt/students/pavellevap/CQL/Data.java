package ru.fizteh.fivt.students.pavellevap.CQL;

public class Data {
    private Integer a;
    private String b;

    public Integer getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public Data(Integer a, String b) {
        this.a = a;
        this.b = b;
    }

    public Data(Data data) {
        this(data.getA(), data.getB());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Data) {
            Data data = (Data) obj;
            return a.equals(data.a) && b.equals(data.b);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (a + b).hashCode();
    }
}