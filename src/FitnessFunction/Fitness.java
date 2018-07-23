package FitnessFunction;


import DataCenterEntity.DataCenter;

public interface Fitness {
    // Transform multi resource into one scalar
    public double evaluate(
                            DataCenter dataCenter,
                            double binCPUremain,
                            double binMEMremain,
                            double itemCPUrequire,
                            double itemMEMrequire,
                            Double optional,
                            Integer type);
}
