# add -Dyield=true to yield on spin
# add -DwaitNanos=1000 to add wait between pings
# $1 - type (spin|block|selectNow|select)
# $2 - server host (default: localhost)
# $3 - server port (default: 12345)
# $4 - message size (default: 32)
java -server -jar lib/java-ping.jar -client $1 $2 $3 $4
