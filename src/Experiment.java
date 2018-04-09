
import FileHandlers.ReadFile;

import java.io.IOException;
import java.util.Arrays;

import DataCenterEntity.*;
public class Experiment {
    public static void main(String[] args) throws IOException {


        // test from case N to M
        int[] testCases = {0, 40};
        int[] testCaseSize = new int[40];
//        Arrays.fill(testCaseSize, 20);
        Arrays.fill(testCaseSize, 0, 19, 20);
        Arrays.fill(testCaseSize, 20, 39, 40);

        int vmTypes = 5;
        String base = "/home/tanboxi/workspace/BilevelData/dataset";
        String PMConfig = base + "/PMConfig.csv";
        String VMConfig = base + "/VMConfig.csv";


        ReadFile readFiles = new ReadFile(vmTypes, testCases, testCaseSize, PMConfig, base, VMConfig);
//        readFiles.readVMConfig(VMConfig, vmTypes);
        double pmCpu = readFiles.getPMCpu();
        double pmMem = readFiles.getPMMem();
        double pmEnergy = readFiles.getPMEnergy();
        double[] vmMem = readFiles.getVMMem();
        double[] vmCpu = readFiles.getVMCpu();


        // Experiment starts here
        for(int testCase = testCases[0] + 1; testCase <= testCases[1]; ++testCase){
            System.out.println("testCase: " + testCase);

            double[] taskCpu = readFiles.getTaskCpu(testCase - 1);
            double[] taskMem = readFiles.getTaskMem(testCase - 1);
            double[] taskOS = readFiles.getTaskOS(testCase - 1);
            Container[] containers = new Container[testCases[1]];



            for(int i = 0; i < testCaseSize[testCase - 1]; ++i) {
                System.out.println(taskCpu[i] + " : " + taskMem[i] + " : " + taskOS[i]);
                containers[i] = new Container(taskCpu[i], taskMem[i], taskOS[i], i);
            }
        }


//        WriteFileBilevel writeFiles = new WriteFileBilevel(fitnessAddr, timeAddr);




        System.out.println("Done!");
    }



}
