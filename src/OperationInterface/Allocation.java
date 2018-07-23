package OperationInterface;

import DataCenterEntity.DataCenter;
import DataCenterEntity.DataCenterInterface;
import DataCenterEntity.Holder;

import java.util.ArrayList;

public interface Allocation {
    public int execute(DataCenterInterface datacenter, Holder item, int flag);
}
