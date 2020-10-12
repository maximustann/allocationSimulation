package experimentScenarios;

public enum TestCaseRange {
    START(100),
    END(130);
//    START(0),
//    END(50);
    private int num;

    TestCaseRange(int num){
        this.num = num;
    }
    public int getNum() {
        return num;
    }
}
