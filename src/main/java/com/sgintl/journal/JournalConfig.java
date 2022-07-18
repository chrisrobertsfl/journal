package com.sgintl.journal;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "journal")
public interface JournalConfig {

    @WithName("message")
    String message();

}