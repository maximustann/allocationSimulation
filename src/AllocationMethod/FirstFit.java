package AllocationMethod;


import DataCenterEntity.*;
import OperationInterface.*;
import java.util.ArrayList;


/**
 * FirstFitSelection is a bin packing algorithm.
 *
 * In a general form, a heuristic algorithm can be represented as
 * A(N, R, M, S, W, P)
 *
 * N is the number of items
 * R is the number of items that need to be packed when refill the queue,  1 <= R <= N
 * M is the number of bins
 * S is the number of bins that need to be shipped when no item can fit into current bins
 * W is the preprocessing (sorting) algorithm
 * P is the packing rule
 *
 *
 * So, the first fit algorithm can be represented in a general form as
 * A(1, 1, n, n, W0, PG) which means
 *
 * Every arrival container will be allocated immediately.
 * There is n number of VM exists and to be considered.
 *
 *
 * W0 represents that the item will be First-come-First-serve.
 * No sorting preprocess on the items.
 * PG means packing rule is global:
 *
 * The global means, we will exam VM one by one from the beginning of the vmList until we find
 * a VM which has enough resource to hold the current container.
 *
 *
 * Algorithm Description:
 * 1. Check if there is enough resources on the VM start from the beginning of the vmList
 * 2. Allocate the container to the first VM that has the enough resource
 *
 *
 * Notice that, this implementation returns the VM ID instead of the VM itself.
 * Also, this algorithm only select the VM ID.
 * The algorithm does not allocate container to the selected VM.
 * We leave the allocation functionality to DataCenter
 *
 * If there is no capable VM, return 0 which means no suitable VM exists.
 *
 */
public class FirstFit implements Allocation {
    @Override
    public int execute(ArrayList<? extends Holder> binList, Holder item) {
        // init the choosedVMID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable VM exists.
        int choosedVMID = 0;


        // No VM exists yet. Return 0
        if(binList.isEmpty()) {
            System.out.println("No VM in the list");
            return 0;
        }

        // Look for the VM which has sufficient resources on 2 dimensions
        // return the VM's ID
        for(int i = 0; i < binList.size(); ++i){

            // If the current vm has enough resource and the OS is compatible with
            // the container
            if(binList.get(i).getCpu_remain() >= item.getCpu_configuration() &&
                    binList.get(i).getMem_remain() >= item.getMem_configuration() &&
                    binList.get(i).getExtraInfo() == item.getExtraInfo()){
//                System.out.println("VM OS: " + vmList.get(i).getOs() + ", container OS: " + container.getOs());
                choosedVMID = binList.get(i).getID();
//                System.out.println("Select a VM ID : " + choosedVMID);
                break;
            } // End If
        } // End for

        return choosedVMID;
    }
}
