package experimentScenarios;

public enum SelectionRules {
    BESTFIT(0),
    FIRSTFIT(1);

    private int num;
    SelectionRules(int num) {
        this.num = num;
    }
}
