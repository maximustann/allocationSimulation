package DataCenterEntity;

import java.util.*;

public class VM implements Holder {


    // This is a global variable. It will be incremented by 1 whenever a new VM is created
    // This variable is useful because it prevents duplicated vmID
    static int vmCounter = 0;

    // We fix the overhead of CPU and MEM according to Mann's paper
    public static double CPU_OVERHEAD_RATE = 0.1;
    public static double MEM_OVERHEAD = 200;


    /**
     * @param id is the serial number of VM, id starts from 1
     * @param cpu_configuration is the cpu of a certain type of VM
     * @param mem_configuration is the mem of a certain type of VM
     * @param cpu_used is the cpu that has been used. Include all the containers and VM overhead.
     * @param mem_used is the cpu that has been used. Include all the containers and VM overhead.
     * @param cpu_remain = cpu_configuration - cpu_used
     * @param mem_remain = mem_configuration - mem_used
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
    private double cpu_used;
    private double mem_used;
    private double cpu_remain;
    private double mem_remain;
    private double cpu_utilization;
    private double mem_utilization;
    private int type;
    private PM allocateTo;
    private ArrayList<Container> containerList;
    private HashMap containerIDtoIndex;



    public VM(double cpu, double mem, int type) {
        // initialize cpu and memory configuration
        this.cpu_configuration = cpu;
        this.mem_configuration = mem;

        // cpu and mem are used from the creation of the VM
        cpu_used = cpu_configuration * CPU_OVERHEAD_RATE;
        mem_used = MEM_OVERHEAD;
        cpu_remain = cpu_configuration - cpu_used;
        mem_remain = mem_configuration - mem_used;

        // utilization
        cpu_utilization = cpu_used / cpu_configuration;
        mem_utilization = mem_used / mem_configuration;

        // create the list and mapping for containers
        containerList = new ArrayList<>();
        containerIDtoIndex = new HashMap();
        this.type = type;

        // the global vm counter increment by 1
        vmCounter += 1;
        id = vmCounter;
    }

    /**
     * calculate the current cpu utilization.
     */
    private void updateCpuUtilization(){
        cpu_utilization = cpu_used / cpu_configuration;
    }

    /**
     * calculate the current mem utilization.
     */
    private void updateMemUtilization(){
        mem_utilization = mem_used / mem_configuration;
    }

    public double getCpu_configuration() {
        return cpu_configuration;
    }

    public double getMem_configuration() {
        return mem_configuration;
    }

    // First update, then return the utilization
    public double getCpu_utilization() {
        updateCpuUtilization();
        return cpu_utilization;
    }

    // First update, then return the utilization
    public double getMem_utilization() {
        updateMemUtilization();
        return mem_utilization;
    }


    /**
     * @param container
     * calculate the remained cpu as well as update the cpu utilization
     */
    private void addCpu(Container container){
        cpu_remain -= container.getCpu_used();
        cpu_used += container.getCpu_used();
        updateCpuUtilization();
    }

    private void removeCpu(Container container){
        cpu_remain += container.getCpu_used();
        cpu_used -= container.getCpu_used();
        updateCpuUtilization();
    }


    /**
     * @param container
     * calculate the remained mem as well as update the mem utilization
     */
    private void addMem(Container container){
        mem_remain -= container.getMem_used();
        mem_used += container.getMem_used();
        updateMemUtilization();
    }

    private void removeMem(Container container){
        mem_remain += container.getMem_used();
        mem_used -= container.getMem_used();
        updateMemUtilization();
    }

    // This method is called by the host PM
    public void setAllocateTo(PM pm){
        allocateTo = pm;
    }


    /**
     * @param container The container that wants to deploy in this VM
     * @return a list of containers
     *
     */
    public ArrayList<Container> addContainer(Container container){
//        System.out.println("OS: " + os);
        // First, we check if the VM has installed an OS
        // If there is no existing OS, then further check the remain CPU and Mem
        // If there is no OS, that means this VM has not been allocated to a PM yet.
        if(os == 0) {
            if (cpu_remain >= container.getCpu_used() && mem_remain >= container.getMem_used()) {
                containerList.add(container);
                addCpu(container);
                addMem(container);

                // call container to init allocateTo
                container.setAllocateTo(this);

                // update mapping
                int currentIndex = containerList.size();
                containerIDtoIndex.put(container.getID(), currentIndex);

                // setup OS
                os = container.getOs();
//                System.out.println("After OS: " + os);
            } else {
                System.out.println("ERROR: container allocation failed, insufficient resources");
            }
            // There is an existing OS.
        } else {
            // Check if the OS is compatiable with the container
            if(os == container.getOs()){

                // If there is enough resources. Allocate this container.
                // If there is an OS, that means this VM has been allocated to a PM.
                if(cpu_remain >= container.getCpu_used() && mem_remain >= container.getMem_used()){
                    containerList.add(container);
                    addCpu(container);
                    addMem(container);

                    // call container to init allocateTo
                    container.setAllocateTo(this);

                    // call PM to update its utilization
//                    System.out.println("Call host PM ID：" + allocateTo.getID());
                    allocateTo.allocateNewContainer(container.getCpu_used(), container.getMem_used());

                    // update mapping
                    int currentIndex = containerList.size();
                    containerIDtoIndex.put(container.getID(), currentIndex);
                }
                // Else, the OS is not compatible with the VM
            } else {
                System.out.println("ERROR: OS is not compatiable");
                return null;
            }
        }
        return containerList;
    }


    public ArrayList<Container> removeContainer(double containerID){
        int containerIndex = (int) containerIDtoIndex.get(containerID);
        Container container = containerList.get(containerIndex);
        removeCpu(container);
        removeMem(container);

        // call container to detach
        container.detach();

        // call the host PM to update its utilization
        allocateTo.releaseOldContainer(container.getCpu_used(), container.getMem_used());
        containerList.remove(containerIndex);

        // Then, we update the mapping: All the containers behind the left container will need to be adjusted.
        // Their index in the containerList will decrease by 1.
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

    // get cpu_used
    public double getCpu_used() {
        return cpu_used;
    }

    // get mem_used
    public double getMem_used() {
        return mem_used;
    }

    // set the allocatedTo to null
    public void detach(){
        setAllocateTo(null);
    }



    // get cpu remained
    public double getCpu_remain() {
        return cpu_remain;
    }

    // get mem remained
    public double getMem_remain() {
        return mem_remain;
    }

    // get type
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    // get OS
    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    // get the PM that allocated to
    public PM getAllocateTo() {
        return allocateTo;
    }

    public void print(){
        System.out.println("VM ID: " + id + ", CPU: "+ cpu_used + ", Mem: " + mem_used +
                ", OS: " + os);
    }

    public int getID() {
        return id;
    }

    public static void resetCounter(){
        vmCounter = 0;
    }
}
