 
package fitnessFunction;
import dataCenterEntity.DataCenterInterface;
import expressionParser.*;
import preprocessing.Normalization;

/**
 *
 * The essential idea for sub method, is trying to minimize the difference between two resources.
 * That is, the more balance between two resources after allocation, the better the choice.
 *
 */
public class VmAllocationEvolvedMethod implements SelectionFitness {
    private Normalization norm;
    private String expression;

    public VmAllocationEvolvedMethod(Normalization norm, String expression) {
        this.norm = norm;
        this.expression = expression;
    }
    public double evaluate(DataCenterInterface dataCenter,
                           double binCpuConfiguration,
                           double binMemOverhead,
                           double binCpuOverheadRate,
                           double binCpuRemain,
                           double binMemRemain,
                           double itemCpuRequire,
                           double itemMemRequire){
        System.out.println("Wrong method calling, You should use the evaluate for pm selection");
        return 0;
    }


    // Pre-processing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
            DataCenterInterface dataCenter,
            double binCpuRemain,
            double binMemRemain,
            double binActualCpuUsed,
            double binActualMemUsed,
            double itemCpuRequire,
            double itemMemRequire,
            double itemActualCpuUsed,
            double itemActualMemUsed) {


        double[] normalizedBinRemain;
        double[] normalizedBinActualUsed;
        double[] normalizedItemRequired;
        double[] normalizedItemActualUsed;
        double value = 0;


        // We use PM to normalize both, therefore, the type is null.
        normalizedBinRemain = norm.normalize(binCpuRemain, binMemRemain);
        normalizedItemRequired = norm.normalize(itemCpuRequire, itemMemRequire);

        normalizedBinActualUsed = norm.normalize(binActualCpuUsed, binActualMemUsed);
        normalizedItemActualUsed = norm.normalize(itemActualCpuUsed, itemActualMemUsed);



//        double leftVmCpu = normalizedBinRemain[0] - normalizedItemRequired[0];
//        double leftVmMem = normalizedBinRemain[1] - normalizedItemRequired[1];


        double normalizedItemCpuRequired = normalizedItemRequired[0];
        double normalizedItemMemRequired = normalizedItemRequired[1];

        double normalizedBinCpuRemain = normalizedBinRemain[0] - normalizedItemCpuRequired;
        double normalizedBinMemRemain = normalizedBinRemain[1] - normalizedItemMemRequired;

        double normalizedItemActualCpuUsed = normalizedItemActualUsed[0];
        double normalizedItemActualMemUsed = normalizedItemActualUsed[1];

        double normalizedBinActualCpuUsed = normalizedBinActualUsed[0];
        double normalizedBinActualMemUsed = normalizedBinActualUsed[1];

//        double coCpuDivPmMem = normalizedItemCpu / leftVmMem;

        Parser parser = new Parser();

        try {
            ExpressionNode expr = parser.parse(expression);
            expr.accept(new SetVariable("vmMem", normalizedItemMemRequired));
            expr.accept(new SetVariable("vmCpu", normalizedItemCpuRequired));
            expr.accept(new SetVariable("vmUsedCpu", normalizedItemActualCpuUsed));
            expr.accept(new SetVariable("vmUsedMem", normalizedItemActualMemUsed));
            expr.accept(new SetVariable("pmCpu", normalizedBinCpuRemain));
            expr.accept(new SetVariable("pmMem", normalizedBinMemRemain));
            expr.accept(new SetVariable("pmUsedCpu", normalizedBinActualCpuUsed));
            expr.accept(new SetVariable("pmUsedMem", normalizedBinActualMemUsed));
            value = expr.getValue();

        } catch (ParserException e) {
            System.out.println(e.getMessage());
        } catch (EvaluationException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }
}

