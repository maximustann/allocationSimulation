package OperationInterface;

import DataCenterEntity.DataCenterInterface;
import DataCenterEntity.Holder;

/**
 * Allocation interface is used for two purposes including:
 * 1. vm selection: select VM for containers
 * 2. vm allocation: select PM for VMs
 *
 *
 *
 */
public interface Allocation {
    int execute(DataCenterInterface dataCenter, Holder item, int flag);
}
