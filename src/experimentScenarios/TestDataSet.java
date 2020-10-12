package experimentScenarios;

public enum TestDataSet {
    AUVERGRID_TEN(
            0,
            10,
            "PMConfig/PMConfig_small",
            "VMConfig/LVMnePM/VMConfig_ten",
            "_AUVERGRID_TEN",
            "_AUVERGRID_TEN",
            "_AUVERGRID_TEN_bestContainerGPTree",
            "_AUVERGRID_TEN_bestVmGPTree"),
    BITBRAINS_TEN(
            1,
            10,
            "PMConfig/PMConfig_small",
            "VMConfig/LVMnePM/VMConfig_ten",
            "_BITBRAINS_TEN",
            "_BITBRAINS_TEN",
            "_BITBRAINS_TEN_bestContainerGPTree",
            "_BITBRAINS_TEN_bestVmGPTree"),
    AUVERGRID_TWENTY(
            2,
            20,
            "PMConfig/PMConfig_small",
            "VMConfig/LVMnePM/VMConfig_twenty",
            "_AUVERGRID_TWENTY",
            "_AUVERGRID_TWENTY",
            "_AUVERGRID_TWENTY_bestContainerGPTree",
            "_AUVERGRID_TWENTY_bestVmGPTree"),
    BITBRAINS_TWENTY(
            3,
            20,
            "PMConfig/PMConfig_small",
            "VMConfig/LVMnePM/VMConfig_twenty",
            "_BITBRAINS_TWENTY",
            "_BITBRAINS_TWENTY",
            "_BITBRAINS_TWENTY_bestContainerGPTree",
            "_BITBRAINS_TWENTY_bestVmGPTree");



    private int id;
    private int numOfVmTypes;
    private String pmConfig;
    private String vmConfig;
    private String initEnvDirectory;
    private String testCaseDirectory;
    private String containerAllocationRulesResultDirectory;
    private String vmAllocationRulesResultDirectory;

    TestDataSet(
                int id,
                int numOfVmTypes,
                String pmConfig,
                String vmConfig,
                String initEnvDirectory,
                String testCaseDirectory,
                String containerAllocationRulesResultDirectory,
                String vmAllocationRulesResultDirectory){
        this.id = id;
        this.numOfVmTypes = numOfVmTypes;
        this.pmConfig = pmConfig;
        this.vmConfig = vmConfig;
        this.initEnvDirectory = initEnvDirectory;
        this.testCaseDirectory= testCaseDirectory;
        this.containerAllocationRulesResultDirectory = containerAllocationRulesResultDirectory;
        this.vmAllocationRulesResultDirectory = vmAllocationRulesResultDirectory;
    }

    public int getId(){
        return id;
    }
    public String getPmConfig(){
        return pmConfig;
    }
    public String getVmConfig(){
        return vmConfig;
    }

    public String getTestCaseDirectory() {
        return testCaseDirectory;
    }

    public int getNumOfVmTypes() {
        return numOfVmTypes;
    }

    public String getInitEnvDirectory() {
        return initEnvDirectory;
    }

    public String getContainerAllocationRulesResultDirectory() {
        return containerAllocationRulesResultDirectory;
    }

    public String getVmAllocationRulesResultDirectory() {
        return vmAllocationRulesResultDirectory;
    }
}
