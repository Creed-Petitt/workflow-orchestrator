package com.creedpetitt.workersdk;

@FunctionalInterface
public interface Handler {
    String handle(String payload);
}