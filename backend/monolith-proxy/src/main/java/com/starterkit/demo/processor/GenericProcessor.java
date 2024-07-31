/* (C)2024 */
package com.starterkit.demo.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GenericProcessor<T> {
    private final T target;
    private final List<Function<T, T>> transformations = new ArrayList<>();

    private GenericProcessor(T target) {
        this.target = target;
    }

    public static <T> GenericProcessor<T> of(T target) {
        return new GenericProcessor<>(target);
    }

    public GenericProcessor<T> validate(Function<T, T> validation) {
        transformations.add(validation);
        return this;
    }

    public GenericProcessor<T> transform(Function<T, T> transformation) {
        transformations.add(transformation);
        return this;
    }

    public T process() {
        T result = target;
        for (Function<T, T> transformation : transformations) {
            result = transformation.apply(result);
        }
        return result;
    }
}
