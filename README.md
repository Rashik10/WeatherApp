# Weather App 🌤️

A modern Android weather application built with **Kotlin** and **Jetpack Compose** that provides current weather information and 5-day forecasts using the OpenWeatherMap API.

## Features

- 🌡️ **Current Weather**: Real-time weather data including temperature, humidity, wind speed, and conditions
- 📅 **5-Day Forecast**: Extended weather forecast with daily predictions
- 📍 **Location-Based**: Get weather for your current location automatically
- 🔍 **City Search**: Search for weather in any city worldwide
- 🎨 **Modern UI**: Beautiful Material 3 design with Jetpack Compose
- 📱 **Responsive**: Optimized for different screen sizes

## Screenshots

![Weather App Screenshots](screenshots/weather_app_preview.png)

## Architecture

This app follows **MVVM (Model-View-ViewModel)** architecture pattern and uses modern Android development best practices:

- **UI Layer**: Jetpack Compose for declarative UI
- **Business Logic**: ViewModel with LiveData/StateFlow
- **Data Layer**: Repository pattern with Retrofit for API calls
- **Dependency Injection**: Manual injection (can be upgraded to Hilt/Dagger)

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Networking**: Retrofit + OkHttp
- **JSON Parsing**: Gson
- **Location Services**: Google Play Services Location
- **Permissions**: Accompanist Permissions
- **API**: OpenWeatherMap API

## Setup Instructions

### Prerequisites

1. **Android Studio**: Latest stable version (Electric Eel or newer)
2. **Android SDK**: API level 24 (Android 7.0) or higher
3. **OpenWeatherMap API Key**: Free account at [OpenWeatherMap](https://openweathermap.org/api)

### Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/weather-app.git
   cd weather-app
   ```

2. **Get OpenWeatherMap API Key**
   - Visit [OpenWeatherMap](https://openweathermap.org/api)
   - Sign up for a free account
   - Generate your API key from the dashboard

3. **Configure API Key**
   - Open `app/src/main/java/com/example/weatherapp/repository/WeatherRepository.kt`
   - Replace `YOUR_API_KEY_HERE` with your actual API key:
   ```kotlin
   private val apiKey = "your_actual_api_key_here"
   ```

4. **Build and Run**
   - Open the project in Android Studio
   - Wait for Gradle sync to complete
   - Run the app on an emulator or physical device

### API Key Security (Production)

For production apps, store your API key securely:

1. **Add to `local.properties`**:
   ```properties
   WEATHER_API_KEY=your_actual_api_key_here
   ```

2. **Update `build.gradle.kts`**:
   ```kotlin
   android {
       defaultConfig {
           buildConfigField "String", "WEATHER_API_KEY", "\"${project.findProperty("WEATHER_API_KEY")}\""
       }
   }
   ```

3. **Use in code**:
   ```kotlin
   private val apiKey = BuildConfig.WEATHER_API_KEY
   ```

## Permissions

The app requires the following permissions:

- **INTERNET**: For API calls to fetch weather data
- **ACCESS_FINE_LOCATION**: For precise location-based weather
- **ACCESS_COARSE_LOCATION**: For approximate location-based weather

## Project Structure

```
app/src/main/java/com/example/weatherapp/
├── data/                   # Data models and DTOs
│   ├── WeatherModels.kt
│   └── ForecastModels.kt
├── network/                # API service and network configuration
│   ├── WeatherApiService.kt
│   └── WeatherApiClient.kt
├── repository/             # Data repository layer
│   └── WeatherRepository.kt
├── viewmodel/              # ViewModels for UI state management
│   └── WeatherViewModel.kt
├── ui/
│   ├── components/         # Reusable UI components
│   │   ├── CurrentWeatherCard.kt
│   │   ├── ForecastCard.kt
│   │   ├── SearchBar.kt
│   │   ├── CommonComponents.kt
│   │   └── LocationPermissionHandler.kt
│   └── theme/              # App theming
├── utils/                  # Utility classes
│   ├── WeatherUtils.kt
│   └── LocationUtils.kt
├── location/               # Location management
│   └── LocationManager.kt
└── MainActivity.kt         # Main activity
```

## API Endpoints

The app uses the following OpenWeatherMap API endpoints:

- **Current Weather**: `GET /weather`
- **5-Day Forecast**: `GET /forecast`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Future Enhancements

- [ ] Weather icons from OpenWeatherMap
- [ ] Hourly forecast view
- [ ] Weather maps integration
- [ ] Multiple location favorites
- [ ] Weather alerts and notifications
- [ ] Dark/Light theme toggle
- [ ] Weather data caching for offline use
- [ ] Weather widgets
- [ ] Unit conversion (Celsius/Fahrenheit)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for the weather API
- [Material Design 3](https://m3.material.io/) for design guidelines
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the modern UI toolkit

---

Built with ❤️ using Kotlin and Jetpack Compose