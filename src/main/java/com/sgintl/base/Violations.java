package com.sgintl.base;

import lombok.Data;

import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public final class Violations {
    public static UsingViolations using(Validator validator) {
        UsingViolations instance = new UsingViolations();
        instance.setValidator(validator);
        return instance;
    }


    @Data
    public static class UsingViolations {
        private Validator validator;
        private List<String> errorMessages;
        private ResourceRequest resourceRequest;

        public UsingViolations validate(ResourceRequest resourceRequest) {
            this.resourceRequest = resourceRequest;
            errorMessages = validator.validate(resourceRequest).stream()
                    .map(v -> v.getMessage())
                    .collect(toList());
            return this;
        }

        public Optional<ResourceResponse> onFailure(Function<List<String>, ResourceResponse> onFailureFunction) {
            return errorMessages.isEmpty() ? empty() : ofNullable(onFailureFunction.apply(errorMessages));
        }

        public Optional<ResourceResponse> onSuccess(Function<ResourceRequest, ResourceResponse> onSuccessFunction) {
            return errorMessages.isEmpty() ? ofNullable(onSuccessFunction.apply(resourceRequest)) : empty();
        }

        public Optional<ResourceResponse> onOutcome(Function<ResourceRequest, ResourceResponse> onSuccessFunction, Function<List<String>, ResourceResponse> onFailureFunction) {
            return ofNullable(errorMessages.isEmpty() ? onSuccessFunction.apply(resourceRequest) : onFailureFunction.apply(errorMessages));
        }
    }
}
