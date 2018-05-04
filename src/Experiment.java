
import FileHandlers.ReadFile;

import java.io.File;
import java.io.IOException;

import DataCenterEntity.*;
import FitnessFunction.SubMethod;
import OperationInterface.*;
import PM_Creation.SimplePM;
import Preprocessing.LinearNormalize;
import Preprocessing.Normalization;
import FitnessFunction.Fitness;
import FitnessFunction.SumMethod;
import VM_Allocation.*;
import VM_Selection.BestFitSelection;
import VM_Creation.*;
import VM_Selection.FirstFitSelection;

public class Experiment {
    public static void main(String[] args) throws IOException {

        // Fitness functions
        final int SUB_METHOD = 0;
        final int SUM_METHOD = 1;


        // vm creation rules
        final int SIMPLE = 0;
        final int LARGEST = 1;


        // vm selection rules
        final int BESTFIT = 0;
        final int FIRSTFIT = 1;

        // vm allocation rules
        final int FIRSTFITALLOVM = 0;

        // pm creation rules
        final int SIMPLEPM = 0;


        // test case size
        final int SMALL = 0;
        final int MEDIUM = 1;
        final int LARGE = 2;

        // os choice
        final int SINGLE = 0;
        final int THREE = 1;
        final int FIVE = 2;

        experimentRunner(
                        SMALL,
                        SINGLE,
                        SUB_METHOD,
                        SIMPLE,
                        FIRSTFITALLOVM,
                        FIRSTFIT,
                        SIMPLEPM);
    }



    public static void experimentRunner(
                                int testCaseSizeChoice,
                                int osChoice,
                                int fitnessChoice,
                                int vmCreationChoice,
                                int vmAllocationChoice,
                                int vmSelectionChoice,
                                int pmCreationChoice
                                ){

        // test from case 0 to 40
        int[] testCases = {0, 1};
//        int[] testCases = {0, 10};
//        int[] testCases = {0, 200};

        // Each test case may have a different size (number of containers to be allocated)
        int[] testCaseSize = new int[1];
        switch(testCaseSizeChoice){
            case 0: testCaseSize[0] = 80; break;
            case 1: testCaseSize[0] = 200; break;
            case 2: testCaseSize[0] = 5000; break;
            default: testCaseSize[0] = 80;
        }

//        int[] testCaseSize = new int[10];

        // In this case, the first 20 test cases have 20 containers, then, 40 containers
//        Arrays.fill(testCaseSize, 0, 10, 20);
//        Arrays.fill(testCaseSize, 50, 100, 80);
//        Arrays.fill(testCaseSize, 100, 150, 200);
//        Arrays.fill(testCaseSize, 150, 200, 5000);


        // we have 5 types of Virtual Machines (VMs)
        int vmTypes = 5;

        // These are the addresses of test cases
//        String base = "/Users/maximustann/Work/allocationSimulation/BilevelData/dataset";
        String base = "/home/tanboxi/workspace/containerAllocation/BilevelData";
        String PMConfigPath = base + "/PM_DataSet/";
        String VMConfigPath = base + "/VM_DataSet/";
        String testCaseFilesPath = base + "/Container_DataSet/";
        String osConfigPath = base + "/OS_DataSet/";
        String resultBase= "/local/scratch/tanboxi/containerAllocationResults/";


        /*
            VM size select from:

            VMConfig_small
            VMConfig_medium
            VMConfig_large
            VMConfig_xLarge

         */
        String VMConfig = "VMConfig_small";
//        VMConfig += "VMConfig_medium";
//        String VMConfig = "VMConfig_xLarge";
        VMConfigPath = VMConfigPath + VMConfig + ".csv";



        /*
            PM size select from:

            PMConfig_small
            PMConfig_medium
            PMConfig_large
            PMConfig_xLarge

         */
        String PMConfig = "PMConfig_small";
//        String PMConfig = "PMConfig_xLarge";
        PMConfigPath = PMConfigPath + PMConfig + ".csv";

        // Artificial or real world data, select from "artificial" or "real"
//      String testCaseFiles = "artificial";
        String testCaseFiles = "real";
        testCaseFilesPath = testCaseFilesPath + testCaseFiles + "/";

        // This is the container's size related to vm config set.
        // select from "1" to "4"
//        String VMConfigSet = "80/fiveOS";

        String VMConfigSet = testCaseSize[0] + "/";
        String osChoosed = null;
        switch (osChoice){
            case 0: osChoosed = "singleOS"; break;
            case 1: osChoosed = "threeOS"; break;
            case 2: osChoosed = "fiveOS"; break;
            default: osChoosed = "singleOS";
        }
        VMConfigSet = VMConfigSet + osChoosed;

//        String VMConfigSet = "4";
        testCaseFilesPath = testCaseFilesPath + VMConfigSet + "/";

        // OS type, select from single, three, five
//        osConfigPath = "/home/tanboxi/workspace/containerAllocation/BilevelData/Container_DataSet/real/80/fiveOS/";
        osConfigPath = "/home/tanboxi/workspace/containerAllocation/BilevelData/Container_DataSet/real/" + testCaseSize[0] + "/" + osChoosed + "/";


        // Read files from disk
        ReadFile readFiles = new ReadFile(
                vmTypes, testCases,
                testCaseSize, PMConfigPath, VMConfigPath,
                testCaseFilesPath, osConfigPath);
        double pmCpu = readFiles.getPMCpu();
        double pmMem = readFiles.getPMMem();
        double pmEnergy = readFiles.getPMEnergy();
        double[] vmMem = readFiles.getVMMem();
        double[] vmCpu = readFiles.getVMCpu();

        // We set the idle Physical machine (PM) account for 70% energy consumption compared with a full PM.
        double k = 0.7;


        // Normalization method, pass the vm configuration
        Normalization norm = new LinearNormalize(vmCpu, vmMem);

        // fitness Function List
        Fitness fitnessfunc = null;
        switch (fitnessChoice){
            case 0: fitnessfunc = new SumMethod(norm); break;
            case 1: fitnessfunc = new SubMethod(norm); break;
            default: fitnessfunc = new SumMethod(norm);
        }


        /* Four allocation strategies will be used in the experiments
            VMCreation: when there is no suitable VM exist. Create a new one.
                        VMCreation decides the size of VM.

            VMSelection: First we select from existed VMs to allocate a container.
                        VMSelection choose the suitable VM.

            VMAllocation: Choose the suitable PM to allocate VM

            PMCreation: Create a PM when there is suitable PM exist
         */

        VMCreation vmCreationRule = null;
        switch (vmCreationChoice){
            case 0: vmCreationRule = new Simple(); break;
            case 1: vmCreationRule = new Largest(); break;
            default: vmCreationRule =  new Simple();
        }

        VMAllocation vmAllocationRule = null;
        switch (vmAllocationChoice){
            case 0: vmAllocationRule = new FirstFitAllocation(); break;
            default: vmAllocationRule = new FirstFitAllocation();
        }

        VMSelection vmSelectionRule = null;
        switch (vmSelectionChoice){
            case 0: vmSelectionRule = new BestFitSelection(fitnessfunc); break;
            case 1: vmSelectionRule = new FirstFitSelection(); break;
            default: vmSelectionRule = new BestFitSelection(fitnessfunc);
        }

        PMCreation pmCreationRule = null;
        switch (pmCreationChoice){
            case 0: pmCreationRule = new SimplePM();
        }





        // Experiment starts here,
        // We run the test case from 1 to N
        for(int testCase = testCases[0]; testCase < testCases[1]; ++testCase){
            System.out.println("==================================================");
            System.out.println("testCase: " + (testCase + 1));

            // Create result folder
            String resultPath = resultBase + testCase + "/";
            File rB = new File(resultPath);
            if(!rB.exists()){
                rB.mkdir();
            }

            // For each test case, we create a new empty data center
            DataCenter myDataCenter = new DataCenter(
                    resultPath,
                    pmEnergy, k, pmCpu, pmMem, vmCpu, vmMem,
                    vmAllocationRule, vmSelectionRule, vmCreationRule, pmCreationRule);

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
                myDataCenter.print();
            }
//            myDataCenter.print();


            // Reset Counters
            PM.resetCounter();
            VM.resetCounter();
        }


//        WriteFileBilevel writeFiles = new WriteFileBilevel(fitnessAddr, timeAddr);


        System.out.println("Done!");


    }




}
