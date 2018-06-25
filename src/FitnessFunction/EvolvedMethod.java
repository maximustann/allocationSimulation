 
package FitnessFunction;
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


    // Preprocessing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
                        double binCPUremain,
                        double binMEMremain,
                        double itemCPUrequire,
                        double itemMEMrequire,
                        Double optional,
                        Integer type){
        double[] normalizedBin;
        double[] normalizedItem;
        double value = 0;


        // We use PM to normalize both, therefore, the type is null.
        normalizedBin = norm.normalize(binCPUremain, binMEMremain, null);
        normalizedItem = norm.normalize(itemCPUrequire, itemMEMrequire, null);
		// 
		double normalizedLeftVMBalance = 0.0;
		double leftVMCpu = normalizedBin[0] - normalizedItem[0];
		double leftVMMem = normalizedBin[1] - normalizedItem[1];
		if(leftVMCpu > leftVMMem)
			normalizedLeftVMBalance = leftVMCpu / leftVMMem;
		else
			normalizedLeftVMBalance = leftVMMem / leftVMCpu;
value = 
(((leftVMCpu - normalizedItem[0]) - normalizedItem[0]) * (normalizedLeftVMBalance * (((normalizedLeftVMBalance - leftVMMem) * normalizedLeftVMBalance) * ((normalizedLeftVMBalance - leftVMMem) * normalizedLeftVMBalance)))) - ((((leftVMCpu - normalizedItem[0]) - normalizedItem[0]) / (normalizedLeftVMBalance * ((normalizedLeftVMBalance - leftVMMem) - leftVMMem))) + leftVMCpu)
;return value;}}
