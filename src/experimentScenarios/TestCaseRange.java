package experimentScenarios;

public enum TestCaseRange {
    START(50),
    END(100);
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
