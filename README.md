java-ping
=========

A ping utility to baseline Java socket performance expectations. </br>
The dist ant target will produce a zip you can throw at your servers.
The jar produced is runnable, use as illustrated in usage and scripts.</br>
Usage:
>java-ping:</br>
>>   no-args   this usage</br>
>>    -server [-spin | -block | -select | -selectNow] interface port</br>
>>>        defaults: interface = 0.0.0.0
>>>                  port = 12345

>>    -client [-spin | -block | -select | -selectNow] host port message-size </br>
>>>        defaults: host = localhost
>>>                  port = 12345
>>>                  message-size = 32

Start the server, then the client.</br>
You can bind the server to a particular interface, default is 0.0.0.0(all).</br>
Enjoy.
