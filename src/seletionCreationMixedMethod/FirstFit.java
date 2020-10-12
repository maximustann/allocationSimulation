package seletionCreationMixedMethod;

import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.Holder;
import dataCenterEntity.PM;
import dataCenterEntity.VM;
import operationInterface.HolderSelectionCreation;
import operationInterface.PMCreation;

import java.util.ArrayList;

public class FirstFit implements HolderSelectionCreation{

    private PMCreation pmCreation;
    public FirstFit(PMCreation pmCreation) {
        this.pmCreation = pmCreation;

    }

    public Holder execute(DataCenterInterface dataCenter, Holder item) {
        VM vm = (VM) item;
        PM bestPM = null;
        // create another pmList in case of contamination
        ArrayList<PM> newPmList = (ArrayList<PM>) dataCenter.getPmList().clone();
        int pmNum = newPmList.size();


        // init the chosen HolderID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable Holder exists.
//        Double[] selectionFitnessValue = new Double[newVMList.size()];
        Double bestFitness = null;

        // No VM exists yet. Return 0
        if(pmNum == 0) {
            System.out.println("No VM in the list");
            bestPM = pmCreation.execute(dataCenter, vm);
            return bestPM;
        }

        // iterate all the candidate PMs
        for(PM pm:newPmList){

            // Evaluate PM
            Double fitnessValue = fitnessFunction(dataCenter, pm, vm);

            // skip the infeasible solution
            if(fitnessValue == null) {
                continue;
            }

            // Core of FF algorithm
            if(fitnessValue > 0){
                bestPM = pm;
                break;
            }
        } // End for

        if(bestPM == null)
            bestPM = pmCreation.execute(dataCenter, vm);

        return bestPM;
    }

    public Double fitnessFunction(DataCenterInterface dataCenter, Holder bin, Holder item) {
        Double fitnessValue;

        // Two differences between container allocation and VM allocation
        // The major difference is that,
        // For VM allocation, we use the resource boundary as the filter & evaluation criteria, instead of the actual
        // used resources
        // In addition, we do not need to compare the OS type in VM allocation
        if (bin.getCpuRemain() < item.getCpuConfiguration() ||
                bin.getMemRemain() < item.getMemConfiguration()
                ) {
            return null;
        } else {
            // For FF, if the PM is available, we set the fitness value to 1
            fitnessValue = 1.0;
        }

        return fitnessValue;
    }


}
