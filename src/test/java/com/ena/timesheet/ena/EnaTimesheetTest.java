package com.ena.timesheet.ena;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnaTimesheetTest {

    private static final Set<String> clientTaskSet = Set.of("2003#75 CD - Architecture (Lobby)"
            , "2003#75 CA - Architecture / Millwork (Punchlist)"
            , "2003#75 DD - FF&E", "2003#75 CD - PHD Procurements (note items)"
            , "2003#75 Add l Servs - Lite Loads, Mng Subs/Consults"
            , "2003#75 Add l Servs - FF&E Delivery Coordination");

    @Test
    void testBestMatch() {
        String clientTask = "2003#75 Addtl Servs - FF&E Delivery Coordination";
        String expected = "2003#75 Add l Servs - FF&E Delivery Coordination";
        String bestMatch = EnaTimesheet.bestMatch(clientTask, clientTaskSet);
        assertEquals(bestMatch, expected);
    }
}
