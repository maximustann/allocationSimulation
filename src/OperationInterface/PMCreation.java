package OperationInterface;
import DataCenterEntity.*;

public interface PMCreation {
    public PM execute(
                double pmCpu,
                double pmMem,
                int number,
                double k,
                double maxEnergy);
}
