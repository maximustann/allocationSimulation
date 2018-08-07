package FitnessFunction;


import DataCenterEntity.DataCenterInterface;

public interface Fitness {
    // Transform multi resource into one scalar
    double evaluate(
                            DataCenterInterface dataCenter,
                            double binCpuRemain,
                            double binMemRemain,
                            double itemCpuRequire,
                            double itemMemRequire);
}
