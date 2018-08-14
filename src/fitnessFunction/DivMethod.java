package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import preprocessing.Normalization;


/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class DivMethod implements SelectionFitness {
    private Normalization norm;

    public DivMethod(Normalization norm){
        this.norm = norm;
    }


    // Pre-processing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
                    DataCenterInterface dataCenter,
                    double binCpuRemain,
                    double binMemRemain,
                    double itemCpuRequire,
                    double itemMemRequire){
        double[] cpuMem;
        double div;

        // calculate the left resources
        double cpu = binCpuRemain - itemCpuRequire;
        double mem = binMemRemain - itemMemRequire;

        cpuMem = norm.normalize(cpu, mem);

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
