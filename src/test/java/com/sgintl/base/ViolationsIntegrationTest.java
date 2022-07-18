package com.sgintl.base;

import com.sgintl.journal.Journal;
import io.quarkus.test.junit.QuarkusTest;
import lombok.Value;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static com.sgintl.base.Violations.using;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ViolationsIntegrationTest {

    public static final Journal JOURNAL_ENTITY = Journal.builder()
            .id("abc")
            .description("valid text")
            .build();

    @Inject
    Validator validator;
    FindByIdRequest findByIdRequest;

    @Test
    void violationsArePresentSoReturnMissing() {
        findByIdRequest = new FindByIdRequest(null);
        ResourceResponse expected = ResourceResponse.builder().addDetail("id is missing").build();
        Optional<ResourceResponse> actual = using(validator)
                .validate(findByIdRequest)
                .onFailure(ViolationsIntegrationTest::badId);
        assertAll(
                () -> assertTrue(actual.isPresent(), "Resource response should be present"),
                () -> assertEquals(expected, actual.get()));
    }

    @Test
    void violationsArePresentSoReturnBadNumber() {
        findByIdRequest = new FindByIdRequest(-4L);
        ResourceResponse expected = ResourceResponse.builder().addDetail("id minimum value is 0").build();
        Optional<ResourceResponse> actual = using(validator)
                .validate(findByIdRequest)
                .onFailure(ViolationsIntegrationTest::badId);
        assertAll(
                () -> assertTrue(actual.isPresent(), "Resource response should be present"),
                () -> assertEquals(expected, actual.get()));
    }

    @Test
    void noViolationsReturnsSomethingGood() {
        findByIdRequest = new FindByIdRequest(1L);
        ResourceResponse expected = ResourceResponse.builder().summary("Success").addBodyEntry(JOURNAL_ENTITY).build();
        Optional<ResourceResponse> actual = using(validator)
                .validate(findByIdRequest)
                .onSuccess(ViolationsIntegrationTest::findById);
        assertAll(
                () -> assertTrue(actual.isPresent(), "Resource response should be present"),
                () -> assertEquals(expected, actual.get()));
    }

    @Test
    void missingIdOnForkingOperation() {
        findByIdRequest = new FindByIdRequest(null);
        ResourceResponse expected = ResourceResponse.builder().addDetail("id is missing").build();
        Optional<ResourceResponse> actual = using(validator)
                .validate(findByIdRequest)
                .onOutcome(ViolationsIntegrationTest::findById, ViolationsIntegrationTest::badId);
        assertAll(
                () -> assertTrue(actual.isPresent(), "Resource response should be present"),
                () -> assertEquals(expected, actual.get()));

    }

    @Test
    void validOnForkingOperation() {
        findByIdRequest = new FindByIdRequest(1L);
        ResourceResponse expected = ResourceResponse.builder().summary("Success").addBodyEntry(JOURNAL_ENTITY).build();
        Optional<ResourceResponse> actual = using(validator)
                .validate(findByIdRequest)
                .onOutcome(ViolationsIntegrationTest::findById, ViolationsIntegrationTest::badId);
        assertAll(
                () -> assertTrue(actual.isPresent(), "Resource response should be present"),
                () -> assertEquals(expected, actual.get()));

    }

    private static ResourceResponse findById(ResourceRequest resourceRequest) {
        return ResourceResponse.builder()
                .summary("Success")
                .addBodyEntry(JOURNAL_ENTITY)
                .build();
    }

    private static ResourceResponse badId(List<String> errorMessages) {
        return ResourceResponse.builder()
                .details(errorMessages)
                .build();
    }

    @Value
    class FindByIdRequest implements ResourceRequest {
        @Min(message = "id minimum value is 0", value = 0L)
        @NotNull(message = "id is missing")
        private Long id;
    }

}
