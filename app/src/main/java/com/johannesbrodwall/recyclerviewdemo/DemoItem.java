package com.johannesbrodwall.recyclerviewdemo;

import java.util.UUID;

public class DemoItem implements Comparable<DemoItem> {
    private String name;
    private final UUID categoryId;

    public DemoItem(String name, DemoCategory categoryId) {
        this.name = name;
        this.categoryId = categoryId.getId();
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + name + ",category=" + categoryId + "}";
    }

    @Override
    public int compareTo(DemoItem another) {
        return name.compareTo(another.name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
