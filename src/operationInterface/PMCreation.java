package operationInterface;
import dataCenterEntity.*;

public interface PMCreation {
    PM execute(
                double pmCpu,
                double pmMem,
                double k,
                double maxEnergy);
}
