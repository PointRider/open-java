@echo off

mode con cols=20 lines=10
cls

cd bin

::java --class-path="lib\ext\mp3plugin.jar";"lib\jmf.jar" com.sun.media.codec.audio.mp3.JavaDecoder
java startTheWorld.Start_GUI