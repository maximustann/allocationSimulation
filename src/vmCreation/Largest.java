package vmCreation;

import dataCenterEntity.*;
import operationInterface.VMCreation;

public class Largest implements VMCreation {

    /**
     * @param container
     * @return the new VM that we created
     */
    public VM execute(DataCenterInterface dataCenter, Container container){

        double[] vmCpu = dataCenter.getVmCpu();
        double[] vmMem = dataCenter.getVmMem();

        return new VM(vmCpu[vmCpu.length - 1], vmMem[vmMem.length - 1], vmCpu.length - 1);
    }


}
