package fileHandlers;

public interface ReadCsvFile {
	void read(String fileName, double[][] data);
	void setSep(String sep);
}
