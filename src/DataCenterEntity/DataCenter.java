package DataCenterEntity;

import OperationInterface.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DataCenter {

    // static data
    public static final boolean FAILED = false;
    public static final boolean CLOSED = false;
    public static final boolean OPEN = true;
    public static final boolean ALLOCATED = true;
    public static final int VMALLOCATION = 1;
    public static final int VMSELECTION = 0;

    // scheduling methods
    private Allocation vmAllocation;
    private Allocation vmSelection;
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
    private String base;

    // These two HashMaps map ID to their index in the list.
    private HashMap VMIDtoListIndex;
    private HashMap PMIDtoListIndex;


    // Monitor update the latest information of the data center
    public Monitor monitor;

    // We initialize these parameters when the datacenter is created
    public DataCenter(
            String base,
            double maxEnergy,
            double k,
            double pmCpu,
            double pmMem,
            double[] vmCpu,
            double[] vmMem,
            Allocation vmAllocation,
            Allocation vmSelection,
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
        this.VMIDtoListIndex = new HashMap();
        this.PMIDtoListIndex = new HashMap();
        this.base = base;
        monitor = new Monitor();


        pmList = new ArrayList<PM>();
        vmList = new ArrayList<VM>();


    }

    // This method is called when new container comes
    public void receiveContainer(Container container){
        int choosedVMID = vmSelection.execute(vmList, container, VMSELECTION);

        // If there is no suitable VM to select, then we will need to create a new one
        if(choosedVMID == 0){

            System.out.println("Create VM branch");
            // 1. We create a new VM
            // 2. Add the container to the new VM
            // 3. Add the new VM to the vmList
            VM vm = vmCreation.execute(vmCpu, vmMem, container);
            vm.addContainer(container);
            vm.print();
            int currentVMnum = vmList.size();
            vmList.add(vm);

            // We map VM ID and its index in the vmList
            VMIDtoListIndex.put(vm.getID(), currentVMnum);

            // After we created the vm, we will need to allocate it to a PM, and if there is no suitable PM,
            // We must create a new PM to accommodate and add the PM to pmList
            int choosedPMID = vmAllocation.execute(pmList, vm, VMALLOCATION);
            if(choosedPMID == 0){
                PM pm = pmCreation.execute(pmCpu, pmMem, k, maxEnergy);
                int currentPMnum = pmList.size();
                pm.addVM(vm);
                pmList.add(pm);


                // We map PM ID and its index in the pmList
                PMIDtoListIndex.put(pm.getID(), currentPMnum);

                // Allocate to an existing PM
            } else{

                // First, we look for PM's index in the list
                // Then, we retrieve it from the list
                // Finally, we add the VM to the PM.
                PM choosedPM = pmList.get((int) PMIDtoListIndex.get(choosedPMID));
                choosedPM.addVM(vm);

            }
            // If there is a suitable VM, then allocate to this VM
        } else{

            // First, we look for VM's index in the list
            // Then, we retrieve it from the list
            // Finally, we add the container to the VM.
            VM choosedVM = vmList.get((int) VMIDtoListIndex.get(choosedVMID));
//            System.out.println("Select VM branch");
            choosedVM.addContainer(container);
        }
        // Recording happen in every 20 allocations
        int testCase = container.getID() + 1;
        if(testCase  % 20 == 0){
            File bF = new File(base + testCase + "/");
            if(!bF.exists()) bF.mkdir();
            monitor.writeStatusFiles(base + testCase + "/", pmList);
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
//        System.out.println("Total energy: " + calEnergy());
        for(PM pm:pmList){
            pm.print();
        }
    }


}
