package FileHandlers;
import java.util.*;

public class ReadFile {
    private ReadCsvFile readByRow;
    private ReadCsvFile readByCol;

    private double[] testCaseNum;
    private double VMTypes;
    private double PMCpu;
    private double PMMem;
    private double PMEnergy;
    private double[][] PMConfig;
    private double[][] VMConfig;
    private double[] VMCpu;
    private double[] VMMem;
    private ArrayList<double[][]> testCases;
//    private double[][] testCase;

    public ReadFile(
            int VMTypes,
            int[] testCaseNum,
            int[] testCaseSize,
            String PMConfigPath,
            String base,
            String VMConfigPath
    ) {

        this.testCases = new ArrayList<>();

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

        for(int i = testCaseNum[0] + 1; i <= testCaseNum[1]; ++i) {
            double[][] testCase = new double[3][testCaseSize[i - 1]];
            String testCasePath = base +  "/testCase" + i + ".csv";
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
    public double[] getTaskOS(int testCaseNum) {
        return testCases.get(testCaseNum)[2].clone();
    }


}
