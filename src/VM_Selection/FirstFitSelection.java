package VM_Selection;


import DataCenterEntity.*;
import OperationInterface.VMSelection;
import java.util.ArrayList;


/**
 * FirstFitSelection is a bin packing algorithm.
 * In this implementation, we implemented a First Fit Decreasing.
 *
 *
 * Algorithm Description:
 * 1. Sort the vmList in a decreasing order according to their remain resources
 * 2. Select the first VM that has sufficient resources
 *
 * Notice that, this implementation returns the VM ID instead of the VM itself.
 * Also, this algorithm only select the VM ID.
 * The algorithm does not allocate container to the selected VM.
 * We leave the allocation functionality to DataCenter
 *
 * If there is no capable VM, return 0 which means no suitable VM exists;
 *
 */
public class FirstFitSelection implements VMSelection {
    @Override
    public int execute(ArrayList<VM> vmList, Container container) {
        // init the choosedVMID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable VM exists.
        int choosedVMID = 0;

        // We sort VM in this list so that it won't mess up the original one.
        ArrayList<VM> sortedVMList;

        // No VM exists yet. Return 0
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
