package com.aws.codestar.projecttemplates.ena;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class InvoiceByDayAndWeekEntryTest {
    //@Test
    @DisplayName("Serialization roundtrip")
    void serDe() throws JsonProcessingException {
        InvoiceByDayAndWeekEntry entry = new InvoiceByDayAndWeekEntry();
        entry.setProject("project");
        entry.setActivity("activity");
        entry.setDate(LocalDate.now());
        entry.setHours(1.5f);
        entry.setRate(60.0f);
        entry.setDescription("description");
        entry.setCharge(90.0f);
        String json = entry.toJson();
        InvoiceByDayAndWeekEntry entry2 = entry.parse(json);
        assert entry.equals(entry2);
    }
}
