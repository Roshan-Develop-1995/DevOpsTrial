package io.roshan.com.coronavirus.tracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.roshan.com.coronavirus.tracker.models.LocationStats;

@Service
public class CoroVirusService {
	
	private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
	private List<LocationStats> allStats = new ArrayList<>();
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}
	@PostConstruct
	@Scheduled(cron = "* * * * * *")
	public void getCoronaResult() {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(DATA_URL)).build();
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			List <LocationStats> newStats = new ArrayList<>();
			StringReader csvBodyReader = new StringReader(response.body());
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
			for (CSVRecord record : records) {
				LocationStats stat = new LocationStats();
				stat.setCountry(record.get("Country/Region"));
				stat.setState(record.get("Province/State"));
				if(null != record.get(record.size()-1) && !record.get(record.size()-1).isEmpty() && !record.get(record.size()-1).isBlank()) {
					int currentCases = Integer.parseInt(record.get(record.size()-1));
					int prevDayCases = Integer.parseInt(record.get(record.size()-2));
					stat.setTotalCases(currentCases);
					stat.setDiffFromPrevDay(currentCases - prevDayCases);
				}
				newStats.add(stat);
			}
			allStats = newStats;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	
	}

}
