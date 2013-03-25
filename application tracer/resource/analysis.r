exeInfo <- do.call(rbind, strsplit(readLines('C:/Users/scnchw/git/application_tracer/application tracer/output/execution_data.txt'), '  ____  '));
exeInfo <- data.frame(exeInfo);
colnames(exeInfo) <- c('method', 'start time', 'result', 'cost');
attach(exeInfo);
hist(as.numeric(levels(cost)))
detach(exeInfo);


#as.POSIXlt(1364116067257/1000, origin='1960-01-01 00:00:00');