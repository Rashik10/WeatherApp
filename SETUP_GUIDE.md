# Quick Setup Guide 🚀

## Step 1: Get Your OpenWeatherMap API Key

1. **Go to OpenWeatherMap**
   - Visit: https://openweathermap.org/api

2. **Sign Up (Free)**
   - Click "Sign Up" in the top right
   - Fill in your details (email, password, etc.)
   - Verify your email address

3. **Get Your API Key**
   - After logging in, go to your account dashboard
   - Navigate to "API keys" tab
   - Copy your default API key (or create a new one)

## Step 2: Add API Key to Your App

1. **Open the file**: `app/src/main/java/com/example/weatherapp/repository/WeatherRepository.kt`

2. **Find this line**:
   ```kotlin
   private val apiKey = "YOUR_API_KEY_HERE"
   ```

3. **Replace with your actual key**:
   ```kotlin
   private val apiKey = "abcd1234efgh5678ijkl9012mnop3456" // Your actual key
   ```

## Step 3: Build and Run

1. **Open Android Studio**
2. **Open the WeatherApp project**
3. **Wait for Gradle sync**
4. **Click the Run button** ▶️

## That's it! 🎉

Your weather app should now be working. Try:
- Searching for a city (e.g., "London", "New York", "Tokyo")
- Tapping the location icon 📍 to get weather for your current location

## Need Help?

- **API Key not working?** Make sure you copied the entire key and it's activated (can take a few minutes)
- **Location not working?** Make sure you granted location permissions
- **Build errors?** Try "Build > Clean Project" then "Build > Rebuild Project"

---

**Important**: Keep your API key private! Don't share it publicly or commit it to version control in production apps.