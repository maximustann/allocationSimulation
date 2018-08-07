package OperationInterface;
import DataCenterEntity.Container;
import DataCenterEntity.DataCenterInterface;
import DataCenterEntity.VM;

public interface VMCreation {
    VM execute(DataCenterInterface dataCenter, Container container);
}
