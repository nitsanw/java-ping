#usage: host port size, defaults to: localhost 12345 32
taskset -c 1 java -cp lib/java-ping.jar PingClient $1 $2 $3