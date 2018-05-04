package FileHandlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import FileHandlers.WriteByRow;

public class WriteFile {
	private WriteByRow pmWriter;
	private WriteByRow vmWriter;

	/**
	 * constructor
	 */
	public WriteFile(){
		pmWriter = new WriteByRow(",", 1);
		vmWriter = new WriteByRow(",", 1);
	}
	
	/**
	 * write results to files. 
	 */
	public void writeResults(String base, ArrayList pmStatus, ArrayList vmStatus) throws IOException{
		pmWriter.writeArray(base + "pmStatus.csv", pmStatus);
		// create a directory for vms
		File vmDir = new File(base + "vm/");
		if(!vmDir.exists()){
			vmDir.mkdir();
		}
		// Write vm status
		for(int i = 0; i < pmStatus.size(); ++i){
			vmWriter.writeArray( base + "vm/pm_" + (i + 1) + ".csv", (ArrayList) vmStatus.get(i));
		}
	}
}
