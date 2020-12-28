package ru.vlarp.cp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import ru.vlarp.cp.logic.DeltaCalculator;
import ru.vlarp.cp.logic.PartyInfoHelper;
import ru.vlarp.cp.pojo.TransferInfo;
import ru.vlarp.cp.service.PartyInfoParser;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Slf4j
public class AppTest {
    private final DeltaCalculator deltaCalculator = new DeltaCalculator();
    private final DecimalFormat formatter = new DecimalFormat("#0.00");

    @Test
    public void appTest1() throws IOException {
        PartyInfoHelper info = new PartyInfoParser().parseFile("src/test/resources/info/test_info");

        List<TransferInfo> result = deltaCalculator.buildTransfers(info);

        for (TransferInfo transferInfo : result) {
            log.info("from: \"{}\", to: \"{}\", value: {}",
                    info.getMemberName().get(transferInfo.getFrom()),
                    info.getMemberName().get(transferInfo.getTo()),
                    formatter.format(transferInfo.getVal())
            );
        }

        assertEquals(9, result.size());
    }
}