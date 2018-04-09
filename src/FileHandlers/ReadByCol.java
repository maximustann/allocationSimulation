package FileHandlers;

import java.io.*;
import java.util.ArrayList;

public class ReadByCol implements ReadCsvFile{
	String csvSplitBy;
	String line;

	public ReadByCol(){
		csvSplitBy = ",";
		line = "";
	}

	@Override
	public void read(String fileName, double[][] data) {
		File fd = new File(fileName);
		double[][] myData = readCsv(fd, data.length);
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[0].length; j++) data[i][j] = myData[i][j];
		}
	}

	private double[][] readCsv(File fileName, int colNum){
		BufferedReader br = null;
		double[][] content = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			ArrayList<Double> col = new ArrayList<Double>();
			while((line = br.readLine()) != null){
				String[] con = line.split(csvSplitBy);
				for(int j = 0; j < con.length; j++){
					col.add(Double.parseDouble(con[j]));
				}

			}
			int rowNum = col.size() / colNum;
			content = new double[colNum][rowNum];
			for(int i = 0; i < rowNum; i++) {
				for(int j = 0; j < colNum; j++) {
					content[j][i] = col.get(i * colNum + j);
				}
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
		// TODO Auto-generated method stub
		csvSplitBy = sep;
	}

}
