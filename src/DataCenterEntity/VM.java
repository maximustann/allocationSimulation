package DataCenterEntity;

import java.util.*;

public class VM implements Holder {
    /**
     * @param number is the serial number of VM
     * @param cpu_configuration is the cpu of a certain type of VM
     * @param mem_configuration is the mem of a certain type of VM
     * @param cpu is the remained cpu of a VM
     * @param mem is the remained mem of a VM
     * @param cpu_utilization is the cpu utilization of the current VM
     * @param mem_utilization is the mem utilization of the current VM
     * @param type is the type of VM
     * @param os is the configured OS of VM
     * @param allocateTo is the PM that this VM allocated to.
     * @param containerList contains all the containers that allocated to this VM.
     */

    private int number;
    private double cpu_configuration;
    private double mem_configuration;
    private double cpu;
    private double mem;
    private double cpu_utilization;
    private double mem_utilization;
    private double type;
    private double os;
    private PM allocateTo;
    private ArrayList<Container> containerList;



    public VM(double cpu, double mem, double type, int number) {
        this.cpu_configuration = this.cpu = cpu;
        this.mem_configuration = this.mem = mem;
        this.type = type;
        this.number = number;
        cpu_utilization = 0;
        mem_utilization = 0;
    }

    /**
     * calculate the current cpu utilization.
     * current_cpu_usage = cpu_configuration - cpu
     */
    private void updateCpuUtilization(){
        cpu_utilization = (cpu_configuration - cpu) / cpu_configuration;
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
     * calculate the current mem utilization.
     * current_mem_usage = mem_configuration - mem
     */
    private void updateMemUtilization(){
        mem_utilization = (mem_configuration - mem) / mem_configuration;
    }

    /**
     * @param container
     * calculate the remained cpu as well as update the cpu utilization
     */
    public void addCpu(Container container){
        cpu -= container.getCpu();
        updateCpuUtilization();
    }

    public void removeCpu(Container container){
        cpu += container.getCpu();
        updateCpuUtilization();
    }


    /**
     * @param container
     * calculate the remained mem as well as update the mem utilization
     */

    public void addMem(Container container){
        mem -= container.getMem();
        updateMemUtilization();
    }

    public void removeMem(Container container){
        mem += container.getMem();
        updateMemUtilization();
    }


    /**
     * @param container The container that wants to deploy in this VM
     * @return a list of containers
     *
     * First, we check if this VM has not been assigned an OS
     *
     *
     */
    public ArrayList<Container> addContainer(Container container){
        if(os == 0) {
            if (cpu >= container.getCpu() && mem >= container.getMem()) {
                containerList.add(container);
                addCpu(container);
                addMem(container);
                os = container.getOs();
            } else {
                System.out.println("ERROR: container allocation failed, insufficient resources");
            }
        } else {
            System.out.println("ERROR: container allocation failed, OS does not match");
        }
        return containerList;
    }

    public ArrayList<Container> removeContainer(double num){
        for(int i = 0; i < containerList.size(); i++){
            if(containerList.get(i).getNumber() == num){
                removeCpu(containerList.get(i));
                removeMem(containerList.get(i));
                containerList.remove(i);
                break;
            }
        }
        return containerList;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public double getMem() {
        return mem;
    }

    public void setMem(double mem) {
        this.mem = mem;
    }

    public double getType() {
        return type;
    }

    public void setType(double type) {
        this.type = type;
    }

    public double getOs() {
        return os;
    }

    public void setOs(double os) {
        this.os = os;
    }

    public PM getAllocateTo() {
        return allocateTo;
    }

    public void setAllocateTo(PM allocateTo) {
        this.allocateTo = allocateTo;
    }

    public void print(){
        System.out.print("VM No: " + number + ", CPU: "+ cpu + ", Mem: " + mem +
                ", OS: " + os + ", allocated to: " + allocateTo.getNumber());
    }
}
