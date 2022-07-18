package com.sgintl.journal;

import com.sgintl.base.ResourceRequest;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class FindJournalByIdRequest implements ResourceRequest {
    @NotBlank(message = "id is missing")
    private String id;
}
