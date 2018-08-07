package OperationInterface;

import DataCenterEntity.DataCenterInterface;
import DataCenterEntity.Holder;
import DataCenterEntity.VM;

/**
 * The mixed method between VM selection and creation.
 * It will return the selected VM or it will create a new VM and return it.
 */
public interface VMSelectionCreation {
   VM execute(DataCenterInterface dataCenter, Holder item);
}
