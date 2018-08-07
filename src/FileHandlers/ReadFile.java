package FileHandlers;
import java.util.*;

public class ReadFile {
    private ReadCsvFile readByRow;
    private ReadCsvFile readByCol;

    private int[] testCaseSize;
    private double VMTypes;
    private double PMCpu;
    private double PMMem;
    private double PMEnergy;
    private double[][] PMConfig;
    private double[][] VMConfig;
    private double[] VMCpu;
    private double[] VMMem;
    private ArrayList<double[][]> testCases;
    private ArrayList<double[][]> testCaseOsList;
    private double[] osProb;

    public ReadFile(
            int VMTypes,
            int osNum,
            int[] testCaseNum,
            int[] testCaseSize,
            String PMConfigPath,
            String VMConfigPath,
            String testCaseBase,
            String osConfigPath,
            String osProbPath
    ) {

        this.testCases = new ArrayList<>();
        this.testCaseOsList = new ArrayList<>();
        this.testCaseSize = testCaseSize.clone();

        readByRow = new ReadByRow();
        readByCol = new ReadByCol();
        PMConfig = new double[1][3];
        readByCol.read(PMConfigPath, PMConfig);
        PMCpu = PMConfig[0][0];
        PMMem = PMConfig[0][1];
        PMEnergy = PMConfig[0][2];


        this.VMTypes = VMTypes;

        VMConfig = new double[VMTypes][2];
        readByRow.read(VMConfigPath, VMConfig);
        VMCpu = new double[VMTypes];
        VMMem = new double[VMTypes];
        for(int i = 0; i < VMTypes; ++i){
            VMCpu[i] = VMConfig[i][0];
            VMMem[i] = VMConfig[i][1];
        }

        double[][] temp = new double[1][osNum];
        readByCol.read(osProbPath, temp);
        osProb = new double[osNum];
        for(int i = 0; i < osNum; ++i){
            osProb[i] = temp[0][i];
        }

        for(int i = 0; i + testCaseNum[0] < testCaseNum[1]; ++i) {
            double[][] testCase = new double[2][testCaseSize[i]];
            double[][] testCaseOs = new double[1][testCaseSize[i]];
            String testCasePath = testCaseBase +  "testCase" + (i + testCaseNum[0]) + ".csv";
            String testCaseOSPath = osConfigPath + "testCase" + (i + testCaseNum[0]) + ".csv";
            readByCol.read(testCasePath, testCase);
            readByCol.read(testCaseOSPath, testCaseOs);
            testCases.add(testCase);
            testCaseOsList.add(testCaseOs);
        }

    }

    public double[] getOsProb(){
        return osProb;
    }
    public double getVMTypes() {
        return VMTypes;
    }
    public double getPMCpu() {
        return PMCpu;
    }
    public double getPMMem() {
        return PMMem;
    }
    public double getPMEnergy(){
        return PMEnergy;
    }
    public double[] getVMCpu() {
        return VMCpu;
    }
    public double[] getVMMem() {
        return VMMem;
    }
    public double[] getTaskCpu(int testCaseNum) {
        return testCases.get(testCaseNum)[0].clone();
    }
    public double[] getTaskMem(int testCaseNum) {
        return testCases.get(testCaseNum)[1].clone();
    }
    public int[] getTaskOS(int testCaseNum) {
        int[] temp = new int[testCaseSize[testCaseNum]];
        for(int i = 0; i < testCaseSize[testCaseNum]; i++){
            temp[i] = (int) testCaseOsList.get(testCaseNum)[0][i];
        }
        return temp.clone();
    }


}
