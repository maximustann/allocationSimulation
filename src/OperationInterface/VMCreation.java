package OperationInterface;
import DataCenterEntity.Container;
import DataCenterEntity.VM;

import java.util.*;
public interface VMCreation {
    public VM execute(ArrayList<VM> vmList, Container container);
}
