package VM_Creation;
import DataCenterEntity.*;
import OperationInterface.VMCreation;

import java.util.*;

public class Simple implements VMCreation {

    /**
     *
     * @param vmCpu an array of vmCpu settings, contains all VM configuration
     * @param vmMem an array of vmMem settings, contains all VM configuration
     * @param container
     * @return the new VM that we created
     */
    public VM execute(double[] vmCpu, double[] vmMem, Container container){
        VM vm = null;
        // Search through the VM array from the smallest to the largest
        // until we find the smallest VM which has enough capacity to host the container
        for(int i = 0; i < vmCpu.length; ++i){

            // If this vm is capable to host the container
            if(vmCpu[i] >= container.getCpu() && vmMem[i] >= container.getMem()){

                // We create a new vm in this type i
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
