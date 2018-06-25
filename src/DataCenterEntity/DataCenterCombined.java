package DataCenterEntity;

import OperationInterface.Allocation;
import OperationInterface.PMCreation;
import OperationInterface.VMCreation;

import java.util.ArrayList;
import java.util.HashMap;

public class DataCenterCombined {

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

    // Accumulated Energy consumption
    private double accumulatedEnergyConsumption = 0;

    // parameters
    private double maxEnergy;
    private double k;
    private double pmCpu;
    private double pmMem;
    private double[] vmCpu;
    private double[] vmMem;
    private double[] osProb;
    private String base;

    // These two HashMaps map ID to their index in the list.
    private HashMap VMIDtoListIndex;
    private HashMap PMIDtoListIndex;


    // Monitor update the latest information of the data center
    public Monitor monitor;

    // We initialize these parameters when the datacenter is created
    public DataCenterCombined(
            String base,
            double maxEnergy,
            double k,
            double pmCpu,
            double pmMem,
            double[] vmCpu,
            double[] vmMem,
            double[] osProb,
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
        this.osProb = osProb.clone();
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

    private void updateAccumulatedEnergy(){
        accumulatedEnergyConsumption += calEnergy();
    }
    public double getAccumulatedEnergyConsumption(){
        return accumulatedEnergyConsumption;
    }


    // Initialize Data center
    public void initialization(
                            ArrayList<Double[]> initPmList,
                            ArrayList<Double[]> initVmList,
                            ArrayList<Double[]> containerList,
                            ArrayList<Double[]> osList){
        int globalVMCounter = 0;
        // for each PM
        for(int i = 0; i < initPmList.size(); ++i){

            // Get the VMs of this PM
            Double[] vms = initPmList.get(i);

            // Create a new PM
            PM pm = new PM(pmCpu, pmMem, k, maxEnergy);

            // for this each VM in this PM
            for(int vmCounter = 0; vmCounter < vms.length; ++vmCounter){

                // Get the type of this VM
                int vmType = vms[vmCounter].intValue() - 1;

                // Create this VM
                VM vm = new VM(vmCpu[vmType], vmMem[vmType], vmType);
                Double[] os = osList.get(vm.getID() - 1);
                vm.setOs(os[0].intValue());

                // get the containers allocated on this VM
                Double[] containers = initVmList.get(vmCounter + globalVMCounter);

                // Allocate the VM to this PM
                pm.addVM(vm);

                // for each container
                for(int conContainer = containers[0].intValue() - 1;
                        conContainer < containers[containers.length - 1].intValue();
                        ++conContainer){
//                    System.out.println("conContainer = " + conContainer);
                    // Get the container's cpu and memory
                    Double[] cpuMem = containerList.get(conContainer);
                    //Create this container
                    Container container = new Container(cpuMem[0], cpuMem[1], os[0].intValue(), conContainer);

                    //Allocate the container to this VM
                    vm.addContainer(container);
                } // Finish allocate containers to VMs



                // add this vm to the data center vm list
                this.vmList.add(vm);

                // We map VM ID and its index in the vmList
                VMIDtoListIndex.put(vm.getID(), this.vmList.size() - 1);

            } // End  of each VM
            // we must update the globalVMCounter
            globalVMCounter += vms.length;

            // add this pm to the data center pm list
            this.pmList.add(pm);
            PMIDtoListIndex.put(pm.getID(), this.pmList.size() - 1);

        } // End of each PM

        //calculate Energy consumption, not acculated...
        updateAccumulatedEnergy();

        // save the current energy consumption
        monitor.addEnergy(calEnergy());

    }


    // This method is called when new container comes
    public void receiveContainer(Container container){

//        System.out.println();
        int choosedVMID = vmSelection.execute(vmList, container, VMSELECTION);



        // If there is no suitable VM to select, then we will need to create a new one
        if(choosedVMID == 0){

            System.out.println("Create VM branch");
            // 1. We create a new VM
            // 2. Add the container to the new VM
            // 3. Add the new VM to the vmList
            VM vm = vmCreation.execute(vmCpu, vmMem, container, osProb);

            vm.addContainer(container);
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

//            System.out.println("VM Selection branch");
            // First, we look for VM's index in the list
            // Then, we retrieve it from the list
            // Finally, we add the container to the VM.
            VM choosedVM = vmList.get((int) VMIDtoListIndex.get(choosedVMID));
//            choosedVM.print();
            choosedVM.addContainer(container);
        }
        // Recording happen in every 20 allocations
//        int testCase = container.getID() + 1;
//        if(testCase  % 20 == 0){
//            File bF = new File(base + testCase + "/");
//            if(!bF.exists()) bF.mkdir();
//            monitor.writeStatusFiles(base + testCase + "/", pmList);
//        }

        // After allocating a container, update the accumulated energy consumption of the current data center
        updateAccumulatedEnergy();
        // store the current energy consumption, not accumulated energy consumption
//        System.out.println("Energy: " + calEnergy());
        monitor.addEnergy(calEnergy());
    }

    // Terminated this datacenter
    public void selfDestruction(String resultPath){
        monitor.writeStatusFiles(resultPath, pmList);
        PM.resetCounter();
        VM.resetCounter();
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

    public void printAll(){
        for(VM vm:vmList){
            vm.print();
        }

    }


}
