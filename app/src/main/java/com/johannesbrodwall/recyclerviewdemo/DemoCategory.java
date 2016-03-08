package com.johannesbrodwall.recyclerviewdemo;

import java.util.UUID;

public class DemoCategory implements Comparable<DemoCategory> {
    private final String name;
    private UUID id;

    public DemoCategory(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public int compareTo(DemoCategory another) {
        return name.compareTo(another.name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + name + ",id=" + id + "}";
    }

}
