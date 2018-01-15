# manual steps to release alerts-ui project
# assumes that nginx is already set up.
0. Go to alerts-ui folder
1. Build the app: `ng build --prod`
2. Zip the build: `zip alerts-ui.zip dist/*`
3. Upload the zip: `scp alerts-ui.zip $SERVER_IP:~/`
4. Log in into the server: `ssh $SERVER_IP`
5. Unzip the app: `sudo unzip ~/alerts-ui.zip -d /var/www/html`
6. Remove nested folder: `sudo mv /var/www/html/dist/* /var/www/html`
7. Exit server: `exit`
