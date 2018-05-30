 
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
        
        
        
        
        
value = 
(((((normalizedItem[1] + normalizedItem[1]) + (normalizedItem[1] - normalizedItem[1])) + ((normalizedItem[1] / normalizedItem[0]) * (normalizedItem[1] + normalizedItem[1]))) - (((normalizedItem[0] - normalizedItem[0]) - (normalizedItem[1] - normalizedItem[1])) + ((normalizedItem[1] / normalizedItem[1]) - normalizedItem[0]))) + ((((normalizedItem[1] - normalizedItem[1]) * (normalizedItem[1] * normalizedItem[0])) + ((normalizedItem[0] - normalizedItem[1]) + (normalizedItem[0] - normalizedItem[1]))) + (((normalizedItem[0] - normalizedItem[1]) * (normalizedItem[1] - normalizedItem[1])) + ((normalizedItem[1] * normalizedItem[0]) - (normalizedItem[1] - normalizedItem[0]))))) * (((((normalizedItem[1] - normalizedItem[0]) + (normalizedBin[0] + normalizedItem[1])) + ((normalizedItem[1] * normalizedBin[0]) - (normalizedItem[1] + normalizedItem[0]))) / (((normalizedItem[1] - normalizedItem[1]) + (normalizedItem[0] - normalizedItem[1])) / ((normalizedItem[1] + normalizedItem[1]) - (normalizedItem[1] + normalizedItem[0])))) + ((((normalizedBin[0] - normalizedItem[0]) * (normalizedBin[0] / normalizedItem[0])) * ((normalizedItem[0] - normalizedItem[1]) * (normalizedItem[1] - normalizedItem[1]))) / ((normalizedItem[1] + normalizedItem[0]) - ((normalizedItem[1] * normalizedItem[0]) * (normalizedBin[0] + normalizedItem[1])))))
;
//        System.out.println(value);
return value;}}
