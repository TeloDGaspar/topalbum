# iTunes Top Albums

Android app that lists the top 100 albums from the iTunes US store. Tap an album to see its details.

Data comes from `https://itunes.apple.com/us/rss/topalbums/limit=100/json`.

## Stack

Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit + OkHttp, Coil, Coroutines/StateFlow, Navigation Compose.

Tests use JUnit, MockK, coroutines-test and MockWebServer.

## Modules

```
app/             application, navigation, theme
core/            shared network setup (Retrofit/OkHttp)
feature/albums/  data, domain and presentation for the albums feature
```

The albums feature follows a data/domain/presentation split. The repository caches the feed in memory, so the detail screen doesn't make another request.

## Build and run

```bash
./gradlew :app:installDebug
```

## Tests

```bash
./gradlew test
```
