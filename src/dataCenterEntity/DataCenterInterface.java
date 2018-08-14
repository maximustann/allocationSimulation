package dataCenterEntity;

import java.util.ArrayList;

public interface DataCenterInterface {
    ArrayList<VM> getVmList();
    ArrayList<PM> getPmList();
    double[] getVmCpu();
    double[] getVmMem();
    double[] getOsProb();
    double getPmCpu();
    double getPmMem();
    double getVmCpuOverhead(int vmType);
    double getVmMemOverhead();
}
