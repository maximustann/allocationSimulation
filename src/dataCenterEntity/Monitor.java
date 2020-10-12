package dataCenterEntity;

import java.io.IOException;
import java.util.ArrayList;

import fileHandlers.*;

public class Monitor {
    private WriteFile writeFile;
    private ArrayList<Double> energyList;
    private ArrayList<Double> accEnergyList;
    private ArrayList<Double> wastedCPU;
    private ArrayList<Double> wastedMem;
    private ArrayList<Double> wastedCpuOverhead;
    private ArrayList<Double> wastedMemOverhead;

    public Monitor(){
        writeFile = new WriteFile();
        energyList = new ArrayList<Double>();
        accEnergyList = new ArrayList<Double>();
        wastedCPU = new ArrayList<Double>();
        wastedMem = new ArrayList<Double>();
        wastedCpuOverhead = new ArrayList<Double>();
        wastedMemOverhead = new ArrayList<Double>();
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

    public void udpateWasteEnergy(DataCenterInterface dataCenter, ArrayList<PM> pmList){
        DataCenter myDataCenter = (DataCenter) dataCenter;

        double currentWastedCpu = 0;
        double currentWastedCpuOverhead = 0;

        double currentWastedMem = 0;
        double currentWastedMemOverhead = 0;

        double k = myDataCenter.getK();
        double maxEnergy = myDataCenter.getMaxEnergy();

        // for each pm
        for(PM pm:pmList){
            double pmWastedCpu = 0;
            double pmWastedMem = 0;

            double pmWastedCpuOverhead = 0;
            double pmWastedMemOverhead = 0;

            // get the VMs in this PM
            ArrayList<VM> indVMList = pm.getVMList();

            // calculate the wasted resources and overheads
            for(VM vm:indVMList){
                pmWastedCpu += vm.getCpuConfiguration()  - vm.getCpuUsed();
                pmWastedMem += vm.getMemConfiguration() - vm.getMemUsed();
                pmWastedCpuOverhead += vm.getCpuConfiguration() * VM.CPU_OVERHEAD_RATE;
                pmWastedMemOverhead += VM.MEM_OVERHEAD;
            }
            // add up the remain CPU and Memory in this PM
            pmWastedCpu += pm.getCpuRemain();
            pmWastedMem += pm.getMemRemain();

            // calculate the wasted resources and overheads
            currentWastedCpu += pmWastedCpu;
            currentWastedMem += pmWastedMem;

            currentWastedCpuOverhead += pmWastedCpuOverhead;
            currentWastedMemOverhead += pmWastedMemOverhead;

        }

        wastedCpuOverhead.add(currentWastedCpuOverhead);
        wastedCPU.add(currentWastedCpu);
        wastedMem.add(currentWastedMem);
        wastedMemOverhead.add(currentWastedMemOverhead);
    }

    public void writeWaste(String path){
        try {
            writeFile.writeWaste(path, wastedCPU, wastedCpuOverhead, wastedMem, wastedMemOverhead);
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
