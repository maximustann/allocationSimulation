generatePM <- function(testCase){
	data <- c(13200, 16000, 540)
	writePMConfig <- function(data, testCase){
		filename <- paste("./testCase", testCase, "/PMConfig.csv", sep = '')
		write.table(data, filename, col.names = F, row.names = F, sep = ',')
	}
	writePMConfig(data, testCase)
	print('PM Done')
}
