package allocationMethod;
import java.util.*;

import dataCenterEntity.*;
import fitnessFunction.Fitness;
import operationInterface.*;


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
public class BestFit implements Allocation {

    public static final int VMSELECTION = 0;
    public static final int VMALLOCATION = 1;


    private Fitness fitnessFunction;



    public BestFit(Fitness fitnessFunction){
        this.fitnessFunction = fitnessFunction;
    }


    public int execute(DataCenterInterface dataCenter, Holder item, int flag){

        ArrayList<? extends Holder> binList;
        if(flag == VMALLOCATION)
            binList = dataCenter.getPmList();
        else
            binList = dataCenter.getVmList();


        // all ID starts from 1, therefore, 0 means NO suitable Holder exists.
        int choosedHolderID = 0;
        Double[] fitnessValue = new Double[binList.size()];
        Double tempFitness = null;


        // No VM exists yet. Return 0
        if(binList.isEmpty()) {
            System.out.println("No VM in the list");
            return 0;
        }

        // Look for the VM which has sufficient resources on 2 dimensions
        // return the VM's ID
        for(int i = 0; i < binList.size(); ++i){
            fitnessValue[i] = fitnessFunction(dataCenter, binList.get(i), item, flag);
            // First VM
            if(tempFitness == null && fitnessValue[i] != null) {
                tempFitness = fitnessValue[i];

                // should not be the index of the binList. should be holder ID !!!
                choosedHolderID = binList.get(i).getID();
                // Does not satisfy the requirement
            } else if(fitnessValue[i] == null){
                continue;

                // This is the core of BestFit
                // A bigger tempFitness is better
            } else if(tempFitness < fitnessValue[i]){
                tempFitness = fitnessValue[i];
                choosedHolderID = binList.get(i).getID();
            }
        } // End for

        return choosedHolderID;
    }

    private Double fitnessFunction(DataCenterInterface dataCenter, Holder bin, Holder item, int flag){

        Double fitnessValue;

        // If it is VM allocation, AKA. no OS constraint.
        // We must use the configuration resources to test it.
        if(flag == VMALLOCATION){
            if (bin.getCpuRemain() <= item.getCpuConfiguration() ||
                    bin.getMemRemain() <= item.getMemConfiguration()) {
                return null;
            }
        // VM selection, has OS constraint. We must use the actual resources left to test it.
        } else {
            if (bin.getCpuRemain() <= item.getCpuUsed() ||
                    bin.getMemRemain() <= item.getMemUsed() ||
                    bin.getExtraInfo() != item.getExtraInfo()
                    ) {
                return null;
            }
        }

        // calculate the fitness value with the residual resources
        if(flag == VMALLOCATION) {
            fitnessValue = fitnessFunction.evaluate(
                                            dataCenter,
                                            bin.getCpuRemain(),
                                            bin.getMemRemain(),
                                            item.getCpuConfiguration(),
                                            item.getMemConfiguration());

            //VM selection, we use the acutual residual resources
        } else{
            fitnessValue = fitnessFunction.evaluate(
                                        dataCenter,
                                        bin.getCpuRemain(),
                                        bin.getMemRemain(),
                                        item.getCpuUsed(),
                                        item.getMemUsed());
        }

        return fitnessValue;
    }
}
