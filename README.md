# Solarisbank IdentHub SDK
Android SDK for Solarisbank IdentHub.

## Installation
Then add the identhub-android to the dependicies in your `build.gradle` file:

```groovy
dependencies {
    implementation 'de.solarisbank.identhub:identhub-android:0.1.0'
}
```

## Usage
Add the required permissions for IdentHub to your app's `Manifest.xml` file:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Start a [Bankident](https://www.solarisbank.com/en/services/bankident/) identification:

```java
IdentHub identHub = new IdentHub();
String token = …;
identHub.startBankident(token);
```

You can request a unique token to start the identification of a person from the Solarisbank API.

### Example
To try out the example app, clone this repository and open it in [Android studio](https://developer.android.com/studio/).

You can find the example code in `example` directory.
