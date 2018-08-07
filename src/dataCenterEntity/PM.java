package dataCenterEntity;

import java.util.*;

public class PM implements Holder {
    // This is a global variable. It will be incremented by 1 whenever a new PM is created
    // This variable is useful because it prevents duplicated pmID
    private static int pmCounter = 0;


    static double CLOSED = 0;
    static double OPEN = 1;

    // id starts from 1
    private int id;
    // configuration is how PM is configured
    private double cpuConfiguration;
    private double memConfiguration;

    // _remain is the boundary of allocation
    // for example, give a PM CPU = 6600, allocate a VM CPU = 3300,
    // cpu_remain = 6600 - 3300 = 3300
    private double cpuRemain;
    private double memRemain;

    // used is the acutual used of resources by containers and VM overheads
    private double cpuUsed;
    private double memUsed;

    // utilization is calculate as: used / configuration
    private double cpuUtilization;
    private double memUtilization;
    private double status;
    private double k;
    private double maxEnergy;
    private ArrayList<VM> vmList;
    private HashMap vmIDtoListIndex;
    private double energyConsumption;

    public PM(double cpu, double mem, double k, double maxEnergy) {
        // initialize cpu and memory
        cpuConfiguration = cpu;
        memConfiguration = mem;
        cpuUsed = 0;
        memUsed = 0;
        cpuRemain = cpu;
        memRemain = mem;

        // initialize the vmList and map
        vmList = new ArrayList<>();
        vmIDtoListIndex = new HashMap();
        this.k = k;
        this.maxEnergy = maxEnergy;

        // increment the pm counter by 1
        // initialize pm id
        pmCounter += 1;
        id = pmCounter;
    }

    // Balance should always be in [0, 1], the closer to 1, the more balance
    public double getBalance(){
        double balance = 0;
        if(cpuUtilization >= memUtilization)
            balance = memUtilization / cpuUtilization;
        else balance = cpuUtilization / memUtilization;
        return balance;
    }


    // return the number of VMs
    public int vmNum(){
        return vmList.size();
    }

    public static void resetCounter(){
        pmCounter = 0;
    }

    // according to a famous energy function
    public double calEnergy(){
        energyConsumption = k * maxEnergy + (1 - k) * cpuUtilization * maxEnergy;
        return energyConsumption;
    }

    // check
    private void updateCpuUtilization(){
        cpuUtilization = cpuUsed / cpuConfiguration;
    }

    //check
    private void updateMemUtilization(){
        memUtilization = memUsed / memConfiguration;
    }

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    public double getMemUtilization() {
        return memUtilization;
    }

    // add VM, update CPU
    private void addCpu(VM vm){
        // cpu_remain to the VM is NOT the actual use of the CPU resource. It is a constraint.
        cpuRemain -= vm.getCpuConfiguration();

        // cpu_used is the actual used of VM which includes all the containers and overhead of the VM
        cpuUsed += vm.getCpuUsed();
//        System.out.println("After add: " + cpu_remain);

        // update CPU utilization
        updateCpuUtilization();
    }

    // remove VM, update CPU
    private void removeCpu(VM vm){
        cpuRemain += vm.getCpuConfiguration();
        cpuUsed -= vm.getCpuUsed();
        updateCpuUtilization();
    }

    // add VM, update MEM
    private void addMem(VM vm){
        memRemain -= vm.getMemConfiguration();
        memUsed += vm.getMemUsed();
        updateMemUtilization();
    }

    // remove VM, update MEM
    private void removeMem(VM vm){
        memRemain += vm.getMemConfiguration();
        memUsed -= vm.getMemUsed();
        updateMemUtilization();
    }

    // get cpu_remain
    public double getCpuRemain(){
        return cpuRemain;
    }

    // get mem_remain
    public double getMemRemain(){
        return memRemain;
    }

    // check
    public double getCpuUsed(){
        return cpuUsed;
    }

    // check
    public double getMemUsed(){
        return memUsed;
    }


    // This method is called by VM.
    // Whenever a new container is allocated to an hosted VM, this container bring new resource consumption
    // Therefore, we need to update the cpu_used and mem_used
    public void allocateNewContainer(double containerCpu, double containerMem){
        cpuUsed += containerCpu;
        memUsed += containerMem;
        updateCpuUtilization();
        updateMemUtilization();
    }

    // This method is called by VM after release a container.
    public void releaseOldContainer(double containerCpu, double containerMem){
        cpuUsed -= containerCpu;
        memUsed -= containerMem;
        updateCpuUtilization();
        updateMemUtilization();
    }


    /**
     * Add VM
     * @param vm Virtual machine
     * @return vmList
     *
     * 1. Add the VM to the vmList
     * 2. Update CPU and MEM
     * 3. Update the VM ID--vmList index mapping
     * 4. VM add this PM to its allocateTo field
     *
     */
    public ArrayList<VM> addVM(VM vm){
        // Check if this PM has enough cpu and memory to allocate to this VM
        // If yes, add this VM to the vmList, update utilization
        // And update the mapping between VM ID and vmList index
        if(cpuRemain >= vm.getCpuConfiguration() && memRemain >= vm.getMemConfiguration()) {
            int currentIndex = vmList.size();
            vmList.add(vm);
            addCpu(vm);
            addMem(vm);
            vmIDtoListIndex.put(vm.getID(), currentIndex);

            // call VM to add the host PM to its allocateTo
            vm.setAllocateTo(this);

            if(vmList.size() == 1)
                status = OPEN;


            // If there is not enough resources
        } else {
            System.out.println("ERROR: VM allocation failed, insufficient resources");
        }
        return vmList;
    }


    // We first remove the vm from vmList
    // All the VM that behind the left VM, their index in the vmList will decrease by 1.
    public ArrayList<VM> removeVM(int vmID){
        int vmIndex = (int) vmIDtoListIndex.get(vmID);
        VM vm = vmList.get(vmIndex);
        removeCpu(vm);
        removeMem(vm);

        // call the VM to detach
        vm.detach();
        vmList.remove(vmIndex);

        // Then, we update the VMIDtoIndex mapping
        Iterator entries = vmIDtoListIndex.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            Integer value = (Integer)entry.getValue();
            if(value > vmIndex){
                value -= 1;
                entry.setValue(value);
            }
        }
        return vmList;
    }

    public double getStatus() {
        return status;
    }

    public int getID() {
        return id;
    }

    // getExtraInfo
    public int getExtraInfo(){
        return 1;
    }


    public void print(){
        System.out.println("PM ID: " + id +
                ", CPU: "+ Math.round(cpuUsed * 100) / 100.0 +
                ", Mem: " + Math.round(memUsed * 100) / 100.0 +
                ", CPU remain: " + Math.round(cpuRemain * 100) / 100.0 +
                ", Mem remain: " + Math.round(memRemain * 100) / 100.0 +
                ", CPU utilization: " + Math.round(cpuUtilization * 10000) / 10000.0 +
                ", Mem utilization: " + Math.round(memUtilization * 10000) / 10000.0 +
                ", energy: " + Math.round(calEnergy() * 100) / 100.0 +
                ", number of containers = " + getContainerNum());
//        for(VM vm:vmList) vm.print();
    }

    // return the number of all containers in all VMs in this PM
    private int getContainerNum(){
        int sum = 0;
        for(VM vm:vmList){
            sum += vm.getContainerNum();
        }
        return sum;
    }

    // return the number of vm in this pm
    public int getVMNum(){
        return vmList.size();
    }

    // the type of PM is null
    public Integer getType(){
        return null;
    }
    public double getCpuConfiguration() {
        return cpuConfiguration;
    }

    public double getMemConfiguration() {
        return memConfiguration;
    }

    public ArrayList<VM> getVMList(){
        return vmList;
    }
}
