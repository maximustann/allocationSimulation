package operationInterface;
import dataCenterEntity.*;

public interface PMCreation {
    PM execute(DataCenterInterface dataCenter, VM vm);
}
