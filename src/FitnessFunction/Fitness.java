package FitnessFunction;
import  DataCenterEntity.*;


public interface Fitness {
    // Transform multi resource into one scalar
    public double evaluate(
                            double binCPUremain,
                            double binMEMremain,
                            double itemCPUrequire,
                            double itemMEMrequire,
                            Double optional,
                            Integer type);
}
