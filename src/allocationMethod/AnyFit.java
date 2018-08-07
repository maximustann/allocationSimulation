package allocationMethod;

import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.Holder;
import operationInterface.Allocation;

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
public class AnyFit implements Allocation {

    public static final int VMSELECTION = 0;
    public static final int VMALLOCATION = 1;


    public int execute(DataCenterInterface dataCenter, Holder item, int flag){

        ArrayList<? extends Holder> binList;
        if(flag == VMALLOCATION)
            binList = dataCenter.getPmList();
        else
            binList = dataCenter.getVmList();

        // init the choosedHolderID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable Holder exists.
        int choosedHolderID = 0;
//        Double[] fitnessValue = new Double[binList.size()];
//        Double tempFitness = null;

        ArrayList<Integer> feasibleVM = new ArrayList<>();

        // No VM exists yet. Return 0
        if(binList.isEmpty()) {
            System.out.println("No VM in the list");
            return 0;
        }

        // check which vm is suitable for allocation
        for(int i = 0; i < binList.size(); ++i){

            double cpu_require = item.getCpuConfiguration();
            double mem_require = item.getMemConfiguration();

            int os_require = item.getExtraInfo();

            double cpu_remain = binList.get(i).getCpuRemain();
            double mem_remain = binList.get(i).getMemRemain();
            double os = binList.get(i).getExtraInfo();

            // If the resource and os requirement both suitable, add to the feasible list
            if(cpu_remain >= cpu_require && mem_remain >= mem_require && os_require == os)
                feasibleVM.add(i);
        } // end for

//        System.out.println("feasible solutions: ");
//        for(int i = 0; i < feasibleVM.size(); i++){
//            binList.get(feasibleVM.get(i)).print();
//        }
        // no feasible solution
        if(feasibleVM.isEmpty()) {
            System.out.println("No feasible VM exists");
        } else {
            int feasibleVMNUM = feasibleVM.size();
            int index = (int) (Math.random() * feasibleVMNUM);
            int choosed = feasibleVM.get(index);
            choosedHolderID = binList.get(choosed).getID();
        }

        System.out.println("eventually selected " + choosedHolderID);
        return choosedHolderID;
    }
}
