# add -Dyield=true to yield on spin
# add -DwaitNanos=1000 to add wait between pings
# $1 - message size
java -server -cp lib/java-ping.jar uncoordinated.TcpSpinPingClient $1 $2 $3
