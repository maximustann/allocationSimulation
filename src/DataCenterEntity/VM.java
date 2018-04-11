package DataCenterEntity;

import java.util.*;

public class VM implements Holder {


    // This is a global variable. It will be incremented by 1 whenever a new VM is created
    // This variable is useful because it prevents duplicated vmID
    static int vmCounter = 0;


    /**
     * @param id is the serial number of VM, id starts from 1
     * @param cpu_configuration is the cpu of a certain type of VM
     * @param mem_configuration is the mem of a certain type of VM
     * @param cpu is the remained cpu of a VM
     * @param mem is the remained mem of a VM
     * @param cpu_utilization is the cpu utilization of the current VM
     * @param mem_utilization is the mem utilization of the current VM
     * @param type is the type of VM
     * @param os is the configured OS of VM, and the OS is initialized as 0 (No OS).
     * @param allocateTo is the PM that this VM allocated to.
     * @param containerList contains all the containers that allocated to this VM.
     */

    private int id;
    private int os = 0;
    private double cpu_configuration;
    private double mem_configuration;
    private double cpu_allocated;
    private double mem_allocated;
    private double cpu_remain;
    private double mem_remain;
    private double cpu_utilization;
    private double mem_utilization;
    private int type;
    private PM allocateTo;
    private ArrayList<Container> containerList;
    private HashMap containerIDtoIndex;



    public VM(double cpu, double mem, int type) {
        this.cpu_configuration = this.cpu_remain = cpu;
        this.mem_configuration = this.mem_remain = mem;
        cpu_allocated = 0;
        mem_allocated = 0;
        containerList = new ArrayList<>();
        containerIDtoIndex = new HashMap();
        this.type = type;
        cpu_utilization = 0;
        mem_utilization = 0;
        vmCounter += 1;
        id = vmCounter;
    }

    /**
     * calculate the current cpu utilization.
     */
    private void updateCpuUtilization(){
        cpu_utilization = cpu_allocated / cpu_configuration;
    }

    private void updateMemUtilization(){
        cpu_utilization = mem_allocated / cpu_configuration;
    }

    public double getCpu_configuration() {
        return cpu_configuration;
    }

    public double getMem_configuration() {
        return mem_configuration;
    }

    public double getCpu_utilization() {
        return cpu_utilization;
    }


    public double getMem_utilization() {
        return mem_utilization;
    }

    /**
     * @param container
     * calculate the remained cpu as well as update the cpu utilization
     */
    private void addCpu(Container container){
        cpu_remain -= container.getCpu();
        cpu_allocated += container.getCpu();
        updateCpuUtilization();
    }

    private void removeCpu(Container container){
        cpu_remain += container.getCpu();
        cpu_allocated -= container.getCpu();
        updateCpuUtilization();
    }


    /**
     * @param container
     * calculate the remained mem as well as update the mem utilization
     */

    private void addMem(Container container){
        mem_remain -= container.getMem();
        mem_allocated += container.getMem();
        updateMemUtilization();
    }

    private void removeMem(Container container){
        mem_remain += container.getMem();
        mem_allocated -= container.getMem();
        updateMemUtilization();
    }


    /**
     * @param container The container that wants to deploy in this VM
     * @return a list of containers
     *
     */
    public ArrayList<Container> addContainer(Container container){
        // First, we check if the VM has installed an OS
        // If there is no existing OS, then further check the remain CPU and Mem
        // If there is no OS, that means this VM has not been allocated to a PM yet.
        if(os == 0) {
            if (cpu_remain >= container.getCpu() && mem_remain >= container.getMem()) {
                containerList.add(container);
                addCpu(container);
                addMem(container);

                // update mapping
                int currentIndex = containerList.size();
                containerIDtoIndex.put(container.getID(), currentIndex);

                // setup OS
                os = container.getOs();
            } else {
                System.out.println("ERROR: container allocation failed, insufficient resources");
            }
            // There is an existing OS.
        } else {
            // Check if the OS is compatiable with the container
            if(os == container.getOs()){
                // If there is enough resources. Allocate this container.
                // If there is an OS, that means this VM has been allocated to a PM.
                if(cpu_remain >= container.getCpu() && mem_remain >= container.getMem()){
                    containerList.add(container);
                    addCpu(container);
                    addMem(container);

                    // call PM to update its utilization
                    allocateTo.allocateNewContainer(container.getCpu(), container.getMem());

                    // update mapping
                    int currentIndex = containerList.size();
                    containerIDtoIndex.put(container.getID(), currentIndex);
                }
                // Else, the OS is not compatible with the VM
            } else {
                System.out.println("ERROR: OS is not compatiable");
            }
        }
        return containerList;
    }

    public ArrayList<Container> removeContainer(double containerID){
        int containerIndex = (int) containerIDtoIndex.get(containerID);
        Container container = containerList.get(containerIndex);
        removeCpu(container);
        removeMem(container);
        // call the host PM to update its utilization
        allocateTo.releaseOldContainer(container.getCpu(), container.getMem());
        containerList.remove(containerIndex);

        // Then, we update the mapping
        Iterator entries = containerIDtoIndex.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            Integer key = (Integer)entry.getKey();
            Integer value = (Integer)entry.getValue();
            if(value > containerIndex){
                value -= 1;
                entry.setValue(value);
            }
        }
        return containerList;
    }

    public double getCpu_allocated() {
        return cpu_allocated;
    }


    public double getMem_allocated() {
        return mem_allocated;
    }

    public double getCpu_remain() {
        return cpu_remain;
    }

    public double getMem_remain() {
        return mem_remain;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    public PM getAllocateTo() {
        return allocateTo;
    }

    public void setAllocateTo(PM allocateTo) {
        this.allocateTo = allocateTo;
    }

    public void print(){
        System.out.print("VM ID: " + id + ", CPU: "+ cpu_allocated + ", Mem: " + mem_allocated +
                ", OS: " + os + ", allocated to: " + allocateTo.getID());
    }

    public int getID() {
        return id;
    }
}
