package DataCenterEntity;

public interface Holder {
    public double getCpu_used();
    public double getMem_used();
    public double getCpu_remain();
    public double getMem_remain();
    public double getCpu_configuration();
    public double getMem_configuration();
    public int getExtraInfo();
    public int getType();
    public int getID();
    public void print();
}
