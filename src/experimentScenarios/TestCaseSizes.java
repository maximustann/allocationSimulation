package experimentScenarios;

public enum TestCaseSizes {
    SMALL(
            80,
            "Container80"),

    MEDIUM(
            200,
            "Container200"),

    LARGE(
            5000,
            "Container5000");

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
