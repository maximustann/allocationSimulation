package experimentScenarios;

public enum FitnessFunctions {
    SUB(0),
    SUM(1),
    MIX(2),
    EVO(3),
    DIV(4),
    VOLUME(5),
    ENERGYAWARE(6),
    RANDOM(7);

    private int num;
    FitnessFunctions(int num){
        this.num = num;
    }
}
