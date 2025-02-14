package com.inf1.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.inf1.app.batch.data.collect.SituationReelleProcessor;

import lombok.Getter;
import lombok.Setter;

public class DatabaseUtils {

	private static final Logger LOG = LoggerFactory.getLogger(SituationReelleProcessor.class);

	@Getter
	@Setter
	static JdbcTemplate jdbcTemplate;

	public static void cleanSituationReelle() {
		LOG.warn("*** Clean situation_relle ***");
		jdbcTemplate.execute("DELETE FROM situation_reelle");
		jdbcTemplate.execute("ALTER TABLE situation_reelle auto_increment = 1");
	}

	public static void cleanModelisation() {
		LOG.warn("*** Clean modelisation ***");
		jdbcTemplate.execute("DELETE FROM modelisation_indicateur");
		jdbcTemplate.execute("DELETE FROM modelisation_coefficient");
		jdbcTemplate.execute("DELETE FROM modelisation");
		jdbcTemplate.execute("ALTER TABLE modelisation auto_increment = 1");
		jdbcTemplate.execute("DELETE FROM indicateur");
		jdbcTemplate.execute("ALTER TABLE indicateur auto_increment = 1");
		jdbcTemplate.execute("DELETE FROM coefficient");
		jdbcTemplate.execute("ALTER TABLE coefficient auto_increment = 1");
		jdbcTemplate.execute("DELETE FROM evenement");
		jdbcTemplate.execute("ALTER TABLE evenement auto_increment = 1");
	}

}
