package SeletionCreationMixedMethod;

import DataCenterEntity.*;
import FitnessFunction.Fitness;
import OperationInterface.VMSelectionCreation;

import java.util.ArrayList;


/**
 * This is a non-AnyFit framework.
 * Non-AnyFit means that when allocate a container, it is not necessary to allocate to the existing bin.
 * Without this restriction, we might create an algorithm with more flexiable of allocation
 *
 */
public class NonAnyFitFramework implements VMSelectionCreation {

    public static final int VMSELECTION = 0;
    public static final int VMALLOCATION = 1;


    private Fitness fitnessFunction;



    public NonAnyFitFramework(Fitness fitnessFunction){
        this.fitnessFunction = fitnessFunction;
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
//        DataCenterCombined myDataCenter = (DataCenterCombined) dataCenter;

        // create another vmList in case of contamination
        ArrayList<VM> newVMList = (ArrayList<VM>) dataCenter.getVmList().clone();

        //
        double[] vmCpu = dataCenter.getVmCpu();
        double[] vmMem = dataCenter.getVmMem();
        double[] osProb = dataCenter.getOsProb();

        // save the current vm counter
        VM.saveCounter();

        // store the fitness values
        Double[] fitnessValue = new Double[newVMList.size() + vmCpu.length];

        // temp fitness
        Double tempFitness = null;

        // temp VM
        VM tempVM = null;

        // temporarily create five VMs, do not forget to eliminate them if we did not create one
        for(int i = 0; i < vmCpu.length; i++){
            newVMList.add(new VM(vmCpu[i], vmMem[i], i));
        }

        for(int i = 0; i < newVMList.size(); ++i){
            // evaluate the VMs
            fitnessValue[i] = fitnessFunction(dataCenter, newVMList.get(i), container);
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

    private Double fitnessFunction(DataCenterInterface dataCenter, Holder bin, Holder item){
        Double fitnessValue = 0.0;

        if (bin.getCpuRemain() <= item.getCpuUsed() ||
                bin.getMemRemain() <= item.getMemUsed() ||
                bin.getExtraInfo() != item.getExtraInfo()
                ) {
                return null;
        } else {
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
