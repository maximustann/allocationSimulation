package FitnessFunction;
import DataCenterEntity.DataCenter;
import Preprocessing.Normalization;


/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class DivMethod implements Fitness {
    private Normalization norm;

    public DivMethod(Normalization norm){
        this.norm = norm;
    }


    // Preprocessing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
                    DataCenter dataCenter,
                    double binCPUremain,
                    double binMEMremain,
                    double itemCPUrequire,
                    double itemMEMrequire,
                    Double optional,
                    Integer type){
        double[] cpuMem;
        double div = 0;

        // calculate the left resources
        double cpu = binCPUremain - itemCPUrequire;
        double mem = binMEMremain - itemMEMrequire;

        cpuMem = norm.normalize(cpu, mem, type);

        // If any of the two resources is used up, return the highest score
        if(cpuMem[0] == 0 || cpuMem[1] == 0)
            return 1;


        // core, 1 is the highest score
        if(cpuMem[0] >= cpuMem[1])
            div = cpuMem[1] / cpuMem[0];
        else
            div = cpuMem[0] / cpuMem[1];
        return div;
    }
}
