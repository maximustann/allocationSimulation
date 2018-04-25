package VM_Allocation;


import DataCenterEntity.*;
import OperationInterface.VMAllocation;
import java.util.*;

/**
 * FirstFitAllocation is a bin packing algorithm.
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
 * Every arrival VM will be allocated immediately.
 * There is n number of PM exists and to be considered.
 *
 *
 * W0 represents that the item will be First-come-First-serve.
 * No sorting preprocess on the items.
 * PG means packing rule is global:
 *
 * The global means, we will try to allocate the VM into every PM and then
 * calculate their fitness value.
 * Then, we will return the PM who got the highest fitness value.
 *
 *
 *
 * Algorithm Description:
 * 1. Check if there is enough resources on the PM start from the beginning of the pmList
 * 2. Allocate the VM to the first PM that has the enough resource
 *
 *
 * Notice that, this implementation returns the PM ID instead of the PM itself.
 * Also, this algorithm only select the PM ID.
 * The algorithm does not allocate VM to the selected PM.
 * We leave the allocation functionality to DataCenter
 *
 * If there is no capable PM, return 0 which means no suitable PM exists.
 *
 */
public class FirstFitAllocation implements VMAllocation{

    public int execute(ArrayList<PM> pmList, VM vm){
        int choosePMID = 0;

        // If no PM exists, return 0
        if(pmList.isEmpty()){
            System.out.println("No Existing PM");
            return 0;
        }

        // Look for the PM which has sufficient resources on 2 dimensions
        // add the VM into its local vmList
        // return the PM's ID
        for(int i = 0; i < pmList.size(); ++i){
            if(pmList.get(i).getCpu_allocated() >= vm.getCpu_configuration() &&
                    pmList.get(i).getMem_allocated() >= vm.getMem_configuration()){
                choosePMID = pmList.get(i).getID();
//                System.out.println("Select a PM ID: " + choosePMID);
                break;
            } // End If
        } // End for
        return choosePMID;
    }
}
