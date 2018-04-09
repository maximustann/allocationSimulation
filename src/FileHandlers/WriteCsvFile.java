package FileHandlers;

import java.io.IOException;
import java.util.ArrayList;

public interface WriteCsvFile {
	public void write(String fileName, ArrayList<Double> data) throws IOException;
}
