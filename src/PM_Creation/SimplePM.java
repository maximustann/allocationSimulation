package PM_Creation;

import DataCenterEntity.PM;
import DataCenterEntity.VM;
import OperationInterface.PMCreation;

public class SimplePM implements PMCreation {
    @Override
    public PM execute(
                double pmCpu,
                double pmMem,
                int number,
                double k,
                double maxEnergy) {

        PM pm = new PM(pmCpu, pmMem, number, k, maxEnergy);
        return pm;
    }
}
