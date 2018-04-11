package OperationInterface;
import DataCenterEntity.*;

public interface PMCreation {
    public PM execute(
                double pmCpu,
                double pmMem,
                double k,
                double maxEnergy);
}
