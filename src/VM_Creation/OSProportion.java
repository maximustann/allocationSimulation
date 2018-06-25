package VM_Creation;

import DataCenterEntity.Container;
import DataCenterEntity.VM;
import OperationInterface.VMCreation;

public class OSProportion implements VMCreation {

    /**
     * @param vmCpu an array of vmCpu settings, contains all VM configuration
     * @param vmMem an array of vmMem settings, contains all VM configuration
     * @param container
     * @return the new VM that we created
     */
    public VM execute(double[] vmCpu, double[] vmMem, Container container, double[] osProb){
        int osIndex = container.getOs() - 1;
        double currentOsProb = osProb[osIndex];
        VM vm = null;
        // We already know there are two types of OS, so we just give the largest to the most frequent one
        if(currentOsProb > 0.5) {
            vm = new VM(vmCpu[vmCpu.length - 1], vmMem[vmMem.length - 1], vmCpu.length - 1);

            // otherwise, we just assign a suitable one
        } else {
            for(int i = 0; i < vmCpu.length; ++i){
                // If this vm is capable to host the container
                // include the container's resource requirement and the VM overhead
                if(vmCpu[i] >= (container.getCpu_used() + VM.CPU_OVERHEAD_RATE * vmCpu[i])
                        && vmMem[i] >= (container.getMem_used() + VM.MEM_OVERHEAD)){

                    // We create a new vm in this type i, starts from 0
                    vm = new VM(vmCpu[i], vmMem[i], i);
                    System.out.println("create a VM cpu: " + vmCpu[i] + ", mem: " + vmMem[i]);

                    // immediately break the process
                    break;
                } // end if
            } // end for

        } // end else

        // return the vm
        return vm;
    }


}
