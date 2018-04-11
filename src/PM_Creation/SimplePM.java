package PM_Creation;

import DataCenterEntity.PM;
import DataCenterEntity.VM;
import OperationInterface.PMCreation;

public class SimplePM implements PMCreation {
    public PM execute(
                double pmCpu,
                double pmMem,
                double k,
                double maxEnergy) {

        PM pm = new PM(pmCpu, pmMem, k, maxEnergy);
        return pm;
    }
}
