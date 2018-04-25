package Preprocessing;
import DataCenterEntity.*;

public class LinearNormalize implements Normalization {
    private double pmCpu;
    private double pmMem;

    public LinearNormalize(double pmCpu, double pmMem){
        this.pmCpu = pmCpu;
        this.pmMem = pmMem;
    }


    public double[] normalize(double cpu, double mem){
        double[] cpuMem = new double[2];
        cpuMem[0] = (cpu - 0) / (pmCpu - 0);
        cpuMem[1] = (mem - 0) / (pmMem - 0);
        return cpuMem;
    }
}
