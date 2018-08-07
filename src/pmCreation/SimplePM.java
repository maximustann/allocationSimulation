package pmCreation;

import dataCenterEntity.PM;
import operationInterface.PMCreation;

public class SimplePM implements PMCreation {
    public PM execute(
                double pmCpu,
                double pmMem,
                double k,
                double maxEnergy) {

        return new PM(pmCpu, pmMem, k, maxEnergy);
    }
}
