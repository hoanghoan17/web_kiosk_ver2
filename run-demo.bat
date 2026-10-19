@echo off
cd /d "%~dp0"
java -jar dist\fastpos-admin.jar --spring.profiles.active=demo
pause
