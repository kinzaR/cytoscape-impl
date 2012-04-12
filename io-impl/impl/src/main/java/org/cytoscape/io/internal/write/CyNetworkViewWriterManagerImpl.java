package org.cytoscape.io.internal.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

public class CyNetworkViewWriterManagerImpl extends AbstractWriterManager<CyNetworkViewWriterFactory> implements CyNetworkViewWriterManager {
	public CyNetworkViewWriterManagerImpl() {
		super(DataCategory.NETWORK);		
	}

	@Override
	public CyWriter getWriter(CyNetworkView view, CyFileFilter filter, File file) throws Exception {
		return getWriter(view, filter, new FileOutputStream(file));
	}

	@Override
	public CyWriter getWriter(CyNetworkView view, CyFileFilter filter, OutputStream os) throws Exception {
		CyNetworkViewWriterFactory factory = getMatchingFactory(filter);
		if (factory == null) {
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		}
		return factory.getWriterTask(os,view);
	}

	@Override
	public CyWriter getWriter(CyNetwork network, CyFileFilter filter, File file)
			throws Exception {
		return getWriter(network, filter, new FileOutputStream(file));
	}

	@Override
	public CyWriter getWriter(CyNetwork network, CyFileFilter filter, OutputStream os) throws Exception {
		CyNetworkViewWriterFactory factory = getMatchingFactory(filter);
		if (factory == null) {
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		}
		return factory.getWriterTask(os,network);
	}
}
