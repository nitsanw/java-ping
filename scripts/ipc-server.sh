# add -Dyield=true to yield on spin
# $1 - message size
java -server -cp lib/java-ping.jar IpcPingServerSOLV $1
