package be.vrt.services.log.collector.transaction.dto;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;

public class JobTransactionLogDto extends AbstractTransactionLog {
	@Override
	public String getType() {
		return "JOB";
	}


}
