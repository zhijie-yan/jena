package jena.generate;

import java.util.Arrays;

public class Entry {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAlias() {
        return alias;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    private String[] alias;
    private String context;

    @Override
    public String toString() {
        return "entry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias=" + Arrays.toString(alias) +
                ", context='" + context + '\'' +
                '}';
    }
}
