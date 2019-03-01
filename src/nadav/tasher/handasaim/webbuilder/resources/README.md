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