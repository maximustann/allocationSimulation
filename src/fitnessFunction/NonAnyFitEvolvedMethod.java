
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
public class NonAnyFitEvolvedMethod implements SelectionCreationFitness {
    private Normalization norm;
    private String expression;

    public NonAnyFitEvolvedMethod(Normalization norm, String expression){
        this.norm = norm;
        this.expression = expression;
    }


    // Pre-processing on the multi-dimensional resources transform multiple resources into one scalar
    public double evaluate(
						DataCenterInterface dataCenter,
                        boolean creationFlag,
                        int vmType,
                        int osType,
                        double binCpuRemain,
                        double binMemRemain,
                        double itemCpuRequire,
                        double itemMemRequire) {
        double[] normalizedBin;
        double[] normalizedItem;
        double normalizedVmCpuOverhead;
        double normalizedVmMemOverhead;
        double value = 0;


        if (creationFlag) {
            normalizedVmCpuOverhead = dataCenter.getVmCpuOverhead(vmType) / dataCenter.getPmCpu();
            normalizedVmMemOverhead = dataCenter.getVmMemOverhead() / dataCenter.getPmMem();
        } else {
            normalizedVmCpuOverhead = 0;
            normalizedVmMemOverhead = 0;
        }

        // We use PM to normalize both, therefore, the type is null.
        normalizedBin = norm.normalize(binCpuRemain, binMemRemain);
        normalizedItem = norm.normalize(itemCpuRequire, itemMemRequire);

        double normalizedItemCpu = normalizedItem[0];
        double normalizedItemMem = normalizedItem[1];
        double leftVmCpu = normalizedBin[0] - normalizedItem[0];
        double leftVmMem = normalizedBin[1] - normalizedItem[1];
        double coOsProb = dataCenter.getOsProb()[osType];

        Parser parser = new Parser();

        try
        {
            ExpressionNode expr = parser.parse(expression);
            expr.accept(new SetVariable("normalizedItemMem", normalizedItemMem));
            expr.accept(new SetVariable("leftVmMem", leftVmMem));
            expr.accept(new SetVariable("leftVmCpu", leftVmCpu));
            expr.accept(new SetVariable("normalizedItemCpu", normalizedItemCpu));
            expr.accept(new SetVariable("normalizedVmCpuOverhead", normalizedVmCpuOverhead));
            expr.accept(new SetVariable("normalizedVmMemOverhead", normalizedVmMemOverhead));
            expr.accept(new SetVariable("coOsProb", coOsProb));
            value = expr.getValue();

        }
        catch (ParserException e)
        {
            System.out.println(e.getMessage());
        }
        catch (EvaluationException e)
        {
            System.out.println(e.getMessage());
        }

        return value;
    }
}