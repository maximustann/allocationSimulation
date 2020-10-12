package expressionParser;

public class Testing {
    public static void main(String[] args){
        String expression1 = "((c + (((c * f) * f) / (b / d))) * d) / (a / (((d - c) + ((c + a) / (b / d))) * ((b * (b / d)) * d)))";
        String expression2 = "(c*(b+f^2*d)*d*(d*(b+c+a)-b*c))/a";
        Parser parser = new Parser();
        double value1 = 0;
        double value2 = 0;
        try {
            ExpressionNode expr = parser.parse(expression1);
            expr.accept(new SetVariable("a", 1));
            expr.accept(new SetVariable("b", 2));
            expr.accept(new SetVariable("c", 3));
            expr.accept(new SetVariable("d", 4));
            expr.accept(new SetVariable("e", 5));
            expr.accept(new SetVariable("f", 6));

            value1 = expr.getValue();

            expr = parser.parse(expression2);
            expr.accept(new SetVariable("a", 1));
            expr.accept(new SetVariable("b", 2));
            expr.accept(new SetVariable("c", 3));
            expr.accept(new SetVariable("d", 4));
            expr.accept(new SetVariable("e", 5));
            expr.accept(new SetVariable("f", 6));

            value2 = expr.getValue();
        } catch (ParserException e) {
            System.out.println(e.getMessage());
        } catch (EvaluationException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("value1 = " + value1);
        System.out.println("value2 = " + value2);
    }
}
