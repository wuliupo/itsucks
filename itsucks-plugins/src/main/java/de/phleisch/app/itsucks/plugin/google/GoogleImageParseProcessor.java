package de.phleisch.app.itsucks.plugin.google;

import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessorInfo;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.AbstractDataParser;

public class GoogleImageParseProcessor extends AbstractDataParser {

	@Override
	public void resumeAt(long pByteOffset) {
	}

	@Override
	public DataChunk process(DataChunk pDataChunk) throws ProcessingException {
		return pDataChunk;
	}

	@Override
	public DataProcessorInfo getInfo() {
		return null;
	}

	@Override
	public boolean supports(Job pJob) {
		return false;
	}

}
