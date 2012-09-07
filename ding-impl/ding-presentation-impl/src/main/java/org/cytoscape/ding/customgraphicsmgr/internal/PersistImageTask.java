package org.cytoscape.ding.customgraphicsmgr.internal;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.cytoscape.ding.customgraphics.CustomGraphicsManager;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics;
import org.cytoscape.ding.customgraphics.ImageUtil;
import org.cytoscape.ding.customgraphics.NullCustomGraphics;
import org.cytoscape.ding.customgraphics.bitmap.URLImageCustomGraphics;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistImageTask implements Task {

	private static final Logger logger = LoggerFactory
			.getLogger(PersistImageTask.class);

	private final File location;
	private final CustomGraphicsManager manager;

	private static final int TIMEOUT = 1000;
	private static final int NUM_THREADS = 4;

	private static final String METADATA_FILE = "image_metadata.props";

	/**
	 * Constructor.<br>
	 * 
	 * @param fileName
	 *            Absolute path to the Session file.
	 */
	PersistImageTask(final File location, final CustomGraphicsManager manager) {
		this.location = location;
		this.manager = manager;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor
				.setStatusMessage("Saving image library to your local disk.\n\nPlease wait...");
		taskMonitor.setProgress(0.0);

		// Remove all existing files
		final File[] files = location.listFiles();
		if (files != null) {
			for (File old : files)
				old.delete();
		}

		final long startTime = System.currentTimeMillis();

		final ExecutorService exService = Executors
				.newFixedThreadPool(NUM_THREADS);

		for (final CyCustomGraphics<?> cg : manager.getAllCustomGraphics()) {

			// Save ONLY bitmap image Custom Graphics.
			if (cg instanceof NullCustomGraphics
					|| cg instanceof URLImageCustomGraphics == false)
				continue;

			final Image img = cg.getRenderedImage();
			if (img != null) {
				try {
					exService.submit(new SaveImageTask(location, cg
							.getIdentifier().toString(), ImageUtil
							.toBufferedImage(img)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		try {
			exService.shutdown();
			exService.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

			e.printStackTrace();
			throw e;
		}

		try {
			manager.getMetadata().store(
					new FileOutputStream(new File(location, METADATA_FILE)),
					"Image Metadata");
		} catch (IOException e) {
			throw new IOException("Could not save image metadata file.", e);
		}

		long endTime = System.currentTimeMillis();
		double sec = (endTime - startTime) / (1000.0);
		logger.info("Image saving process finished in " + sec + " sec.");

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	private final class SaveImageTask implements Callable<String> {
		private final File imageHome;
		private String fileName;
		private final BufferedImage image;

		public SaveImageTask(final File imageHomeDirectory, String fileName,
				BufferedImage image) {
			this.imageHome = imageHomeDirectory;
			this.fileName = fileName;
			this.image = image;
		}

		public String call() throws Exception {

			logger.debug("  Saving Image: " + fileName);

			if (!fileName.endsWith(".png"))
				fileName += ".png";
			File file = new File(imageHome, fileName);

			try {
				file.createNewFile();
				ImageIO.write(image, "PNG", file);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return file.toString();
		}
	}

}
