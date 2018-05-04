package FitnessFunction;
import  DataCenterEntity.*;


public interface Fitness {
    // Transform multi resource into one scalar
    public double evaluate(double cpu, double mem, int vmType);
}
