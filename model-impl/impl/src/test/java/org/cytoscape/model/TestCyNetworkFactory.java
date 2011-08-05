/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.model;


import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.equations.Interpreter;
import org.cytoscape.equations.internal.interpreter.InterpreterImpl;
import org.cytoscape.model.internal.ArrayGraph;
import org.cytoscape.model.internal.CyTableManagerImpl;
import org.cytoscape.model.internal.CyTableFactoryImpl;
import org.cytoscape.model.subnetwork.CyRootNetwork;


import org.junit.Test;
import static org.junit.Assert.*;


public class TestCyNetworkFactory {
	public TestCyNetworkFactory() { }

	public static CyNetwork getInstance() {
		return getPublicRootInstance().getBaseNetwork();
	}

	public static CyNetwork getInstanceWithPrivateTables() {
		return getPrivateRootInstance().getBaseNetwork();
	}

	public static CyRootNetwork getPublicRootInstance() {	
		DummyCyEventHelper deh = new DummyCyEventHelper();
		CyTableManagerImpl tm = new CyTableManagerImpl(deh);
		Interpreter interp = new InterpreterImpl();
		ArrayGraph ar = new ArrayGraph(deh, tm, new CyTableFactoryImpl(deh, tm, interp), true);
		return ar; 
	}

	public static CyRootNetwork getPrivateRootInstance() {	
		DummyCyEventHelper deh = new DummyCyEventHelper();
		CyTableManagerImpl tm = new CyTableManagerImpl(deh);
		Interpreter interp = new InterpreterImpl();
		ArrayGraph ar = new ArrayGraph(deh, tm, new CyTableFactoryImpl(deh, tm, interp), false);
		return ar; 
	}
	
	@Test
	public void testFactoryWithPublicNetwork() throws Exception {
		CyNetwork n = getInstance();
		assertNotNull(n);
		assertTrue(n.getDefaultNetworkTable().isPublic());
		assertTrue(n.getDefaultNodeTable().isPublic());
		assertTrue(n.getDefaultEdgeTable().isPublic());
	}
	
	@Test
	public void testFactoryWithPrivateNetwork() throws Exception {
		CyNetwork n = getInstanceWithPrivateTables();
		assertNotNull(n);
		assertFalse(n.getDefaultNetworkTable().isPublic());
		assertFalse(n.getDefaultNodeTable().isPublic());
		assertFalse(n.getDefaultEdgeTable().isPublic());
	}
}

