package VM_Creation;

public class VMSelected {
    private double vmNum;
    private double vmType;
    private double vmRemainCpu;
    private double vmRemainMem;
    private double vmOS;

    public VMSelected(
            double vmNum,
            double vmType,
            double vmRemainCpu,
            double vmRemainMem,
            double vmOS
    ){
        this.vmNum = vmNum;
        this.vmOS = vmOS;
        this.vmRemainCpu = vmRemainCpu;
        this.vmRemainMem = vmRemainMem;
    }

}
