package vmCreation;

import dataCenterEntity.Container;
import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.PM;
import dataCenterEntity.VM;
import operationInterface.VMCreation;

import java.util.ArrayList;

public class PossibleLargestVm implements VMCreation {

    /**
     * @param container
     * @return the new VM that we created
     */
    public VM execute(DataCenterInterface dataCenter, Container container){
        int osIndex = container.getOs() - 1;

        // grab the data from dataCenter object
        double[] osProb = dataCenter.getOsProb();
        double[] vmCpu = dataCenter.getVmCpu();
        double[] vmMem = dataCenter.getVmMem();
        int vmTypeNum = vmCpu.length;

        ArrayList<PM> pmList = dataCenter.getPmList();



        VM vm = null;
        // First Fit search, from the oldest to the latest
        for(PM pm: pmList){

            // If the pm is able to hold this container
            if(pm.getCpuRemain() >= container.getCpuConfiguration() &&
                    pm.getMemRemain() >= container.getMemConfiguration()){

                // Check if this PM can host the second largest, because it needs to be an empty PM to host the
                // largest VM, so we skip it
                for(int i = vmTypeNum - 2; i >= 0; --i){
                    if(pm.getCpuRemain() >= vmCpu[i] && pm.getMemRemain() >= vmMem[i]){

                        // Check if the VM can host this container, if it can, create this VM
                        if(vmCpu[i] - vmCpu[i] * VM.CPU_OVERHEAD_RATE >= container.getCpuConfiguration() &&
                                vmMem[i] - VM.MEM_OVERHEAD >= container.getMemConfiguration()) {
                            vm = new VM(vmCpu[i], vmMem[i], i);

                            // just return the vm
                            return vm;
                        }
                    }
                } // End for

            } // End if
        } // End iterate through pms

        // No existing pm can host this container, we must create the largest VM for this container
        vm = new VM(vmCpu[vmTypeNum - 1], vmMem[vmTypeNum - 1], vmTypeNum - 1);

        return vm;
    }


}
