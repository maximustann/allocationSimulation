package OperationInterface;
import DataCenterEntity.PM;
import DataCenterEntity.VM;

import java.util.*;
public interface VMAllocation {
    public void execute(ArrayList<PM> pmList, VM vm);
}
