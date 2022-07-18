package com.sgintl.journal;

import com.sgintl.base.ResourceRequest;
import lombok.Data;

@Data
public class UpdateJournalRequest implements ResourceRequest {
    private String id;
    private String description;

    public static final Builder builder() {
        return new Builder();
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

        public UpdateJournalRequest build() {
            UpdateJournalRequest instance = new UpdateJournalRequest();
            instance.setId(id);
            instance.setDescription(description);
            return instance;
        }
    }
}
