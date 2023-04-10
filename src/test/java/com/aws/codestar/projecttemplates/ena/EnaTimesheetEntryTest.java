package com.aws.codestar.projecttemplates.ena;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class EnaTimesheetEntryTest {
    @Test
    @DisplayName("Serialization roundtrip")
    void serDe() throws JsonProcessingException {
        LocalTime start = LocalTime.now();
        EnaTimesheetEntry entry = new EnaTimesheetEntry(1, "project_id", "activity", 1, start, null, 1.0f, "description", "error");
        String json = entry.toJson();
        EnaTimesheetEntry entry2 = entry.parse(json);
        assert entry.equals(entry2);
    }
}
