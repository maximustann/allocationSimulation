package SeletionCreationMixedMethod;

import DataCenterEntity.Container;
import DataCenterEntity.Holder;
import DataCenterEntity.VM;
import FitnessFunction.Fitness;
import OperationInterface.VMSelectionCreation;

import java.util.ArrayList;


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
public class BestFit_sub implements VMSelectionCreation {

    public static final int VMSELECTION = 0;
    public static final int VMALLOCATION = 1;


    Fitness fitnessFunction;



    public BestFit_sub(Fitness fitnessFunction){
        this.fitnessFunction = fitnessFunction;
    }


    public VM execute(ArrayList<VM> vmList, double[] vmCpu, double[] vmMem,  Container container, double[] osProb){

        // save the current vm counter
        VM.saveCounter();

        // store the fitness values
        Double[] fitnessValue = new Double[vmList.size() + vmCpu.length];

        // temp fitness
        Double tempFitness = null;

        // create another vmList in case of contamination
        ArrayList<VM> newVMList = (ArrayList<VM>) vmList.clone();

        // temp VM
        VM tempVM = null;

        // temporarily create five VMs, do not forget to eliminate them if we did not create one
        for(int i = 0; i < vmCpu.length; i++){
            newVMList.add(new VM(vmCpu[i], vmMem[i], i));
        }

        // evaluate the VMs
        for(int i = 0; i < newVMList.size(); ++i){
            fitnessValue[i] = fitnessFunction(newVMList.get(i), container);
            // First VM
            if(tempFitness == null && fitnessValue[i] != null){
                tempFitness = fitnessValue[i];
                tempVM = newVMList.get(i);

                // Does not satisfy the requirement
            } else if(fitnessValue[i] == null){
                continue;
            } else if(tempFitness > fitnessValue[i]){
                tempFitness = fitnessValue[i];
                tempVM = newVMList.get(i);
            }
        }

        // restore the VM global counter
        VM.restoreCounter();

//        System.out.println("eventually selected " + choosedHolderID);
        return tempVM;
    }

    public Double fitnessFunction(Holder bin, Holder item){
        Double fitnessValue = 0.0;

        if (bin.getCpu_remain() <= item.getCpu_used() ||
                bin.getMem_remain() <= item.getMem_used() ||
                bin.getExtraInfo() != item.getExtraInfo()
                ) {
                return null;
        } else {
//            fitnessValue = fitnessFunction.evaluate(
//                    bin.getCpu_remain(),
//                    bin.getMem_remain(),
//                    item.getCpu_used(),
//                    item.getMem_used(),
//                    null,
//                    null);
        }

        return fitnessValue;
    }
}
