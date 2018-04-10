package OperationInterface;
import DataCenterEntity.*;

import java.util.*;

 /** In the current version of VM selection, we pass the vmList to the selection algorithm
 The algorithm will retrieve the information from the list,
 analyze it. And make the decision.
 After a vm is decided.
 It return the ID of the VM
  */
public interface VMSelection {
    public int execute(ArrayList<VM> vmList, Container container);
}
