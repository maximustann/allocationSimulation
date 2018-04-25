package DataCenterEntity;

public class Container implements Holder {

    // id starts from 1
    private int id;
    private int os;
    private double cpu_used;
    private double mem_used;
    private VM allocateTo;



    // initialize container
    public Container(double cpu, double mem, int os, int id) {
        this.cpu_used = cpu;
        this.mem_used = mem;
        this.os = os;
        this.id = id;
    }

    // check
    public int getID() {
        return id;
    }

    // check
    public double getCpu_used() {
        return cpu_used;
    }

    // check
    public double getMem_used() {
        return mem_used;
    }

    // check
    public int getOs() {
        return os;
    }

    // check
    public void setOs(int os) {
        this.os = os;
    }


    // check
    public VM getAllocateTo() {
        return allocateTo;
    }

    // check
    public void detach(){
        setAllocateTo(null);
    }

    // check
    public void setAllocateTo(VM vm) {
        this.allocateTo = vm;
    }

    // check
    public double getCpu_configuration() {
        return getCpu_used();
    }

    // check
    public double getMem_configuration() {
        return getMem_used();
    }

    // check
    public void print(){
        System.out.print("container ID: " + id + ", CPU: "+ cpu_used + ", Mem: " + mem_used +
                        ", OS: " + os);
    }
}
