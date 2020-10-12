package seletionCreationMixedMethod;

import dataCenterEntity.*;
import fitnessFunction.Fitness;
import fitnessFunction.SelectionFitness;
import operationInterface.PMCreation;

import java.util.ArrayList;

public class PmSelectionBestFitSmall implements BestFitFramework{

    private SelectionFitness selectionFitness;
    private PMCreation pmCreation;
    public PmSelectionBestFitSmall(Fitness selectionFitness, PMCreation pmCreation) {

        this.selectionFitness = (SelectionFitness) selectionFitness;
        this.pmCreation = pmCreation;

    }
    @Override
    public PM execute(DataCenterInterface dataCenter, Holder item) {
        VM vm = (VM) item;
        PM bestPM = null;

        // create another vmList in case of contamination
        ArrayList<PM> newPmList = (ArrayList<PM>) dataCenter.getPmList().clone();
        int vmNUM = newPmList.size();


        // init the chosen HolderID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable Holder exists.
//        Double[] selectionFitnessValue = new Double[newVMList.size()];
        Double bestFitness = null;

        // No VM exists yet. Return 0
        if(vmNUM == 0) {
            System.out.println("No VM in the list");
            bestPM = pmCreation.execute(dataCenter, vm);
            return bestPM;
        }

        // iterate all the candidate VMs
        for(PM pm:newPmList){

            // Evaluate VM
            Double fitnessValue = fitnessFunction(dataCenter, pm, vm);

            // skip the infeasible solution
            if(fitnessValue == null) {
                continue;
            }

            // Core of VmSelectionBestFitSmall algorithm
            // First or smaller the better
            if(bestFitness == null || fitnessValue < bestFitness) {
                bestFitness = fitnessValue;
                bestPM = pm;
            }
        } // End for

        if(bestPM == null)
            bestPM = pmCreation.execute(dataCenter, vm);

        return bestPM;
    }

    @Override
    public Double fitnessFunction(DataCenterInterface dataCenter, Holder bin, Holder item) {
        Double fitnessValue;

        // Two differences between container allocation and VM allocation
        // The major difference is that,
        // For VM allocation, we use the resource boundary as the filter & evaluation criteria, instead of the actual
        // used resources
        // In addition, we do not need to compare the OS type in VM allocation
        if (bin.getCpuRemain() <= item.getCpuConfiguration() ||
                bin.getMemRemain() <= item.getMemConfiguration()
                ) {
            return null;
        } else {
            fitnessValue = selectionFitness.evaluate(
                    dataCenter,
                    bin.getCpuRemain(),
                    bin.getMemRemain(),
                    bin.getCpuUsed(),
                    bin.getMemUsed(),
                    item.getCpuConfiguration(),
                    item.getMemConfiguration(),
                    item.getCpuUsed(),
                    item.getMemUsed());
        }

        return fitnessValue;
    }
}
