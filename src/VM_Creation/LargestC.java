package VM_Creation;

import DataCenterEntity.Container;
import DataCenterEntity.DataCenterCombined;
import DataCenterEntity.DataCenterInterface;
import DataCenterEntity.VM;
import OperationInterface.VMCreation;

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
