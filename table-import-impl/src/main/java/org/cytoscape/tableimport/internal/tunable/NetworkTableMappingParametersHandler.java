package org.cytoscape.tableimport.internal.tunable;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle;

import org.cytoscape.model.CyTableManager;
import org.cytoscape.tableimport.internal.reader.AttributeMappingParameters;
import org.cytoscape.tableimport.internal.reader.NetworkTableMappingParameters;
import org.cytoscape.tableimport.internal.ui.ImportTablePanel;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.AbstractGUITunableHandler;

import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.util.swing.FileUtil;
import java.util.Map;
import java.util.HashMap;

public class NetworkTableMappingParametersHandler extends AbstractGUITunableHandler {

	private final int dialogType;
    private final CyTableManager tableManager;
    
	private ImportTablePanel importTablePanel;
	private NetworkTableMappingParameters ntmp;
	private final FileUtil fileUtil;
    
	protected NetworkTableMappingParametersHandler(Field field,Object instance, Tunable tunable, 
			final int dialogType, final CyTableManager tableManager) {
		super(field, instance, tunable);
		this.dialogType = dialogType;
		this.tableManager = tableManager;
		this.fileUtil = CytoscapeServices.fileUtil;
		init();
	}
	
	
	protected NetworkTableMappingParametersHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable,
			final int dialogType, final CyTableManager tableManager) {
		super(getter, setter, instance, tunable);
		this.dialogType = dialogType;
		this.tableManager = tableManager;
		this.fileUtil = CytoscapeServices.fileUtil;
		init();
	}
	
	private void init(){
		
		try {
			ntmp = (NetworkTableMappingParameters) getValue();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		try {
			importTablePanel =
				new ImportTablePanel(dialogType, ntmp.is,
				                     ntmp.fileType, null,null, null, null,
				                     null, null, null, tableManager, fileUtil); 
		} catch (Exception e) {
			throw new IllegalStateException("Could not initialize ImportTablePanel.", e);
		}
		panel = new JPanel(new BorderLayout(10, 10));
		panel.add(importTablePanel, BorderLayout.CENTER);
	}
	
	@Override
	public void handle() {
		try{
			ntmp = importTablePanel.getNetworkTableMappingParameters();
			setValue(ntmp);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
