package com.sgintl.journal;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class JournalEntity {
    private ObjectId id;
    private String description;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ObjectId id;
        private String description;

        public Builder id(final ObjectId id) {
            this.id = id;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public JournalEntity build() {
            JournalEntity instance = new JournalEntity();
            instance.setId(id);
            instance.setDescription(description);
            return instance;
        }
    }

}


