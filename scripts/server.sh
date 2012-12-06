#usage: interface port, defaults to: 0.0.0.0 12345
taskset -c 0 java -cp lib/java-ping.jar PingServer $1 $2
