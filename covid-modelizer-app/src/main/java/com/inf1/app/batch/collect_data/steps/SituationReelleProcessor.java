package com.inf1.app.batch.collect_data.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.inf1.app.utils.DatabaseUtils;
import com.inf1.app.dto.SituationReelleDTO;

public class SituationReelleProcessor implements ItemProcessor<SituationReelleDTO, SituationReelleDTO> {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SituationReelleProcessor.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	private boolean databaseCleaned = false;

	@Override
	public SituationReelleDTO process(SituationReelleDTO item) throws Exception {
		if (this.databaseCleaned == false) {
			this.databaseCleaned = true;
			DatabaseUtils.cleanDatabase(jdbcTemplate);
		}
		return item;
	}
}
