package VM_Creation;

import DataCenterEntity.Container;
import DataCenterEntity.VM;
import OperationInterface.VMCreation;

public class Largest implements VMCreation {

    /**
     * @param vmCpu an array of vmCpu settings, contains all VM configuration
     * @param vmMem an array of vmMem settings, contains all VM configuration
     * @param container
     * @return the new VM that we created
     */
    public VM execute(double[] vmCpu, double[] vmMem, Container container, double[] osProb){
        VM vm = new VM(vmCpu[vmCpu.length - 1], vmMem[vmMem.length - 1], vmCpu.length - 1);

        // return the vm
        return vm;
    }


}
