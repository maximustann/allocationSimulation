package operationInterface;

import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.Holder;
import dataCenterEntity.VM;

/**
 * The mixed method between VM selection and creation.
 * It will return the selected VM/PM or it will create a new VM/PM and return it.
 */
public interface HolderSelectionCreation {
   Holder execute(DataCenterInterface dataCenter, Holder item);
}
