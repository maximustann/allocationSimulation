package Preprocessing;
import DataCenterEntity.*;

public class LinearNormalize implements Normalization {
    private double[] vmCpu;
    private double[] vmMem;
    private double pmCpu;
    private double pmMem;

    // initialized with the VM Types
    public LinearNormalize(double[] vmCpu, double[] vmMem, double pmCpu, double pmMem){
        this.vmCpu = vmCpu.clone();
        this.vmMem = vmMem.clone();
        this.pmCpu = pmCpu;
        this.pmMem = pmMem;
    }


    // normalize the cpu/mem based on the VM type
    public double[] normalize(double cpu, double mem, Integer vmType){
        double[] cpuMem = new double[2];
        // If it is VM allocate to PM, use the PM configuration to normalize
        if(vmType == null){
            cpuMem[0] = cpu / pmCpu;
            cpuMem[1] = mem / pmMem;

            // If it is container allocate to VM, use the VM configuration to normalize
        } else {
            cpuMem[0] = (cpu - 0) / (vmCpu[vmType] - 0);
            cpuMem[1] = (mem - 0) / (vmMem[vmType] - 0);
        }
        return cpuMem;
    }
}
