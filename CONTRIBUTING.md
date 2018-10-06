# Contributing
This is an incomplete list of things to take care while contributing.

## Commits
- Every commit message should have this format `$project: title` being `$project` one of `alerts-server`, `alerts-ui` or `infra`, see the [commit list](https://github.com/AlexITC/crypto-coin-alerts/commits/master) to get a better idea, also, the message should be meaningful and describe what is changed.
- Don't touch files or pieces non-related to the commit message, create a different commit instead.
- Keep the commits simple to make the reviews easy.
- Merge commits will be rejected, use rebase instead, run `git config pull.rebase true` after cloning the repository to rebase automatically.
- Every commit should have working code with all tests passing.
- Every commit should include tests unless it is not practical.

## Code style
- Try to keep the code style while we integrate a code formatter tool like scalafmt.

## Pull requests
- Pull requests should go to the `develop` branch.

## Other
- `master` branch should never be broken, it contains the current version running in production.



## Environment
It is simpler to use the recommended developer environment.

### alerts-server
- IntelliJ with the Scala plugin.

### alerts-ui
Use [TSLint](https://palantir.github.io/tslint/) to keep the code format consistent, please run `tslint -c tslint.json 'src/**/*.ts'` and fix the errors before every commit, or use [visual code](https://code.visualstudio.com/) with the `Angular Language Service` and `TSLint` plugin to see the errors while typing.