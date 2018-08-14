package fitnessFunction;


import dataCenterEntity.DataCenterInterface;

public interface SelectionFitness extends Fitness {
    // Transform multi resource into one scalar
    double evaluate(
                            DataCenterInterface dataCenter,
                            double binCpuRemain,
                            double binMemRemain,
                            double itemCpuRequire,
                            double itemMemRequire);
}
