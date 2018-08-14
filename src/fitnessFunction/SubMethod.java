package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import preprocessing.Normalization;


/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class SubMethod implements SelectionFitness {
    private Normalization norm;

    public SubMethod(Normalization norm){
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
        double sub;

        // calculate the left resources
        double cpu = binCpuRemain - itemCpuRequire;
        double mem = binMemRemain - itemMemRequire;

        cpuMem = norm.normalize(cpu, mem);


        // core
        if(cpuMem[0] >= cpuMem[1])
            sub = cpuMem[0] - cpuMem[1];
        else
            sub = cpuMem[1] - cpuMem[0];
        return sub;
    }
}
