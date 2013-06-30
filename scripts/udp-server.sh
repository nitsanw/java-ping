# add -Dyield=true to yield on spin
# $1 - server host (default: localhost)
# $2 - server port (default: 12345)
# $3 - client port (default: 22345)
java -server -cp lib/java-ping.jar UdpPingServer $1 $2
