import dataCenterEntity.Container;
import dataCenterEntity.DataCenter;
import fileHandlers.ReadConfigures;
import fileHandlers.ReadExpression;
import fileHandlers.ReadFile;
import fileHandlers.WriteFile;
import fitnessFunction.*;
import operationInterface.PMCreation;
import operationInterface.VMCreation;
import operationInterface.HolderSelectionCreation;
import pmCreation.SimplePM;
import preprocessing.LinearNormalize;
import preprocessing.Normalization;
import seletionCreationMixedMethod.*;
import vmCreation.*;
import experimentScenarios.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Experiment {
    public static void main(String[] args){
        int run = Integer.parseInt(args[1]);
        String folder = args[0];

        experimentRunner(
                run,
                folder,
                TestDataSet.AUVERGRID_TEN,
                TestCaseSizes.xLarge,
                OperatingSystems.THREE,
                FitnessFunctions.EVO, // vm selection rule
                FitnessFunctions.SUB, // pm selection rule
                VMCreationRules.EVO,
                VMAllocationFramework.ANYFIT, // vm selection-creation framework, when select NONANYFIT framework,
                                                // the VMCreationRules will be automatically canceled.
                SelectionRules.FIRSTFIT, // pm selection framework,
                                        // If we choose FirstFit, then, the fitness functions of pm selection rule
                                        // will be ignored
                PMCreationRules.LARGEST); // No other choice
    }



    public static void experimentRunner(
            Integer run,
            String folder,
            TestDataSet testScenario,
            TestCaseSizes testCaseSizeChoice,
            OperatingSystems osChoice,
            FitnessFunctions vmSelectionFitnessChoice,
            FitnessFunctions pmSelectionFitnessChoice,
            VMCreationRules vmCreationChoice,
            VMAllocationFramework vmAllocationFrameworkChoice,
            SelectionRules pmSelectionChoice,
            PMCreationRules pmCreationChoice
                                ){

        // test from case 50 to 100, always, because we have used 0 to 49 to train
        int[] testCases = {TestCaseRange.START.getNum(), TestCaseRange.END.getNum()};

        // Each test case may have a different size (number of containers to be allocated)
        int[] testCaseSize = new int[TestCaseRange.END.getNum() - TestCaseRange.START.getNum()];


        // we have N types of Virtual Machines (VMs), normally N == 5, but there are others
        int vmTypes = testScenario.getNumOfVmTypes();

        // We set the idle Physical machine (PM) account for 70% energy consumption compared with a full PM, always
        double k = 0.7;


        // default just-fit is based on CPU = 0,
        int justFit = 1;


        // These are the addresses of test cases
        String base = "/home/tanboxi/PH.D_project/data/";
        String ConfigPath = base + "/baseConfig/";
        String testCaseFilesPath = base + "/containerData/";
        String initEnvPath = base + "/InitEnv/";
        String osProPath = base + "/OSPro/";
        String osPath = base + "/OSData/";
        String resultBase= "/local/scratch/tanboxi/containerAllocationResults/";
//        String evolvedMethodPath = folder + "/Container200";
        String evolvedMethodForContainerAllocationPath = folder + "/" + testCaseSizeChoice.getDirectory();
        String evolvedMethodForVmAllocationPath = folder + "/" + testCaseSizeChoice.getDirectory();


        String VMConfig = testScenario.getVmConfig();
        String PMConfig = testScenario.getPmConfig();
        String VMConfigPath = ConfigPath + VMConfig + ".csv";
        String PMConfigPath = ConfigPath + PMConfig + ".csv";



        String chosenOS = osChoice.getDirectory();
        osProPath += (chosenOS + "/probability.csv");


        evolvedMethodForContainerAllocationPath += testScenario.getContainerAllocationRulesResultDirectory() + "/bestGPTree_";
        evolvedMethodForVmAllocationPath += testScenario.getVmAllocationRulesResultDirectory() + "/bestGPTree_";
        String testCaseRoot = testCaseSizeChoice.getDirectory();
        String chosenTestCase = testCaseRoot + testScenario.getTestCaseDirectory() + "/";
        String chosenInitEnv = testCaseRoot + testScenario.getInitEnvDirectory() + "/";
        Arrays.fill(testCaseSize, 0,
                TestCaseRange.END.getNum() - TestCaseRange.START.getNum(),
                testCaseSizeChoice.getTestSize()
                    );

        testCaseFilesPath += chosenTestCase;
        osPath += (chosenOS + testCaseRoot + "/");
        initEnvPath += (chosenOS + "/" + chosenInitEnv);

        // WriteFile instance
        WriteFile writer = new WriteFile();

        // Expression Reader
        ReadExpression expReaderForContainerAllocation = new ReadExpression(evolvedMethodForContainerAllocationPath);
        ReadExpression expReaderForVmAllocation = new ReadExpression(evolvedMethodForVmAllocationPath);


        // Read files from disk
        ReadFile readFiles = new ReadFile(
                vmTypes, osChoice.getNumOfOs(), testCases,
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




        // Normalization method, pass the vm configuration
        Normalization norm = new LinearNormalize(pmCpu, pmMem);

        // vm selection fitness Function List
        Fitness vmSelectionFitnessFunction;
//        String expression = "normalizedItemCpu - normalizedItemCpu";
        switch (vmAllocationFrameworkChoice){
            case ANYFIT:
                switch (vmSelectionFitnessChoice){
                    case SUB: vmSelectionFitnessFunction = new SubMethod(norm); break;
                    case SUM: vmSelectionFitnessFunction = new SumMethod(norm); break;
                    case MIX: vmSelectionFitnessFunction = new MixedMethod(norm); break;
                    case EVO:
                        String expression = expReaderForContainerAllocation.readExpFrom(run);
                        vmSelectionFitnessFunction = new ContainerAllocationEvolvedMethod(norm, expression);
                        break;
                    case DIV: vmSelectionFitnessFunction = new DivMethod(norm); break;
                    case VOLUME: vmSelectionFitnessFunction = new VolumeMethod(norm);break;
                    case RANDOM: vmSelectionFitnessFunction = new RandomSelection(norm);break;
                    default: vmSelectionFitnessFunction = new SubMethod(norm);
                }
                break;
            case NONANYFIT:
                String expression = expReaderForContainerAllocation.readExpFrom(run);
                vmSelectionFitnessFunction = new NonAnyFitEvolvedMethod(norm, expression);
                break;
            default:
                vmSelectionFitnessFunction = new SubMethod(norm);
        }



        // pm selection fitness Function List
        Fitness pmSelectionFitnessFunction;
        switch(pmSelectionFitnessChoice){
            case SUB: pmSelectionFitnessFunction = new SubMethod(norm); break;
            case SUM: pmSelectionFitnessFunction = new SumMethod(norm); break;
            case MIX: pmSelectionFitnessFunction = new MixedMethod(norm); break;
            case EVO:
                String expression = expReaderForVmAllocation.readExpFrom(run);
                pmSelectionFitnessFunction = new VmAllocationEvolvedMethod(norm, expression);
                break;
            case DIV: pmSelectionFitnessFunction = new DivMethod(norm);break;
            case VOLUME:pmSelectionFitnessFunction = new VolumeMethod(norm);break;
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
            case JUSTFIT: vmCreationRule = new Simple(justFit); break;
            case LARGEST: vmCreationRule = new Largest(); break;
            case OSPROB: vmCreationRule = new OSProportion(); break;
            case POSSLAR: vmCreationRule = new PossibleLargestVm(); break;
            default: vmCreationRule =  new Simple(justFit);
        }

        PMCreation pmCreationRule;
        switch (pmCreationChoice){
            case LARGEST: pmCreationRule = new SimplePM();
            default: pmCreationRule = new SimplePM();
        }

//        Allocation pmSelectionRule;
        HolderSelectionCreation pmSelectionRule;
        switch (pmSelectionChoice){
            case BESTFIT:
                // both sub rule and sum rule require the residual or balance to be minimized
                switch (pmSelectionFitnessChoice){
                    case SUB:
                    case SUM:
                    case MIX:
                    case VOLUME:
                    case RANDOM:
                    case ENERGYAWARE:
                        pmSelectionRule = new PmSelectionBestFitSmall(pmSelectionFitnessFunction, pmCreationRule);
                        break;
                    case EVO:
                    case DIV:
                        pmSelectionRule = new PmSelectionBestFitLarge(pmSelectionFitnessFunction, pmCreationRule);
                        break;
                    default:
                        pmSelectionRule = new PmSelectionBestFitSmall(pmSelectionFitnessFunction, pmCreationRule);
                    }
                    break;
            case FIRSTFIT:
                    pmSelectionRule = new FirstFit(pmCreationRule); break;
            default: pmSelectionRule = new FirstFit(pmCreationRule);
        }


        HolderSelectionCreation selectionCreationFramework;
        switch(vmAllocationFrameworkChoice){
            case ANYFIT:
                switch (vmSelectionFitnessChoice){
                    case SUB:
                    case SUM:
                    case MIX:
                    case VOLUME:
                    case ENERGYAWARE:
                        selectionCreationFramework = new VmSelectionBestFitSmall(vmSelectionFitnessFunction, vmCreationRule);
                        break;
                    case EVO:
                    case DIV:
                        selectionCreationFramework = new VmSelectionBestFitLarge(vmSelectionFitnessFunction, vmCreationRule);
                        break;
                    default:
                        selectionCreationFramework = new VmSelectionBestFitSmall(vmSelectionFitnessFunction, vmCreationRule);
                }
                break;
            case NONANYFIT:
                selectionCreationFramework = new NonAnyFitFramework(vmSelectionFitnessFunction);
                break;
            default:
                selectionCreationFramework = new NonAnyFitFramework(vmSelectionFitnessFunction);
        }



        // accumulated energy arrayList for each test case
        ArrayList<Double> accumulatedEnergyList = new ArrayList<>();

        ArrayList<Double> averageAccEnergyList = new ArrayList<>();


        // Experiment starts here,
        // We run the test case from N to M
        for(int testCaseCount = 0;
            testCaseCount + TestCaseRange.START.getNum() < TestCaseRange.END.getNum();
            ++testCaseCount){

            System.out.println("==================================================");
            System.out.println("testCase: " + (testCaseCount + 1));

//            String resultPath = resultBase;
            // Create result folder
            String resultPath = resultBase + testCaseCount + "/";
            File rB = new File(resultPath);
            if(!rB.exists()){
                rB.mkdir();
            }

            // For each test case, we create a new empty data center
            DataCenter myDataCenter = new DataCenter(
                    pmEnergy, k, pmCpu, pmMem, vmCpu, vmMem, osProb,
                    pmSelectionRule, selectionCreationFramework);

//            Initialize the data center
            ArrayList<Double[]> pmList = initPM.get(testCaseCount);
            ArrayList<Double[]> vmList = initVM.get(testCaseCount);
            ArrayList<Double[]> containerList = initContainer.get(testCaseCount);
            ArrayList<Double[]> osList = initOS.get(testCaseCount);

            myDataCenter.initialization(pmList, vmList, containerList, osList);
//            Double energy = myDataCenter.calEnergy();

//            myDataCenter.print();
            System.out.println("After initialization = " + myDataCenter.calEnergy());

            // We have already read all the files at the beginning.
            // Now we just retrieve all the information from readFiles object
            double[] taskCpu = readFiles.getTaskCpu(testCaseCount);
            double[] taskMem = readFiles.getTaskMem(testCaseCount);
            int[] taskOS = readFiles.getTaskOS(testCaseCount);

            // For each testCase, we need to send container one by one
            // After each container allocation, the data center prints its energy consumption
            for(int i = 0; i < testCaseSize[testCaseCount]; ++i) {
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
            System.out.println("Average Accumulated Energy: " + myDataCenter.getAccumulatedEnergyConsumption() / testCaseSizeChoice.getTestSize());
//             Round to two decimal point
            accumulatedEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0);
//
//            for each test case calculate the average acc energy
            averageAccEnergyList.add(Math.round(myDataCenter.getAccumulatedEnergyConsumption() * 100) / 100.0
                                                / testCaseSizeChoice.getTestSize());
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
        System.out.println("Average Incremental Energy for all test cases: "
                + sumAccForAll
                / (TestCaseRange.END.getNum() - TestCaseRange.START.getNum()));
        System.out.println("Done!");
    }

}
