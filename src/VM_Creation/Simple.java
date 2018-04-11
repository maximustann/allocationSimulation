package VM_Creation;
import DataCenterEntity.*;
import OperationInterface.VMCreation;

import java.util.*;

public class Simple implements VMCreation {

    public VM execute(double[] vmCpu, double[] vmMem, Container container){
        VM vm = null;
        for(int i = 0; i < vmCpu.length; ++i){
            if(vmCpu[i] >= container.getCpu() && vmMem[i] >= container.getMem()){
                vm = new VM(vmCpu[i], vmMem[i], i);
                System.out.println("Select a VM cpu: " + vmCpu[i] + ", mem: " + vmMem[i]);
                break;
            }
        }

        return vm;
    }
}
