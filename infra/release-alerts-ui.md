# manual steps to release alerts-ui project
# assumes that nginx is already set up.
0. Go to alerts-ui folder
1. Build the app: `ng build --prod`
2. Zip the build: `zip -r alerts-ui.zip dist/*`
3. Upload the zip: `scp alerts-ui.zip $SERVER_IP:~/`
4. Log in into the server: `ssh $SERVER_IP`
5. Unzip the app: `unzip ~/alerts-ui.zip -d ~/`
6. Remove nested folder: `sudo rsync -a ~/dist/ /var/www/html/ --remove-source-files`
7. Exit server: `exit`
