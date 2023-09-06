Web page describing the DORISAndroid application: https://doris.gitlabpages.inria.fr/


## Development

## How to build

TODO

### Creation of a release

- update version code and version name in `src/main/AndroidManifest.xml`
- update changelog in `src/main/res/raw/apropos.html`

- Tag and push to gitlab.

```sh
git tag -a 4.9.3-rc1 -m "my very nice version 4.9.3-rc1"
git push origin 4.9.3-rc1
```

The result will be in https://gitlab.inria.fr/doris/doris-android/-/releases

- upload apk to https://play.google.com/console
- check in test deployment and publish

