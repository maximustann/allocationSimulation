package experimentScenarios;

public enum VMAllocationFramework {
    ANYFIT(0),
    NONANYFIT(1);

    private int num;

    VMAllocationFramework(int num){
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
