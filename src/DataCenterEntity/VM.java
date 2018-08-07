package DataCenterEntity;

import javax.sound.midi.Soundbank;
import java.util.*;

public class VM implements Holder {


    // This is a global variable. It will be incremented by 1 whenever a new VM is created
    // This variable is useful because it prevents duplicated vmID
    private static int vmCounter = 0;

    //save point for vmCounter
    private static int savePointVmCounter = 0;

    // We fix the overhead of CPU and MEM according to Mann's paper
    public static double CPU_OVERHEAD_RATE = 0.1;
    public static double MEM_OVERHEAD = 200;


    /**
     * id is the serial number of VM, id starts from 1
     * cpuConfiguration is the cpu of a certain type of VM
     * memConfiguration is the mem of a certain type of VM
     * cpuUsed is the cpu that has been used. Include all the containers and VM overhead.
     * memUsed is the cpu that has been used. Include all the containers and VM overhead.
     * cpuRemain = cpu_configuration - cpu_used
     * memRemain = mem_configuration - mem_used
     * cpuUtilization is the cpu utilization of the current VM
     * memUtilization is the mem utilization of the current VM
     * type is the type of VM, start from 0
     * os is the configured OS of VM, and the OS is initialized as 0 (No OS).
     * allocateTo is the PM that this VM allocated to.
     * containerList contains all the containers that allocated to this VM.
     */

    private int id;
    private int os = 0;
    private double cpuConfiguration;
    private double memConfiguration;
    private double cpuUsed;
    private double memUsed;
    private double cpuRemain;
    private double memRemain;
    private double cpuUtilization;
    private double memUtilization;
    private int type;
    private PM allocateTo;
    private ArrayList<Container> containerList;
    private HashMap containerIDtoIndex;



    public VM(double cpu, double mem, int type) {
        // initialize cpu and memory configuration
        this.cpuConfiguration = cpu;
        this.memConfiguration = mem;

        // cpu and mem are used from the beginning of the VM
        cpuUsed = cpuConfiguration * CPU_OVERHEAD_RATE;
        memUsed = MEM_OVERHEAD;
        cpuRemain = cpuConfiguration - cpuUsed;
        memRemain = memConfiguration - memUsed;

        // utilization
        cpuUtilization = cpuUsed / cpuConfiguration;
        memUtilization = memUsed / memConfiguration;

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
        cpuUtilization = cpuUsed / cpuConfiguration;
    }

    /**
     * calculate the current mem utilization.
     */
    private void updateMemUtilization(){
        memUtilization = memUsed / memConfiguration;
    }

    public double getCpuConfiguration() {
        return cpuConfiguration;
    }

    public double getMemConfiguration() {
        return memConfiguration;
    }

    public int getContainerNum(){
        return containerList.size();
    }

    public double getBalance(){
        double balance;
        if(cpuUtilization >= memUtilization)
            balance = memUtilization / cpuUtilization;
        else balance = cpuUtilization / memUtilization;
        return balance;
    }

    // First update, then return the utilization
    public double getCpuUtilization() {
        updateCpuUtilization();
        return cpuUtilization;
    }

    // First update, then return the utilization
    public double getMemUtilization() {
        updateMemUtilization();
        return memUtilization;
    }


    /**
     * @param container
     * calculate the remained cpu as well as update the cpu utilization
     */
    private void addCpu(Container container){
        cpuRemain -= container.getCpuUsed();
        cpuUsed += container.getCpuUsed();
        updateCpuUtilization();
    }

    private void removeCpu(Container container){
        cpuRemain += container.getCpuUsed();
        cpuUsed -= container.getCpuUsed();
        updateCpuUtilization();
    }


    /**
     * @param container
     * calculate the remained mem as well as update the mem utilization
     */
    private void addMem(Container container){
        memRemain -= container.getMemUsed();
        memUsed += container.getMemUsed();
        updateMemUtilization();
    }

    private void removeMem(Container container){
        memRemain += container.getMemUsed();
        memUsed -= container.getMemUsed();
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
            if (cpuRemain >= container.getCpuUsed() && memRemain >= container.getMemUsed()) {
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

                // call PM to update its utilization
                if(allocateTo != null) {
//                    System.out.println("Call host PM ID：" + allocateTo.getID());
                    allocateTo.allocateNewContainer(container.getCpuUsed(), container.getMemUsed());
                }

//                System.out.println("After OS: " + os);
            } else {
                System.out.println("ERROR: container allocation failed, insufficient resources");
                System.out.println("Container CPU = " + container.getCpuConfiguration() + ", " +
                        "Mem = " + container.getMemConfiguration());
            }
            // There is an existing OS.
        } else {
            // Check if the OS is compatiable with the container
            if(os == container.getOs()){

                // If there is enough resources. Allocate this container.
                // If there is an OS, that means this VM has been allocated to a PM.
                if(cpuRemain >= container.getCpuUsed() && memRemain >= container.getMemUsed()){
                    containerList.add(container);
                    addCpu(container);
                    addMem(container);

                    // call container to init allocateTo
                    container.setAllocateTo(this);

                    // call PM to update its utilization
//                    System.out.println("Call host PM ID：" + allocateTo.getID());
                    allocateTo.allocateNewContainer(container.getCpuUsed(), container.getMemUsed());

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
        allocateTo.releaseOldContainer(container.getCpuUsed(), container.getMemUsed());
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
    public double getCpuUsed() {
        return cpuUsed;
    }

    // get mem_used
    public double getMemUsed() {
        return memUsed;
    }

    // set the allocatedTo to null
    public void detach(){
        setAllocateTo(null);
    }



    // get cpu remained
    public double getCpuRemain() {
        return cpuRemain;
    }

    // get mem remained
    public double getMemRemain() {
        return memRemain;
    }

    // get type
    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    // get OS
    public int getOs() {
        return os;
    }

    // get ExtraInfor
    public int getExtraInfo(){
        return getOs();
    }

    public void setOs(int os) {
        this.os = os;
    }

    // get the PM that allocated to
    public PM getAllocateTo() {
        return allocateTo;
    }

    public void print(){
        System.out.println("VM ID: " + id + ", CPU_used: "+ cpuUsed + ", Mem_used: " + memUsed +
                ", OS: " + os + ", Balance = " + getBalance());
    }

    public int getID() {
        return id;
    }

    // make sure the id is consistent with the global counter!!!
    public void setID(int id) {
        this.id = id;
    }

    public static void resetCounter(){
        vmCounter = 0;
    }

    public static int getGlobalCounter(){
        return vmCounter;
    }
    public static void setGlobalCounter(int counter){
        vmCounter = counter;
    }
    public static void saveCounter(){
        savePointVmCounter = vmCounter;
    }

    public static void restoreCounter(){
        vmCounter = savePointVmCounter;
    }
}
