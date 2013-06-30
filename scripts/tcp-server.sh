# add -Dyield=true to yield on spin
# $1 - type (spin|block|selectNow|select)
# $2 - server NIC (default: 0.0.0.0)
# $3 - server port (default: 12345)
java -server -jar lib/java-ping.jar -server -$1 $2 $3
