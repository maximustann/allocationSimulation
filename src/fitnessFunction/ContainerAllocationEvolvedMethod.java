 
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
public class ContainerAllocationEvolvedMethod implements SelectionFitness {
    private Normalization norm;
    private String expression;

    public ContainerAllocationEvolvedMethod(Normalization norm, String expression) {
        this.norm = norm;
        this.expression = expression;
    }

    @Override
    public double evaluate(DataCenterInterface dataCenter,
                           double binCpuRemain,
                           double binMemRemain,
                           double binActualCpuUsed,
                           double binActualMemUsed,
                           double itemCpuUsed,
                           double itemMemUsed,
                           double itemActualCpuUsed,
                           double itemActualMemUsed) {
//        return evaluate(dataCenter, binCpuRemain, binMemRemain, itemCpuUsed, itemMemUsed);
        return 0;
    }

    // Pre-processing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
            DataCenterInterface dataCenter,
            double binCpuConfiguration,
            double binMemOverhead,
            double binCpuOverheadRate,
            double binCpuRemain,
            double binMemRemain,
            double itemCpuRequire,
            double itemMemRequire) {
        double[] normalizedBin;
        double[] normalizedItem;
        double[] normalizedOverhead;
        double value = 0;


        // We use PM to normalize both, therefore, the type is null.
        normalizedBin = norm.normalize(binCpuRemain, binMemRemain);
        normalizedItem = norm.normalize(itemCpuRequire, itemMemRequire);
        normalizedOverhead = norm.normalize(binCpuConfiguration * binCpuOverheadRate, binMemOverhead);

        double leftVmCpu = normalizedBin[0] - normalizedItem[0];
        double leftVmMem = normalizedBin[1] - normalizedItem[1];
        double normalizedItemCpu = normalizedItem[0];
        double normalizedItemMem = normalizedItem[1];
        double normalizedVmCpuOverhead = normalizedOverhead[0];
        double normalizedVmMemOverhead = normalizedOverhead[1];


//        double coCpuDivPmMem = normalizedItemCpu / leftVmMem;

        Parser parser = new Parser();

        try {
            ExpressionNode expr = parser.parse(expression);
            expr.accept(new SetVariable("normalizedItemMem", normalizedItemMem));
            expr.accept(new SetVariable("normalizedItemCpu", normalizedItemCpu));
            expr.accept(new SetVariable("leftVmMem", leftVmMem));
            expr.accept(new SetVariable("leftVmCpu", leftVmCpu));
            expr.accept(new SetVariable("normalizedVmCpuOverhead", normalizedVmCpuOverhead));
            expr.accept(new SetVariable("normalizedVmMemOverhead", normalizedVmMemOverhead));
//            expr.accept(new SetVariable("CoCPUdivPmMem", coCpuDivPmMem));
            value = expr.getValue();

        } catch (ParserException e) {
            System.out.println(e.getMessage());
        } catch (EvaluationException e) {
            System.out.println(e.getMessage());
        }

        return value;
    }
}

