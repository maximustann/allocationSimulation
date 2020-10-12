package seletionCreationMixedMethod;

import dataCenterEntity.*;
import fitnessFunction.Fitness;
import fitnessFunction.SelectionCreationFitness;
import operationInterface.HolderSelectionCreation;

import java.util.ArrayList;


/**
 * This is a non-AnyFit framework.
 * Non-AnyFit means that when allocate a container, it is not necessary to allocate to the existing bin.
 * Without this restriction, we might create an algorithm with more flexiable of allocation
 *
 */
public class NonAnyFitFramework implements HolderSelectionCreation {

    private SelectionCreationFitness fitnessFunction;

    public NonAnyFitFramework(Fitness fitnessFunction){
        this.fitnessFunction = (SelectionCreationFitness) fitnessFunction;
    }


    /**
     *
     * @param dataCenter
     * @param container
     * @return an VM
     *
     * This method will temporarily create 5 new VMs for selection
     * Therefore, we need to save the VM counter.
     * After this method is finished, we will need to restore the VM counter.
     *
     *
     */
    public VM execute(DataCenterInterface dataCenter, Holder container){
//        DataCenter myDataCenter = (DataCenter) dataCenter;

        // create another vmList in case of contamination
        ArrayList<VM> newVMList = (ArrayList<VM>) dataCenter.getVmList().clone();
//        System.out.println(dataCenter.getVmList().size());

        double[] vmCpu = dataCenter.getVmCpu();
        double[] vmMem = dataCenter.getVmMem();
        double[] osProb = dataCenter.getOsProb();

        //current number of VMs in the data center
        int currentVmNum = dataCenter.getVmList().size();

        // save the current vm counter
        VM.saveCounter();

        // store the fitness values
//        Double[] fitnessValue = new Double[newVMList.size() + vmCpu.length];
//        Double fitnessValue;

        // Best fitness
        Double bestFitness = null;

        // temp VM
        VM bestVM = null;

        //vm Counter
        int vmCount = 0;


        // new vm flag
        boolean newVmFlag = false;

        // temporarily create five VMs, do not forget to eliminate them if we did not create one
        for(int i = 0; i < vmCpu.length; i++){
            VM vm = new VM(vmCpu[i], vmMem[i], i);
            vm.setOs(container.getExtraInfo());
            newVMList.add(vm);
        }



        for(VM vm:newVMList){
            //Check if the vm exists
            newVmFlag = vmCount >= currentVmNum;

            int vmType = vm.getType();
            int osType = container.getExtraInfo() - 1;


            // evaluate the VMs
//            fitnessValue[i] = fitnessFunction(dataCenter, newVMList.get(i), container, creationFlag, vmType);
//            System.out.println("i = " + i);
            Double fitnessValue = fitnessFunction(dataCenter, vm, container, newVmFlag, vmType, osType);
            // skip the infeasible solution
            if(fitnessValue == null) {
                vmCount += 1;
                continue;
            }
            // First VM
            if(bestVM == null || fitnessValue > bestFitness){
                bestFitness = fitnessValue;
                bestVM = vm;
                // Does not satisfy the requirement
            }
//            else if(fitnessValue[i] != null && creationFlag  > fitnessValue[i]){
//                // Bigger the better
//                creationFlag = fitnessValue[i];
//                bestVM = newVMList.get(i);
//            }
            vmCount += 1;
        }

        // restore the VM global counter
        VM.restoreCounter();

        return bestVM;
    }

    private Double fitnessFunction(
                            DataCenterInterface dataCenter,
                            Holder bin, Holder item,
                            boolean creationFlag,
                            int vmType, int osType){
        Double fitnessValue;

        if (bin.getCpuRemain() <= item.getCpuUsed() ||
                bin.getMemRemain() <= item.getMemUsed() ||
                bin.getExtraInfo() != item.getExtraInfo()
                ) {
//            System.out.println("bin.getCpuRemain() = " + bin.getCpuRemain() + " vs " + item.getCpuUsed());
//            System.out.println("bin.getMemRemain() = " + bin.getMemRemain() + " vs " + item.getMemUsed());
//            System.out.println("vmOS = " + bin.getExtraInfo() + " vs " + item.getExtraInfo());
                return null;
        } else {
            fitnessValue = fitnessFunction.evaluate(
                    dataCenter,
                    creationFlag,
                    vmType,
                    osType,
                    bin.getCpuRemain(),
                    bin.getMemRemain(),
                    item.getCpuUsed(),
                    item.getMemUsed());
        }

        return fitnessValue;
    }
}
