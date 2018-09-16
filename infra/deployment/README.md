# Deployment

This folder contains all the required scripts to deploy the projects.

These scripts were tested with ansible 2.6.4, higher versions should work too, it might not work with smaller versions.

## Requirements

1. Register the servers on the ssh config file (`~/.ssh/config`), for example:

```
Host test-server
    HostName 142.93.24.244
    User dummy
```


2. Create the `.vault` file which should contain the password to decrypt the [alerts-server.env](config/alerts-server.env) file, if you don't have the key, just create a plain-text version containing the environment variables required to run the application.

3. Ensure the `hosts.ini` file contains the correct servers.


## Limitations
- This assumes you will deploy all services to a single server.
- There is no simple way to go to production or a development server.
- There is no SSL support.
- Assumes that the server ports are open.

## alerts-server

Execute the following command to deploy the application:
`ansible-playbook -i hosts.ini --ask-become-pass --vault-password-file .vault alerts-server.yml`


## alerts-ui

1. Ensure the web-ui project is pointing to the correct host [environment.prod.ts](../../alerts-ui/src/environments/environment.prod.ts)

2. Execute the following command to deploy the application:
`ansible-playbook -i hosts.ini --ask-become-pass --vault-password-file .vault alerts-ui.yml`
