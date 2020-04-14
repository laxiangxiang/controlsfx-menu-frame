::获取管理员权限
%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit
cd C:\
md YAML-File-Generator\history
cd YAML-File-Generator
@echo off
@echo Windows Registry Editor Version 5.00> prefs.reg
@echo [HKEY_CURRENT_USER\SOFTWARE\JavaSoft\Prefs]>> prefs.reg
::@echo @="yaml file generate platform">> prefs.reg
::@echo "theme"="DEFAULT">> prefs.reg
::@echo "filePath"="">> prefs.reg
::regedit /S C:\project\mine\OPCUA-YML-Generate-platform\src\main\resources\prefs.reg
regedit /S prefs.reg
exit