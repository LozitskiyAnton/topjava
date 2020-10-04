package ru.javawebinar.topjava.util;

import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorId {
    private final static AtomicInteger counter = new AtomicInteger();

    public static int nextId() {
        return counter.incrementAndGet();
    }
}
