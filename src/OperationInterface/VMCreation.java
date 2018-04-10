package OperationInterface;
import DataCenterEntity.Container;
import DataCenterEntity.VM;

public interface VMCreation {
    public VM execute(double[] vmCpu, double[] vmMem, Container container);
}
