package DataCenterEntity;

public class Container implements Holder {
    private int number;
    private double cpu;
    private double mem;
    private double os;
    private VM allocateTo;



    public Container(double cpu, double mem, double os, int number) {
        this.cpu = cpu;
        this.mem = mem;
        this.os = os;
        this.number = number;
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

    public double getOs() {
        return os;
    }

    public void setOs(double os) {
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
        System.out.print("container No: " + number + ", CPU: "+ cpu + ", Mem: " + mem +
                        ", OS: " + os + ", allocated to: " + allocateTo.getNumber());
    }
}
