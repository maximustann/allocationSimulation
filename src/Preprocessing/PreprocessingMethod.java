package Preprocessing;
import  DataCenterEntity.*;


public interface PreprocessingMethod {
    // Transform multi resource into one scalar
    public double tranform(double cpu, double mem);
}
