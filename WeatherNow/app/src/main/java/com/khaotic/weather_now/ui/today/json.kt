package com.khaotic.weather_now.ui.today

//for weather

data class json(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<WeatherX>,
    val wind: Wind
)

data class Clouds(
    val all: Int
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)

data class WeatherX(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

data class Wind(
    val deg: Int,
    val speed: Double
)

//for pollution

data class Pollution(
    val coord: Coord,
    val list: List<LIST>
)

data class LIST(
    val components: Components,
    val dt: Int,
    val main: MainP
)

data class MainP(
    val aqi: Int
)

data class Components(
    val co: Double,
    val nh3: Double,
    val no: Double,
    val no2: Double,
    val o3: Double,
    val pm10: Double,
    val pm2_5: Double,
    val so2: Double
)

//for forecast

data class Forecast(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<olo>,
    val message: Int
)

data class olo(
    val clouds: Clouds,
    val dt: Int,
    val dt_txt: String,
    val main: Main2,
    val pop: Double,
    val rain: Rain,
    val sys: Sys2,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind2
)

data class City(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int
)

data class Main2(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_kf: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class Rain(
    val `3h`: Double
)

data class Sys2(
    val pod: String
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

data class Wind2(
    val deg: Int,
    val gust: Double,
    val speed: Double
)