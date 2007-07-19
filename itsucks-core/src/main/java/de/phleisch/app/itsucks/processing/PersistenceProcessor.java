/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.processing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.io.FileManager;

/**
 * An data processor which saves the data in an file on the disk.
 * 
 * @author olli
 *
 */
public class PersistenceProcessor extends AbstractDataProcessor implements DataProcessor {

	private static Log mLog = LogFactory.getLog(PersistenceProcessor.class);
	
	private File mFile;
	private FileOutputStream mFileOut = null;
	private OutputStream mBufferedOut = null;
	
	private long mResumeAt;
	
	public PersistenceProcessor() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#supports(de.phleisch.app.itsucks.Job)
	 */
	@Override
	public boolean supports(Job pJob) {
		if(pJob instanceof DownloadJob) {
			DownloadJob downloadJob = (DownloadJob) pJob;
			return downloadJob.isSaveToFile();
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#init()
	 */
	@Override
	public void init() throws Exception {
	
		DataProcessorChain processorChain = getProcessorChain();
		
		DownloadJob downloadJob = (DownloadJob) processorChain.getJob();
		File target_path = downloadJob.getSavePath();
		
		
		FileManager fileManager = new FileManager(target_path);
		
		URL url = processorChain.getDataRetriever().getUrl();
		mFile = fileManager.buildSavePath(url);
	}

	private void prepareOutputStream() throws IOException, FileNotFoundException {
		
		//create the folder
		File folder = mFile.getParentFile();
		
		if(!(folder.exists() && folder.isDirectory())) {
			createFolders(folder);
		}
		
		mLog.debug("saving file: " + mFile);
		
		if(mResumeAt > 0) { //skip bytes when resuming
			mFileOut = new FileOutputStream(mFile, true);
			mFileOut.getChannel().position(mResumeAt);
		} else {
			mFileOut = new FileOutputStream(mFile, false);
		}
		
		mBufferedOut = new BufferedOutputStream(mFileOut);
	}

	private void createFolders(File pFolder) throws IOException {
		
		DownloadJob downloadJob = (DownloadJob) getProcessorChain().getJob();
		
		File baseSavePath = downloadJob.getSavePath();
		
		//move away any files which are in the way
		moveBlockingFiles(pFolder, baseSavePath);
		
		if(!pFolder.exists() && !pFolder.mkdirs()) {
			throw new IOException("Cannot create folder(s): " + pFolder);
		}
		
	}

	private void moveBlockingFiles(File pFolder, File pBaseSavePath) throws IOException {
		
		if(pFolder.equals(pBaseSavePath)) {
			return;
		}
		
		if(pFolder.exists() && pFolder.isFile()) {
			if(!pFolder.renameTo(new File(pFolder.getParentFile(), pFolder.getName() + ".moved"))) {
				throw new IOException("Cannot rename file which is in the way: " + pFolder);
			}
		}
		
		moveBlockingFiles(pFolder.getParentFile(), pBaseSavePath);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#canResume()
	 */
	@Override
	public boolean canResume() {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#resumeAt(long)
	 */
	@Override
	public void resumeAt(long pByteOffset) {
		mResumeAt = pByteOffset;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#process(byte[], int)
	 */
	public byte[] process(byte[] pBuffer, int pBytes) throws Exception {
		
		if(mBufferedOut == null) {
			prepareOutputStream();
		}
		
		mBufferedOut.write(pBuffer, 0, pBytes);
		return pBuffer;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
		if(mBufferedOut != null) {
			mBufferedOut.close();
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#needsDataAsWholeChunk()
	 */
	public boolean needsDataAsWholeChunk() {
		return false;
	}


}
