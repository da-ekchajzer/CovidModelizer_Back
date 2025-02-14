package com.inf1.app.endpoint;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inf1.app.dto.SituationReelleDTO;
import com.inf1.app.jpa.repository.SituationReelleRepository;

@RestController
@RequestMapping("/reel/complet")
public class ReelCompletEndpoint {

	@Autowired(required = true)
	SituationReelleRepository repository;

	@GetMapping
	public Iterable<SituationReelleDTO> findAll() {
		return repository.findallDTO();
	}

	@GetMapping("/date")
	public List<SituationReelleDTO> findByDate(@RequestParam(name = "date") String date) {
		return repository.findByDate(LocalDate.parse(date));
	}

	@GetMapping("/between")
	public Iterable<SituationReelleDTO> findBetween(@RequestParam(name = "start") String start,
			@RequestParam(name = "end") String end) {
		return repository.findBetweenDate(LocalDate.parse(start), LocalDate.parse(end));
	}

	@GetMapping("/Before")
	public Iterable<SituationReelleDTO> findBefore(@RequestParam(name = "date") String date) {
		return repository.findBeforeDate(LocalDate.parse(date));
	}

	@GetMapping("/after")
	public Iterable<SituationReelleDTO> findAfter(@RequestParam(name = "date") String date) {
		return repository.findAfterDate(LocalDate.parse(date));
	}

	@GetMapping("/lasWeek")
	public Iterable<SituationReelleDTO> LastWeek() {
		return repository.findLastDate(7);
	}

	@GetMapping("/lastDays")
	public Iterable<SituationReelleDTO> LastDays(@RequestParam(name = "nb") int nb) {
		return repository.findLastDate(nb);
	}
}
