package VM_Selection;


import DataCenterEntity.*;
import OperationInterface.VMSelection;

import java.util.ArrayList;


public class FirstFitSelection implements VMSelection {
    @Override
    public int execute(ArrayList<VM> vmList, Container container) {
        if(vmList.isEmpty()) {
            System.out.println("No VM in the list");
            return 0;
        }

        return 0;
    }
}
