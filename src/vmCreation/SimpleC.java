package vmCreation;

import dataCenterEntity.Container;
import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.VM;
import operationInterface.VMCreation;

public class SimpleC implements VMCreation {

    /**
     *
     * @param container
     * @return the new VM that we created
     */
    public VM execute(DataCenterInterface dataCenter, Container container){
        double[] vmCpu = dataCenter.getVmCpu();
        double[] vmMem = dataCenter.getVmMem();

        VM vm = null;
        // Search through the VM array from the smallest to the largest
        // until we find the smallest VM which has enough capacity to host the container
        for(int i = 0; i < vmCpu.length; ++i){

            // If this vm is capable to host the container
            // include the container's resource requirement and the VM overhead
            if(vmCpu[i] >= (container.getCpuUsed() + VM.CPU_OVERHEAD_RATE * vmCpu[i])
                        && vmMem[i] >= (container.getMemUsed() + VM.MEM_OVERHEAD)){

                // We create a new vm in this type i, starts from 0
                vm = new VM(vmCpu[i], vmMem[i], i);
                System.out.println("create a VM cpu: " + vmCpu[i] + ", mem: " + vmMem[i]);

                // immediately break the process
                break;
            }
        }

        // If vm has not been created, that means something wrong happened
        if(vm == null){
            System.out.println("Error: No VM is suitable!");
        }

        // return the vm
        return vm;
    }
}
