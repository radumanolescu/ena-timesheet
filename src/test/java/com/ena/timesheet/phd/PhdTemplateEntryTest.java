package com.ena.timesheet.phd;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhdTemplateEntryTest {

    //@Test
    @DisplayName("Serialization roundtrip")
    void serDe() throws JsonProcessingException {
        PhdTemplateEntry entry = new PhdTemplateEntry(0, "client", "task");
        String json = entry.toJson();
        PhdTemplateEntry entry2 = entry.parse(json);
        assertEquals(entry, entry2);
    }
}
