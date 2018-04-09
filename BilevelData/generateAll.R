source('generateProblemConfig.R')
source('generatePM.R')
source('generateVM.R')
source('generateTaskCases.R')
source('generateOS.R')
set.seed(1)
generateAll <- function(){
	
	testCase <- 6
	size <- c(20, 40, 60, 80, 100, 200)
	VMtypes <- 5
	base <- paste("./testCase")
	for(i in seq(testCase)){
		dir.create(paste(base, i, sep=""))
		generateProblemConfig(i, size[i], VMtypes)
		generatePM(i)
		generateVM(i)
		generateTask(size[i], i)
		generateOS(size[i], i)
	}
}
