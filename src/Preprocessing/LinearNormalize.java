package Preprocessing;
import DataCenterEntity.*;

public class LinearNormalize implements Normalization {
    private double[] vmCpu;
    private double[] vmMem;

    // initialized with the VM Types
    public LinearNormalize(double[] vmCpu, double[] vmMem){
        this.vmCpu = vmCpu.clone();
        this.vmMem = vmMem.clone();
    }


    // normalize the cpu/mem based on the VM type
    public double[] normalize(double cpu, double mem, int vmType){
        double[] cpuMem = new double[2];
        cpuMem[0] = (cpu - 0) / (vmCpu[vmType] - 0);
        cpuMem[1] = (mem - 0) / (vmMem[vmType] - 0);
        return cpuMem;
    }
}
