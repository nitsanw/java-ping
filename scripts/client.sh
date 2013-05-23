#usage: host port size, defaults to: localhost 12345 32
java -server -cp lib/java-ping.jar -client -spin $1 $2 $3