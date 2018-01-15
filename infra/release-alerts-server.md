# manual steps to release alerts-server app
# assumes that systemd service and environment config file are already set up.
0. Go to alerts-server folder
1. Build the app: `sbt dist`
2. Upload the zip: `scp target/universal/crypto-coin-alerts-0.1-SNAPSHOT.zip $SERVER_IP:~/`
3. Log in into the server: `ssh $SERVER_IP`
4. Unzip the app: `sudo unzip ~/crypto-coin-alerts-0.1-SNAPSHOT.zip -d /home/play/alerts-server`
5. Give permissions to play user to manage the app folder: `sudo chown -R play:nogroup /home/play/alerts-server`
6. Restart the app: `sudo service alerts-server restart`
7. Exit server: `exit`
