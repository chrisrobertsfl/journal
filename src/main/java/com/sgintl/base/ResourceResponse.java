package com.sgintl.base;

import lombok.Data;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.CREATED;

@Data
public class ResourceResponse {

    public static ResourceResponse internalError(final String detailEntry) {
        return ResourceResponse.builder()
                .summary("Could not complete operation")
                .addDetail(detailEntry)
                .build();
    }

    public Response toResponse() {
        ResourceEntity entity = ResourceEntity.builder()
                .status(status)
                .summary(summary)
                .details(details)
                .body(body)
                .build();
        Response build = Response
                .status(status)
                .entity(entity)
                .build();
        return build;
    }

    private String summary = "";
    private List<String> details = new ArrayList<>();
    private List<?> body = new ArrayList<>();
    private Integer status;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> details = new ArrayList<>();
        private final List<Object> body = new ArrayList<>();

        private Integer status;
        private String summary = "";

        public Builder addDetail(final String detail) {
            details.add(detail);
            return this;
        }

        public Builder addBodyEntry(final Object bodyEntry) {
            body.add(bodyEntry);
            return this;
        }

        public Builder details(List<String> details) {
            this.details = details;
            return this;
        }

        public Builder summary(final String summary) {
            this.summary = summary;
            return this;
        }

        public Builder status(final Integer status) {
            this.status = status;
            return this;
        }

        public ResourceResponse build() {
            ResourceResponse instance = new ResourceResponse();
            instance.setDetails(details);
            instance.setSummary(summary);
            instance.setBody(body);
            instance.setStatus(firstNonNull(status, CREATED));
            return instance;
        }
    }
}
