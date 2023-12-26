#!/bin/sh

cd "$( dirname "$0" )"
java -Xms1G -Xmx6G -XX:+UseG1GC -jar spigot-1.12.2.jar nogui