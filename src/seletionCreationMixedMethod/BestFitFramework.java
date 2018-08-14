package seletionCreationMixedMethod;

import dataCenterEntity.*;
import fitnessFunction.Fitness;
import fitnessFunction.SelectionFitness;
import operationInterface.VMCreation;
import operationInterface.VMSelectionCreation;

import java.util.ArrayList;

/**
 * This method uses BestFit (sum rule) for VM selection and Largest rule for VM Creation.
 */
public class BestFitFramework implements VMSelectionCreation {

    private SelectionFitness selectionFitness;
    private VMCreation vmCreation;
    public BestFitFramework(Fitness selectionFitness, VMCreation vmCreation) {

        this.selectionFitness = (SelectionFitness) selectionFitness;
        this.vmCreation = vmCreation;

    }


    public VM execute(DataCenterInterface dataCenter, Holder item){
        Container container = (Container) item;
        VM chosenVM = null;

        // create another vmList in case of contamination
        ArrayList<VM> newVMList = (ArrayList<VM>) dataCenter.getVmList().clone();
        int vmNUM = newVMList.size();


        // init the chosen HolderID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable Holder exists.
        Double[] selectionFitnessValue = new Double[newVMList.size()];
        Double tempSelectionFitness = null;

        // No VM exists yet. Return 0
        if(vmNUM == 0) {
            System.out.println("No VM in the list");
            chosenVM = vmCreation.execute(dataCenter, container);
            return chosenVM;
        }

        // iterate all the candidate VMs
        for(int i = 0; i < vmNUM; ++i){

            // Evaluate VM
            selectionFitnessValue[i] = fitnessFunction(dataCenter, newVMList.get(i), container);

            // First VM
            if(tempSelectionFitness == null && selectionFitnessValue[i] != null) {
                tempSelectionFitness = selectionFitnessValue[i];
                chosenVM = newVMList.get(i);

                // Does not satisfy the requirement
            } else if(selectionFitnessValue[i] == null){
                continue;

                // This is the core of BestFit
                // A smaller tempFitness is better
            } else if(tempSelectionFitness > selectionFitnessValue[i]){
                tempSelectionFitness = selectionFitnessValue[i];
                chosenVM = newVMList.get(i);
            }
        } // End for

        if(chosenVM == null)
            chosenVM = vmCreation.execute(dataCenter, container);

        return chosenVM;
    }

    private Double fitnessFunction(DataCenterInterface dataCenter, Holder bin, Holder item){
        Double fitnessValue;

        if (bin.getCpuRemain() <= item.getCpuUsed() ||
                bin.getMemRemain() <= item.getMemUsed() ||
                bin.getExtraInfo() != item.getExtraInfo()
                ) {
            return null;
        } else {
            fitnessValue = selectionFitness.evaluate(
                    dataCenter,
                    bin.getCpuRemain(),
                    bin.getMemRemain(),
                    item.getCpuUsed(),
                    item.getMemUsed());
        }

        return fitnessValue;
    }

}
