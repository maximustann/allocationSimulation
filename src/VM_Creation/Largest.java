package VM_Creation;

import DataCenterEntity.*;
import OperationInterface.VMCreation;

public class Largest implements VMCreation {

    /**
     * @param container
     * @return the new VM that we created
     */
    public VM execute(DataCenterInterface dataCenter, Container container){

        DataCenter myDataCenter = (DataCenter) dataCenter;
        double[] vmCpu = myDataCenter.getVmCpu();
        double[] vmMem = myDataCenter.getVmMem();

        return new VM(vmCpu[vmCpu.length - 1], vmMem[vmMem.length - 1], vmCpu.length - 1);
    }


}
