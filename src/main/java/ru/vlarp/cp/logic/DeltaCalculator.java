package ru.vlarp.cp.logic;

import org.springframework.stereotype.Component;
import ru.vlarp.cp.pojo.CashDeltaRecord;
import ru.vlarp.cp.pojo.TransferInfo;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DeltaCalculator {
    /**
     * Переводит покупки в каждой категории в дельты, т.е: у того кто покупал будет отрицательная дельта на всю
     * стоимость покупки; у того кто отмечался в категории будет положительная дельта на его часть покупки.
     * Сумма дельт должна дать 0.
     *
     * @param partyInfo исходные данные
     * @return записи значений дельты по мемберам и покупкам.
     */
    public List<CashDeltaRecord> calcDeltas(PartyInfo partyInfo) {
        Set<Long> activeCategories = partyInfo.getPurchases().stream().map(CashDeltaRecord::getCategoryId).collect(Collectors.toSet());

        List<CashDeltaRecord> cashDeltaRecords = new ArrayList<>();

        for (Long activeCategory : activeCategories) {
            Set<Long> activeUsers = partyInfo.getChecks().entries().stream()
                    .filter(userCheck -> activeCategory.equals(userCheck.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            List<CashDeltaRecord> activePurchases = partyInfo.getPurchases().stream().filter(purchase -> activeCategory.equals(purchase.getCategoryId())).collect(Collectors.toList());

            for (CashDeltaRecord activePurchase : activePurchases) {
                cashDeltaRecords.add(new CashDeltaRecord(activePurchase.getUserId(), activePurchase.getCategoryId(), -activePurchase.getDelta()));
            }

            Double sumCost = partyInfo.getPurchases()
                    .stream()
                    .filter(purchase -> activeCategory.equals(purchase.getCategoryId()))
                    .map(CashDeltaRecord::getDelta)
                    .reduce(0.0, Double::sum);

            Double userDeltaVal = sumCost / activeUsers.size();

            for (Long activeUser : activeUsers) {
                cashDeltaRecords.add(new CashDeltaRecord(activeUser, activeCategory, userDeltaVal));
            }
        }

        return cashDeltaRecords;
    }


    /**
     * Сливаем дельты для получения значений кто остался в минусе, а кто в плюсе после приведения к балансу.
     *
     * @param deltaRecords записи значений дельты по мемберам и покупкам.
     * @return Map где ключ - id мембера, значение - дельта в рублях
     */
    public Map<Long, Double> mergeDeltas(List<CashDeltaRecord> deltaRecords) {
        HashMap<Long, Double> result = new HashMap<>();
        for (CashDeltaRecord deltaRecord : deltaRecords) {
            double currentDelta = result.getOrDefault(deltaRecord.getUserId(), 0.0);
            currentDelta += deltaRecord.getDelta();
            result.put(deltaRecord.getUserId(), currentDelta);

        }
        return result;
    }


    /**
     * Формируем список переводов от мембера к мемберу чтобы сравнять разницы в дельтах в 0. Для этого разделяем тех
     * кто в минусе от тех кто в плюсе и переводим значения из одной группы в другую, сохраняя значения переводов.
     * В финале в группах не должно остаться записей.
     *
     * @param mergedDeltas дельты по каждому мемберу, где ключ - id мембера, значение - дельта в рублях
     * @return Список переводов от кого кому и сколько
     */
    public List<TransferInfo> buildTransfers(Map<Long, Double> mergedDeltas) {
        HashMap<Long, Double> fromMap = new HashMap<>();    //  Группа мемберов с + по балансу
        HashMap<Long, Double> toMap = new HashMap<>();  //  Группа мемберов с - по балансу

        List<TransferInfo> result = new ArrayList<>();

        for (Map.Entry<Long, Double> entry : mergedDeltas.entrySet()) {
            if (entry.getValue() < 0) {
                toMap.put(entry.getKey(), entry.getValue());
            } else if (entry.getValue() > 0) {
                fromMap.put(entry.getKey(), entry.getValue());
            }
        }

        // Повторяем пока в группах не останется мемберов
        while (fromMap.size() != 0 && toMap.size() != 0) {
            //  пространство оптимизаций для сокращения кол-ва переводов
            //  Берём по случайному мемберу из обеих групп
            Map.Entry<Long, Double> fromVal = fromMap.entrySet().stream().findAny().orElse(null);
            Map.Entry<Long, Double> toVal = toMap.entrySet().stream().findAny().orElse(null);

            if (fromVal == null || toVal == null) {
                throw new RuntimeException();
            }

            //  Находим сумму для перевода
            double min = Math.min(fromVal.getValue(), -toVal.getValue());

            //  Подводим баланс
            if (Math.abs(fromVal.getValue() - min) < 1e-2) {
                fromMap.remove(fromVal.getKey());   //  если баланс мембера вышел в 0 - то удаляем из группы

                double newTo = toVal.getValue() + min;

                if (Math.abs(newTo) < 1e-2) {
                    toMap.remove(toVal.getKey());   //  если баланс мембера вышел в 0 - то удаляем из группы
                } else {
                    toMap.put(toVal.getKey(), newTo);
                }
            } else {
                toMap.remove(toVal.getKey());   //  если баланс мембера вышел в 0 - то удаляем из группы

                double newFrom = fromVal.getValue() - min;

                if (Math.abs(newFrom) < 1e-2) {
                    fromMap.remove(fromVal.getKey());   //  если баланс мембера вышел в 0 - то удаляем из группы
                } else {
                    fromMap.put(fromVal.getKey(), newFrom);
                }
            }

            //  формируем перевод
            result.add(new TransferInfo(fromVal.getKey(), toVal.getKey(), min));
        }

        return result;
    }

    public List<TransferInfo> buildTransfers(PartyInfo partyInfo) {
        List<CashDeltaRecord> deltas = calcDeltas(partyInfo);
        Map<Long, Double> mergedDeltas = mergeDeltas(deltas);
        return buildTransfers(mergedDeltas);
    }
}
