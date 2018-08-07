package vmCreation;

import dataCenterEntity.Container;
import dataCenterEntity.DataCenterCombined;
import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.VM;
import operationInterface.VMCreation;

public class LargestC implements VMCreation {

    /**
     * @param container
     * @return the new VM that we created
     */
    public VM execute(DataCenterInterface dataCenter, Container container){

        DataCenterCombined myDataCenter = (DataCenterCombined) dataCenter;
        double[] vmCpu = myDataCenter.getVmCpu();
        double[] vmMem = myDataCenter.getVmMem();

        return new VM(vmCpu[vmCpu.length - 1], vmMem[vmMem.length - 1], vmCpu.length - 1);

    }


}
