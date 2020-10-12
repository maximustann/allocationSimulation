package pmCreation;

import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.PM;
import dataCenterEntity.VM;
import operationInterface.PMCreation;

public class SimplePM implements PMCreation {
    public PM execute(DataCenterInterface dataCenter, VM vm) {
        return new PM(dataCenter.getPmCpu(), dataCenter.getPmMem(), dataCenter.getK(), dataCenter.getMaxEnergy());
    }
}
