




generateOS <- function(size, testCase){

	filename <- paste("./testCase", testCase, "/taskOS.csv", sep = "")
	# three types of OS
	# os1 probability = 0.5
	# os2 probability = 0.3
	# os3 probability = 0.2
	os1 <- 0.5
	os2 <- 0.8
	osTypes <- rep(0, size)

	for(i in seq(size)){
		u <- runif(1)
		if(u < os1) osTypes[i] <- 0
		else if((u >= os1) && (u < os2)) osTypes[i] <- 1
		else osTypes[i] <- 2
	}

	write.table(osTypes, filename, row.names=F, col.names=F, sep=',')

}
