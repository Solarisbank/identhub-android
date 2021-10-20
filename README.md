# Solarisbank IdentHub SDK
Android SDK for Solarisbank IdentHub.

It provides an easy way to integrate identification provided by Solarisbank into your Android app.

## Installation
Add `identhub-android` as a dependency to your `build.gradle` file:

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
    // Optional integration of Fourthline SDK
    maven {
        url "https://maven.pkg.github.com/Fourthline-com/FourthlineSDK-Android"
        credentials {
            username = ""
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'de.solarisbank.identhub:identhub-android:1.1.1'
    // Optional integration of Fourthline SDK
    implementation 'de.solarisbank.identhub:identhub-android-fourthline:1.1.1'
}
```

Then issue a developer access token  https://github.com/settings/tokens with scope `read:packages` and trigger a build from your Terminal like this:

```sh
GITHUB_TOKEN=<< github token >> ./gradlew :my-app:build
```

Once the IdentHub SDK has been downloaded, Gradle will use it from your local cache.

### Optional dependency to Fourthline SDK
The Fourthline SDK is not publicly available. Please get in contact with Solarisbank to request access to it.

## Usage
Add the required permissions for IdentHub to your app's `Manifest.xml` file:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

First you need to create an identification session via the Solarisbank API. The session will contain a URL that can be passed to the IdentHub SDK to create a new session.

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
