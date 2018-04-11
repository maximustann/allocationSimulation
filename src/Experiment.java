
import FileHandlers.ReadFile;
import java.io.IOException;
import java.util.Arrays;
import DataCenterEntity.*;
import OperationInterface.*;
import PM_Creation.SimplePM;
import VM_Allocation.*;
import VM_Creation.Simple;
import VM_Selection.FirstFitSelection;

public class Experiment {
    public static void main(String[] args) throws IOException {


        // test from case 0 to 40
        int[] testCases = {0, 1};

        // Each test case may have a different size (number of containers to be allocated)
        int[] testCaseSize = new int[1];

        testCaseSize[0] = 20;

        // In this case, the first 20 test cases have 20 containers, then, 40 containers
//        Arrays.fill(testCaseSize, 0, 20, 20);
//        Arrays.fill(testCaseSize, 20, 40, 40);

        // we have 5 types of Virtual Machines (VMs)
        int vmTypes = 5;

        // These are the addresses of test cases
        String base = "/Users/maximustann/Work/allocationSimulation/BilevelData/dataset";
        String PMConfig = base + "/PMConfig.csv";
        String VMConfig = base + "/VMConfig.csv";

        // Read files from disk
        ReadFile readFiles = new ReadFile(vmTypes, testCases, testCaseSize, PMConfig, base, VMConfig);
        double pmCpu = readFiles.getPMCpu();
        double pmMem = readFiles.getPMMem();
        double pmEnergy = readFiles.getPMEnergy();
        double[] vmMem = readFiles.getVMMem();
        double[] vmCpu = readFiles.getVMCpu();

        // We set the idle Physical machine (PM) account for 70% energy consumption compared with a full PM.
        double k = 0.7;

        // Four allocation strategies will be used in the experiments
        VMCreation simple = new Simple();
        VMAllocation ff_allocation = new FirstFitAllocation();
        VMSelection ff_selection = new FirstFitSelection();
        PMCreation simpleCreation = new SimplePM();





        // Experiment starts here,
        // We run the test case from 1 to N
        for(int testCase = testCases[0]; testCase < testCases[1]; ++testCase){
            System.out.println("testCase: " + (testCase + 1));

            // For each test case, we create a new empty data center
            DataCenter myDataCenter = new DataCenter(
                                        pmEnergy, k, pmCpu, pmMem,
                                        vmCpu, vmMem,
                                        ff_allocation, ff_selection, simple, simpleCreation);


            // We have already read all the files at the beginning.
            // Now we just retrieve all the information from readFiles object
            double[] taskCpu = readFiles.getTaskCpu(testCase);
            double[] taskMem = readFiles.getTaskMem(testCase);
            int[] taskOS = readFiles.getTaskOS(testCase);

            // For each testCase, we need to send container one by one
            // After each container allocation, the data center prints its energy consumption
            for(int i = 0; i < testCaseSize[testCase]; ++i) {
                System.out.println(taskCpu[i] + " : " + taskMem[i] + " : " + taskOS[i]);
                // ID starts from 1
                myDataCenter.receiveContainer(new Container(taskCpu[i], taskMem[i], taskOS[i], i));
                myDataCenter.print();
            }
        }


//        WriteFileBilevel writeFiles = new WriteFileBilevel(fitnessAddr, timeAddr);




        System.out.println("Done!");
    }



}
