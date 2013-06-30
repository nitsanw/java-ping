# add -Dyield=true to yield on spin
# add -DwaitNanos=1000 to add wait between pings
# $1 - server host (default: localhost)
# $2 - client port (default: 22345)
# $3 - server port (default: 12345)
# $4 - message size (default: 32)
java -server -cp lib/java-ping.jar UdpPingClient $1 $2 $3 $4
