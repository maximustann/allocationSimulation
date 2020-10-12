package seletionCreationMixedMethod;

import dataCenterEntity.Container;
import dataCenterEntity.DataCenterInterface;
import dataCenterEntity.Holder;
import dataCenterEntity.VM;
import fitnessFunction.Fitness;
import fitnessFunction.SelectionFitness;
import operationInterface.VMCreation;

import java.util.ArrayList;

public class VmSelectionBestFitLarge implements BestFitFramework{

    private SelectionFitness selectionFitness;
    private VMCreation vmCreation;
    public VmSelectionBestFitLarge(Fitness selectionFitness, VMCreation vmCreation) {

        this.selectionFitness = (SelectionFitness) selectionFitness;
        this.vmCreation = vmCreation;

    }
    @Override
    public VM execute(DataCenterInterface dataCenter, Holder item) {
        Container container = (Container) item;
        VM bestVM = null;

        // create another vmList in case of contamination
        ArrayList<VM> newVMList = (ArrayList<VM>) dataCenter.getVmList().clone();
        int vmNUM = newVMList.size();


        // init the chosen HolderID = 0,
        // all ID starts from 1, therefore, 0 means NO suitable Holder exists.
//        Double[] selectionFitnessValue = new Double[newVMList.size()];
        Double bestFitness = null;

        // No VM exists yet. Return 0
        if(vmNUM == 0) {
            System.out.println("No VM in the list");
            bestVM = vmCreation.execute(dataCenter, container);
            return bestVM;
        }

        // iterate all the candidate VMs
        for(VM vm:newVMList){

            // Evaluate VM
            Double fitnessValue = fitnessFunction(dataCenter, vm, container);

            // skip the infeasible solution
            if(fitnessValue == null) {
                continue;
            }

            // Core of BestFitLarger algorithm
            // First or Larger the better
            if(bestFitness == null || fitnessValue > bestFitness) {
                bestFitness = fitnessValue;
                bestVM = vm;
            }
        } // End for

        if(bestVM == null)
            bestVM = vmCreation.execute(dataCenter, container);

        return bestVM;
    }

    @Override
    public Double fitnessFunction(DataCenterInterface dataCenter, Holder bin, Holder item) {
        Double fitnessValue;

        if (bin.getCpuRemain() <= item.getCpuUsed() ||
                bin.getMemRemain() <= item.getMemUsed() ||
                bin.getExtraInfo() != item.getExtraInfo()
                ) {
            return null;
        } else {
            fitnessValue = selectionFitness.evaluate(
                    dataCenter,
                    bin.getCpuConfiguration(),
                    ((VM) bin).getMemOverhead(),
                    ((VM) bin).getCpuOverheadRate(),
                    bin.getCpuRemain(),
                    bin.getMemRemain(),
                    item.getCpuUsed(),
                    item.getMemUsed());
        }

        return fitnessValue;
    }
}
