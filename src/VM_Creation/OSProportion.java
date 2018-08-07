package VM_Creation;

import DataCenterEntity.*;
import OperationInterface.VMCreation;

import java.util.ArrayList;

public class OSProportion implements VMCreation {

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
        double currentOsProb = osProb[osIndex];
        int vmTypeNum = vmCpu.length;

        ArrayList<PM> pmList = dataCenter.getPmList();



        VM vm = null;
        // We already know there are two types of OS, so we just give the largest to the most frequent one
        if(currentOsProb > 0.5) {
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
                                break;
                            }
                        }
                    } // End for

                } // End if
                // If we have created a VM, we don't need to search anymore
                if(vm != null) break;
            } // End iterate through pms

            // No existing pm can host this container, we must create the largest VM for this container
            vm = new VM(vmCpu[vmTypeNum - 1], vmMem[vmTypeNum - 1], vmTypeNum - 1);

            // otherwise, we just assign a suitable one
        } else {

            // Again, First Fit
            for(PM pm:pmList){
                // If this pm can host this container
                if(pm.getCpuRemain() >= container.getCpuConfiguration() &&
                        pm.getMemRemain() >= container.getMemConfiguration()){

                    // Check if this PM can host the smallest VM
                    for(int i = 0; i < vmTypeNum - 1; ++i){
                        if(pm.getCpuRemain() >= vmCpu[i] && pm.getMemRemain() >= vmMem[i]){

                            // Check if the VM can host this container, if it can, create this VM
                            if(vmCpu[i] - vmCpu[i] * VM.CPU_OVERHEAD_RATE >= container.getCpuConfiguration() &&
                                    vmMem[i] - VM.MEM_OVERHEAD >= container.getMemConfiguration()) {
                                vm = new VM(vmCpu[i], vmMem[i], i);
                                break;
                            }
                        }
                    } // End for

                } // End If
                // If we have created the VM, break out
                if(vm != null) break;
            } // End for
            // No existing PM can host this container, we must create a new one
            // Again, look for the smallest VM that can host this container
            for(int i = 0; i < vmTypeNum; ++i){
                if(vmCpu[i] - vmCpu[i] * VM.CPU_OVERHEAD_RATE >= container.getCpuConfiguration() &&
                        vmMem[i] - VM.MEM_OVERHEAD >= container.getMemConfiguration()){
                    vm = new VM(vmCpu[i], vmMem[i], i);
                    break;
                }
            }

        } // end else

        // Debug
//        if(vm == null){
//            for(PM pm:pmList){
//                pm.print();
//            }
//        }
        // return the vm
        return vm;
    }


}
