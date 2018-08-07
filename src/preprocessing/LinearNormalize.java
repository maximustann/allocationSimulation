package preprocessing;

public class LinearNormalize implements Normalization {
    private double pmCpu;
    private double pmMem;

    // initialized with the VM Types
    public LinearNormalize(double pmCpu, double pmMem){
        this.pmCpu = pmCpu;
        this.pmMem = pmMem;
    }


    // normalize the cpu/mem based on the VM type
    public double[] normalize(double cpu, double mem){
        double[] cpuMem = new double[2];
        // If it is VM allocate to PM, use the PM configuration to normalize
        cpuMem[0] = cpu / pmCpu;
        cpuMem[1] = mem / pmMem;

        return cpuMem;
    }
}
