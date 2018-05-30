package FitnessFunction;
import Preprocessing.Normalization;


/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class SubMethod implements Fitness {
    private Normalization norm;

    public SubMethod(Normalization norm){
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
        double sub = 0;

        // calculate the left resources
        double cpu = binCPUremain - itemCPUrequire;
        double mem = binMEMremain - itemMEMrequire;

        cpuMem = norm.normalize(cpu, mem, type);


        // core
        if(cpuMem[0] >= cpuMem[1])
            sub = cpuMem[0] - cpuMem[1];
        else
            sub = cpuMem[1] - cpuMem[0];
        return sub;
    }
}
