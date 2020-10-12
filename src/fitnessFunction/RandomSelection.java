package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import preprocessing.Normalization;

/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class RandomSelection implements SelectionFitness {
    private Normalization norm;

    public RandomSelection(Normalization norm){
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



        // core
        return Math.random();
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



        // core
        return Math.random();
    }

}
