package DataCenterEntity;

import OperationInterface.VMAllocation;
import OperationInterface.VMCreation;
import OperationInterface.VMSelection;

import java.util.ArrayList;

public class DataCenter {

    // static data
//    static double[] VMCPU;
//    static double[] VMMEM;
    static Double FAILED = null;
    static double CLOSED = 0;
    static double OPEN = 1;
    static double ALLOCATED = 1;

    // scheduling methods
    private VMAllocation vmAllocation;
    private VMSelection vmSelection;
    private VMCreation vmCreation;

    private ArrayList<PM> pmList;
    private ArrayList<VM> vmList;

    private double[] vmCpu;
    private double[] vmMem;

    public DataCenter(
            double[] vmCpu,
            double[] vmMem,
            VMAllocation vmAllocation,
            VMSelection vmSelection,
            VMCreation vmCreation
    ){
        this.vmCpu = vmCpu.clone();
        this.vmMem = vmMem.clone();
        this.vmAllocation = vmAllocation;
        this.vmSelection = vmSelection;
        this.vmCreation = vmCreation;
    }


    public void receiveContainer(Container container){
        if(vmSelection.execute(vmList, container) == null){
            VM vm = vmCreation.execute(vmList, container);
            vmAllocation.execute(pmList, vm);
        }
    }

    public double calEnergy(){
        double energy = 0;
        for(int i = 0; i < pmList.size(); ++i){
            energy += pmList.get(i).calEnergy();
        }
        return energy;
    }

    public void print(){
        System.out.println("Total energy: " + calEnergy());
    }


}
