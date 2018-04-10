package DataCenterEntity;

import OperationInterface.PMCreation;
import OperationInterface.VMAllocation;
import OperationInterface.VMCreation;
import OperationInterface.VMSelection;

import java.util.ArrayList;
import java.util.HashMap;

public class DataCenter {

    // static data
    static boolean FAILED = false;
    static boolean CLOSED = false;
    static boolean OPEN = true;
    static boolean ALLOCATED = true;


    // scheduling methods
    private VMAllocation vmAllocation;
    private VMSelection vmSelection;
    private VMCreation vmCreation;
    private PMCreation pmCreation;

    // List of PMs and List of VMs
    private ArrayList<PM> pmList;
    private ArrayList<VM> vmList;

    // parameters
    private double maxEnergy;
    private double k;
    private double pmCpu;
    private double pmMem;
    private double[] vmCpu;
    private double[] vmMem;
    private HashMap VMIDtoListIndex;

    // Monitor update the latest information of the data center
    public Monitor monitor;

    // We initialize these parameters when the datacenter is created
    public DataCenter(
            double maxEnergy,
            double k,
            double pmCpu,
            double pmMem,
            double[] vmCpu,
            double[] vmMem,
            VMAllocation vmAllocation,
            VMSelection vmSelection,
            VMCreation vmCreation,
            PMCreation pmCreation
    ){
        this.maxEnergy = maxEnergy;
        this.k = k;
        this.pmCpu = pmCpu;
        this.pmMem = pmMem;
        this.vmCpu = vmCpu.clone();
        this.vmMem = vmMem.clone();
        this.vmAllocation = vmAllocation;
        this.vmSelection = vmSelection;
        this.vmCreation = vmCreation;
        this.pmCreation = pmCreation;

        pmList = new ArrayList<PM>();
        vmList = new ArrayList<VM>();

    }

    // This method is called when new container comes
    public void receiveContainer(Container container){
        int choosedVMID = vmSelection.execute(vmList, container);

        /* If there is no suitable VM to select, then we will need to create a new one */
        if(choosedVMID == 0){
            // We create a new VM and add it to the vmList
            VM vm = vmCreation.execute(vmCpu, vmMem, container);
            vmList.add(vm);

            // After we created the vm, we will need to allocate it to a PM, and if there is no suitable PM,
            // We must create a new PM to accommodate
            if(vmAllocation.execute(pmList, vm) == 0){
                PM pm = pmCreation.execute(pmCpu, pmMem, pmList.size(), k, maxEnergy);
                pm.addVM(vm);
            }
            // If there is a suitable VM, then allocate to this VM
        } else{
//            vmList.get()
        }
    }

    // Calculate the energy by suming up the energy consumption of all PMs
    public double calEnergy(){
        double energy = 0;
        for(int i = 0; i < pmList.size(); ++i){
            energy += pmList.get(i).calEnergy();
        }
        return energy;
    }

    // Print the state of the data center
    public void print(){
        System.out.println("Total energy: " + calEnergy());
    }


}
