package fitnessFunction;


import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.Holder;
import dataCenterEntity.VM;

public interface SelectionFitness extends Fitness {
    // Used for container allocation
    double evaluate(DataCenterInterface dataCenter,
                    double binCpuConfiguration,
                    double binMemOverhead,
                    double binCpuOverheadRate,
                    double binCpuRemain,
                    double binMemRemain,
                    double itemCpuUsed,
                    double itemMemUsed);

    // Used for VM allocation
    double evaluate(DataCenterInterface dataCenter,
                    double binCpuRemain,
                    double binMemRemain,
                    double binActualCpuUsed,
                    double binActualMemUsed,
                    double itemCpuUsed,
                    double itemMemUsed,
                    double itemActualCpuUsed,
                    double itemActualMemUsed);

}
