package com.chrisrobertsfl.journal.model;

public sealed interface Item permits Task {
    boolean exists(String label);
    String id();

    State state();
}
