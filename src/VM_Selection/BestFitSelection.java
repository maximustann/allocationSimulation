package VM_Selection;
import java.util.*;
import OperationInterface.VMSelection;
import DataCenterEntity.*;
import FitnessFunction.Fitness;


/**
 * BestFitSelection is a bin packing algorithm.
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
 * So, the Best fit algorithm can be represented in a general form as
 * A(1, 1, n, n, W0, PG) which means
 *
 * The waiting queue for container equals one.
 * This means, as soon as one container arrives. This container will be allocated at once.
 * There is n number of VM exists and to be considered.
 *
 *
 * W0 represents that the item will be First-come-First-serve.
 * No sorting preprocess on the items.
 * PG means packing rule is global:
 *
 * The global means, we will try to allocate the container into every VM and then
 * calculate their fitness value.
 * Then, we will return the VM who got the highest fitness value.
 *
 *
 * Algorithm Description:
 * 1. Use a fitness function to evaluate all VM
 * 2. Return the VM who has the highest fitness value
 *
 *
 * The difference between FF and BF is that, in FF,
 * once we find a VM has enough resource, we break from examing all the VMs.
 *
 * In BF, we need to go through all the VMs and assign them fitness values.
 * Then, we return the VM has the highest fitness value.
 *
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
public class BestFitSelection implements VMSelection{

    Fitness fitnessFunction;


    public BestFitSelection(Fitness fitnessFunction){
        this.fitnessFunction = fitnessFunction;
    }


    public int execute(ArrayList<VM> vmList, Container container){
        // init the choosedVMID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable VM exists.
        int choosedVMID = 0;
        Double[] fitnessValue = new Double[vmList.size()];
        Double tempFitness = null;


        // No VM exists yet. Return 0
        if(vmList.isEmpty()) {
            System.out.println("No VM in the list");
            return 0;
        }

        // Look for the VM which has sufficient resources on 2 dimensions
        // return the VM's ID
        for(int i = 0; i < vmList.size(); ++i){
            fitnessValue[i] = fitnessFunction(vmList.get(i), container);
            // First VM
            if(tempFitness == null && fitnessValue[i] != null) {
                tempFitness = fitnessValue[i];
                choosedVMID = i + 1;
                // Does not satisfy the requirement
            } else if(fitnessValue[i] == null){
                continue;

                // find a better solution
            } else if(tempFitness > fitnessValue[i]){
                tempFitness = fitnessValue[i];
                choosedVMID = i + 1;
            }
        } // End for

        return choosedVMID;
    }

    public Double fitnessFunction(VM vm, Container container){
        Double fitnessValue = 0.0;
        if(vm.getCpu_remain() <= container.getCpu_used() ||
                vm.getMem_remain() <= container.getMem_used() ||
                vm.getOs() != container.getOs()){
            return null;
        }

        fitnessValue = fitnessFunction.evaluate((vm.getCpu_remain() - container.getCpu_used()),
                                            (vm.getMem_remain() - container.getMem_used()), vm.getType());
//        System.out.println("container" + container.getID() + " allocate to VM" + vm.getID() + "fitness = " + fitnessValue);
//        System.out.println();
        return fitnessValue;
    }
}
