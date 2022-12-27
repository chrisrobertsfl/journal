package com.chrisrobertsfl.journal.service;

import com.chrisrobertsfl.coreengine.CoreEngine;
import com.chrisrobertsfl.coreengine.Drools8Engine;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class JournalEngine implements CoreEngine {
    Drools8Engine delegate;

    public JournalEngine(Drools8Engine delegate) {
        this.delegate = delegate;
    }

    @Override
    public CoreEngine init() {
        return delegate.init();
    }
    @Override
    public boolean exists(Object o) {
        return delegate.exists(o);
    }

    @Override
    public <T> Collection<T> findAll(Class<T> aClass) {
        return delegate.findAll(aClass);
    }

    @Override
    public CoreEngine dumpRules() {
        return delegate.dumpRules();
    }

    @Override
    public CoreEngine insert(Object o) {
        return delegate.insert(o);
    }

    @Override
    public CoreEngine run() {
        return delegate.run();
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    @Override
    public <T> Stream<T> query(String s, Class<T> aClass) {
        return delegate.query(s, aClass);
    }

    @Override
    public CoreEngine insertAll(List<?> list) {
        return delegate.insertAll(list);
    }

}
