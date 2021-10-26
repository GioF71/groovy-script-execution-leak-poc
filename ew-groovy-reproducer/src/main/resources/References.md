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