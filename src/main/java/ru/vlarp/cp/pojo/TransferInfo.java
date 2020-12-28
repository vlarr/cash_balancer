package ru.vlarp.cp.pojo;

import lombok.Value;

@Value
public class TransferInfo {
    Long from;
    Long to;
    Double val;
}
