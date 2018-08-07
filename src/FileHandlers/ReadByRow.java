package FileHandlers;

import java.io.*;

public class ReadByRow implements ReadCsvFile {
	private String csvSplitBy;
	private String line;

	public ReadByRow(){
		csvSplitBy = ",";
		line = "";
	}

	@Override
	public void read(String fileName, double[][] data) {
		File fd = new File(fileName);
		double[][] temp = readCsv(fd, data.length, data[0].length);

		for(int i = 0; i < data.length; i++){
		    System.arraycopy(temp[i], 0, data[i], 0, temp[i].length);
		}
	}

	private double [][] readCsv(File fileName, int rowNum, int colNum){
		BufferedReader br;

		double[][] content = new double[rowNum][colNum];
		try {
			br = new BufferedReader(new FileReader(fileName));
			int rowCount = 0;
			while((line = br.readLine()) != null){
				String[] con = line.split(csvSplitBy);
				for(int i = 0; i < con.length; i++){
					content[rowCount][i] = Double.parseDouble(con[i]);
				}
				rowCount++;
			}
		} catch(FileNotFoundException e){
				e.printStackTrace();
		} catch(IOException e){
				e.printStackTrace();
		}
		return content;
	}

	@Override
	public void setSep(String sep) {
		csvSplitBy = sep;
	}

}
