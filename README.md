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
Add the required permissions for IdentHub to your app's `Manifest.xml` file:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

First you need to create an identification session via the Solaris API. The session will contain a URL that can be passed to the IdentHub SDK to create a new session.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // …

    val sessionUrl = … // from the API
    val button = findViewById(R.id.button)

    val identHubSession = IdentHub().sessionWithUrl(sessionUrl) {
        .onCompletion(this, ::onSuccess, ::onFailure)

    button.setOnClickListener { identHubSession.start() }
}

private fun onSuccess(result: IdentHubSessionResult) {
    val identificationId = result.identficationId
    // Continue with your flow.
}

private fun onFailure(failure: IdentHubSessionFailure) {
    // Continue after failed identification.
}
```

When the IdentHub session is completed, the `onCompletion` callback is invoked to give back control to your activity.

### Add custom step after payment initiation
You can define listener that will be called after a successful payment initiation. You can use this to do something else in your app, before you continue the identification later.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // …

    val sessionUrl = … // from the API
    val startButton = findViewById(R.id.startButton)
    val resumeButton = findViewById(R.id.resumeButton)

    val identHubSession = IdentHub().sessionWithUrl(sessionUrl)
        .onPaymentSuccess(this, ::onPaymentSuccess)
        .onCompletion(this, ::onSuccess, ::onFailure)

    startButton.setOnClickListener { identHubSession.start() }
    resumeButton.setOnClickListener { identHubSession.resume() }
}

private fun onPaymentSuccess(result: IdentHubSessionResult) {
    val identificationId = result.identficationId
    // Continue with your flow.
}

// …
```

The call to `resume` will continue the identification.

You can also resume the IdentHub session in a different activity. But this requires to add a new `onCompletion` listener that is scoped to the other activity.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // …

    val button = findViewById(R.id.button)

    val identHubSession = IdentHub().currentSession()
        .onCompletion(this, ::onSuccess, ::onFailure)

    button.setOnClickListener { identHubSession.resume() }
}

// …
```

## Failure

The SDK strives to catch any error/failures and offers inline options to recover those. This could be either a re-try of the current process or offering an alternative one (fallback). If in anycase an identification is not possible the SDK will call the callback as defined `::onFailure`. When called, stop the onboarding flow.

### Build with fourthline SDK

FOR being able to download & build the fourthline SDK, ensure you have repo access to https://github.com/Fourthline-com/FourthlineSDK-Android.
Once granted, issue a developer access token  https://github.com/settings/tokens with scope `read:packages`. Lastly run

```
FOURTHLINE_GRADLE_TOKEN=<< github token >> ./gradlew :core:build :identhub:build :fourthline:build
```

The SDK will be in cache afterwards and picked up by Android Studio.

### Example
You can open the example app in [Android studio](https://developer.android.com/studio/) to try it out.

You can find the example code in `example` directory.
