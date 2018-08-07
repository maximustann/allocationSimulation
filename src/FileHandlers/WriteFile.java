package FileHandlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import FileHandlers.WriteByRow;

public class WriteFile {
	private WriteByRow pmWriter;
	private WriteByRow vmWriter;
	private WriteByRow energyWriter;

	/**
	 * constructor
	 */
	public WriteFile(){
		pmWriter = new WriteByRow(",", 1);
		vmWriter = new WriteByRow(",", 1);
		energyWriter = new WriteByRow("\n", 1);
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
		// for each pm, write vm status on that pm
		for(int i = 0; i < pmStatus.size(); ++i){
			vmWriter.writeArray( base + "vm/pm_" + (i + 1) + ".csv", (ArrayList) vmStatus.get(i));
		}
	}

	// write Energy to file
	public void writeEnergy(String base, ArrayList<Double> energy) throws IOException{
		energyWriter.write(base + "energy.csv", energy);
	}

	// write Wasted Energy to file
	// flag == 0: wasted cpu
	// flag == 1: overhead
	public void writeWaste(String base, ArrayList<Double> wastedCPU, ArrayList<Double>  wastedOverhead) throws IOException{
		energyWriter.write(base + "wastedCPU.csv", wastedCPU);
		energyWriter.write(base + "wastedOverhead.csv", wastedOverhead);
	}

	// write Acc Energy to file
	public void writeAccumulatedEnergy(String base, ArrayList<Double> energy) throws IOException{
		energyWriter.write(base + "AccumulatedEnergy.csv", energy);
	}
}
