// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  api: {
    url: 'http://localhost:9000'
  },
  recaptcha: {
    siteKey: '6Lesrz8UAAAAAEJT7iYUhWJfq04v8uTB-lFgFyOY'
  },
  gtag: {
    // don't track events on dev
    id: 'UA-XXXXXXXXX-X'
  }
};
