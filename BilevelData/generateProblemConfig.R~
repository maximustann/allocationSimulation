generateProblemConfig <- function(testCase, serviceNo, VMtypes){
	data <- c(VMtypes, serviceNo)
	writeProblemConfig <- function(data, testCase){
		filename <- paste("./testCase", testCase, "/ProblemConfig.csv", sep = '')
		write.table(data, filename, col.names = F, row.names = F, sep = ',')
	}
	writeProblemConfig(data, testCase)
	print('ProblemConfig Done')
}
