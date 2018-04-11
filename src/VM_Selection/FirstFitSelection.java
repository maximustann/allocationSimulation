package VM_Selection;


import DataCenterEntity.*;
import OperationInterface.VMSelection;

import java.util.ArrayList;


public class FirstFitSelection implements VMSelection {
    int choosedVMID = 0;
    @Override
    public int execute(ArrayList<VM> vmList, Container container) {
        if(vmList.isEmpty()) {
            System.out.println("No VM in the list");
            return 0;
        }

        // Look for the VM which has sufficient resources on 2 dimensions
        // return the VM's ID
        for(int i = 0; i < vmList.size(); ++i){
            // If the current vm has enough resource and the OS is compatible with
            // the container

            if(vmList.get(i).getCpu_remain() >= container.getCpu_configuration() &&
                    vmList.get(i).getMem_remain() >= container.getMem_configuration() &&
                    vmList.get(i).getOs() == container.getOs()){
                System.out.println("VM OS: " + vmList.get(i).getOs() + ", container OS: " + container.getOs());
                choosedVMID = vmList.get(i).getID();
                System.out.println("Select a VM ID : " + choosedVMID);
                break;
            } // End If
        } // End for

        // No suitable VM exists
        return 0;
    }
}
