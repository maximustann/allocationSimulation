package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import preprocessing.Normalization;


/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class VolumeMethod implements SelectionFitness {
    private Normalization norm;

    public VolumeMethod(Normalization norm){
        this.norm = norm;
    }


    // Preprocessing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
                    DataCenterInterface dataCenter,
                    double binCpuConfiguration,
                    double binMemOverhead,
                    double binCpuOverheadRate,
                    double binCpuRemain,
                    double binMemRemain,
                    double itemCpuRequire,
                    double itemMemRequire){
        double[] cpuMem;
        double volume;

        double cpu = binCpuRemain;
        double mem = binMemRemain;

        cpuMem = norm.normalize(cpu, mem);


        // core
        volume = (1 / (1 - cpu)) * (1 / (1 - mem));

        return volume;
    }

    // Used for PM Selection
    public double evaluate(
            DataCenterInterface dataCenter,
            double binCpuRemain,
            double binMemRemain,
            double binActualCpuUsed,
            double binActualMemUsed,
            double itemCpuRequire,
            double itemMemRequire,
            double itemActualCpuUsed,
            double itemActualMemUsed){
        double[] cpuMem;
        double volume;

        double cpu = binCpuRemain;
        double mem = binMemRemain;

        cpuMem = norm.normalize(cpu, mem);


        // core
        volume = (1 / (1 - cpu)) * (1 / (1 - mem));

        return volume;
    }

}
