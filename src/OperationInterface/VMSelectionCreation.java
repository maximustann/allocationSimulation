package OperationInterface;

import DataCenterEntity.Container;
import DataCenterEntity.VM;

import java.util.ArrayList;

/**
 * The mixed method between VM selection and creation.
 * It will return the selected VM or it will create a new VM and return it.
 */
public interface VMSelectionCreation {
   public VM execute(ArrayList<VM> vmList, double[] vmCpu, double[] vmMem, Container container, double[] osProb);
}
