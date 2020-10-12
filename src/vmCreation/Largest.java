package vmCreation;

import dataCenterEntity.*;
import operationInterface.VMCreation;

public class Largest implements VMCreation {

    /**
     * @param container
     * @return the new VM that we created
     */
    public VM execute(DataCenterInterface dataCenter, Container container){

        double[] vmCpu = dataCenter.getVmCpu();
        double[] vmMem = dataCenter.getVmMem();

        int index = findLargest(vmCpu, vmMem);
        return new VM(vmCpu[index], vmMem[index], index);
    }

    private int findLargest(double[] cpu, double[] mem){
        double total = 0;
        int index = 0;
        for(int i = 0; i < cpu.length; i++){
            if(total < cpu[i] + mem[i]){
                total = cpu[i] + mem[i];
                index = i;
            }
        }
        return index;
    }


}
