package experimentScenarios;

public enum TestCaseSizes {
    MINI(
            80,
            "Container80"),

    SMALL(
            200,
            "Container200"),
    MEDIUM(500,
            "Container500"),

    LARGE(
            1000,
            "Container1000"),
    xLarge(
            2500,
            "Container2500"
    );

    private int testSize;
    private String directory;

    TestCaseSizes(int testSize, String directory){
        this.testSize = testSize;
        this.directory = directory;
    }

    public int getTestSize() {
        return testSize;
    }

    public String getDirectory() {
        return directory;
    }
}
