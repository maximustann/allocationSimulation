
import FileHandlers.ReadFile;
import java.io.IOException;
import java.util.Arrays;
import DataCenterEntity.*;
import OperationInterface.*;
import PM_Creation.SimplePM;
import Preprocessing.LinearNormalize;
import Preprocessing.Normalization;
import Preprocessing.PreprocessingMethod;
import Preprocessing.SumMethod;
import VM_Allocation.*;
import VM_Selection.BestFitSelection;
import VM_Selection.FirstFitSelection;
import VM_Creation.*;

public class Experiment {
    public static void main(String[] args) throws IOException {


        // test from case 0 to 40
//        int[] testCases = {0, 1};
        int[] testCases = {0, 200};

        // Each test case may have a different size (number of containers to be allocated)
//        int[] testCaseSize = new int[1];
//
//        testCaseSize[0] = 20;
        int[] testCaseSize = new int[200];

        // In this case, the first 20 test cases have 20 containers, then, 40 containers
        Arrays.fill(testCaseSize, 0, 20, 20);
        Arrays.fill(testCaseSize, 20, 40, 40);
        Arrays.fill(testCaseSize, 40, 60, 60);
        Arrays.fill(testCaseSize, 60, 80, 80);
        Arrays.fill(testCaseSize, 80, 100, 100);
        Arrays.fill(testCaseSize, 100, 200, 1000);

        // we have 5 types of Virtual Machines (VMs)
        int vmTypes = 5;

        // These are the addresses of test cases
//        String base = "/Users/maximustann/Work/allocationSimulation/BilevelData/dataset";
        String base = "/home/tanboxi/workspace/containerAllocation/BilevelData/dataset";
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


        // Normalization method
        Normalization norm = new LinearNormalize(pmCpu, pmMem);

        // Preprocessing method
        PreprocessingMethod preMethod = new SumMethod(norm);


        // Four allocation strategies will be used in the experiments
//        VMCreation simple = new Simple();
        VMCreation largest = new Largest();
        VMAllocation ff_allocation = new FirstFitAllocation();
//        VMSelection ff_selection = new FirstFitSelection();
        VMSelection bf_selection = new BestFitSelection(preMethod);
        PMCreation simpleCreation = new SimplePM();





        // Experiment starts here,
        // We run the test case from 1 to N
        for(int testCase = testCases[0]; testCase < testCases[1]; ++testCase){
            System.out.println("==================================================");
            System.out.println("testCase: " + (testCase + 1));

//            // For each test case, we create a new empty data center
//            DataCenter myDataCenter = new DataCenter(
//                                        pmEnergy, k, pmCpu, pmMem,
//                                        vmCpu, vmMem,
//                                        ff_allocation, ff_selection, largest, simpleCreation);

//             For each test case, we create a new empty data center
            DataCenter myDataCenter = new DataCenter(
                                        pmEnergy, k, pmCpu, pmMem,
                                        vmCpu, vmMem,
                                        ff_allocation, bf_selection, largest, simpleCreation);

            // We have already read all the files at the beginning.
            // Now we just retrieve all the information from readFiles object
            double[] taskCpu = readFiles.getTaskCpu(testCase);
            double[] taskMem = readFiles.getTaskMem(testCase);
            int[] taskOS = readFiles.getTaskOS(testCase);

            // For each testCase, we need to send container one by one
            // After each container allocation, the data center prints its energy consumption
            for(int i = 0; i < testCaseSize[testCase]; ++i) {
//                System.out.println();
//                System.out.println(taskCpu[i] + " : " + taskMem[i] + " : " + taskOS[i]);
                // ID starts from 1
                myDataCenter.receiveContainer(new Container(taskCpu[i], taskMem[i], taskOS[i], i));
//                myDataCenter.print();
            }
            myDataCenter.print();

            // Reset Counters
            PM.resetCounter();
            VM.resetCounter();
        }


//        WriteFileBilevel writeFiles = new WriteFileBilevel(fitnessAddr, timeAddr);


        System.out.println("Done!");
    }



}
