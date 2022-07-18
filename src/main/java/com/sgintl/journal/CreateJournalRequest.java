package com.sgintl.journal;

import com.sgintl.base.ResourceRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateJournalRequest implements ResourceRequest {

    @NotBlank(message = "description is missing")
    private String description;

    public static final Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String description;

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public CreateJournalRequest build() {
            CreateJournalRequest instance = new CreateJournalRequest();
            instance.setDescription(description);
            return instance;
        }
    }
}
