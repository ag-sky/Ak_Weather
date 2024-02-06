package com.example.akweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.akweatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//api key --> 9d03373a1a3c9724ae452bde734ccd91
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("morena")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.svCity
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchWeatherData((query))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String?) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            cityName?.let { retrofit.getWeatherData(it,"9d03373a1a3c9724ae452bde734ccd91", "metric") }
        response?.enqueue(object : Callback<WeatherData>{
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seeLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.tvDegree.setText(temperature + "°C")
                    binding.tvType.text = condition
                    binding.tvMaxValue.text = maxTemp.toString() + "°C"
                    binding.tvMinValue.text = minTemp.toString() + "°C"
                    binding.tvHumidityy.text = humidity.toString() + "%"
                    binding.tvWind.text = windSpeed.toString() + "m/s"
                    binding.tvSunrise.text = time(sunRise)
                    binding.tvSuset.text = time(sunSet)
                    binding.tvSunny.text = condition.toString()
                    binding.tvDayName.text = dayName(System.currentTimeMillis())
                        binding.tvDate.text = date()
                        binding.tvLocation.text = cityName.toString()
                    binding.tvSea.text = seeLevel.toString()+ "hPa"
       changeImageWeather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }
    fun dayName(timeStamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun time(timeStamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timeStamp*1000))
    }

    private fun changeImageWeather(condition: String){
        when(condition){
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.loteeIv.setAnimation(R.raw.cloud)

            }
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.loteeIv.setAnimation(R.raw.sun)

            }
            "Light Rain", "Drizzle", "Moderate Rain","Showers", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.loteeIv.setAnimation(R.raw.rain)

            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.loteeIv.setAnimation(R.raw.snow)

            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.loteeIv.setAnimation(R.raw.sun)
            }
        }
        binding.loteeIv.playAnimation()
    }

}