# add -Dyield=true to yield on spin
# $1 - message size
java -server -cp lib/java-ping.jar IpcPingServer $1
