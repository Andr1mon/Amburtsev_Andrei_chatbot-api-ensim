package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.openWeather.City;
import fr.ensim.interop.introrest.model.openWeather.CutWeather;
import fr.ensim.interop.introrest.model.openWeather.Forecast;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class WeatherRestController {
	private static final Logger logger = Logger.getLogger("WeatherRestController");

	@Value("${open.weather.api.url}")
	private String weatherApiUrl;
	@Value("${open.weather.api.token}")
	private String weatherApiToken;

	// Opérations sur la ressource Message
	@GetMapping("/weather")
	public ResponseEntity<List<CutWeather>> weather(@RequestParam String city, @RequestParam (required = false) Integer days) {
		// Exceptions for days
		if (days == null)
			days = 1;
		else
			if (days > 5 || days < 1)
				return ResponseEntity.badRequest().build();
		logger.log(Level.INFO, "/weather?city="+city+"&days="+days);


		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<City[]> responseEntity = restTemplate.getForEntity(weatherApiUrl+"/geo/1.0/direct?q={city}&limit=1&appid=" + weatherApiToken,
				City[].class, city);
		City[] cities = responseEntity.getBody();
		// Exception for city
		if (cities == null || cities.length == 0) {
			logger.log(Level.INFO, "/weather OnFailure: city '" + city + "' not found");
			return ResponseEntity.notFound().build();
		}
		City firstCity = cities[0];

		Forecast forecast = restTemplate.getForObject(weatherApiUrl+"/data/2.5/forecast?units=metric&lat={lat}&lon={longitude}&appid=" + weatherApiToken,
				Forecast.class, firstCity.getLat(), firstCity.getLon());
			// Exception for forecast
        if (forecast == null)
            return ResponseEntity.notFound().build();
		List<CutWeather> cutWeather = new ArrayList<>();
		CutWeather tempCutWeather;
		for(int day = 0, newDayForecastIndex = 0; day < days; day++, newDayForecastIndex += 8) {
			tempCutWeather = new CutWeather();
			tempCutWeather.setDescription(forecast.getList().get(newDayForecastIndex).getWeather().get(0).getDescription());
			tempCutWeather.setMain(forecast.getList().get(newDayForecastIndex).getWeather().get(0).getMain());
			tempCutWeather.setTemp(forecast.getList().get(newDayForecastIndex).getMain().getTemp());
			tempCutWeather.setDt_txt(forecast.getList().get(newDayForecastIndex).getDt_txt());
			cutWeather.add(tempCutWeather);
		}

		return ResponseEntity.ok().body(cutWeather);
	}
}
