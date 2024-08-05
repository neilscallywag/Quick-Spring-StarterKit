package com.starterkit.demo.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericProcessor<T> {
	private final Supplier<T> targetSupplier;
	private final List<Function<T, T>> transformations = new ArrayList<>();

	private GenericProcessor(Supplier<T> targetSupplier) {
		this.targetSupplier = targetSupplier;
	}

	public static <T> GenericProcessor<T> of(T target) {
		return new GenericProcessor<>(() -> target);
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
		T result = targetSupplier.get();
		for (Function<T, T> transformation : transformations) {
			result = transformation.apply(result);
		}
		return result;
	}
}
