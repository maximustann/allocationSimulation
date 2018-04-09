package FileHandlers;

public interface ReadCsvFile {
	public void read(String fileName, double[][] data);
	public void setSep(String sep);
}
