package fitnessFunction;


import dataCenterEntity.DataCenterInterface;

public interface SelectionCreationFitness extends Fitness {
    // Transform multi resource into one scalar
    double evaluate(
            DataCenterInterface dataCenter,
            boolean creationFlag,
            int vmType,
            double binCpuRemain,
            double binMemRemain,
            double itemCpuRequire,
            double itemMemRequire);
}
