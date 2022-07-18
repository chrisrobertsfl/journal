package com.sgintl.journal;

import com.sgintl.base.ResourceRequest;
import lombok.Value;

@Value
public class DeleteJournalRequest implements ResourceRequest {
    private String id;

}
