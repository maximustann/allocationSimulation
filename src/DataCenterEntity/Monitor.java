package DataCenterEntity;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import DataCenterEntity.*;
import FileHandlers.*;

public class Monitor {
    private WriteFile writeFile;
    private ArrayList<Double> energyList;
    private ArrayList<Double> accEnergyList;
    private ArrayList<Double> wastedCPU;
    private ArrayList<Double> wastedOverhead;

    public Monitor(){
        writeFile = new WriteFile();
        energyList = new ArrayList<Double>();
        accEnergyList = new ArrayList<Double>();
        wastedCPU = new ArrayList<Double>();
        wastedOverhead = new ArrayList<Double>();
    }


    public void addEnergy(Double energy){
        energyList.add(Math.round(energy * 100) / 100.0);
    }

    public void addAccEnergy(Double energy){
        accEnergyList.add(Math.round(energy * 100) / 100.0);
    }

    public void writeEnergy(String path){
        try {
            writeFile.writeEnergy(path, energyList);
        } catch (IOException e1){
            e1.printStackTrace();
        }
    }

    public void udpateWasteEnergy(DataCenterInterface dataCenter, ArrayList<PM> pmList, ArrayList<VM> vmList){
        DataCenterCombined myDataCenter = (DataCenterCombined) dataCenter;

        double wastedC = 0;
        double wastedO = 0;

        double k = myDataCenter.getK();
        double maxEnergy = myDataCenter.getMaxEnergy();

        // for each pm
        for(PM pm:pmList){
            double pmWastedCpu = 0;
            double pmWastedOverhead = 0;
            ArrayList<VM> indVMList = pm.getVMList();

            // calculate the wasted CPU inside each VM, add the wasted CPU up
            for(VM vm:indVMList){
                pmWastedCpu += vm.getCpuConfiguration() - vm.getCpuConfiguration() * VM.CPU_OVERHEAD_RATE - vm.getCpuUsed();
                pmWastedOverhead += vm.getCpuConfiguration() * VM.CPU_OVERHEAD_RATE;
            }
            // add up the remain CPU in this PM
            pmWastedCpu += pm.getCpuRemain();

            // calculate the wasted utilization, then calculate the wasted energy
            wastedC += (pmWastedCpu / pm.getCpuConfiguration()) * (1 - k) * maxEnergy;
            wastedO += (pmWastedOverhead / pm.getCpuConfiguration()) * (1 - k) * maxEnergy;
        }
        if(!wastedCPU.isEmpty())
            wastedC += wastedCPU.get(wastedCPU.size() - 1);
        if(!wastedOverhead.isEmpty())
            wastedO += wastedOverhead.get(wastedOverhead.size() - 1);

        wastedCPU.add(wastedC);
        wastedOverhead.add(wastedO);
    }

    public void writeWaste(String path){
        try {
            writeFile.writeWaste(path, wastedCPU, wastedOverhead);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeAccEnergy(String path){
        try {
            writeFile.writeAccumulatedEnergy(path, accEnergyList);
        } catch (IOException e1){
            e1.printStackTrace();
        }
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
        ArrayList<double[]> statusList = new ArrayList();

        for(PM pm:pmList){
            // 9 attributes
            double[] status = new double[9];

            status[0] = pm.getCpuUsed();
            status[1] = pm.getMemUsed();
            status[2] = pm.getCpuRemain();
            status[3] = pm.getMemRemain();
            status[4] = pm.getCpuUtilization();
            status[5] = pm.getMemUtilization();
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
    private ArrayList<double[]> getVmStatus(PM pm) {

        ArrayList statusList = new ArrayList();
        ArrayList<VM> vmList = pm.getVMList();
        for(VM vm:vmList){
            // 10 attributes
            double[] status = new double[10];

            status[0] = vm.getCpuUsed();
            status[1] = vm.getMemUsed();
            status[2] = vm.getCpuRemain();
            status[3] = vm.getMemRemain();
            status[4] = vm.getCpuUtilization();
            status[5] = vm.getMemUtilization();
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
        for(PM pm:pmList){
            ArrayList<double[]> vmStatusForOne = getVmStatus(pm);
            vmStatus.add(vmStatusForOne);
        }
        try {
            writeFile.writeResults(base, getPMStatus(pmList), vmStatus);
        } catch(IOException e){
            e.printStackTrace();
        }
    }



}
