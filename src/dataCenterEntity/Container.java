package dataCenterEntity;

public class Container implements Holder {

    // id starts from 1
    private int id;
    private int os;
    private double cpuUsed;
    private double memUsed;
    private VM allocateTo;



    // initialize container
    public Container(double cpu, double mem, int os, int id) {
        this.cpuUsed = cpu;
        this.memUsed = mem;
        this.os = os;
        this.id = id;
    }

    // check
    public int getID() {
        return id;
    }

    // check
    public double getCpuUsed() {
        return cpuUsed;
    }

    // check
    public double getMemUsed() {
        return memUsed;
    }

    // check
    public int getOs() {
        return os;
    }

    public int getExtraInfo(){
        return getOs();
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
    public double getCpuConfiguration() {
        return getCpuUsed();
    }

    public double getMemConfiguration() {
        return getMemUsed();
    }

    public double getCpuRemain(){
        return getCpuUsed();
    }

    public double getMemRemain(){
        return getMemUsed();
    }

    // This method is only for bin, not for item!!
    public Integer getType(){
        System.out.println("Error: This should not be called!");
        return null;
    }

    public void print(){
        System.out.print("container ID: " + id + ", CPU: "+ cpuUsed + ", Mem: " + memUsed +
                        ", OS: " + os);
    }
}
