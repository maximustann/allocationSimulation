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

    final int VMSELECTION = 0;
    final int VMALLOCATION = 1;

    public int execute(ArrayList<? extends Holder> binList, Holder item, int flag) {
        // init the choosedVMID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable VM exists.
        int choosedHolderID = 0;


        // No VM exists yet. Return 0
        if (binList.isEmpty()) {
            System.out.println("No VM in the list");
            return 0;
        }

        // Look for the VM which has sufficient resources on 2 dimensions
        // return the VM's ID
        for (int i = 0; i < binList.size(); ++i) {

            // If the current PM has enough resources
            // VM allocation, select a PM for a VM
            if (flag == VMALLOCATION) {
                if (binList.get(i).getCpu_remain() >= item.getCpu_configuration() &&
                        binList.get(i).getMem_remain() >= item.getMem_configuration()) {
                        choosedHolderID = binList.get(i).getID();
                        break;
                    }
                    // If the current VM has enough resources
                    // VM selection, select a VM for a container
                // Here, for contianer, the .getCpu_configuration() and .getCpu_used() methods retrieve
                // exact the same value, therefore, they can be alternated.
                } else {
                    if (binList.get(i).getCpu_remain() >= item.getCpu_configuration() &&
                            binList.get(i).getMem_remain() >= item.getMem_configuration() &&
                            binList.get(i).getExtraInfo() == item.getExtraInfo()) {
                        choosedHolderID = binList.get(i).getID();
                        break;
                    } // End If
                } // End Else
            } // End for

            return choosedHolderID;
        } // End execute
} // End class