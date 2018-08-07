package dataCenterEntity;

public interface Holder {
    double getCpuUsed();
    double getMemUsed();
    double getCpuRemain();
    double getMemRemain();
    double getCpuConfiguration();
    double getMemConfiguration();
    int getExtraInfo();
    Integer getType();
    int getID();
    void print();
}
