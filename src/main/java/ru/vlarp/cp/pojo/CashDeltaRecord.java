package ru.vlarp.cp.pojo;

import lombok.Value;

@Value
public class CashDeltaRecord {
    Long userId;
    Long categoryId;
    Double delta;
}
