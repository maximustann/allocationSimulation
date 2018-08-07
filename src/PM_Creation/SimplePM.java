package PM_Creation;

import DataCenterEntity.PM;
import OperationInterface.PMCreation;

public class SimplePM implements PMCreation {
    public PM execute(
                double pmCpu,
                double pmMem,
                double k,
                double maxEnergy) {

        return new PM(pmCpu, pmMem, k, maxEnergy);
    }
}
