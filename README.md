

DORIS Android is an illustrated guide to underwater species in mainland France and overseas that can be taken "almost" anywhere.
Based on data from the http://doris.ffessm.fr website, it helps you identify and observe marine and freshwater species, so you can make the most of your underwater dives or walks on the foreshore.

Public web site (in French) about the DORISAndroid application: https://doris-ffessm.github.io

The recommanded dowload is via the Google Play store: [![Get it on GooglePlay](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=fr.ffessm.doris.android)

## Development

The application is organised in 2 main parts with shared components:

- ```DorisAndroidPrefecth``` is a java application that is in charge of creating a sqlite database 
  from https://doris.ffessm.fr. This Application is run on a regular basis in the CI/CD.
- ```DorisAndroid``` is the Android application.


Shared components and libs

- ```DorisAndroidCommons``` is a java library that is used by both ```DorisAndroidPrefecth``` and
  ```DorisAndroid```. It mainly contains the accessor part for the data model of the sqlite database
  and other part common for the java application and the android application.
- ```GenandroidLib``` is a library for the  Android application. This is actually a legacy of the original 
  design of the application that was using an EMF based code generator. 


## How to build


### Build the database
Build the data base (ask for authentication)
```shell
./gradlew buildDorisDB -Pargs='-u dorisUserID -i'
```

Get a reusable access token (ask for authentication)
```shell
./gradlew buildDorisDB -Pargs='-u dorisUserID -i -noFetch'
```

Build the data base with previous AccessToken
```shell
./gradlew buildDorisDB -Pargs='-u dorisUserID --token=PREVIOUSACCESSTOKEN'
```


other useful args:  

- ```-help``` get all suppported args
- ```--max=nbFiches``` limit the number of "Fiche" that are collected to nbFiches (doesn't limit the 
  collect of authors and glossary in order to allow incremental build of the DB)

### Build the android application
Copy a db in DorisAndroid/src/main/assets
```shell
cp run/database/DorisAndrois.db DorisAndroid/src/main/assets
```

Compile the Android app.
```shell
./gradlew clean build lint
```


## How to contribute

The project welcomes any contributions: from bug reporting, feature suggestions, comment on the application UI, to bug fix and feature development.

Most changes should be proposed first as an issue for discussion. If you're confident enough in your proposal, you can also directly propose a Pull Request :wink:.


## Developers notes

### Creation of a release

- update version code and version name in `src/main/AndroidManifest.xml`
- update changelog in `src/main/res/raw/apropos.html`
- make sure the DB is complete in the latest CI pipeline

- Tag and push to github.

```sh
git tag -a 4.9.3-rc1 -m "my very nice version 4.9.3-rc1"
git push origin 4.9.3-rc1
```

The result will be in https://github.com/doris-ffessm/doris-android/releases

- upload apk to https://play.google.com/console as test deployment
- check in test deployment and publish
- promote test as production

