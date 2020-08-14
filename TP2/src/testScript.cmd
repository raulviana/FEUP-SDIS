start cmd.exe /k java TestApp localhost 8082 BACKUP "C:\\text.txt" 2
ping 127.0.0.1 -n 6 > nul
start cmd.exe /k java TestApp localhost 8081 RECLAIM 0