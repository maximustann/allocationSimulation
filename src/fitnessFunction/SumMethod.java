package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import preprocessing.Normalization;


/**
 * In sum method, all the dimension will be normalized based on VM's resources.
 * Then, they will be summed up.
 *
 *
 * The essential idea of sum method, is trying to minimize the sum of left resources
 * That is, the less resources left after allocation, the better the choice
 *
 */
public class SumMethod implements Fitness {
    private Normalization norm;

    public SumMethod(Normalization norm){
        this.norm = norm;
    }


    // Preprocessing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
                        DataCenterInterface dataCenter,
                        double binCpuRemain,
                        double binMemRemain,
                        double itemCpuRequire,
                        double itemMemRequire){
        double[] cpuMem;
        double sum;
        // calculate the left resources
        double cpu = binCpuRemain - itemCpuRequire;
        double mem = binMemRemain - itemMemRequire;

        cpuMem = norm.normalize(cpu, mem);


        // core
        sum = cpuMem[0] + cpuMem[1];
        return sum;
    }
}
