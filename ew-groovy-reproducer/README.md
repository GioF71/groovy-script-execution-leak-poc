# References

## Disable compressed class pointers
<https://stackoverflow.com/questions/39491325/understanding-metaspace-size>

## Tuning Java heap size, metaspace size and other such items
<https://cf-docs.jp-east-1.paas.cloud.global.fujitsu.com/en/manual/overview/overview/topics/t-fjbp-tuning.html>

## Launch Options
	-XX:MaxMetaspaceSize=50M 
	-XX:-UseCompressedClassPointers
	-Xloggc:gclog.log 
	-XX:+PrintGCDetails 
	-XX:+PrintGCDateStamps
	
## Requirements
	-JDK 8+

## Run
	-Set the RUNNER_TYPE environment variable to one of the values of the RunnerType enum (Optional)
	-Execute with the following: mvn clean compile test
	-The tests should run for a few seconds and then exits.
	
## Environment Variables
	-RUNNER_TYPE (one among RunnerType)
	-NUMBER_OF_THREADS
	-DURATION_SEC
	-STAT_DEBUG_ENABLED
