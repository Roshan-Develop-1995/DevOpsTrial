package io.roshan.com.coronavirus.tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.roshan.com.coronavirus.tracker.models.LocationStats;
import io.roshan.com.coronavirus.tracker.service.CoroVirusService;

@Controller
public class HomeController {
	
	@Autowired
	CoroVirusService coroVirusService;
	
	@GetMapping("/")
	public String home(Model model) {
		List<LocationStats> allStats = coroVirusService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getTotalCases()).sum();
		int newReportedCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		allStats.sort((o1,o2)->(o1.getCountry()+o1.getState()).compareTo(o2.getCountry()+o2.getState()));
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("newReportedCases", newReportedCases);
		return "home";
	}

}
