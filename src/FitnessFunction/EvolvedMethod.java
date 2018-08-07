 
package FitnessFunction;
import DataCenterEntity.DataCenterInterface;
import Preprocessing.Normalization;

/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class EvolvedMethod implements Fitness {
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
        //
        double normalizedLeftVMBalance;
        double leftVMCpu = normalizedBin[0] - normalizedItem[0];
        double leftVMMem = normalizedBin[1] - normalizedItem[1];
        if (leftVMCpu > leftVMMem)
            normalizedLeftVMBalance = leftVMCpu / leftVMMem;
        else
            normalizedLeftVMBalance = leftVMMem / leftVMCpu;

    value =
(((normalizedItem[0] / leftVMCpu) + (normalizedItem[1] / leftVMMem)) + ((normalizedItem[1] / leftVMMem) + (normalizedItem[1] + (normalizedItem[1] / (leftVMMem + leftVMCpu))))) * ((normalizedItem[1] - leftVMCpu) - leftVMMem)
;return value;}}
