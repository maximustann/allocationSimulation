package DataCenterEntity;

import java.util.*;

public class PM implements Holder {
    // This is a global variable. It will be incremented by 1 whenever a new PM is created
    // This variable is useful because it prevents duplicated pmID
    static int pmCounter = 0;


    static double CLOSED = 0;
    static double OPEN = 1;

    // id starts from 1
    private int id;
    // configuration is how PM is configured
    private double cpu_configuration;
    private double mem_configuration;

    // allocated is the resource that allocate to VMs according to VM's size
    private double cpu_remain;
    private double mem_remain;

    // used is the acutual used of resources by containers and VM overheads
    private double cpu_used;
    private double mem_used;

    // utilization is calculate as: used / configuration
    private double cpu_utilization;
    private double mem_utilization;
    private double status;
    private double k;
    private double maxEnergy;
    private ArrayList<VM> vmList;
    private HashMap VMIDtoListIndex;
    private double energyConsumption;

    public PM(double cpu, double mem, double k, double maxEnergy) {
        // initialize cpu and memory
        cpu_configuration = cpu;
        mem_configuration = mem;
        cpu_used = 0;
        mem_used = 0;
        cpu_remain = cpu;
        mem_remain = mem;

        // initialize the vmList and map
        vmList = new ArrayList<>();
        VMIDtoListIndex = new HashMap();
        this.status = status;
        this.k = k;
        this.maxEnergy = maxEnergy;

        // increment the pm counter by 1
        // initialize pm id
        pmCounter += 1;
        id = pmCounter;
    }

    public static void resetCounter(){
        pmCounter = 0;
    }

    // according to a famous energy function
    public double calEnergy(){
        energyConsumption = k * maxEnergy + (1 - k) * cpu_utilization * maxEnergy;
        return energyConsumption;
    }

    // check
    private void updateCpuUitlization(){
        cpu_utilization = cpu_used / cpu_configuration;
    }

    //check
    private void updateMemUitlization(){
        mem_utilization = mem_used / mem_configuration;
    }

    // add VM, update CPU
    private void addCpu(VM vm){
        // cpu_remain to the VM is NOT the actual use of the CPU resource. It is a constraint.
        cpu_remain -= vm.getCpu_configuration();

        // cpu_used is the actual used of VM which includes all the containers and overhead of the VM
        cpu_used += vm.getCpu_used();

        // update CPU utilization
        updateCpuUitlization();
    }

    // remove VM, update CPU
    private void removeCpu(VM vm){
        cpu_remain += vm.getCpu_configuration();
        cpu_used -= vm.getCpu_used();
        updateCpuUitlization();
    }

    // add VM, update MEM
    private void addMem(VM vm){
        mem_remain -= vm.getMem_configuration();
        mem_used += vm.getMem_used();
        updateMemUitlization();
    }

    // remove VM, update MEM
    private void removeMem(VM vm){
        mem_remain += vm.getMem_configuration();
        mem_used -= vm.getMem_used();
        updateMemUitlization();
    }

    // get cpu_remain
    public double getCpu_allocated(){
        return cpu_remain;
    }

    // get mem_remain
    public double getMem_allocated(){
        return mem_remain;
    }

    // check
    public double getCpu_used(){
        return cpu_used;
    }

    // check
    public double getMem_used(){
        return mem_used;
    }


    // This method is called by VM.
    // Whenever a new container is allocated to an hosted VM, this container bring new resource consumption
    // Therefore, we need to update the cpu_used and mem_used
    public void allocateNewContainer(double containerCpu, double containerMem){
//        System.out.println("Before allocate a container, cpu_used: " + cpu_used);
        cpu_used += containerCpu;
        mem_used += containerMem;
        updateCpuUitlization();
        updateMemUitlization();
//        System.out.println("After allocate a container, cpu_used: " + cpu_used);
    }

    // This method is called by VM after release a container.
    public void releaseOldContainer(double containerCpu, double containerMem){
        cpu_used -= containerCpu;
        mem_used -= containerMem;
        updateCpuUitlization();
        updateMemUitlization();
    }


    /**
     * Add VM
     * @param vm
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
        if(cpu_remain >= vm.getCpu_configuration() && mem_remain >= vm.getMem_configuration()) {
            int currentIndex = vmList.size();
            vmList.add(vm);
            addCpu(vm);
            addMem(vm);
            VMIDtoListIndex.put(vm.getID(), currentIndex);

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
        int vmIndex = (int) VMIDtoListIndex.get(vmID);
        VM vm = vmList.get(vmIndex);
        removeCpu(vm);
        removeMem(vm);

        // call the VM to detach
        vm.detach();
        vmList.remove(vmIndex);

        // Then, we update the VMIDtoIndex mapping
        Iterator entries = VMIDtoListIndex.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            Integer key = (Integer)entry.getKey();
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


    public void print(){
        System.out.println("PM ID: " + id + ", CPU: "+ cpu_used + ", Mem: " + mem_used +
                ", CPU remain: " + cpu_remain + ", Mem remain: " + mem_remain +
                ", CPU utilization: " + Math.round(cpu_utilization * 10000) / 10000.0 +
                ", Mem utilization: " + Math.round(mem_utilization * 10000) / 10000.0 +
                ", status: " + status + ", energy: " + Math.round(calEnergy() * 100) / 100.0);
//        for(VM vm:vmList) vm.print();
    }

    public double getCpu_configuration() {
        return cpu_configuration;
    }

    public double getMem_configuration() {
        return mem_configuration;
    }
}
