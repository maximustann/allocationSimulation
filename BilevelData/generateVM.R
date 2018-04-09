generateVM <- function(testCase){
	data <- c(3300, 500,
			  3300, 1000,
			  6600, 4000,
			  6600, 8000,
			  12000, 15000)
	data <- matrix(data, ncol = 2, byrow = T)
	writePMConfig <- function(data, testCase){
		filename <- paste("./testCase", testCase, "/VMConfig.csv", sep = '')
		write.table(data, filename, col.names = F, row.names = F, sep = ',')
	}
	writePMConfig(data, testCase)
	print('VM Done')
}
