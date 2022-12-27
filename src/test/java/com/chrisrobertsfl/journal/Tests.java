package com.chrisrobertsfl.journal;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class Tests {
    public static final String DOT_PATTERN = "\\.";
    public static final String SLASH = "/";

    public static UnderClassBuilder under(Class<?> target) {
        return new UnderClassBuilder(target);
    }

    public static UnderClassBuilder under(final Object target) {
        return new UnderClassBuilder(ofNullable(target)
                .map(Object::getClass)
                .orElseThrow(() -> new RuntimeException("Target object for test is not present")));
    }

    public static class UnderClassBuilder {
        final Class<?> target;

        public UnderClassBuilder(final Class<?> target) {
            this.target = target;
        }

        public String file(final String fileName) {
            return format("src/test/resources/%s/%s", target.getName().replaceAll(DOT_PATTERN, SLASH), fileName);
        }
    }
}
