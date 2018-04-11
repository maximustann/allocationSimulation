generateTask <- function(size, testCase){
	vmConfig <- paste("./dataset/VMConfig.csv")
	vmCpu <- unlist(read.csv(vmConfig, header = F, sep = ",")[, 1])
	vmMem <- unlist(read.csv(vmConfig, header = F, sep = ",")[, 2])

	vmCpuLimit <- max(vmCpu)
	vmMemLimit <- max(vmMem)



	selectFromData <- function(size){
		data <- unlist(read.table('tpMatrix.txt', header = F))
		data <- data[!data < 0]
		myData <- sample(unlist(data), size, replace = T)
	}


	taskM <- ceiling(rexp(size, 0.001))
	taskTh <- unlist(selectFromData(size))
	taskA <- ceiling(rexp(size, 0.001))
	# repair the generated dataset
	for(i in seq_along(taskA)){
		if(taskTh[i] * taskA[i] > vmCpuLimit){
			repeat {
				taskA[i] <- ceiling(rexp(1, 0.0168))
				if(taskTh[i] * taskA[i] <= vmCpuLimit)
					break;
			}
		}
	}

	for(i in seq_along(taskM)){
		if(taskTh[i] * taskM[i] > vmMemLimit){
			repeat {
				taskM[i] <- ceiling(rexp(1, 0.0168))
				if(taskTh[i] * taskM[i] <= vmMemLimit)
					break;
			}
		}
	}

	# three types of OS
	# os1 probability = 0.5
	# os2 probability = 0.3
	# os3 probability = 0.2
	os1 <- 0.5
	os2 <- 0.8
	osTypes <- rep(0, size)

	for(i in seq(size)){
		u <- runif(1)
		if(u < os1) osTypes[i] <- 1
		else if((u >= os1) && (u < os2)) osTypes[i] <- 2
		else osTypes[i] <- 3
	}

	testCaseData <- cbind(taskA, taskM, osTypes)


	writeTask <- function(data, testCase){
		filename <- paste("./dataset/testCase", testCase, ".csv", sep='')
		write.table(data, filename, row.names = F, col.names = F, sep=',')
	}

	writeTask(testCaseData, testCase)
	print('Tasks done')
}




