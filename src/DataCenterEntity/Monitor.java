package DataCenterEntity;

import java.io.IOException;
import java.util.ArrayList;
import DataCenterEntity.*;
import FileHandlers.*;

public class Monitor {
    WriteFile writeFile;

    public Monitor(){
        writeFile = new WriteFile();
    }



    private ArrayList<double[]> getPMStatus(ArrayList<PM> pmList){
        /*
            For each PM, we store the value of:

            CPU_used
            MEM_used
            CPU_remain
            MEM_remain
            CPU_utilization
            MEM_utilization
            Energy
            NUM of VM
            Two Resource balance

         */
        ArrayList statusList = new ArrayList();

        for(int i = 0; i < pmList.size(); ++i){
            // 9 attributes
            double[] status = new double[9];
            PM pm = pmList.get(i);

            status[0] = pm.getCpu_used();
            status[1] = pm.getMem_used();
            status[2] = pm.getCpu_remain();
            status[3] = pm.getMem_remain();
            status[4] = pm.getCpu_utilization();
            status[5] = pm.getMem_utilization();
            status[6] = pm.calEnergy();
            status[7] = pm.vmNum();
            status[8] = pm.getBalance();
            statusList.add(status);
        } // end for

        return statusList;
    }

        /*
            For each VM, we store the value of:

            CPU_used
            MEM_used
            CPU_remain
            MEM_remain
            CPU_utilization
            MEM_utilization
            OS
            TYPE
            NUM of container
            Two Resource balance

         */
    private ArrayList<double[]> getVMStatus(PM pm) {

        ArrayList statusList = new ArrayList();
        ArrayList vmList = pm.getVMList();
        for(int i = 0; i < vmList.size(); ++i){
            // 10 attributes
            double[] status = new double[10];
            VM vm = (VM) vmList.get(i);

            status[0] = vm.getCpu_used();
            status[1] = vm.getMem_used();
            status[2] = vm.getCpu_remain();
            status[3] = vm.getMem_remain();
            status[4] = vm.getCpu_utilization();
            status[5] = vm.getMem_utilization();
            status[6] = vm.getOs();
            status[7] = vm.getType();
            status[8] = vm.getContainerNum();
            status[9] = vm.getBalance();
            statusList.add(status);
        } // end for

        return statusList;
    }



    public void writeStatusFiles(String base, ArrayList<PM> pmList){

        ArrayList<ArrayList<double[]>> vmStatus = new ArrayList<>();
        // gather all VM status from all PMs
        for(int i = 0; i < pmList.size(); ++i){
            ArrayList<double[]> vmStatusForOne = getVMStatus(pmList.get(i));
            vmStatus.add(vmStatusForOne);
        }
        try {
            writeFile.writeResults(base, getPMStatus(pmList), vmStatus);
        } catch(IOException e){
            e.printStackTrace();
        }
    }



}
