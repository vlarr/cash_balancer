package ru.vlarp.cp.logic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vlarp.cp.pojo.CashDeltaRecord;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartyInfo {
    protected List<CashDeltaRecord> purchases = new ArrayList<>();
    protected Multimap<Long, Long> checks = HashMultimap.create();
}
