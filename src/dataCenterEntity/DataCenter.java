package dataCenterEntity;

import operationInterface.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataCenter implements DataCenterInterface{

    // static data
    private static final int VMALLOCATION = 1;
    private static final int VMSELECTION = 0;

    // scheduling methods
    private Allocation vmAllocation;
    private Allocation vmSelection;
    private VMCreation vmCreation;
    private PMCreation pmCreation;

    // List of PMs and List of VMs
    private ArrayList<PM> pmList;
    private ArrayList<VM> vmList;

    // List of container CPU collection and Mem collection
    private ArrayList<Double> coCpuList;
    private ArrayList<Double> coMemList;

    // Accumulated Energy consumption
    private double accumulatedEnergyConsumption = 0;

    // parameters
    private final double maxEnergy;
    private final double k;
    private final double pmCpu;
    private final double pmMem;
    private double[] vmCpu;
    private double[] vmMem;
    private double[] osProb;



    // max/min value of containers
    private double maxCpu;
    private double minCpu;

    private double maxMem;
    private double minMem;


    // median value of containers
    private double coCpuMedian;
    private double coMemMedian;

    private double normalizedCoCpuMedian;
    private double normalizedCoMemMedian;

    // Mode of containers
    private double coCpuMode;
    private double coMemMode;
    private int interval;

    private Double[] cpuBounds;
    private Double[] memBounds;
    private int[] cpuFreq;
    private int[] memFreq;


    // normalized min/max value of containers
    private double normalizedMinCpu;
    private double normalizedMaxCpu;
    private double normalizedMaxMem;
    private double normalizedMinMem;

    // These two HashMaps map ID to their index in the list.
    private HashMap vmIDtoListIndex;
    private HashMap pmIDtoListIndex;


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
        this.vmIDtoListIndex = new HashMap();
        this.pmIDtoListIndex = new HashMap();

        // a monitor to keep some temp data
        monitor = new Monitor();


        pmList = new ArrayList<>();
        vmList = new ArrayList<>();
        coCpuList = new ArrayList<>();
        coMemList = new ArrayList<>();
        initialMode();

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

        //calculate Energy consumption, not accumulated energy
        updateAccumulatedEnergy();

        // save the current energy consumption
        monitor.addEnergy(calEnergy());

    }

    private void initialMode(){

        // define intervals
        interval = 20;

        // find the maximum and minimum value of CPU and memory
        Double maxCpu = pmCpu;
        Double minCpu = 0.0;

        Double maxMem = pmMem;
        Double minMem = 0.0;

        cpuBounds = new Double[interval + 1];
        memBounds = new Double[interval + 1];

        Double cpuIncrement = (maxCpu - minCpu) / interval;
        Double memIncrement = (maxMem - minMem) / interval;

        // store the boundaries of interval in the arrays
        cpuBounds[0] = minCpu;
        cpuBounds[interval] = maxCpu;
        memBounds[0] = minMem;
        memBounds[interval] = maxMem;

        for(int i = 1; i < interval; ++i){
            cpuBounds[i] = cpuBounds[i - 1] + cpuIncrement;
            memBounds[i] = memBounds[i - 1] + memIncrement;
        }

        cpuFreq = new int[interval];
        memFreq = new int[interval];
        Arrays.fill(cpuFreq, 0);
        Arrays.fill(memFreq, 0);

    }

    public ArrayList<PM> getPmList() {
        return pmList;
    }

    public ArrayList<VM> getVmList() {
        return vmList;
    }

    public double getK() {
        return k;
    }

    public double getPmCpu() {
        return pmCpu;
    }

    public double getPmMem() {
        return pmMem;
    }

    public double[] getOsProb() {
        return osProb;
    }

    public double[] getVmCpu() {
        return vmCpu;
    }

    public double[] getVmMem() {
        return vmMem;
    }

    public double getVmCpuOverhead(int vmType) {
        return VM.CPU_OVERHEAD_RATE * vmCpu[vmType];
    }

    public double getVmMemOverhead(){
        return VM.MEM_OVERHEAD;
    }

    private void updateMode(Container container){
        Double containerCPU = container.getCpuConfiguration();
        Double containerMem = container.getMemConfiguration();


        // Count the frequency of cpu and memory in those intervals
        for(int i = 0; i < interval; i++){
            double lowerBound = cpuBounds[i];
            double upperBound = cpuBounds[i + 1];
            if(containerCPU >= lowerBound && containerCPU <= upperBound){
                cpuFreq[i] += 1;
            }
        }

        for(int i = 0; i < interval; i++){
            double lowerBound = memBounds[i];
            double upperBound = memBounds[i + 1];
            if(containerMem >= lowerBound && containerMem <= upperBound){
                memFreq[i] += 1;
            }
        }

        // find the most frequent index
        int maxFreqCpu = 0;
        int maxCpuIndex = 0;

        int maxFreqMem = 0;
        int maxMemIndex = 0;

        for(int i = 0; i < interval; i++){
            if(maxFreqCpu < cpuFreq[i]) {
                maxFreqCpu = cpuFreq[i];
                maxCpuIndex = i;
            }

            if(maxFreqMem < memFreq[i]) {
                maxFreqMem = memFreq[i];
                maxMemIndex = i;
            }
        }

        // calculate the mode for cpu and memory
        coCpuMode = (cpuBounds[maxCpuIndex] + cpuBounds[maxCpuIndex + 1]) / 2;
        coMemMode = (memBounds[maxMemIndex] + memBounds[maxMemIndex + 1]) / 2;
    }


    private void updateMedian(Container container){
        Double containerCPU = container.getCpuConfiguration();
        Double containerMem = container.getMemConfiguration();

        if(coCpuList.size() == 0){
            coCpuList.add(containerCPU);
            coMemList.add(containerMem);
            return;
        }

        // insert to the list
        for(int i = 0; i < coCpuList.size(); ++i){
            if(containerCPU <= coCpuList.get(i)){
                coCpuList.add(i, containerCPU);
                break;
            } else if(containerCPU > coCpuList.get(i) && i == coCpuList.size() - 1){
                coCpuList.add(containerCPU);
                break;
            }
        }

        for(int i = 0; i < coMemList.size() - 1; ++i){
            if(containerMem <= coMemList.get(i)){
                coMemList.add(i, containerMem);
                break;
            } else if(containerMem > coMemList.get(i) && i == coMemList.size() - 1){
                coMemList.add(containerMem);
                break;
            }
        }

        // update the median value
        if(coCpuList.size() % 2 == 0){
            coCpuMedian = (coCpuList.get(coCpuList.size() / 2 - 1) + coCpuList.get(coCpuList.size() /  2)) / 2;
        } else{
            coCpuMedian = coCpuList.get((int) Math.floor(coCpuList.size() / 2.0));
        }

        if(coMemList.size() % 2 == 0){
            coMemMedian = (coMemList.get(coMemList.size() / 2 - 1) + coMemList.get(coMemList.size() /  2)) / 2;
        } else{
            coMemMedian = coMemList.get((int) Math.floor(coMemList.size() / 2.0));
        }

        normalizedCoCpuMedian = coCpuMedian / pmCpu;
        normalizedCoMemMedian = coMemMedian / pmMem;

    }


    private void updateMaxMin(Container container){
        Double containerCPU = container.getCpuConfiguration();
        Double containerMem = container.getMemConfiguration();

        if(maxCpu == 0) {
            maxCpu = containerCPU;
        } else if(maxCpu < containerCPU){
            maxCpu = containerCPU;
        }

        if(maxMem == 0) {
            maxMem = containerMem;
        } else if(maxMem < containerMem){
            maxMem = containerMem;
        }

        if(minCpu == -1) {
            minCpu = containerCPU;
        } else if(minCpu > containerCPU){
            minCpu = containerCPU;
        }

        if(minMem == -1) {
            minMem = containerMem;
        } else if(minMem > containerMem){
            maxMem = containerMem;
        }

        normalizedMinCpu = minCpu / pmCpu;
        normalizedMaxCpu = maxCpu / pmCpu;
        normalizedMaxMem = maxMem / pmMem;
        normalizedMinMem = minMem / pmMem;

    }




    // This method is called when new container comes
    public void receiveContainer(Container container){

//        updateMedian(container);
//        updateMaxMin(container);
//        updateMode(container);
//        System.out.println();
        int chosenVmID = vmSelection.execute(this, container, VMSELECTION);



        // If there is no suitable VM to select, then we will need to create a new one
        if(chosenVmID == 0){

            System.out.println("Create VM branch");
            // 1. We create a new VM
            // 2. Add the container to the new VM
            // 3. Add the new VM to the vmList
            VM vm = vmCreation.execute(this, container);

            vm.addContainer(container);
            int currentVmNum = vmList.size();
            vmList.add(vm);

            // We map VM ID and its index in the vmList
            vmIDtoListIndex.put(vm.getID(), currentVmNum);

            // After we created the vm, we will need to allocate it to a PM, and if there is no suitable PM,
            // We must create a new PM to accommodate and add the PM to pmList
            int chosenPmID = vmAllocation.execute(this, vm, VMALLOCATION);
            if(chosenPmID == 0){
                PM pm = pmCreation.execute(pmCpu, pmMem, k, maxEnergy);
                int currentPmNum = pmList.size();
                pm.addVM(vm);
                pmList.add(pm);


                // We map PM ID and its index in the pmList
                pmIDtoListIndex.put(pm.getID(), currentPmNum);

                // Allocate to an existing PM
            } else{

                // First, we look for PM's index in the list
                // Then, we retrieve it from the list
                // Finally, we add the VM to the PM.
                PM chosenPM = pmList.get((int) pmIDtoListIndex.get(chosenPmID));
                chosenPM.addVM(vm);

            }
            // If there is a suitable VM, then allocate to this VM
        } else{

            // First, we look for VM's index in the list
            // Then, we retrieve it from the list
            // Finally, we add the container to the VM.
            VM chosenVM = vmList.get((int) vmIDtoListIndex.get(chosenVmID));
            chosenVM.addContainer(container);
        }

        // After allocating a container, update the accumulated energy consumption of the current data center
        updateAccumulatedEnergy();
        // store the current energy consumption, not accumulated energy consumption
        monitor.addEnergy(calEnergy());
        // store the accumulated energy consumption
        monitor.addAccEnergy(accumulatedEnergyConsumption);
    }

    // Terminated this data center
    public void selfDestruction(String resultPath){
        monitor.writeStatusFiles(resultPath, pmList);
        PM.resetCounter();
        VM.resetCounter();
    }

    // Calculate the energy by sum up the energy consumption of all PMs
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
