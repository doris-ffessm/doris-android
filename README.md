Public web site about the DORISAndroid application: https://doris.gitlabpages.inria.fr/


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

TODO  (contribute to discussions, post issue, create pull request, ...)

## Developers notes

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

