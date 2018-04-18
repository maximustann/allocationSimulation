package Multi_Resource_Handling;
import  DataCenterEntity.*;

public interface MultiResource {
    // Transform multi resource into one scalar
    public double tranform(Holder bin);
}
