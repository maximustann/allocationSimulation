package experimentScenarios;

public enum TestDataSet {
    REAL_WORLD_SMALL(
            0,
            "PMConfig/PMConfig_xSmall",
            "VMConfig/VMConfig_xSmall",
            "_REALWORLD_SMALL"),

    BALANCED_DATA_SMALL(
            1,
            "PMConfig/PMConfig_xSmall",
            "VMConfig/VMConfig_xSmall",
            "_BALANCED_SMALL/"),

    BALANCED_VM_SMALL(
            2,
            "PMConfig/PMConfig_xSmall_balanced",
            "VMConfig/VMConfig_xSmall_balanced",
            "_BALANCED_VM_SMALL");

    private int id;
    private String pmConfig;
    private String vmConfig;
    private String directory;

    TestDataSet(int id, String pmConfig, String vmConfig, String directory){
        this.id = id;
        this.pmConfig = pmConfig;
        this.vmConfig = vmConfig;
        this.directory = directory;
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

    public String getDirectory() {
        return directory;
    }
}
