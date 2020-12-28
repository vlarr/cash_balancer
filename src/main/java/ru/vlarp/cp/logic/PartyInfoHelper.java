package ru.vlarp.cp.logic;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import ru.vlarp.cp.pojo.CashDeltaRecord;

@Getter
public class PartyInfoHelper extends PartyInfo {

    private long currentMemberId = 0L;
    private long currentCategoryId = 0L;

    protected BiMap<Long, String> memberName = HashBiMap.create();
    protected BiMap<Long, String> categoryName = HashBiMap.create();


    public PartyInfoHelper addMember(String name) {
        memberName.put(currentMemberId++, name);
        return this;
    }

    public PartyInfoHelper addCategory(String name) {
        categoryName.put(currentCategoryId++, name);
        return this;
    }


    public PartyInfoHelper addPurchase(String member, String category, Double price) {
        if (!memberName.containsValue(member)) {
            addMember(member);
        }

        if (!categoryName.containsValue(category)) {
            addCategory(category);
        }


        purchases.add(new CashDeltaRecord(memberName.inverse().get(member),
                categoryName.inverse().get(category), price));

        return this;
    }

    public PartyInfoHelper addCheck(String member, String... memberChecks) {
        if (!memberName.containsValue(member)) {
            addMember(member);
        }

        Long memberId = getMemberName().inverse().get(member);

        for (String check : memberChecks) {
            if (!categoryName.containsValue(check)) {
                addCategory(check);
            }
            this.checks.put(memberId, getCategoryName().inverse().get(check));
        }

        return this;
    }


}
