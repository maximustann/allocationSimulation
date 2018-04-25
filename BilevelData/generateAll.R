source('generateTaskCases.R')
source('generateOScases.R')
set.seed(1)
generateAll <- function(){
	
	testCase <- 200
	#size <- c(rep(20,20) , rep(40, 20), rep(60, 20), rep(80, 20), rep(100, 20))
	size <- c(rep(20, 50), rep(80, 50), rep(200, 50), rep(5000, 50))
	#VMtypes <- 5
	for(j in 1:2){ # artificial and real world data
		for(k in 1:4){ # Difference VM settings
			for(i in seq(testCase)){
				generateTask(j, k, size[i], i)
			}
		}
	}

	for(i in seq(testCase)){
		for(j in c(1, 3, 5)){
			generateOScases(size[i], j, i)
		}
	}
}
