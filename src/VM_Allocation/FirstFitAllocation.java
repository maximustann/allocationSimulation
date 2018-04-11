package VM_Allocation;


import DataCenterEntity.*;
import OperationInterface.VMAllocation;
import java.util.*;

public class FirstFitAllocation implements VMAllocation{

    public int execute(ArrayList<PM> pmList, VM vm){
        int choosePMID = 0;
        if(pmList.isEmpty()){
            System.out.println("No Existing PM");
            return 0;
        }

        // Look for the PM which has sufficient resources on 2 dimensions
        // add the VM into its local vmList
        // return the PM's ID
        for(int i = 0; i < pmList.size(); ++i){
            if(pmList.get(i).getCpu_allocated() >= vm.getCpu_configuration() &&
                    pmList.get(i).getMem_allocated() >= vm.getMem_configuration()){
                pmList.get(i).addVM(vm);
                choosePMID = pmList.get(i).getID();
            } // End If
        } // End for
        return choosePMID;
    }
}
