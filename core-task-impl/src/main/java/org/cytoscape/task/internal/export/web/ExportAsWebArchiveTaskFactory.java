package org.cytoscape.task.internal.export.web;

import java.util.Map;

import org.cytoscape.io.write.CySessionWriterFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskIterator;

public class ExportAsWebArchiveTaskFactory extends AbstractTaskFactory {

	private CySessionWriterFactory writerFactory;

	/**
	 * 
	 * Find correct writer for the web archive.
	 * 
	 * @param writerFactory
	 * @param props
	 */
	@SuppressWarnings("rawtypes")
	public void registerFactory(final CySessionWriterFactory writerFactory, final Map props) {
		if (props.get(ServiceProperties.ID).equals("webSessionWriterFactory")) {
			this.writerFactory = writerFactory;
		}
	}

	@SuppressWarnings("rawtypes")
	public void unregisterFactory(final CySessionWriterFactory writerFactory, final Map props) {
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ExportAsWebArchiveTask(writerFactory));
	}
}
