package ru.vlarp.cp.logic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import ru.vlarp.cp.pojo.CashDeltaRecord;
import ru.vlarp.cp.pojo.TransferInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DeltaCalculatorTest {

    private final DeltaCalculator deltaCalculator = new DeltaCalculator();

    private void assertDelta(Long userId, Long categoryId, Double deltaVal, CashDeltaRecord deltaRecord) {
        assertEquals(userId, deltaRecord.getUserId());
        assertEquals(categoryId, deltaRecord.getCategoryId());
        assertEquals(deltaVal, deltaRecord.getDelta(), 1e-2);
    }

    @Test
    public void testCalcDeltas1() {
        //  Given
        List<CashDeltaRecord> purchases = new ArrayList<>();
        purchases.add(new CashDeltaRecord(0L, 0L, 100.0));
        HashMultimap<Long, Long> checks = HashMultimap.create();
        checks.put(1L, 0L);
        checks.put(2L, 0L);

        PartyInfo partyInfo = new PartyInfo(purchases, checks);

        //  When
        List<CashDeltaRecord> deltas = deltaCalculator.calcDeltas(partyInfo);

        //  Then
        assertEquals(3, deltas.size());
        assertDelta(0L, 0L, -100.0, deltas.get(0));
        assertDelta(1L, 0L, 50.0, deltas.get(1));
        assertDelta(2L, 0L, 50.0, deltas.get(2));

        double sum = deltas.stream().mapToDouble(CashDeltaRecord::getDelta).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testCalcDeltas2() {
        //  Given
        List<CashDeltaRecord> purchases = new ArrayList<>();
        purchases.add(new CashDeltaRecord(0L, 0L, 100.0));
        purchases.add(new CashDeltaRecord(1L, 1L, 1000.0));
        HashMultimap<Long, Long> checks = HashMultimap.create();
        checks.put(0L, 0L);
        checks.put(1L, 0L);
        checks.put(2L, 0L);
        checks.put(3L, 0L);
        checks.put(0L, 1L);
        checks.put(1L, 1L);

        PartyInfo partyInfo = new PartyInfo(purchases, checks);

        //  When
        List<CashDeltaRecord> deltas = deltaCalculator.calcDeltas(partyInfo);

        //  Then
        assertEquals(8, deltas.size());
        assertDelta(0L, 0L, -100.0, deltas.get(0));
        assertDelta(0L, 0L, 25.0, deltas.get(1));
        assertDelta(1L, 0L, 25.0, deltas.get(2));
        assertDelta(2L, 0L, 25.0, deltas.get(3));
        assertDelta(3L, 0L, 25.0, deltas.get(4));
        assertDelta(1L, 1L, -1000.0, deltas.get(5));
        assertDelta(0L, 1L, 500.0, deltas.get(6));
        assertDelta(1L, 1L, 500.0, deltas.get(7));

        double sum = deltas.stream().mapToDouble(CashDeltaRecord::getDelta).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testCalcDeltas3() {
        //  Given
        List<CashDeltaRecord> purchases = new ArrayList<>();
        purchases.add(new CashDeltaRecord(0L, 0L, 100.0));
        purchases.add(new CashDeltaRecord(1L, 0L, 1000.0));
        HashMultimap<Long, Long> checks = HashMultimap.create();
        checks.put(0L, 0L);
        checks.put(1L, 0L);
        checks.put(2L, 0L);
        checks.put(3L, 0L);

        PartyInfo partyInfo = new PartyInfo(purchases, checks);

        //  When
        List<CashDeltaRecord> deltas = deltaCalculator.calcDeltas(partyInfo);

        //  Then
        assertEquals(6, deltas.size());
        assertDelta(0L, 0L, -100.0, deltas.get(0));
        assertDelta(1L, 0L, -1000.0, deltas.get(1));
        assertDelta(0L, 0L, 275.0, deltas.get(2));
        assertDelta(1L, 0L, 275.0, deltas.get(3));
        assertDelta(2L, 0L, 275.0, deltas.get(4));
        assertDelta(3L, 0L, 275.0, deltas.get(5));

        double sum = deltas.stream().mapToDouble(CashDeltaRecord::getDelta).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testCalcDeltas4() {
        //  Given
        List<CashDeltaRecord> purchases = new ArrayList<>();
        purchases.add(new CashDeltaRecord(0L, 0L, 800.0));
        purchases.add(new CashDeltaRecord(1L, 1L, 1000.0));
        HashMultimap<Long, Long> checks = HashMultimap.create();
        checks.put(0L, 0L);
        checks.put(1L, 0L);
        checks.put(2L, 0L);
        checks.put(3L, 0L);
        checks.put(0L, 1L);
        checks.put(1L, 1L);
        checks.put(2L, 1L);
        checks.put(3L, 1L);

        PartyInfo partyInfo = new PartyInfo(purchases, checks);

        //  When
        List<CashDeltaRecord> deltas = deltaCalculator.calcDeltas(partyInfo);

        //  Then
        assertEquals(10, deltas.size());
        assertDelta(0L, 0L, -800.0, deltas.get(0));
        assertDelta(0L, 0L, 200.0, deltas.get(1));
        assertDelta(1L, 0L, 200.0, deltas.get(2));
        assertDelta(2L, 0L, 200.0, deltas.get(3));
        assertDelta(3L, 0L, 200.0, deltas.get(4));
        assertDelta(1L, 1L, -1000.0, deltas.get(5));
        assertDelta(0L, 1L, 250.0, deltas.get(6));
        assertDelta(1L, 1L, 250.0, deltas.get(7));
        assertDelta(2L, 1L, 250.0, deltas.get(8));
        assertDelta(3L, 1L, 250.0, deltas.get(9));

        double sum = deltas.stream().mapToDouble(CashDeltaRecord::getDelta).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testMergeDeltas1() {
        //  Given
        ArrayList<CashDeltaRecord> deltas = new ArrayList<>();
        deltas.add(new CashDeltaRecord(0L, 0L, -100.0));
        deltas.add(new CashDeltaRecord(1L, 0L, 50.0));
        deltas.add(new CashDeltaRecord(2L, 0L, 50.0));

        //  When
        Map<Long, Double> result = deltaCalculator.mergeDeltas(deltas);

        //  Then
        assertEquals(3, result.size());
        assertEquals(-100.0, result.get(0L), 1e-2);
        assertEquals(50.0, result.get(1L), 1e-2);
        assertEquals(50.0, result.get(2L), 1e-2);

        double sum = result.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testMergeDeltas2() {
        //  Given
        ArrayList<CashDeltaRecord> deltas = new ArrayList<>();
        deltas.add(new CashDeltaRecord(0L, 0L, -100.0));
        deltas.add(new CashDeltaRecord(0L, 0L, 25.0));
        deltas.add(new CashDeltaRecord(1L, 0L, 25.0));
        deltas.add(new CashDeltaRecord(2L, 0L, 25.0));
        deltas.add(new CashDeltaRecord(3L, 0L, 25.0));
        deltas.add(new CashDeltaRecord(1L, 1L, -1000.0));
        deltas.add(new CashDeltaRecord(0L, 1L, 500.0));
        deltas.add(new CashDeltaRecord(1L, 1L, 500.0));

        //  When
        Map<Long, Double> result = deltaCalculator.mergeDeltas(deltas);

        //  Then
        assertEquals(4, result.size());
        assertEquals(425.0, result.get(0L), 1e-2);
        assertEquals(-475.0, result.get(1L), 1e-2);
        assertEquals(25.0, result.get(2L), 1e-2);
        assertEquals(25.0, result.get(3L), 1e-2);

        double sum = result.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testMergeDeltas3() {
        //  Given
        ArrayList<CashDeltaRecord> deltas = new ArrayList<>();
        deltas.add(new CashDeltaRecord(0L, 0L, -100.0));
        deltas.add(new CashDeltaRecord(1L, 0L, -1000.0));
        deltas.add(new CashDeltaRecord(0L, 0L, 275.0));
        deltas.add(new CashDeltaRecord(1L, 0L, 275.0));
        deltas.add(new CashDeltaRecord(2L, 0L, 275.0));
        deltas.add(new CashDeltaRecord(3L, 0L, 275.0));

        //  When
        Map<Long, Double> result = deltaCalculator.mergeDeltas(deltas);

        //  Then
        assertEquals(4, result.size());
        assertEquals(175.0, result.get(0L), 1e-2);
        assertEquals(-725.0, result.get(1L), 1e-2);
        assertEquals(275.0, result.get(2L), 1e-2);
        assertEquals(275.0, result.get(3L), 1e-2);

        double sum = result.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testMergeDeltas4() {
        //  Given
        ArrayList<CashDeltaRecord> deltas = new ArrayList<>();
        deltas.add(new CashDeltaRecord(0L, 0L, -800.0));
        deltas.add(new CashDeltaRecord(0L, 0L, 200.0));
        deltas.add(new CashDeltaRecord(1L, 0L, 200.0));
        deltas.add(new CashDeltaRecord(2L, 0L, 200.0));
        deltas.add(new CashDeltaRecord(3L, 0L, 200.0));
        deltas.add(new CashDeltaRecord(1L, 1L, -1000.0));
        deltas.add(new CashDeltaRecord(0L, 1L, 250.0));
        deltas.add(new CashDeltaRecord(1L, 1L, 250.0));
        deltas.add(new CashDeltaRecord(2L, 1L, 250.0));
        deltas.add(new CashDeltaRecord(3L, 1L, 250.0));

        //  When
        Map<Long, Double> result = deltaCalculator.mergeDeltas(deltas);

        //  Then
        assertEquals(4, result.size());
        assertEquals(-350.0, result.get(0L), 1e-2);
        assertEquals(-550.0, result.get(1L), 1e-2);
        assertEquals(450.0, result.get(2L), 1e-2);
        assertEquals(450.0, result.get(3L), 1e-2);

        double sum = result.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(0.0, sum, 1e-2);
    }

    @Test
    public void testBuildTransfers1() {
        //  Given
        Map<Long, Double> deltas = ImmutableMap
                .<Long, Double>builder()
                .put(0L, 100.0)
                .put(1L, -100.0)
                .build();

        //  When
        List<TransferInfo> result = deltaCalculator.buildTransfers(deltas);

        //  Then
        assertEquals(1, result.size());
        TransferInfo transferInfo = result.get(0);
        assertEquals(0L, transferInfo.getFrom().longValue());
        assertEquals(1L, transferInfo.getTo().longValue());
        assertEquals(100.0, transferInfo.getVal(), 1e-2);
    }

    @Test
    public void testBuildTransfers2() {
        //  Given
        Map<Long, Double> deltas = ImmutableMap
                .<Long, Double>builder()
                .put(0L, -100.0)
                .put(1L, 50.0)
                .put(2L, 50.0)
                .build();

        //  When
        List<TransferInfo> result = deltaCalculator.buildTransfers(deltas);

        //  Then
        assertEquals(2, result.size());
        TransferInfo transferInfo1 = result.get(0);
        assertEquals(1L, transferInfo1.getFrom().longValue());
        assertEquals(0L, transferInfo1.getTo().longValue());
        assertEquals(50.0, transferInfo1.getVal(), 1e-2);

        TransferInfo transferInfo2 = result.get(1);
        assertEquals(2L, transferInfo2.getFrom().longValue());
        assertEquals(0L, transferInfo2.getTo().longValue());
        assertEquals(50.0, transferInfo2.getVal(), 1e-2);
    }

    @Test
    public void testBuildTransfers4() {
        //  Given
        Map<Long, Double> deltas = ImmutableMap
                .<Long, Double>builder()
                .put(0L, -350.0)
                .put(1L, -550.0)
                .put(2L, 450.0)
                .put(3L, 450.0)
                .build();

        //  When
        List<TransferInfo> result = deltaCalculator.buildTransfers(deltas);

        //  Then
        assertEquals(3, result.size());
        TransferInfo transferInfo1 = result.get(0);
        assertEquals(2L, transferInfo1.getFrom().longValue());
        assertEquals(0L, transferInfo1.getTo().longValue());
        assertEquals(350.0, transferInfo1.getVal(), 1e-2);

        TransferInfo transferInfo2 = result.get(1);
        assertEquals(2L, transferInfo2.getFrom().longValue());
        assertEquals(1L, transferInfo2.getTo().longValue());
        assertEquals(100.0, transferInfo2.getVal(), 1e-2);

        TransferInfo transferInfo3 = result.get(2);
        assertEquals(3L, transferInfo3.getFrom().longValue());
        assertEquals(1L, transferInfo3.getTo().longValue());
        assertEquals(450.0, transferInfo3.getVal(), 1e-2);
    }
}