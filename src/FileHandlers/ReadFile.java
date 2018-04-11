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

    public ReadFile(
            int VMTypes,
            int[] testCaseNum,
            int[] testCaseSize,
            String PMConfigPath,
            String base,
            String VMConfigPath
    ) {

        this.testCases = new ArrayList<>();
        this.testCaseSize = testCaseSize.clone();

        readByRow = new ReadByRow();
        readByCol = new ReadByCol();

        PMConfig = new double[1][3];
        readByCol.read(PMConfigPath, PMConfig);
        PMCpu = PMConfig[0][0];
        PMMem = PMConfig[0][1];
        PMEnergy = PMConfig[0][2];


        this.VMTypes = VMTypes;

        VMConfig = new double[(int) VMTypes][2];
        readByRow.read(VMConfigPath, VMConfig);
        VMCpu = new double[(int) VMTypes];
        VMMem = new double[(int) VMTypes];
        for(int i = 0; i < VMTypes; ++i){
            VMCpu[i] = VMConfig[i][0];
            VMMem[i] = VMConfig[i][1];
        }

        for(int i = testCaseNum[0]; i < testCaseNum[1]; ++i) {
            double[][] testCase = new double[3][testCaseSize[i]];
            String testCasePath = base +  "/testCase" + (i + 1) + ".csv";
            readByCol.read(testCasePath, testCase);
            testCases.add(testCase);
        }

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
//        return (int[] testCases.get(testCaseNum)[2]).clone();
        int[] temp = new int[testCaseSize[testCaseNum]];
        for(int i = 0; i < testCaseNum; ++i)
            temp[i] = (int) testCases.get(testCaseNum)[2][i];
        return temp.clone();
    }


}
