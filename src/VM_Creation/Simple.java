package VM_Creation;
import DataCenterEntity.*;
import OperationInterface.VMCreation;

import java.util.*;

public class Simple implements VMCreation {

    private VMSelected vm;

    public VM execute(ArrayList<VM> vmList, Container container){

        double vmRemainCpu = 0;
        double vmRemainMem = 0;
        double type = 0;

//        double diff = Math.abs(taskCpu - taskMem)
        // Check every VM from smallest to the largest, find the most suitable VM for the container
//        for(int i = 0; i < VMCPU.length; i++){
//            vmRemainCpu = VMCPU[i] - container.getCpu();
//            vmRemainMem = VMMEM[i] - container.getMem();
//            type = i + 1;
//            if(vmRemainCpu >= 0 && vmRemainMem >= 0) break;
//        }
//
//        return new VMSelected(0, type, vmRemainCpu, vmRemainMem, taskOS);
        return null;
    }
}
