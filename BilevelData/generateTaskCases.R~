



generateTask <- function(whichDataSet, whichVMsize, size, testCase){
	set.seed(1)
	dataSetSource <- c('realWorldData.csv', 'tpMatrix.txt')
	dataset <- dataSetSource[whichDataSet]
	vmConfigFile <- c('VMConfig_small.csv', 'VMConfig_medium.csv', 'VMConfig_large.csv', 'VMConfig_xLarge.csv')


	vmConfig <- paste('./VM_DataSet/', vmConfigFile[whichVMsize], sep='')
	vmCpu <- unlist(read.csv(vmConfig, header = F, sep = ",")[, 1])
	vmMem <- unlist(read.csv(vmConfig, header = F, sep = ",")[, 2])

	vmCpuLimit <- max(vmCpu)
	vmMemLimit <- max(vmMem)



	writeTask <- function(whichDataSet, data, vmType, testCase){
		if(whichDataSet == 1){
			filename <- paste("./Container_DataSet/real/", vmType, "/testCase", testCase, ".csv", sep='')
		} else {
			filename <- paste("./Container_DataSet/artificial/", vmType, "/testCase", testCase, ".csv", sep='')
		}
		write.table(data, filename, row.names = F, col.names = F, sep=',')
	}

	generateRealWorldData <- function(datasetName, whichVMsize, vmCpuLimit, vmMemLimit, size, testCase){

		selectFromData <- function(datasetName, size){
			data <- read.csv(datasetName, header=F, sep=',')
			choose <- sample(1:nrow(data), size, replace=F)
			myData <- data[choose, ]
			myData
		}

		fix <- function(size, data, vmCpuLimit, vmMemLimit, datasetName){
			dataset <- read.csv(datasetName, header=F, sep=',')
			for(i in 1:size){
				if(data[i, 1] > vmCpuLimit || data[i, 2] > vmMemLimit){
					repeat{
						data[i, ] <- dataset[sample(1:nrow(dataset), 1, replace=F), ]
						if(data[i, 1] <= vmCpuLimit && data[i, 2] <= vmMemLimit) break
					} # End repeat
				} # End If
			} # End for
			
			data
		
		} # End fix


		# function begins
		myData <- selectFromData(datasetName, size)
		myData <- fix(size, myData, vmCpuLimit, vmMemLimit, datasetName)
		writeTask(whichDataSet, myData, whichVMsize, testCase)
		print('generate real world data')
	}

	generateArtificialData <- function(datasetName, whichVMsize, vmCpuLimit, vmMemLimit, size, testCase){

		selectFromData <- function(datasetName, size){
			data <- unlist(read.table(datasetName, header = F))
			data <- data[!data < 0]
			myData <- sample(unlist(data), size, replace = T)
			myData
		}



		taskM <- ceiling(rexp(size, 0.001))
		taskTh <- unlist(selectFromData(datasetName, size))
		taskA <- ceiling(rexp(size, 0.001))
		# repair the generated dataset
		for(i in seq_along(taskA)){
			if(taskTh[i] * taskA[i] > vmCpuLimit){
				repeat {
					taskA[i] <- ceiling(rexp(1, 0.001))
					if(taskTh[i] * taskA[i] <= vmCpuLimit)
						break;
				}
			}
		}
	
		for(i in seq_along(taskM)){
			if(taskTh[i] * taskM[i] > vmMemLimit){
				repeat {
					taskM[i] <- ceiling(rexp(1, 0.001))
					if(taskTh[i] * taskM[i] <= vmMemLimit)
						break;
				}
			}
		}
	
		# three types of OS
		# os1 probability = 0.5
		# os2 probability = 0.3
		# os3 probability = 0.2
		#os1 <- 0.5
		#os2 <- 0.8
		#osTypes <- rep(0, size)
	
		#for(i in seq(size)){
			#u <- runif(1)
			#if(u < os1) osTypes[i] <- 0
			#else if((u >= os1) && (u < os2)) osTypes[i] <- 1
			#else osTypes[i] <- 2
		#}
	
		#testCaseData <- cbind(taskA, taskM, osTypes)
		testCaseData <- cbind(taskA, taskM)
	
		writeTask(whichDataSet, testCaseData, whichVMsize, testCase)
		print('generate articial data')

	} # End of generateArtificialData


	if(whichDataSet == 1){ # 1 means real world dataset
		generateRealWorldData(dataset, whichVMsize, vmCpuLimit, vmMemLimit, size, testCase)
	} else { 				# 2 means articial dataset
		generateArtificialData(dataset, whichVMsize, vmCpuLimit, vmMemLimit, size, testCase)
	}


}




