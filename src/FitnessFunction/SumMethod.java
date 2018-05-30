package FitnessFunction;
import Preprocessing.Normalization;


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
                        double binCPUremain,
                        double binMEMremain,
                        double itemCPUrequire,
                        double itemMEMrequire,
                        Double optional,
                        Integer type){
        double[] cpuMem;
        double sum  = 0;
        // calculate the left resources
        double cpu = binCPUremain - itemCPUrequire;
        double mem = binMEMremain - itemMEMrequire;

        cpuMem = norm.normalize(cpu, mem, type);


        // core
        sum = cpuMem[0] + cpuMem[1];
        return sum;
    }
}
