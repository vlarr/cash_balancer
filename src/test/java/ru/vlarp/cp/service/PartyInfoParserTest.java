package ru.vlarp.cp.service;

import org.junit.Test;
import ru.vlarp.cp.logic.PartyInfoHelper;

import java.io.IOException;

import static org.junit.Assert.*;

public class PartyInfoParserTest {

    @Test
    public void parseTest() throws IOException {
        PartyInfoHelper partyInfoHelper = new PartyInfoParser().parseFile("src/test/resources/info/test_info");

        assertEquals(10, partyInfoHelper.getMemberName().size());
        assertEquals(4, partyInfoHelper.getCategoryName().size());
        assertEquals(5, partyInfoHelper.getPurchases().size());
        assertEquals(10, partyInfoHelper.getChecks().keySet().size());
    }
}