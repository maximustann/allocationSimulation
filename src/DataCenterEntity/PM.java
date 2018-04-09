package DataCenterEntity;

import java.util.*;

public class PM implements Holder {
    static double CLOSED = 0;
    static double OPEN = 1;

    private int number;
    private double cpu_configuration;
    private double mem_configuration;
    private double cpu;
    private double mem;
    private double cpu_utilization;
    private double mem_utilization;
    private double status;
    private double k;
    private double maxEnergy;
    private ArrayList<VM> vmList;
    private double energyConsumption;

    public PM(double cpu, double mem, double status, int number, double k, double maxEnergy) {
        cpu_configuration = this.cpu = cpu;
        mem_configuration = this.mem = mem;
        this.status = status;
        this.number = number;
        this.k = k;
        this.maxEnergy = maxEnergy;
    }

    public double calEnergy(){
        energyConsumption = k * maxEnergy + (1 - k) * cpu_configuration * maxEnergy;
        return energyConsumption;
    }

    private void updateCpuUitlization(){
        cpu_utilization = (cpu_configuration - cpu) / cpu_configuration;
    }

    private void updateMemUitlization(){
        mem_utilization = (mem_configuration - mem) / mem_configuration;
    }

    public void addCpu(VM vm){
        cpu -= vm.getCpu();
        updateCpuUitlization();
    }

    public void removeCpu(VM vm){
        cpu += vm.getCpu();
        updateCpuUitlization();
    }

    public void addMem(VM vm){
        mem -= vm.getMem();
        updateMemUitlization();
    }

    public void removeMem(VM vm){
        mem += vm.getMem();
        updateMemUitlization();
    }


    public ArrayList<VM> addVM(VM vm){
        if(cpu >= vm.getCpu_configuration() && mem >= vm.getMem_configuration()) {
            vmList.add(vm);
            addCpu(vm);
            addMem(vm);
            if(vmList.size() == 1)
                status = OPEN;
        } else {
            System.out.println("ERROR: VM allocation failed, insufficient resources");
        }
        return vmList;
    }
    public ArrayList<VM> removeVM(double vmNum){
        for(int i = 0; i < vmList.size(); i++){
            if(vmList.get(i).getNumber() == vmNum){
                vmList.remove(i);
                removeCpu(vmList.get(i));
                removeMem(vmList.get(i));
                if(vmList.size() == 0)
                    status = CLOSED;
                break;
            }
        }
        return vmList;
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

    public double getStatus() {
        return status;
    }

    public void setStatus(double status) {
        this.status = status;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public void print(){
        System.out.print("VM No: " + number + ", CPU: "+ cpu + ", Mem: " +
                ", status: " + status + ", energy: " + calEnergy());
    }

    public double getCpu_configuration() {
        return cpu_configuration;
    }

    public double getMem_configuration() {
        return mem_configuration;
    }
}
