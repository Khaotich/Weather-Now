package com.khaotic.weather_now.ui.today

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val key_api = "3d5f88365cd2dc55cd27c66f6ba416b4"
private const val metric = "metric"

interface PlaceholderApi
{
    @GET("/data/2.5/weather")
    fun weather(@Query("lon") lot: String,
                @Query("lat") lat: String,
                @Query("appid") key: String = key_api,
                @Query("units") units: String = metric): Call<json>

    @GET("/data/2.5/air_pollution")
    fun pollution(@Query("lon") lot: String,
                  @Query("lat") lat: String,
                  @Query("appid") key: String = key_api): Call<Pollution>

    @GET("/data/2.5/forecast")
    fun forecast(@Query("lon") lot: String,
                 @Query("lat") lat: String,
                 @Query("appid") key: String = key_api,
                 @Query("units") units: String = metric): Call<Forecast>
}