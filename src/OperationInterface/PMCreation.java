package OperationInterface;
import DataCenterEntity.*;

public interface PMCreation {
    PM execute(
                double pmCpu,
                double pmMem,
                double k,
                double maxEnergy);
}
