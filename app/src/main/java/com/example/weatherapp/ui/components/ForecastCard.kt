package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.ForecastItem
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.*
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.utils.WeatherUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastCard(
    forecast: ForecastResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "5-Day Forecast",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // Take every 8th item to get one forecast per day (3-hour intervals)
                items(forecast.list.filterIndexed { index, _ -> index % 8 == 0 }.take(5)) { item ->
                    ForecastItem(item = item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastItem(
    item: ForecastItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = WeatherUtils.formatDate(item.dt),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = WeatherUtils.capitalizeFirst(item.weather[0].description),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = WeatherUtils.formatTemperature(item.main.temp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "${WeatherUtils.formatTemperature(item.main.tempMin)} / ${WeatherUtils.formatTemperature(item.main.tempMax)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 400)
@Composable
fun ForecastCardPreview() {
    WeatherAppTheme {
        ForecastCard(
            forecast = sampleForecastResponse()
        )
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 200)
@Composable
fun ForecastItemPreview() {
    WeatherAppTheme {
        ForecastItem(
            item = sampleForecastItem()
        )
    }
}

// Sample data for previews
private fun sampleForecastResponse(): ForecastResponse {
    return ForecastResponse(
        cod = "200",
        message = 0,
        cnt = 40,
        list = listOf(
            sampleForecastItem(),
            ForecastItem(
                dt = 1694764800,
                main = Main(
                    temp = 25.0,
                    feelsLike = 25.5,
                    tempMin = 20.0,
                    tempMax = 28.0,
                    pressure = 1015,
                    humidity = 60
                ),
                weather = listOf(
                    Weather(id = 801, main = "Clouds", description = "few clouds", icon = "02d")
                ),
                clouds = Clouds(all = 20),
                wind = Wind(speed = 2.8, deg = 180),
                visibility = 10000,
                pop = 0.1,
                sys = ForecastSys(pod = "d"),
                dtTxt = "2024-09-15 12:00:00"
            ),
            ForecastItem(
                dt = 1694851200,
                main = Main(
                    temp = 19.0,
                    feelsLike = 19.5,
                    tempMin = 15.0,
                    tempMax = 22.0,
                    pressure = 1010,
                    humidity = 75
                ),
                weather = listOf(
                    Weather(id = 500, main = "Rain", description = "light rain", icon = "10d")
                ),
                clouds = Clouds(all = 80),
                wind = Wind(speed = 4.2, deg = 240),
                visibility = 8000,
                pop = 0.8,
                sys = ForecastSys(pod = "d"),
                dtTxt = "2024-09-16 12:00:00"
            )
        ),
        city = City(
            id = 2643743,
            name = "London",
            coord = Coord(lon = -0.1257, lat = 51.5085),
            country = "GB",
            population = 8982000,
            timezone = 3600,
            sunrise = 1694661000,
            sunset = 1694708000
        )
    )
}

private fun sampleForecastItem(): ForecastItem {
    return ForecastItem(
        dt = 1694678400,
        main = Main(
            temp = 22.5,
            feelsLike = 23.1,
            tempMin = 18.0,
            tempMax = 26.0,
            pressure = 1013,
            humidity = 65
        ),
        weather = listOf(
            Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")
        ),
        clouds = Clouds(all = 0),
        wind = Wind(speed = 3.5, deg = 220),
        visibility = 10000,
        pop = 0.0,
        sys = ForecastSys(pod = "d"),
        dtTxt = "2024-09-14 12:00:00"
    )
}