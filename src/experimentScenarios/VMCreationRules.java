package experimentScenarios;

public enum VMCreationRules {
    JUSTFIT(0),
    LARGEST(1),
    OSPROB(2),
    EVO(3),
    POSSLAR(4);

    private int num;
    VMCreationRules(int num){
        this.num = num;
    }
}
