package com.johannesbrodwall.recyclerviewdemo;

import java.util.UUID;

public class DemoItem implements Comparable<DemoItem> {
    private final String name;
    private final UUID categoryId;

    public DemoItem(String name, UUID categoryId) {
        this.name = name;
        this.categoryId = categoryId;
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
}
