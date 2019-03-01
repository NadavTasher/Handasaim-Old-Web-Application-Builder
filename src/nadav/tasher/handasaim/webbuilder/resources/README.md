# Handasaim Web App
This directory, `resources`, is the official repository for the Handasaim+ Web App.

### Instructions for installing on a plasma
You will need to trigger the following command once, every login.
```
chromium --start-fullscreen https://hwbb.github.io/h/plasma.html
```
#### Adding the command to startup script
```
sudo nano .bashrc
```
Scroll to bottom and add
```
chromium --start-fullscreen https://hwbb.github.io/h/plasma.html
```
Press Ctrl+Shift+O then Ctrl+X

### Intructions on creating a build script
Use `git config credential.helper store` to store uname/pwd
```
#!/bin/bash
java -jar $PWD/compiler/compiler.jar $PWD/h/
git --git-dir ./h/.git --work-tree ./h/ add --all
git --git-dir ./h/.git --work-tree ./h/ commit -a -m "Automated ($(date))"
git --git-dir ./h/.git --work-tree ./h/ push
echo Done
```