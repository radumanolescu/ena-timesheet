package com.aws.codestar.projecttemplates.ena;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class InvoiceDayWeekEntryTest {
    @Test
    @DisplayName("Serialization roundtrip")
    void serDe() throws JsonProcessingException {
        InvoiceDayWeekEntry entry = new InvoiceDayWeekEntry();
        entry.setProject("project");
        entry.setActivity("activity");
        entry.setDay(LocalDate.now());
        entry.setHours(1.5f);
        entry.setRate(60.5f);
        entry.setDescription("description");
        entry.setCharge(entry.getHours() * entry.getRate());
        String json = entry.toJson();
        InvoiceDayWeekEntry entry2 = entry.parse(json);
        assert entry.equals(entry2);
    }
}
