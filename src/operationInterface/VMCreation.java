package operationInterface;
import dataCenterEntity.Container;
import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.VM;

public interface VMCreation {
    VM execute(DataCenterInterface dataCenter, Container container);
}
