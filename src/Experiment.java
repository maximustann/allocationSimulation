
import fileHandlers.ReadConfigures;
import fileHandlers.ReadFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import dataCenterEntity.*;
import fileHandlers.WriteFile;
import fitnessFunction.*;
import operationInterface.*;
import pmCreation.SimplePM;
import preprocessing.LinearNormalize;
import preprocessing.Normalization;
import allocationMethod.*;
import vmCreation.*;
import experimentScenarios.*;


public class Experiment {
    public static void main(String[] args){
        experimentRunner(
                        TestDataSet.REAL_WORLD_SMALL,
                        TestCaseSizes.MEDIUM,
                        OperatingSystems.TWO,
                        FitnessFunctions.EVO, // vm selection rule
                        FitnessFunctions.SUB, // pm selection rule
                        VMCreationRules.LARGEST,
                        SelectionRules.BESTFIT, // vm selection framework
                        SelectionRules.FIRSTFIT, // pm selection framework
                        PMCreationRules.LARGEST);
    }



    public static void experimentRunner(
                                TestDataSet testScenario,
                                TestCaseSizes testCaseSizeChoice,
                                OperatingSystems osChoice,
                                FitnessFunctions vmSelectionFitnessChoice,
                                FitnessFunctions pmSelectionFitnessChoice,
                                VMCreationRules vmCreationChoice,
                                SelectionRules vmSelectionChoice,
                                SelectionRules pmSelectionChoice,
                                PMCreationRules pmCreationChoice
                                ){

        // test from case 50 to 100, always, because we used 0 to 49 to train
        int[] testCases = {50, 100};

        // Each test case may have a different size (number of containers to be allocated)
        int[] testCaseSize = new int[50];


        // we always have 5 types of Virtual Machines (VMs)
        int vmTypes = 5;

        // We set the idle Physical machine (PM) account for 70% energy consumption compared with a full PM.
        double k = 0.7;

        // These are the addresses of test cases
        String base = "/home/tanboxi/IdeaProjects/MaxTan/data/";
        String ConfigPath = base + "/baseConfig/";
        String testCaseFilesPath = base + "/containerData/";
        String initEnvPath = base + "/InitEnv/";
        String osProPath = base + "/OSPro/";
        String osPath = base + "/OSData/";
        String resultBase= "/local/scratch/tanboxi/containerAllocationResults/";


        String VMConfig = testScenario.getVmConfig();
        String PMConfig = testScenario.getPmConfig();

        String VMConfigPath = ConfigPath + VMConfig + ".csv";
        String PMConfigPath = ConfigPath + PMConfig + ".csv";



        String chosenOS = osChoice.getDirectory();
        osProPath += (chosenOS + "/probability.csv");

        String testCaseRoot = testCaseSizeChoice.getDirectory();
        String chosenTestCase = testCaseRoot + testScenario.getDirectory() + "/";
        Arrays.fill(testCaseSize, 0, 50, testCaseSizeChoice.getTestSize());

        testCaseFilesPath += chosenTestCase;
        osPath += (chosenOS + testCaseRoot + "/");
        initEnvPath += (chosenOS + "/" + chosenTestCase);


        // WriteFile instance
        WriteFile writer = new WriteFile();

        // Read files from disk
        ReadFile readFiles = new ReadFile(
                vmTypes, osChoice.getNumOfOs(), testCases,
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



        // Normalization method, pass the vm configuration
        Normalization norm = new LinearNormalize(pmCpu, pmMem);

        // vm selection fitness Function List
        SelectionFitness vmSelectionFitnessFunction;
        switch (vmSelectionFitnessChoice){
            case SUB: vmSelectionFitnessFunction = new SubMethod(norm); break;
            case SUM: vmSelectionFitnessFunction = new SumMethod(norm); break;
            case MIX: vmSelectionFitnessFunction = new MixedMethod(norm); break;
            case EVO: vmSelectionFitnessFunction = new EvolvedMethod(norm); break;
            case DIV: vmSelectionFitnessFunction = new DivMethod(norm); break;
            default: vmSelectionFitnessFunction = new SubMethod(norm);
        }

        // pm selection fitness Function List
        SelectionFitness pmSelectionFitnessFunction;
        switch(pmSelectionFitnessChoice){
            case SUB: pmSelectionFitnessFunction = new SubMethod(norm); break;
            case SUM: pmSelectionFitnessFunction = new SumMethod(norm); break;
            case MIX: pmSelectionFitnessFunction = new MixedMethod(norm); break;
            case EVO: pmSelectionFitnessFunction = new EvolvedMethod(norm); break;
            case DIV: pmSelectionFitnessFunction = new DivMethod(norm);break;
            default: pmSelectionFitnessFunction = new SubMethod(norm);
        }


        /* Four allocation strategies will be used in the experiments
            VMCreation: when there is no suitable VM exist. Create a new one.
                        VMCreation decides the size of VM.

            Allocation: Choose VM/PM from existed VM/PM to allocate a container/VM

            PMCreation: Create a PM when there is suitable PM exist
         */

        VMCreation vmCreationRule;
        switch (vmCreationChoice){
            case JUSTFIT: vmCreationRule = new Simple(); break;
            case LARGEST: vmCreationRule = new Largest(); break;
            case OSPROB: vmCreationRule = new OSProportion(); break;
            default: vmCreationRule =  new Simple();
        }

        Allocation pmSelectionRule;
        switch (pmSelectionChoice){
            case BESTFIT:
                // both sub rule and sum rule require the residual or balance to be minimized
                switch (pmSelectionFitnessChoice){
                    case SUB:
                    case SUM:
                    case MIX:
                        pmSelectionRule = new BestFit_Sub(pmSelectionFitnessFunction);
                        break;
                    case EVO:
                    case DIV:
                        pmSelectionRule = new BestFit(pmSelectionFitnessFunction);
                        break;
                    default:
                        pmSelectionRule = new BestFit_Sub(pmSelectionFitnessFunction);
                }
            case FIRSTFIT: pmSelectionRule = new FirstFit(); break;
            default: pmSelectionRule = new FirstFit();
        }

        Allocation vmSelectionRule;
        switch (vmSelectionChoice){
            case BESTFIT:
                // both sub rule and sum rule require the residual or balance to be minimized
                switch (pmSelectionFitnessChoice){
                    case SUB:
                    case SUM:
                    case MIX:
                        vmSelectionRule = new BestFit_Sub(vmSelectionFitnessFunction);
                        break;
                    case EVO:
                    case DIV:
                        vmSelectionRule = new BestFit(vmSelectionFitnessFunction);
                        break;
                    default:
                        vmSelectionRule = new BestFit_Sub(vmSelectionFitnessFunction);
                }
                break;
            case FIRSTFIT: vmSelectionRule = new FirstFit(); break;
            case ANYFIT: vmSelectionRule = new AnyFit(); break;
            default: vmSelectionRule = new FirstFit();
        }

        PMCreation pmCreationRule;
        switch (pmCreationChoice){
            case LARGEST: pmCreationRule = new SimplePM();
            default: pmCreationRule = new SimplePM();
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
                    pmSelectionRule, vmSelectionRule, vmCreationRule, pmCreationRule);

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
//          Round to two decimal point
            accumulatedEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0);
//          for each test case calculate the average acc energy
            averageAccEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0 / 200);
//          Destroy this data Center, save status data
            myDataCenter.selfDestruction(resultPath);
        }


        try {
            writer.writeAccumulatedEnergy(resultBase, accumulatedEnergyList);
        } catch (IOException e1){
            e1.printStackTrace();
        }
        double sumAccForAll = 0;
        for(Double testCase:averageAccEnergyList){
            sumAccForAll += testCase;
        }
        System.out.println("Average Incremental Energy for all test cases: " + sumAccForAll / 50);
        System.out.println("Done!");


    }


}
