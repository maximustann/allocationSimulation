package DataCenterEntity;

public class Container implements Holder {
    // id starts from 1
    private int id;
    private int os;
    private double cpu;
    private double mem;
    private VM allocateTo;



    public Container(double cpu, double mem, int os, int id) {
        this.cpu = cpu;
        this.mem = mem;
        this.os = os;
        this.id = id;
    }

    public int getNumber() {
        return id;
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

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }


    public VM getAllocateTo() {
        return allocateTo;
    }

    public void setAllocateTo(VM allocateTo) {
        this.allocateTo = allocateTo;
    }

    public double getCpu_configuration() {
        return getCpu();
    }

    public double getMem_configuration() {
        return getMem();
    }

    public void print(){
        System.out.print("container ID: " + id + ", CPU: "+ cpu + ", Mem: " + mem +
                        ", OS: " + os + ", allocated to: " + allocateTo.getID());
    }
}
