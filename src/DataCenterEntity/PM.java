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
    private double cpu_allocated;
    private double mem_allocated;

    // used is the acutual used of resources by containers
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
        cpu_configuration = cpu;
        mem_configuration = mem;
        cpu_used = 0;
        mem_used = 0;
        cpu_allocated = cpu;
        mem_allocated = mem;

        vmList = new ArrayList<>();
        VMIDtoListIndex = new HashMap();
        this.status = status;
        this.k = k;
        this.maxEnergy = maxEnergy;
        pmCounter += 1;
        id = pmCounter;
    }

    public double calEnergy(){
        energyConsumption = k * maxEnergy + (1 - k) * cpu_utilization * maxEnergy;
        return energyConsumption;
    }

    private void updateCpuUitlization(){
        cpu_utilization = cpu_used / cpu_configuration;
    }

    private void updateMemUitlization(){
        mem_utilization = mem_used / mem_configuration;
    }

    private void addCpu(VM vm){
        cpu_allocated -= vm.getCpu_configuration();
        cpu_used += vm.getCpu_allocated();
        updateCpuUitlization();
    }

    private void removeCpu(VM vm){
        cpu_allocated += vm.getCpu_configuration();
        cpu_used -= vm.getCpu_allocated();
        updateCpuUitlization();
    }

    private void addMem(VM vm){
        mem_allocated -= vm.getMem_configuration();
        mem_used += vm.getMem_allocated();
        updateMemUitlization();
    }

    private void removeMem(VM vm){
        mem_allocated += vm.getMem_configuration();
        mem_used -= vm.getMem_allocated();
        updateMemUitlization();
    }


    public double getCpu_allocated(){
        return cpu_allocated;
    }

    public double getMem_allocated(){
        return mem_allocated;
    }

    public double getCpu_used(){
        return cpu_used;
    }
    public double getMem_used(){
        return mem_used;
    }


    // This method is called by VM.
    // Whenever a new container is allocated to an hosted VM, this container bring new resource consumption
    // Therefore, we need to update the cpu_used and mem_used
    public void allocateNewContainer(double containerCpu, double containerMem){
        cpu_used += containerCpu;
        mem_used += containerMem;
    }

    // This method is called by VM after release a container.
    public void releaseOldContainer(double containerCpu, double containerMem){
        cpu_used -= containerCpu;
        mem_used -= containerMem;
    }


    public ArrayList<VM> addVM(VM vm){
        // Check if this PM has enough cpu and memory to allocate to this VM
        // If yes, add this VM to the vmList, update utilization
        // And update the mapping between VM ID and vmList index
        if(cpu_allocated >= vm.getCpu_configuration() && mem_allocated >= vm.getMem_configuration()) {
            int currentIndex = vmList.size();
            vmList.add(vm);
            addCpu(vm);
            addMem(vm);
            VMIDtoListIndex.put(vm.getID(), currentIndex);

            if(vmList.size() == 1)
                status = OPEN;


            // If there is not enough resources
        } else {
            System.out.println("ERROR: VM allocation failed, insufficient resources");
        }
        return vmList;
    }


    // We first remove the vm from vmList
    public ArrayList<VM> removeVM(int vmID){
        int vmIndex = (int) VMIDtoListIndex.get(vmID);
        removeCpu(vmList.get(vmIndex));
        removeMem(vmList.get(vmIndex));
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
                ", status: " + status + ", energy: " + calEnergy());
    }

    public double getCpu_configuration() {
        return cpu_configuration;
    }

    public double getMem_configuration() {
        return mem_configuration;
    }
}
