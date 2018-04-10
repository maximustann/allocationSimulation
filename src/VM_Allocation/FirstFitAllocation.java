package VM_Allocation;


import DataCenterEntity.*;
import OperationInterface.VMAllocation;
import java.util.*;

public class FirstFitAllocation implements VMAllocation{

    public boolean execute(ArrayList<PM> pmList, VM vm){
        if(pmList.isEmpty()){
            System.out.println("No Existing PM");
            return false;
        }
        return false;
    }
}
