package com.sgintl.journal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Journal {

    public static Journal EMPTY = Journal.builder().build();
    private String id;
    private String description;

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return EMPTY.equals(this);
    }

    public static class Builder {
        private String id;
        private String description;

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Journal build() {
            Journal instance = new Journal();
            instance.setId(id);
            instance.setDescription(description);
            return instance;
        }
    }
}
