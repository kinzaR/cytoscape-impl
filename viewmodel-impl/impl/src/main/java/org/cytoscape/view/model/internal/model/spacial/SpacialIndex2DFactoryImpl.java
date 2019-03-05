package org.cytoscape.view.model.internal.model.spacial;

import org.cytoscape.view.model.spacial.SpacialIndex2D;
import org.cytoscape.view.model.spacial.SpacialIndex2DFactory;

public class SpacialIndex2DFactoryImpl implements SpacialIndex2DFactory {

	@Override
	public <T> SpacialIndex2D<T> createSpacialIndex2D() {
		return new SpacialIndex2DImpl<T>();
	}

}
