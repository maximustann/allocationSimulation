import AllocationMethod.AnyFit;
import AllocationMethod.BestFit;
import AllocationMethod.BestFit_Sub;
import AllocationMethod.FirstFit;
import DataCenterEntity.Container;
import DataCenterEntity.DataCenter;
import DataCenterEntity.DataCenterCombined;
import FileHandlers.ReadConfigures;
import FileHandlers.ReadFile;
import FileHandlers.WriteFile;
import FitnessFunction.*;
import OperationInterface.Allocation;
import OperationInterface.PMCreation;
import OperationInterface.VMCreation;
import OperationInterface.VMSelectionCreation;
import PM_Creation.SimplePM;
import Preprocessing.LinearNormalize;
import Preprocessing.Normalization;
import SeletionCreationMixedMethod.BestFitFramework;
import SeletionCreationMixedMethod.NonAnyFitFramework;
import VM_Creation.LargestC;
import VM_Creation.OSProportion;
import VM_Creation.SimpleC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Experiment_selection_creation_combined {
    public static void main(String[] args) throws IOException {
        // Test Scenarios
        final int REALWORLD_SMALL = 0;
        final int BALANCED_DATA_SMALL = 1;
        final int BALANCED_VM_SMALL = 2;
        final int REALWORLD = 3;
        final int BALANCED_DATA = 4;

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
                        SUB_METHOD,
                        SUB_METHOD,
                        SIMPLE,
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

        // test from case 50 to 100, always, because we have used 0 to 49 to train
        int[] testCases = {50, 100};

        // Each test case may have a different size (number of containers to be allocated)
        int[] testCaseSize = new int[50];


        // we always have 5 types of Virtual Machines (VMs)
        int vmTypes = 5;

        // These are the addresses of test cases
//        String base = "/Users/maximustann/Work/allocationSimulation/BilevelData/dataset";
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
            case 0: // Realworld small
            case 1: // Balanced container (data) small
                VMConfig = "VMConfig/VMConfig_xSmall";
                PMConfig = "PMConfig/PMConfig_xSmall";
                break;
            case 2: // Balanced container and VM small
                VMConfig = "VMConfig/VMConfig_xSmall_balanced";
                PMConfig = "PMConfig/PMConfig_xSmall_balanced";
                break;

            case 3: // Real world
            case 4: // Balanced container
                VMConfig = "VMConfig/VMConfig_small";
                PMConfig = "PMConfig/PMConfig_small";
                break;
            default:
                PMConfig = "PMConfig/PMConfig_xSmall";
                VMConfig = "VMConfig/VMConfig_xSmall";
        }
        String VMConfigPath = ConfigPath + VMConfig + ".csv";
        String PMConfigPath = ConfigPath + PMConfig + ".csv";



        String osChoosed = null;
        switch(osChoice){
            case 1: osChoosed = "OS1"; break;
            case 2: osChoosed = "OS2"; break;
            case 3: osChoosed = "OS3"; break;
            case 5: osChoosed = "OS5"; break;
            default: osChoosed = "OS3";
        }

        osProPath += (osChoosed + "/probability.csv");

        String testCaseChoosed = null;
        String testCaseRoot = null;
        switch(testCaseSizeChoice){
            case 0:
                    testCaseRoot = "Container80";
                    testCaseChoosed = testCaseRoot + "/";
                    testCaseSize[0] = 80;
                    Arrays.fill(testCaseSize, 0, 50, 80);
                    break;
            case 1:
                    testCaseRoot = "Container200";
                    switch (testScenario){
                        case 0: // realworld smalL
                            testCaseChoosed = testCaseRoot + "_REALWORLD_SMALL/";
                            break;
                        case 1: // balanced container (data) small
                            testCaseChoosed = testCaseRoot + "_BALANCED_SMALL/";
                            break;
                        case 2: // balanced container and VM small
                            testCaseChoosed = testCaseRoot + "_BALANCED_VM_SMALL/";
                            break;

                        default:
                            testCaseChoosed = testCaseRoot + "_REALWORLD_SMALL/";
                    }

                    testCaseSize[0] = 200;
                    Arrays.fill(testCaseSize, 0, 50, 200);
                    break;
            case 2:
                    testCaseRoot = "Container5000";
                    testCaseChoosed = testCaseRoot + "/";
                    testCaseSize[0] = 5000;
                    Arrays.fill(testCaseSize, 0, 50, 5000);
                    break;
            default:
                    testCaseRoot = "Container80";
                    testCaseChoosed = testCaseRoot + "/";
                    testCaseSize[0] = 80;
                    Arrays.fill(testCaseSize, 0, 50, 80);
        }
        testCaseFilesPath += testCaseChoosed;
        osPath += (osChoosed + testCaseRoot + "/");
        initEnvPath += (osChoosed + "/" + testCaseChoosed);
        System.out.println("initEnvPath = " + initEnvPath);


        // WriteFile instance
        WriteFile writer = new WriteFile();

        // Read files from disk
        ReadFile readFiles = new ReadFile(
                vmTypes, osChoice, testCases,
                testCaseSize, PMConfigPath, VMConfigPath,
                testCaseFilesPath, osPath, osProPath);

        // Read initialization files from disk
        ReadConfigures readEnvConfig = new ReadConfigures();
//
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



        // allocationFitnessFunc is used in vmAllocation: allocate VMs to PMs
        Fitness allocationFitnessfunc = null;
        switch(allocationFitnessChoice){
            case 0: allocationFitnessfunc = new SubMethod(norm); break;
            case 1: allocationFitnessfunc = new SumMethod(norm); break;
            case 2: allocationFitnessfunc = new MixedMethod(norm); break;
            case 3: allocationFitnessfunc = new EvolvedMethod(norm); break;
            case 4: allocationFitnessfunc = new DivMethod(norm);break;
            default: allocationFitnessfunc = new SubMethod(norm);
        }


        // VM creation rule is still used in BestFit
        VMCreation vmCreation = null;
        switch (vmCreationChoice){
            case 0: vmCreation = new SimpleC(); break;
            case 1: vmCreation = new LargestC(); break;
            case 2: vmCreation = new OSProportion(); break;
            default: vmCreation = new SimpleC();
        }

        VMSelectionCreation selectionCreation = null;
        switch(selectionFitnessChoice){
            case 0: // ANYFIT framework
                selectionCreation = new BestFitFramework(allocationFitnessfunc, vmCreation);
                break;
            case 3: // NON-ANYFIT framework, Evolved method
                selectionCreation = new NonAnyFitFramework(new EvolvedMethod(norm));
                break;
            default:
                selectionCreation = new BestFitFramework(allocationFitnessfunc, vmCreation);
        }

//        VMSelectionCreation evoSelectionCreation = new SeletionCreationMixedMethod.BestFit(allocationFitnessfunc);

        // vmAllocationRule is used when allocating VMs to PMs
        Allocation vmAllocationRule = null;
        switch (vmAllocationChoice){
            case 0: // BestFit
                vmAllocationRule = new BestFit_Sub(allocationFitnessfunc);
                break;
            case 1: // FirstFit
                vmAllocationRule = new FirstFit();
                break;
            default: // FirstFit
                vmAllocationRule = new FirstFit();
        }


        // PM Creation rule remains SimplePM
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
            DataCenterCombined myDataCenter = new DataCenterCombined(
                    pmEnergy, k, pmCpu, pmMem, vmCpu, vmMem, osProb,
                    vmAllocationRule, selectionCreation, pmCreationRule);

//            Initialize the data center
            ArrayList<Double[]> pmList = initPM.get(testCase);
            ArrayList<Double[]> vmList = initVM.get(testCase);
            ArrayList<Double[]> containerList = initContainer.get(testCase);
            ArrayList<Double[]> osList = initOS.get(testCase);

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
//                 ID starts from 1
                myDataCenter.receiveContainer(new Container(taskCpu[i], taskMem[i], taskOS[i], i));
//                myDataCenter.print();
            }
            //
            myDataCenter.monitor.writeEnergy(resultPath);
            myDataCenter.monitor.writeAccEnergy(resultPath);
            myDataCenter.monitor.writeWaste(resultPath);
            myDataCenter.print();
//
            System.out.println("Current Energy: " + myDataCenter.calEnergy());
            System.out.println("Accumulated Energy: " + myDataCenter.getAccumulatedEnergyConsumption());
//             Round to two decimal point
            accumulatedEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0);
//
//            for each test case calculate the average acc energy
            averageAccEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0 / 200);
//             Destroy this data Center, save status data
            myDataCenter.selfDestruction(resultPath);
        } // End test Case


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
