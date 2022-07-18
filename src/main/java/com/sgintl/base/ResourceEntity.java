package com.sgintl.base;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;

@Data
public class ResourceEntity {
    private Integer status;
    private String summary;
    private List<String> details = new ArrayList<>();
    private List<?> body = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer status;
        private String summary;
        private List<String> details = new ArrayList<>();
        private List<?> body = new ArrayList<>();

        public ResourceEntity build() {
            ResourceEntity instance = new ResourceEntity();
            instance.setStatus(status);
            instance.setSummary(summary);
            instance.setDetails(firstNonNull(details, List.of()));
            instance.setBody(firstNonNull(body, List.of()));
            return instance;
        }

        public Builder summary(final String summary) {
            this.summary = summary;
            return this;
        }

        public Builder details(final List<String> details) {
            this.details = details;
            return this;
        }

        public Builder body(final List<?> body) {
            this.body = body;
            return this;
        }

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }
    }
}
