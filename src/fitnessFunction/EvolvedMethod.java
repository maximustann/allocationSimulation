 
package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import preprocessing.Normalization;

/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class EvolvedMethod implements SelectionFitness {
    private Normalization norm;

    public EvolvedMethod(Normalization norm){
        this.norm = norm;
    }


    // Pre-processing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
						DataCenterInterface dataCenter,
                        double binCpuRemain,
                        double binMemRemain,
                        double itemCpuRequire,
                        double itemMemRequire) {
        double[] normalizedBin;
        double[] normalizedItem;
        double value;


        // We use PM to normalize both, therefore, the type is null.
        normalizedBin = norm.normalize(binCpuRemain, binMemRemain);
        normalizedItem = norm.normalize(itemCpuRequire, itemMemRequire);

        double leftVmCpu = normalizedBin[0] - normalizedItem[0];
        double leftVmMem = normalizedBin[1] - normalizedItem[1];


return 0;}}
