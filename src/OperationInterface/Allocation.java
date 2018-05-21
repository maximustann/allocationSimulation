package OperationInterface;

import DataCenterEntity.Holder;

import java.util.ArrayList;

public interface Allocation {
    public int execute(ArrayList<? extends Holder> binList, Holder item, int flag);
}
