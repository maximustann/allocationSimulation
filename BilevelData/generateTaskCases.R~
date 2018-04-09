
generateTask <- function(size, testCase){
	vmConfig <- paste("./testCase", testCase, "/VMConfig.csv", sep = "")
	vmCpu <- unlist(read.csv(vmConfig, header = F, sep = ",")[, 1])
	vmMem <- unlist(read.csv(vmConfig, header = F, sep = ",")[, 2])

	vmCpuLimit <- max(vmCpu)
	vmMemLimit <- max(vmMem)



	selectFromData <- function(size){
		data <- unlist(read.table('tpMatrix.txt', header = F))
		data <- data[!data < 0]
		myData <- sample(unlist(data), size, replace = T)
	}


	taskM <- floor(rexp(size, 0.001))
	taskTh <- unlist(selectFromData(size))
	taskA <- floor(rexp(size, 0.001))
	# repair the generated dataset
	for(i in seq_along(taskA)){
		if(taskTh[i] * taskA[i] > vmCpuLimit){
			repeat {
				taskA[i] <- floor(rexp(1, 0.0168))
				if(taskTh[i] * taskA[i] <= vmCpuLimit)
					break;
			}
		}
	}

	for(i in seq_along(taskM)){
		if(taskTh[i] * taskM[i] > vmMemLimit){
			repeat {
				taskM[i] <- floor(rexp(1, 0.0168))
				if(taskTh[i] * taskM[i] <= vmMemLimit)
					break;
			}
		}
	}

	writeMem <- function(data, testCase){
		filename <- paste("./testCase", testCase, "/taskMem.csv", sep = '')
		write.table(data, filename, row.names = F, col.names = F, sep = ',')
	}
	writeCpu <- function(data, testCase){
		filename <- paste("./testCase", testCase, "/taskCpu.csv", sep = '')
		write.table(data, filename, row.names = F, col.names = F, sep = ',')
	}


	writeCpu(taskA, testCase)
	writeMem(taskM, testCase)


	print('Tasks done')
}




