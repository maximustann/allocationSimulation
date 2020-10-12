package experimentScenarios;

public enum OperatingSystems {
    ONE(1, "OS1"), TWO(2, "OS2"), THREE(3, "OS3"), FOUR(4, "OS4"), FIVE(5, "OS5");
    private int numOfOs;
    private String directory;

    OperatingSystems(int numOfOs, String directory){
        this.numOfOs = numOfOs;
        this.directory = directory;
    }

    public int getNumOfOs() {
        return numOfOs;
    }

    public String getDirectory() {
        return directory;
    }
}
