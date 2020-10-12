package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import preprocessing.Normalization;


/**
 * Energy Aware method, is essentially equivalent to cpu descending method.
 * In other words, the less cpu use the better
 */
public class EnergyAware implements SelectionFitness {
    private Normalization norm;

    public EnergyAware(Normalization norm){
        this.norm = norm;
    }


    // Preprocessing on the multi-dimensional resources transform multiple resources into one scalar
    // Used for VM selection
    public double evaluate(
                    DataCenterInterface dataCenter,
                    double binCpuConfiguration,
                    double binMemOverhead,
                    double binCpuOverheadRate,
                    double binCpuRemain,
                    double binMemRemain,
                    double itemCpuRequire,
                    double itemMemRequire){

        // calculate the left resources
        double cpu = binCpuRemain;

        return cpu;
    }

    // Used for PM selection
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

        // calculate the left resources
        double cpu = binCpuRemain;

        return cpu;
    }



}
