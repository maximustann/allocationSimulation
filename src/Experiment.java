
import FileHandlers.ReadConfigures;
import FileHandlers.ReadFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import DataCenterEntity.*;
import FileHandlers.WriteFile;
import FitnessFunction.*;
import OperationInterface.*;
import PM_Creation.SimplePM;
import Preprocessing.LinearNormalize;
import Preprocessing.Normalization;
import AllocationMethod.*;
import VM_Creation.*;

public class Experiment {
    public static void main(String[] args) throws IOException {

        // Test Scenarios
        final int REALWORLD_SMALL = 0;
        final int BALANCED_DATA_SMALL = 1;
        final int BALANCED_VM_SMALL = 2;
        // Fitness functions
        final int SUB_METHOD = 0;
        final int SUM_METHOD = 1;
        final int MIX_METHOD = 2;
        final int EVO_METHOD = 3;
        final int DIV_METHOD = 4;


        // vm creation rules
        final int SIMPLE = 0;
        final int LARGEST = 1;
        final int OSPROB = 2;


        // vm selection rules
        final int BESTFIT = 0;
        final int FIRSTFIT = 1;
        final int ANYFIT = 2;

        // pm creation rules
        final int SIMPLEPM = 0;


        // test case size
        final int SMALL = 0;
        final int MEDIUM = 1;
        final int LARGE = 2;

        // os choice
        final int SINGLE = 1;
        final int TWO = 2;
        final int THREE = 3;
        final int FIVE = 5;

        experimentRunner(
                        REALWORLD_SMALL,
                        MEDIUM,
                        TWO,
                        EVO_METHOD,
                        SUB_METHOD,
                        LARGEST,
                        FIRSTFIT,
                        BESTFIT,
                        SIMPLEPM);
    }



    public static void experimentRunner(
                                int testScenario,
                                int testCaseSizeChoice,
                                int osChoice,
                                int selectionFitnessChoice,
                                int allocationFitnessChoice,
                                int vmCreationChoice,
                                int vmAllocationChoice,
                                int vmSelectionChoice,
                                int pmCreationChoice
                                ){

        // test from case 50 to 100, always, because we used 0 to 49 to train
        int[] testCases = {50, 100};

        // Each test case may have a different size (number of containers to be allocated)
        int[] testCaseSize = new int[50];


        // we always have 5 types of Virtual Machines (VMs)
        int vmTypes = 5;

        // These are the addresses of test cases
        String base = "/home/tanboxi/IdeaProjects/MaxTan/data/";
        String ConfigPath = base + "/baseConfig/";
        String testCaseFilesPath = base + "/containerData/";




        String initEnvPath = base + "/InitEnv/";
        String osProPath = base + "/OSPro/";
        String osPath = base + "/OSData/";
        String resultBase= "/local/scratch/tanboxi/containerAllocationResults/";


        String VMConfig;
        String PMConfig;
        switch (testScenario){
            case 0: // Real world small
            case 1: // Balanced container (data)
                VMConfig = "VMConfig/VMConfig_xSmall";
                PMConfig = "PMConfig/PMConfig_xSmall";
                break;
            case 2: // Balanced container and VM
                VMConfig = "VMConfig/VMConfig_xSmall_balanced";
                PMConfig = "PMConfig/PMConfig_xSmall_balanced";
                break;

            default:
                PMConfig = "PMConfig/PMConfig_xSmall";
                VMConfig = "VMConfig/VMConfig_xSmall";
        }
        String VMConfigPath = ConfigPath + VMConfig + ".csv";
        String PMConfigPath = ConfigPath + PMConfig + ".csv";



        String chosenOS = null;
        switch(osChoice){
            case 1: chosenOS = "OS1"; break;
            case 2: chosenOS = "OS2"; break;
            case 3: chosenOS = "OS3"; break;
            case 5: chosenOS = "OS5"; break;
            default: chosenOS = "OS3";
        }

        osProPath += (chosenOS + "/probability.csv");

        String chosenTestCase = null;
        String testCaseRoot = null;
        switch(testCaseSizeChoice){
            case 0:
                    testCaseRoot = "Container80";
                    chosenTestCase = testCaseRoot + "/";
                    testCaseSize[0] = 80;
                    Arrays.fill(testCaseSize, 0, 50, 80);
                    break;
            case 1:
                testCaseRoot = "Container200";
                switch (testScenario){
                    case 0: // realworld smalL
                         chosenTestCase = testCaseRoot + "_REALWORLD_SMALL/";
                         break;
                    case 1: // balanced container (data) small
                        chosenTestCase = testCaseRoot + "_BALANCED_SMALL/";
                        break;
                    case 2: // balanced container and VM small
                        chosenTestCase = testCaseRoot + "_BALANCED_VM_SMALL/";
                        break;

                    default:
                        chosenTestCase = testCaseRoot + "_REALWORLD_SMALL/";
                }
                    testCaseSize[0] = 200;
                    Arrays.fill(testCaseSize, 0, 50, 200);
                    break;
            case 2:
                    testCaseRoot = "Container5000";
                    chosenTestCase = testCaseRoot + "/";
                    testCaseSize[0] = 5000;
                    Arrays.fill(testCaseSize, 0, 50, 5000);
                    break;
            default:
                    testCaseRoot = "Container80";
                    chosenTestCase = testCaseRoot + "/";
                    testCaseSize[0] = 80;
                    Arrays.fill(testCaseSize, 0, 50, 80);
        }
        testCaseFilesPath += chosenTestCase;
        osPath += (chosenOS + testCaseRoot + "/");
        initEnvPath += (chosenOS + "/" + chosenTestCase);


        // WriteFile instance
        WriteFile writer = new WriteFile();

        // Read files from disk
        ReadFile readFiles = new ReadFile(
                vmTypes, osChoice, testCases,
                testCaseSize, PMConfigPath, VMConfigPath,
                testCaseFilesPath, osPath, osProPath);

        ReadConfigures readEnvConfig = new ReadConfigures();

        ArrayList <ArrayList> initPM = readEnvConfig.testCases(initEnvPath, "pm", testCases);
        ArrayList <ArrayList> initVM = readEnvConfig.testCases(initEnvPath, "vm", testCases);
        ArrayList <ArrayList> initOS = readEnvConfig.testCases(initEnvPath, "os", testCases);
        ArrayList <ArrayList> initContainer = readEnvConfig.testCases(initEnvPath, "container", testCases);

        double pmCpu = readFiles.getPMCpu();
        double pmMem = readFiles.getPMMem();
        double pmEnergy = readFiles.getPMEnergy();
        double[] vmMem = readFiles.getVMMem();
        double[] vmCpu = readFiles.getVMCpu();
        double[] osProb = readFiles.getOsProb();

        // We set the idle Physical machine (PM) account for 70% energy consumption compared with a full PM.
        double k = 0.7;


        // Normalization method, pass the vm configuration
        Normalization norm = new LinearNormalize(pmCpu, pmMem);

        // selection fitness Function List
        Fitness selectionFitnessFunction = null;
        switch (selectionFitnessChoice){
            case 0: selectionFitnessFunction = new SubMethod(norm); break;
            case 1: selectionFitnessFunction = new SumMethod(norm); break;
            case 2: selectionFitnessFunction = new MixedMethod(norm); break;
            case 3: selectionFitnessFunction = new EvolvedMethod(norm); break;
            case 4: selectionFitnessFunction = new DivMethod(norm); break;
            default: selectionFitnessFunction = new SubMethod(norm);
        }

        // allocation fitness Function List
        Fitness allocationFitnessFunction = null;
        switch(allocationFitnessChoice){
            case 0: allocationFitnessFunction = new SubMethod(norm); break;
            case 1: allocationFitnessFunction = new SumMethod(norm); break;
            case 2: allocationFitnessFunction = new MixedMethod(norm); break;
            case 3: allocationFitnessFunction = new EvolvedMethod(norm); break;
            case 4: allocationFitnessFunction = new DivMethod(norm);break;
            default: allocationFitnessFunction = new SubMethod(norm);
        }


        /* Four allocation strategies will be used in the experiments
            VMCreation: when there is no suitable VM exist. Create a new one.
                        VMCreation decides the size of VM.

            Allocation: Choose VM/PM from existed VM/PM to allocate a container/VM

            PMCreation: Create a PM when there is suitable PM exist
         */

        VMCreation vmCreationRule = null;
        switch (vmCreationChoice){
            case 0: vmCreationRule = new Simple(); break;
            case 1: vmCreationRule = new Largest(); break;
            case 2: vmCreationRule = new OSProportion(); break;
            default: vmCreationRule =  new Simple();
        }

        Allocation vmAllocationRule = null;
        switch (vmAllocationChoice){
            case 0:
                // both sub rule and sum rule require the residual or balance to be minimized
                if(allocationFitnessChoice == 1 || allocationFitnessChoice == 0 || allocationFitnessChoice == 2)
                    vmAllocationRule = new BestFit_Sub(allocationFitnessFunction);
                else vmAllocationRule = new BestFit(allocationFitnessFunction);
                break;
            case 1: vmAllocationRule = new FirstFit(); break;
            default: vmAllocationRule = new FirstFit();
        }

        Allocation vmSelectionRule = null;
        switch (vmSelectionChoice){
            case 0:
                // both sub rule and sum rule require the residual or balance to be minimized
                if(selectionFitnessChoice == 1 || selectionFitnessChoice == 0 || selectionFitnessChoice == 2)
                    vmSelectionRule = new BestFit_Sub(selectionFitnessFunction);
                else vmSelectionRule = new BestFit(selectionFitnessFunction);
                break;
            case 1: vmSelectionRule = new FirstFit(); break;
            case 2: vmSelectionRule = new AnyFit(); break;
            default: vmSelectionRule = new FirstFit();
        }

        PMCreation pmCreationRule = null;
        switch (pmCreationChoice){
            case 0: pmCreationRule = new SimplePM();
        }

        // accumulated energy arrayList for each test case
        ArrayList<Double> accumulatedEnergyList = new ArrayList<>();
        ArrayList<Double> averageAccEnergyList = new ArrayList<>();



        // Experiment starts here,
        // We run the test case from N to M
        for(int testCase = 0; testCase + testCases[0] < testCases[1]; ++testCase){
            System.out.println("==================================================");
            System.out.println("testCase: " + (testCase + 1));

//            String resultPath = resultBase;
            // Create result folder
            String resultPath = resultBase + testCase + "/";
            File rB = new File(resultPath);
            if(!rB.exists()){
                rB.mkdir();
            }

            // For each test case, we create a new empty data center
            DataCenter myDataCenter = new DataCenter(
                    pmEnergy, k, pmCpu, pmMem, vmCpu, vmMem, osProb,
                    vmAllocationRule, vmSelectionRule, vmCreationRule, pmCreationRule);

//            Initialize the data center
            ArrayList<Double[]> pmList = initPM.get(testCase);
            ArrayList<Double[]> vmList = initVM.get(testCase);
            ArrayList<Double[]> containerList = initContainer.get(testCase);
            ArrayList<Double[]> osList = initOS.get(testCase);
//
            myDataCenter.initialization(pmList, vmList, containerList, osList);
            myDataCenter.print();
//            System.out.println("After initialization = " + myDataCenter.calEnergy());

            // We have already read all the files at the beginning.
            // Now we just retrieve all the information from readFiles object
            double[] taskCpu = readFiles.getTaskCpu(testCase);
            double[] taskMem = readFiles.getTaskMem(testCase);
            int[] taskOS = readFiles.getTaskOS(testCase);

            // For each testCase, we need to send container one by one
            // After each container allocation, the data center prints its energy consumption
            for(int i = 0; i < testCaseSize[testCase]; ++i) {
//                System.out.println();
//                System.out.println("task = " + i + ", CPU = " + taskCpu[i] + ", MEM = " + taskMem[i] + ", OS = " + taskOS[i]);
                // ID starts from 1
                myDataCenter.receiveContainer(new Container(taskCpu[i], taskMem[i], taskOS[i], i));
//                myDataCenter.print();
            }
            //
            myDataCenter.monitor.writeEnergy(resultPath);
            myDataCenter.monitor.writeAccEnergy(resultPath);
            myDataCenter.print();

            System.out.println("Current Energy: " + myDataCenter.calEnergy());
            System.out.println("Accumulated Energy: " + myDataCenter.getAccumulatedEnergyConsumption());
//             Round to two decimal point
            accumulatedEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0);
//            for each test case calculate the average acc energy
            averageAccEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0 / 200);
//             Destroy this data Center, save status data
            myDataCenter.selfDestruction(resultPath);
        }


        try {
            writer.writeAccumulatedEnergy(resultBase, accumulatedEnergyList);
        } catch (IOException e1){
            e1.printStackTrace();
        }
//
        double sumAccForAll = 0;
        for(Double testCase:averageAccEnergyList){
            sumAccForAll += testCase;
        }
//
        System.out.println("Average Incremental Energy for all test cases: " + sumAccForAll / 50);
        System.out.println("Done!");


    }




}
