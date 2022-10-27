# Solaris IdentHub SDK
Android SDK for Solaris IdentHub.

It provides an easy way to integrate identification provided by Solaris into your Android app.

## Installation
Add `identhub-android` as a dependency to your `build.gradle` file. For `$latest_version` check [Releases](../../releases).

```groovy
repositories {
    …

    maven {
        url "https://maven.pkg.github.com/Solarisbank/identhub-android"
        credentials {
            username = ""
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation "de.solarisbank.identhub:identhub-android:$latest_version"
}
```

### IdentHub SDK Modules
The Solaris IdentHub SDK uses a modular approach that allows you to only include components that are necessary for the identification methods that you will actually use.

The required dependency that consists of the core functionality is as follows:
```groovy
    implementation "de.solarisbank.identhub:identhub-android:$latest_version"
```
And here are the optional dependencies that can be added alongside the required one based on the flows you want to use in the SDK:
```groovy
    // Verification with bank account
    implementation "de.solarisbank.identhub:identhub-android-bank:$latest_version"
    // Document verification using Fourthline SDK
    implementation "de.solarisbank.identhub:identhub-android-fourthline:$latest_version"
    // QES (Document Signing)
    implementation "de.solarisbank.identhub:identhub-android-qes:$latest_version"
    // Phone verification
    implementation "de.solarisbank.identhub:identhub-android-phone:$latest_version"
```

#### Optional dependency to Fourthline SDK
For fourthline you should also include the following repository:
```groovy
    // Repository for the optional Fourthline dependency
    maven {
        url "https://maven.pkg.github.com/Fourthline-com/FourthlineSDK-Android"
        credentials {
            username = ""
            password = System.getenv("GITHUB_TOKEN")
        }
    }
```
Since this repository is **not publicly available**, you'll need access to it. Please get in contact with Solaris to request access.
Using a github account that has access to Fourthline repositories, issue an [access token](https://github.com/settings/tokens) with scope `read:packages` and set that as environment variable `GITHUB_TOKEN` as you can see in the code above. You can also trigger a build from Terminal like this:

```sh
GITHUB_TOKEN=<< github token >> ./gradlew :my-app:build
```
This way gradle will cache the required dependencies.

## Usage
First you need to create an identification session via the Solaris API. The session will contain a URL that can be passed to the IdentHub SDK to create a new session.

You can use the new [ActivityResult API](https://developer.android.com/training/basics/intents/result#register) to start the SDK:
```kotlin
val identhub = registerForActivityResult(StartIdenthubContract()) {
    when (it) {
        // Identification was successful
        is IdenthubResult.Success -> onSuccess(it.identificationId)
        // Identification is confirmed and awaiting manual review
        is IdenthubResult.Confirmed -> onConfirmationSuccess(it.identificationId)
        // Identification failed with error message
        is IdenthubResult.Failed -> onFailure(it.message)
    }
}

// You need to pass the sessionUrl that you obtained from Solaris API here:
identhub.launch(StartIdenthubConfig(sessionUrl = sessionUrl))
```
### I hate the new API show me the good ol' way:
You can still use the `startActivityForResult` to start the SDK:
```kotlin
val intent = StartIdenthubContract().createIntent(
    context = this,
    StartIdenthubConfig(sessionUrl = sessionUrl)
)
startActivityForResult(intent, RequestCode)

...

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == RequestCode) {
        when (val it = StartIdenthubContract().parseResult(resultCode, data)) {
            // Handle the result just like above
        }
    }
}

```

## Example
You can find the example app in `example` directory.

## Other configurations
### Change log levels
The log levels for both local and remote loggers can be changed by passing the respective options in `StartIdenthubConfig` when starting the SDK.
