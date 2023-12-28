#!/bin/sh

cd "$( dirname "$0" )" || exit
# "-Xms1G" is the minimum amount of ram the server is allowed to use (In this case 1GB of RAM
# "-Xmx6G" is the maximum amount of ram the server is allowed to use (In this case 6GB of RAM
# Change these values according to your needs
java -Xms1G -Xmx6G -XX:+UseG1GC -jar spigot-1.12.2.jar nogui