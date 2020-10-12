package dataCenterEntity;

import operationInterface.HolderSelectionCreation;

import java.util.ArrayList;
import java.util.HashMap;

/** DataCenter
 * combines to procedure of VM creation and VM selection
 */
public class DataCenter implements DataCenterInterface{


    // scheduling methods
    private HolderSelectionCreation pmSelectionCreation;
    private HolderSelectionCreation vmSelectionCreation;

    // List of PMs and List of VMs
    private ArrayList<PM> pmList;
    private ArrayList<VM> vmList;

    // Accumulated Energy consumption
    private double accumulatedEnergyConsumption = 0;

    // parameters, they are constant and should not be changed
    private final double maxEnergy;
    private final double k;
    private final double pmCpu;
    private final double pmMem;
    private final double[] vmCpu;
    private final double[] vmMem;
    private final double[] osProb;

    // These two HashMaps map ID to their index in the list.
    private HashMap vmIDtoListIndex;
    private HashMap pmIDtoListIndex;


//    private double normalizedVmCpuOverhead;
//    private double normalizedVmMemOverhead;

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
            double[] osProb,
            HolderSelectionCreation pmSelectionCreation,
            HolderSelectionCreation vmSelectionCreation
    ){
        this.maxEnergy = maxEnergy;
        this.k = k;
        this.pmCpu = pmCpu;
        this.pmMem = pmMem;
        this.vmCpu = vmCpu.clone();
        this.vmMem = vmMem.clone();
        this.osProb = osProb.clone();
        this.pmSelectionCreation = pmSelectionCreation;
//        this.vmAllocation = vmAllocation;
        this.vmSelectionCreation = vmSelectionCreation;
        this.vmIDtoListIndex = new HashMap();
        this.pmIDtoListIndex = new HashMap();
        monitor = new Monitor();


        pmList = new ArrayList<>();
        vmList = new ArrayList<>();


    }

    private void updateAccumulatedEnergy(){
        accumulatedEnergyConsumption += calEnergy();
    }
    public double getAccumulatedEnergyConsumption(){
        return accumulatedEnergyConsumption;
    }

    public double getPmCpu() {
        return pmCpu;
    }

    public double getPmMem(){
        return pmMem;
    }

    public double getVmCpuOverhead(int vmType){
        return VM.CPU_OVERHEAD_RATE * vmCpu[vmType];
    }

    public  double getVmMemOverhead(int vmType){
        return VM.MEM_OVERHEAD;
    }

    //    public double getNormalizedVmCpuOverhead() {
//        return normalizedVmCpuOverhead;
//    }
//
//    public double getNormalizedVmMemOverhead(){
//        return normalizedVmMemOverhead;
//    }

    public double[] getVmCpu(){
        return vmCpu;
    }

    public double[] getOsProb(){
        return osProb;
    }
    public double[] getVmMem(){
        return vmMem;
    }

    public ArrayList<PM> getPmList(){
        return pmList;
    }

    public ArrayList<VM> getVmList(){
        return vmList;
    }

    public double getVmMemOverhead() {
        return VM.MEM_OVERHEAD;
    }

    // Initialize Data center
    public void initialization(
                            ArrayList<Double[]> initPmList,
                            ArrayList<Double[]> initVmList,
                            ArrayList<Double[]> containerList,
                            ArrayList<Double[]> osList){





        int globalVMCounter = 0;
        // for each PM
        for(Double[] vms:initPmList){

            // Create a new PM
            PM pm = new PM(pmCpu, pmMem, k, maxEnergy);

            // for this each VM in this PM
            for(int vmCounter = 0; vmCounter < vms.length; ++vmCounter){

                // Get the type of this VM
                int vmType = vms[vmCounter].intValue() - 1;

                // Create this VM
                VM vm = new VM(vmCpu[vmType], vmMem[vmType], vmType);
                Double[] os = osList.get(vmCounter + globalVMCounter);
                vm.setOs(os[0].intValue());

                // get the containers allocated on this VM
                Double[] containers = initVmList.get(vmCounter + globalVMCounter);

                // Allocate the VM to this PM
                pm.addVM(vm);

                // for each container
                for(int conContainer = containers[0].intValue() - 1;
                    conContainer < containers[containers.length - 1].intValue();
                    ++conContainer){
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
                vmIDtoListIndex.put(vm.getID(), this.vmList.size() - 1);

            } // End  of each VM
            // we must update the globalVMCounter
            globalVMCounter += vms.length;

            // add this pm to the data center pm list
            this.pmList.add(pm);
            pmIDtoListIndex.put(pm.getID(), this.pmList.size() - 1);

        } // End of each PM

        updateAccumulatedEnergy();

        // save the current energy consumption
        monitor.addEnergy(calEnergy());

    }

    public double getK() {
        return k;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    // This method is called when new container comes
    public void receiveContainer(Container container){

        // select or create a VM
        VM chosenVM = (VM) vmSelectionCreation.execute(this, container);

        // If the algorithm has chosen an existing VM
        if(vmIDtoListIndex.containsKey(chosenVM.getID())){

            chosenVM.addContainer(container);

            // Else the algorithm has created a new VM, add the container to the VM
            // Add this VM to the vmList
        } else {

            // adjust the current vm global counter and the current VM ID
            int newID = VM.getGlobalCounter() + 1;
            VM.setGlobalCounter(newID);
            chosenVM.setID(newID);




            int currentVmNum = vmList.size();
            vmList.add(chosenVM);

            // We map VM ID and its index in the vmList
            vmIDtoListIndex.put(chosenVM.getID(), currentVmNum);


            // We must first select or create a new PM to accommodate this VM before we allocating the container,
            // and we need to add the new PM to the pmList
            PM chosenPm = (PM) pmSelectionCreation.execute(this, chosenVM);
//
//            // If we have chosen an existing PM
            if(pmIDtoListIndex.containsKey(chosenPm.getID())){
                chosenPm.addVM(chosenVM);

            // We created a new PM
            }else{
                int currentPmNum = pmList.size();
                chosenPm.addVM(chosenVM);
                pmList.add(chosenPm);
                pmIDtoListIndex.put(chosenPm.getID(), currentPmNum);
            }

            // add the container to the VM
            chosenVM.addContainer(container);
        }

        // After allocating a container, update the accumulated energy consumption of the current data center
        updateAccumulatedEnergy();
        // store the current energy consumption, not accumulated energy consumption
//        System.out.println("Energy: " + calEnergy());
        monitor.addEnergy(calEnergy());
        // store the accumulated energy consumption
        monitor.addAccEnergy(accumulatedEnergyConsumption);
        // update the waste energy
        monitor.udpateWasteEnergy(this, pmList);
    }

    // Terminated this data center
    public void selfDestruction(String resultPath){
        monitor.writeStatusFiles(resultPath, pmList);
        PM.resetCounter();
        VM.resetCounter();
    }

    // Calculate the energy by suming up the energy consumption of all PMs
    public double calEnergy(){
        double energy = 0;
        for(PM pm:pmList){
            energy += pm.calEnergy();
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
