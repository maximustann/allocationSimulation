library(ggplot2)

plotComparison <- function(alg1, alg2, alg3='', step, maximum){
	alg1Energy <- vector()
	alg2Energy <- vector()
	alg3Energy <- vector()

	for(i in seq(from = step, to = maximum, by = step)){
		alg1File <- paste(alg1,'/', i, '/pmStatus.csv', sep='')
		alg1Data <- read.csv(alg1File, header=F, sep=',')
		alg1Energy <- c(alg1Energy, sum(alg1Data[7]))

		alg2File <- paste(alg2,'/', i, '/pmStatus.csv', sep='')
		alg2Data <- read.csv(alg2File, header=F, sep=',')
		alg2Energy <- c(alg2Energy, sum(alg2Data[7]))

		if(alg3 != ''){
			alg3File <- paste(alg3,'/', i, '/pmStatus.csv', sep='')
			alg3Data <- read.csv(alg3File, header=F, sep=',')
			alg3Energy <- c(alg3Energy, sum(alg3Data[7]))
		}

	}

	maxEng <- max(c(alg1Energy, alg2Energy, alg3Energy))
	png(filename="/local/scratch/tanboxi/containerAllocationResults/graph/image.png")
	print(maxEng)
	plot(alg1Energy, ylab= '',ylim=range(0, maxEng), col='red')
	par(new=T)
	plot(alg2Energy, ylab='energy', ylim=range(0, maxEng), col='blue')
	par(new=T)
	if(alg3 != '')
		plot(alg3Energy, ylab='energy', ylim=range(0, maxEng), col='black')
	# dev.off()

}


#plotEnergy <- function(base, step, maximum){
	
	#energy <- vector()
	
	#for(i in seq(from = step, to = maximum, by = step)){
	
		#filename <- paste(base,'/', i, '/pmStatus.csv', sep='')
		#data <- read.csv(filename, header=F, sep=',')
		#energy <- c(energy, sum(data[7]))
	#}
	#plot(energy)

#}



