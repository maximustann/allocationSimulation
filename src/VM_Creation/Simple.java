package VM_Creation;
import DataCenterEntity.*;
import OperationInterface.VMCreation;

import java.util.*;

public class Simple implements VMCreation {

//    private VMSelected vm;

    public VM execute(double[] vmCpu, double[] vmMem, Container container){
        VM vm = null;
        for(int i = 0; i < vmCpu.length; ++i){
            if(vmCpu[i] >= container.getCpu() && vmMem[i] >= container.getMem()){
                vm = new VM(vmCpu[i], vmMem[i], i, container.getNumber(), container.getOs());
            }
        }

        return vm;
    }
}
