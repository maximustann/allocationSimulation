package Preprocessing;
import DataCenterEntity.*;


/**
 * In sum method, all the dimension will be normalized based on PM's resources.
 * Then, they will be summed up.
 */
public class SumMethod implements PreprocessingMethod {
    private Normalization norm;

    public SumMethod(Normalization norm){
        this.norm = norm;
    }


    // Preprocessing on the multi-dimensional resources transform multiple resources into one scalar
    public double tranform(double cpu, double mem){
        double[] cpuMem;
        double sum  = 0;
        cpuMem = norm.normalize(cpu, mem);
        sum = cpuMem[0] + cpuMem[1];
        return sum;
    }
}
