package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.data.*
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.utils.WeatherUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherCard(
    weather: WeatherResponse,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location
            Text(
                text = "${weather.name}, ${weather.sys.country}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Weather description
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = WeatherUtils.capitalizeFirst(weather.weather[0].description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Weather icon
                AsyncImage(
                    model = WeatherUtils.getWeatherIconUrl(weather.weather[0].icon),
                    contentDescription = "Weather icon",
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Temperature
            Text(
                text = WeatherUtils.formatTemperature(weather.main.temp),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Feels like
            Text(
                text = "Feels like ${WeatherUtils.formatTemperature(weather.main.feelsLike)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Weather details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    label = "Min",
                    value = WeatherUtils.formatTemperature(weather.main.tempMin)
                )
                WeatherDetailItem(
                    label = "Max",
                    value = WeatherUtils.formatTemperature(weather.main.tempMax)
                )
                WeatherDetailItem(
                    label = "Humidity",
                    value = "${weather.main.humidity}%"
                )
                WeatherDetailItem(
                    label = "Wind",
                    value = "${WeatherUtils.formatWindSpeed(weather.wind.speed)} ${WeatherUtils.getWindDirection(weather.wind.deg)}"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Location and pressure details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    label = "Pressure",
                    value = WeatherUtils.formatPressure(weather.main.pressure)
                )
                WeatherDetailItem(
                    label = "Visibility",
                    value = "${weather.visibility / 1000} km"
                )
                WeatherDetailItem(
                    label = "Sea Level",
                    value = WeatherUtils.formatPressure(weather.main.seaLevel)
                )
                WeatherDetailItem(
                    label = "Ground Level",
                    value = WeatherUtils.formatPressure(weather.main.grndLevel)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Position Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PositionDetailItem(
                    label = "Latitude:",
                    value = WeatherUtils.formatPosition(weather.coord.lat)
                )
                PositionDetailItem(
                    label = "Longitude:",
                    value = WeatherUtils.formatPosition(weather.coord.lon)
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PositionDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 600)
@Composable
fun CurrentWeatherCardPreview() {
    WeatherAppTheme {
        CurrentWeatherCard(
            weather = sampleWeatherResponse()
        )
    }
}

@Preview(showBackground = true, widthDp = 180, heightDp = 120)
@Composable
fun WeatherDetailItemPreview() {
    WeatherAppTheme {
        WeatherDetailItem(
            label = "Humidity",
            value = "65%"
        )
    }
}

@Preview(showBackground = true, widthDp = 180, heightDp = 120)
@Composable
fun PositionDetailItemPreview() {
    WeatherAppTheme {
        PositionDetailItem(
            label = "Latitude",
            value = "51.509"
        )
    }
}

// Sample data for preview
private fun sampleWeatherResponse(): WeatherResponse {
    return WeatherResponse(
        coord = Coord(lon = -0.1257, lat = 51.5085),
        weather = listOf(
            Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        base = "stations",
        main = Main(
            temp = 22.5,
            feelsLike = 23.1,
            tempMin = 18.0,
            tempMax = 26.0,
            pressure = 1013,
            humidity = 65,
            seaLevel = 1020,
            grndLevel = 1005
        ),
        visibility = 10000,
        wind = Wind(speed = 3.5, deg = 220),
        clouds = Clouds(all = 0),
        dt = 1694678400,
        sys = Sys(
            country = "GB",
            sunrise = 1694661000,
            sunset = 1694708000
        ),
        timezone = 3600,
        id = 2643743,
        name = "London",
        cod = 200
    )
}