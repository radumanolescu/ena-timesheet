package com.aws.codestar.projecttemplates.phd;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PhdTemplateEntryTest {

    @Test
    @DisplayName("Serialization roundtrip")
    void serDe() throws JsonProcessingException {
        PhdTemplateEntry entry = new PhdTemplateEntry("client", "task");
        String json = entry.toJson();
        PhdTemplateEntry entry2 = entry.parse(json);
        assert entry.equals(entry2);
    }
}
