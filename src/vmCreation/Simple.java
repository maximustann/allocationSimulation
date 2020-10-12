package vmCreation;
import dataCenterEntity.*;
import operationInterface.VMCreation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Simple implements VMCreation {

    // The flag decides which resource we used to sort the VMs
    int flag;
    public Simple(int resource){
        if(resource == 0){
            flag = 0; // CPU
        } else{
            flag = 1; // Memory
        }
    }

    /**
     *
     * @param container
     * @return the new VM that we created
     */
    public VM execute(DataCenterInterface dataCenter, Container container){
        double[] vmCpu = dataCenter.getVmCpu();
        double[] vmMem = dataCenter.getVmMem();

        // we first sort the VMs according to their CPU or memory
        ArrayList<double[]> vmTypes = new ArrayList<>();
        for(int i = 0; i < vmCpu.length; ++i){
            vmTypes.add(new double[]{vmCpu[i], vmMem[i]});
        }

        Collections.sort(vmTypes, new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                int condition = 0;
                if(o1[flag] - o2[flag] > 0.0) condition = 1;
                else if(o1[flag] - o2[flag] < 0.0) condition = -1;
                else condition = 0;
                return condition;
            }
        });


        VM vm = null;
        // Search through the VM array from the smallest to the largest
        // until we find the smallest VM which has enough capacity to host the container
        for(int i = 0; i < vmCpu.length; ++i){

            // If this vm is capable to host the container
            // include the container's resource requirement and the VM overhead
//            System.out.println("vmCpu[" + i + "]" + vmCpu[i] + " ,vmMem[" + i + "]" + vmMem[i]);
//            System.out.println("container.getCpuUsed + VM.CPU_OVERHEAD_RATE * vmCPU[" + i + "] = " + (container.getCpuUsed() + VM.CPU_OVERHEAD_RATE * vmCpu[i]));
//            System.out.println("container.getMemUsed + VM.MEM_OVERHEAD = " + (container.getMemUsed() + VM.MEM_OVERHEAD));
            if(vmTypes.get(i)[0] >= (container.getCpuUsed() + VM.CPU_OVERHEAD_RATE * vmCpu[i])
                        && vmTypes.get(i)[1] >= (container.getMemUsed() + VM.MEM_OVERHEAD)){

                int type = 0;
                for(int j = 0; j < vmCpu.length; ++j){
                    if(vmTypes.get(i)[0] == vmCpu[j] && vmTypes.get(i)[1] == vmMem[j]) {
                        type = j;
                        break;
                    }
                }
                // We create a new vm in this type i, starts from 0
                vm = new VM(vmTypes.get(i)[0], vmTypes.get(i)[1], type);
//                System.out.println("create a VM cpu: " + vmCpu[i] + ", mem: " + vmMem[i]);

                // immediately break the process
                break;
            }
        }

        // If vm has not been created, that means something wrong happened
        if(vm == null){
            System.out.println("Error: No VM is suitable!");
            System.out.println("container CPU = " + container.getCpuUsed());
            System.out.println("container Mem = " + container.getMemUsed());
        }

        // return the vm
        return vm;
    }
}
