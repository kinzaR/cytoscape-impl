package org.cytoscape.task.internal;

import static org.cytoscape.application.swing.ActionEnableSupport.ENABLE_FOR_NETWORK;
import static org.cytoscape.application.swing.ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW;
import static org.cytoscape.application.swing.ActionEnableSupport.ENABLE_FOR_SELECTED_EDGES;
import static org.cytoscape.application.swing.ActionEnableSupport.ENABLE_FOR_SELECTED_NODES;
import static org.cytoscape.application.swing.ActionEnableSupport.ENABLE_FOR_SELECTED_NODES_OR_EDGES;
import static org.cytoscape.application.swing.ActionEnableSupport.ENABLE_FOR_SINGLE_NETWORK;
import static org.cytoscape.work.ServiceProperties.ACCELERATOR;
import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;
import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_AFTER;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.IN_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.IN_NETWORK_PANEL_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.IN_TOOL_BAR;
import static org.cytoscape.work.ServiceProperties.LARGE_ICON_URL;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.NETWORK_GROUP_MENU;
import static org.cytoscape.work.ServiceProperties.NETWORK_SELECT_MENU;
import static org.cytoscape.work.ServiceProperties.NODE_ADD_MENU;
import static org.cytoscape.work.ServiceProperties.NODE_GROUP_MENU;
import static org.cytoscape.work.ServiceProperties.NODE_SELECT_MENU;
import static org.cytoscape.work.ServiceProperties.PREFERRED_ACTION;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;
import static org.cytoscape.work.ServiceProperties.TOOLTIP;
import static org.cytoscape.work.ServiceProperties.TOOL_BAR_GRAVITY;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.util.RecentlyOpenedTracker;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import org.cytoscape.io.write.CySessionWriterFactory;
import org.cytoscape.io.write.CySessionWriterManager;
import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.task.TableCellTaskFactory;
import org.cytoscape.task.TableColumnTaskFactory;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.task.create.CloneNetworkTaskFactory;
import org.cytoscape.task.create.CreateNetworkViewTaskFactory;
import org.cytoscape.task.create.NewEmptyNetworkViewFactory;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.create.NewNetworkSelectedNodesOnlyTaskFactory;
import org.cytoscape.task.create.NewSessionTaskFactory;
import org.cytoscape.task.destroy.DeleteColumnTaskFactory;
import org.cytoscape.task.destroy.DeleteSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.task.destroy.DeleteTableTaskFactory;
import org.cytoscape.task.destroy.DestroyNetworkTaskFactory;
import org.cytoscape.task.destroy.DestroyNetworkViewTaskFactory;
import org.cytoscape.task.edit.CollapseGroupTaskFactory;
import org.cytoscape.task.edit.ConnectSelectedNodesTaskFactory;
import org.cytoscape.task.edit.EditNetworkTitleTaskFactory;
import org.cytoscape.task.edit.ExpandGroupTaskFactory;
import org.cytoscape.task.edit.GroupNodesTaskFactory;
import org.cytoscape.task.edit.ImportDataTableTaskFactory;
import org.cytoscape.task.edit.MapGlobalToLocalTableTaskFactory;
import org.cytoscape.task.edit.MapTableToNetworkTablesTaskFactory;
import org.cytoscape.task.edit.MergeTablesTaskFactory;
import org.cytoscape.task.edit.RenameColumnTaskFactory;
import org.cytoscape.task.edit.UnGroupNodesTaskFactory;
import org.cytoscape.task.edit.UnGroupTaskFactory;
import org.cytoscape.task.hide.HideSelectedEdgesTaskFactory;
import org.cytoscape.task.hide.HideSelectedNodesTaskFactory;
import org.cytoscape.task.hide.HideSelectedTaskFactory;
import org.cytoscape.task.hide.HideUnselectedEdgesTaskFactory;
import org.cytoscape.task.hide.HideUnselectedNodesTaskFactory;
import org.cytoscape.task.hide.HideUnselectedTaskFactory;
import org.cytoscape.task.hide.UnHideAllEdgesTaskFactory;
import org.cytoscape.task.hide.UnHideAllNodesTaskFactory;
import org.cytoscape.task.hide.UnHideAllTaskFactory;
import org.cytoscape.task.internal.creation.CloneNetworkTaskFactoryImpl;
import org.cytoscape.task.internal.creation.CreateNetworkViewTaskFactoryImpl;
import org.cytoscape.task.internal.creation.NewEmptyNetworkTaskFactoryImpl;
import org.cytoscape.task.internal.creation.NewNetworkCommandTaskFactory;
import org.cytoscape.task.internal.creation.NewNetworkSelectedNodesEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.creation.NewNetworkSelectedNodesOnlyTaskFactoryImpl;
import org.cytoscape.task.internal.destruction.DestroyNetworkTaskFactoryImpl;
import org.cytoscape.task.internal.destruction.DestroyNetworkViewTaskFactoryImpl;
import org.cytoscape.task.internal.edit.ConnectSelectedNodesTaskFactoryImpl;
import org.cytoscape.task.internal.export.graphics.ExportNetworkImageTaskFactoryImpl;
import org.cytoscape.task.internal.export.network.ExportNetworkTaskFactoryImpl;
import org.cytoscape.task.internal.export.network.ExportNetworkViewTaskFactoryImpl;
import org.cytoscape.task.internal.export.network.ExportSelectedNetworkTaskFactoryImpl;
import org.cytoscape.task.internal.export.network.LoadMultipleNetworkFilesTaskFactoryImpl;
import org.cytoscape.task.internal.export.network.LoadNetworkFileTaskFactoryImpl;
import org.cytoscape.task.internal.export.network.LoadNetworkURLTaskFactoryImpl;
import org.cytoscape.task.internal.export.table.ExportNoGuiSelectedTableTaskFactoryImpl;
import org.cytoscape.task.internal.export.table.ExportSelectedTableTaskFactoryImpl;
import org.cytoscape.task.internal.export.table.ExportTableTaskFactoryImpl;
import org.cytoscape.task.internal.export.web.ExportAsWebArchiveTaskFactory;
import org.cytoscape.task.internal.group.AddToGroupTaskFactory;
import org.cytoscape.task.internal.group.GroupNodeContextTaskFactoryImpl;
import org.cytoscape.task.internal.group.GroupNodesTaskFactoryImpl;
import org.cytoscape.task.internal.group.ListGroupsTaskFactory;
import org.cytoscape.task.internal.group.RemoveFromGroupTaskFactory;
import org.cytoscape.task.internal.group.RenameGroupTaskFactory;
import org.cytoscape.task.internal.group.UnGroupNodesTaskFactoryImpl;
import org.cytoscape.task.internal.help.HelpTaskFactory;
import org.cytoscape.task.internal.hide.HideCommandTaskFactory;
import org.cytoscape.task.internal.hide.HideSelectedEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.hide.HideSelectedNodesTaskFactoryImpl;
import org.cytoscape.task.internal.hide.HideSelectedTaskFactoryImpl;
import org.cytoscape.task.internal.hide.HideUnselectedEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.hide.HideUnselectedNodesTaskFactoryImpl;
import org.cytoscape.task.internal.hide.HideUnselectedTaskFactoryImpl;
import org.cytoscape.task.internal.hide.UnHideAllEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.hide.UnHideAllNodesTaskFactoryImpl;
import org.cytoscape.task.internal.hide.UnHideAllTaskFactoryImpl;
import org.cytoscape.task.internal.hide.UnHideCommandTaskFactory;
import org.cytoscape.task.internal.layout.ApplyPreferredLayoutTaskFactoryImpl;
import org.cytoscape.task.internal.layout.GetPreferredLayoutTaskFactory;
import org.cytoscape.task.internal.layout.SetPreferredLayoutTaskFactory;
import org.cytoscape.task.internal.loaddatatable.LoadTableFileTaskFactoryImpl;
import org.cytoscape.task.internal.loaddatatable.LoadTableURLTaskFactoryImpl;
import org.cytoscape.task.internal.networkobjects.AddEdgeTaskFactory;
import org.cytoscape.task.internal.networkobjects.AddNodeTaskFactory;
import org.cytoscape.task.internal.networkobjects.AddTaskFactory;
import org.cytoscape.task.internal.networkobjects.DeleteSelectedNodesAndEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.networkobjects.GetEdgeTaskFactory;
import org.cytoscape.task.internal.networkobjects.GetNetworkTaskFactory;
import org.cytoscape.task.internal.networkobjects.GetNodeTaskFactory;
import org.cytoscape.task.internal.networkobjects.GetPropertiesTaskFactory;
import org.cytoscape.task.internal.networkobjects.ListEdgesTaskFactory;
import org.cytoscape.task.internal.networkobjects.ListNetworksTaskFactory;
import org.cytoscape.task.internal.networkobjects.ListNodesTaskFactory;
import org.cytoscape.task.internal.networkobjects.ListPropertiesTaskFactory;
import org.cytoscape.task.internal.networkobjects.RenameEdgeTaskFactory;
import org.cytoscape.task.internal.networkobjects.RenameNodeTaskFactory;
import org.cytoscape.task.internal.networkobjects.SetCurrentNetworkTaskFactory;
import org.cytoscape.task.internal.networkobjects.SetPropertiesTaskFactory;
import org.cytoscape.task.internal.proxysettings.ProxySettingsTaskFactoryImpl;
import org.cytoscape.task.internal.select.DeselectAllEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.select.DeselectAllNodesTaskFactoryImpl;
import org.cytoscape.task.internal.select.DeselectAllTaskFactoryImpl;
import org.cytoscape.task.internal.select.DeselectTaskFactory;
import org.cytoscape.task.internal.select.InvertSelectedEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.select.InvertSelectedNodesTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectAdjacentEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectAllEdgesTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectAllNodesTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectAllTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectConnectedNodesTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectFirstNeighborsNodeViewTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectFirstNeighborsTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectFromFileListTaskFactoryImpl;
import org.cytoscape.task.internal.select.SelectTaskFactory;
import org.cytoscape.task.internal.session.NewSessionTaskFactoryImpl;
import org.cytoscape.task.internal.session.OpenSessionCommandTaskFactory;
import org.cytoscape.task.internal.session.OpenSessionTaskFactoryImpl;
import org.cytoscape.task.internal.session.SaveSessionAsTaskFactoryImpl;
import org.cytoscape.task.internal.session.SaveSessionTaskFactoryImpl;
import org.cytoscape.task.internal.table.AddRowTaskFactory;
import org.cytoscape.task.internal.table.CopyValueToColumnTaskFactoryImpl;
import org.cytoscape.task.internal.table.CreateColumnTaskFactory;
import org.cytoscape.task.internal.table.CreateNetworkAttributeTaskFactory;
import org.cytoscape.task.internal.table.CreateTableTaskFactory;
import org.cytoscape.task.internal.table.DeleteColumnCommandTaskFactory;
import org.cytoscape.task.internal.table.DeleteColumnTaskFactoryImpl;
import org.cytoscape.task.internal.table.DeleteRowTaskFactory;
import org.cytoscape.task.internal.table.DeleteTableTaskFactoryImpl;
import org.cytoscape.task.internal.table.DestroyTableTaskFactory;
import org.cytoscape.task.internal.table.GetColumnTaskFactory;
import org.cytoscape.task.internal.table.GetNetworkAttributeTaskFactory;
import org.cytoscape.task.internal.table.GetRowTaskFactory;
import org.cytoscape.task.internal.table.GetValueTaskFactory;
import org.cytoscape.task.internal.table.ImportTableDataTaskFactoryImpl;
import org.cytoscape.task.internal.table.ListColumnsTaskFactory;
import org.cytoscape.task.internal.table.ListNetworkAttributesTaskFactory;
import org.cytoscape.task.internal.table.ListRowsTaskFactory;
import org.cytoscape.task.internal.table.ListTablesTaskFactory;
import org.cytoscape.task.internal.table.MapGlobalToLocalTableTaskFactoryImpl;
import org.cytoscape.task.internal.table.MapTableToNetworkTablesTaskFactoryImpl;
import org.cytoscape.task.internal.table.MergeTablesTaskFactoryImpl;
import org.cytoscape.task.internal.table.RenameColumnTaskFactoryImpl;
import org.cytoscape.task.internal.table.SetNetworkAttributeTaskFactory;
import org.cytoscape.task.internal.table.SetTableTitleTaskFactory;
import org.cytoscape.task.internal.table.SetValuesTaskFactory;
import org.cytoscape.task.internal.title.EditNetworkTitleTaskFactoryImpl;
import org.cytoscape.task.internal.view.GetCurrentNetworkViewTaskFactory;
import org.cytoscape.task.internal.view.ListNetworkViewsTaskFactory;
import org.cytoscape.task.internal.view.SetCurrentNetworkViewTaskFactory;
import org.cytoscape.task.internal.view.UpdateNetworkViewTaskFactory;
import org.cytoscape.task.internal.vizmap.ApplyVisualStyleTaskFactoryimpl;
import org.cytoscape.task.internal.vizmap.ClearAllEdgeBendsFactory;
import org.cytoscape.task.internal.vizmap.ExportVizmapTaskFactoryImpl;
import org.cytoscape.task.internal.vizmap.LoadVizmapFileTaskFactoryImpl;
import org.cytoscape.task.internal.zoom.FitContentTaskFactory;
import org.cytoscape.task.internal.zoom.FitSelectedTaskFactory;
import org.cytoscape.task.internal.zoom.ZoomInTaskFactory;
import org.cytoscape.task.internal.zoom.ZoomOutTaskFactory;
import org.cytoscape.task.read.LoadMultipleNetworkFilesTaskFactory;
import org.cytoscape.task.read.LoadNetworkFileTaskFactory;
import org.cytoscape.task.read.LoadNetworkURLTaskFactory;
import org.cytoscape.task.read.LoadTableFileTaskFactory;
import org.cytoscape.task.read.LoadTableURLTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.task.read.OpenSessionTaskFactory;
import org.cytoscape.task.select.DeselectAllEdgesTaskFactory;
import org.cytoscape.task.select.DeselectAllNodesTaskFactory;
import org.cytoscape.task.select.DeselectAllTaskFactory;
import org.cytoscape.task.select.InvertSelectedEdgesTaskFactory;
import org.cytoscape.task.select.InvertSelectedNodesTaskFactory;
import org.cytoscape.task.select.SelectAdjacentEdgesTaskFactory;
import org.cytoscape.task.select.SelectAllEdgesTaskFactory;
import org.cytoscape.task.select.SelectAllNodesTaskFactory;
import org.cytoscape.task.select.SelectAllTaskFactory;
import org.cytoscape.task.select.SelectConnectedNodesTaskFactory;
import org.cytoscape.task.select.SelectFirstNeighborsNodeViewTaskFactory;
import org.cytoscape.task.select.SelectFirstNeighborsTaskFactory;
import org.cytoscape.task.select.SelectFromFileListTaskFactory;
import org.cytoscape.task.visualize.ApplyPreferredLayoutTaskFactory;
import org.cytoscape.task.visualize.ApplyVisualStyleTaskFactory;
import org.cytoscape.task.write.ExportNetworkImageTaskFactory;
import org.cytoscape.task.write.ExportNetworkTaskFactory;
import org.cytoscape.task.write.ExportNetworkViewTaskFactory;
import org.cytoscape.task.write.ExportSelectedNetworkTaskFactory;
import org.cytoscape.task.write.ExportSelectedTableTaskFactory;
import org.cytoscape.task.write.ExportTableTaskFactory;
import org.cytoscape.task.write.ExportVizmapTaskFactory;
import org.cytoscape.task.write.SaveSessionAsTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

/*
 * #%L
 * Cytoscape Core Task Impl (core-task-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2017 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

public class CyActivator extends AbstractCyActivator {
	
	@Override
	public void start(BundleContext bc) {
		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);
		CyEventHelper cyEventHelperRef = getService(bc,CyEventHelper.class);
		RecentlyOpenedTracker recentlyOpenedTrackerServiceRef = getService(bc,RecentlyOpenedTracker.class);
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);
		UndoSupport undoSupportServiceRef = getService(bc,UndoSupport.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);
		CyRootNetworkManager cyRootNetworkFactoryServiceRef = getService(bc,CyRootNetworkManager.class);
		VisualMappingManager visualMappingManagerServiceRef = getService(bc,VisualMappingManager.class);
		StreamUtil streamUtilRef = getService(bc,StreamUtil.class);
		CyNetworkViewWriterManager networkViewWriterManagerServiceRef = getService(bc,CyNetworkViewWriterManager.class);
		CySessionWriterManager sessionWriterManagerServiceRef = getService(bc,CySessionWriterManager.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);
		CySessionManager cySessionManagerServiceRef = getService(bc,CySessionManager.class);
		CyProperty cyPropertyServiceRef = getService(bc,CyProperty.class,"(cyPropertyName=cytoscape3.props)");
		CyTableFactory cyTableFactoryServiceRef = getService(bc,CyTableFactory.class);
		CyTableManager cyTableManagerServiceRef = getService(bc,CyTableManager.class);
		CyLayoutAlgorithmManager cyLayoutsServiceRef = getService(bc,CyLayoutAlgorithmManager.class);
		CyTableWriterManager cyTableWriterManagerRef = getService(bc,CyTableWriterManager.class);
		SynchronousTaskManager<?> synchronousTaskManagerServiceRef = getService(bc,SynchronousTaskManager.class);
		TunableSetter tunableSetterServiceRef = getService(bc,TunableSetter.class);
		CyRootNetworkManager rootNetworkManagerServiceRef = getService(bc, CyRootNetworkManager.class);
		CyNetworkTableManager cyNetworkTableManagerServiceRef = getService(bc, CyNetworkTableManager.class);
		RenderingEngineManager renderingEngineManagerServiceRef = getService(bc, RenderingEngineManager.class);
		CyNetworkViewFactory nullNetworkViewFactory = getService(bc, CyNetworkViewFactory.class, "(id=NullCyNetworkViewFactory)");
		
		CyGroupManager cyGroupManager = getService(bc, CyGroupManager.class);
		CyGroupFactory cyGroupFactory = getService(bc, CyGroupFactory.class);
		
		SelectAllTaskFactoryImpl selectAllTaskFactory = new SelectAllTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		SelectAllEdgesTaskFactoryImpl selectAllEdgesTaskFactory = new SelectAllEdgesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		SelectAllNodesTaskFactoryImpl selectAllNodesTaskFactory = new SelectAllNodesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		SelectAdjacentEdgesTaskFactoryImpl selectAdjacentEdgesTaskFactory = new SelectAdjacentEdgesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		SelectConnectedNodesTaskFactoryImpl selectConnectedNodesTaskFactory = new SelectConnectedNodesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		
		SelectFirstNeighborsTaskFactoryImpl selectFirstNeighborsTaskFactory = new SelectFirstNeighborsTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef, CyEdge.Type.ANY);
		SelectFirstNeighborsTaskFactoryImpl selectFirstNeighborsTaskFactoryInEdge = new SelectFirstNeighborsTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef, CyEdge.Type.INCOMING);
		SelectFirstNeighborsTaskFactoryImpl selectFirstNeighborsTaskFactoryOutEdge = new SelectFirstNeighborsTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef, CyEdge.Type.OUTGOING);
		
		DeselectAllTaskFactoryImpl deselectAllTaskFactory = new DeselectAllTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		DeselectAllEdgesTaskFactoryImpl deselectAllEdgesTaskFactory = new DeselectAllEdgesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		DeselectAllNodesTaskFactoryImpl deselectAllNodesTaskFactory = new DeselectAllNodesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		InvertSelectedEdgesTaskFactoryImpl invertSelectedEdgesTaskFactory = new InvertSelectedEdgesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		InvertSelectedNodesTaskFactoryImpl invertSelectedNodesTaskFactory = new InvertSelectedNodesTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef);
		SelectFromFileListTaskFactoryImpl selectFromFileListTaskFactory = new SelectFromFileListTaskFactoryImpl(undoSupportServiceRef,cyNetworkViewManagerServiceRef,cyEventHelperRef, tunableSetterServiceRef);
		
		SelectFirstNeighborsNodeViewTaskFactoryImpl selectFirstNeighborsNodeViewTaskFactory = new SelectFirstNeighborsNodeViewTaskFactoryImpl(CyEdge.Type.ANY,cyEventHelperRef);
		
		HelpTaskFactory helpTaskFactory = new HelpTaskFactory(serviceRegistrar);
		
		NewSessionTaskFactoryImpl newSessionTaskFactory = new NewSessionTaskFactoryImpl(serviceRegistrar);
		OpenSessionCommandTaskFactory openSessionCommandTaskFactory = new OpenSessionCommandTaskFactory(serviceRegistrar);
		OpenSessionTaskFactoryImpl openSessionTaskFactory = new OpenSessionTaskFactoryImpl(serviceRegistrar);
		SaveSessionTaskFactoryImpl saveSessionTaskFactory = new SaveSessionTaskFactoryImpl( sessionWriterManagerServiceRef, cySessionManagerServiceRef, recentlyOpenedTrackerServiceRef, cyEventHelperRef);
		SaveSessionAsTaskFactoryImpl saveSessionAsTaskFactory = new SaveSessionAsTaskFactoryImpl( sessionWriterManagerServiceRef, cySessionManagerServiceRef, recentlyOpenedTrackerServiceRef, cyEventHelperRef, tunableSetterServiceRef);
		EditNetworkTitleTaskFactoryImpl editNetworkTitleTaskFactory = new EditNetworkTitleTaskFactoryImpl(undoSupportServiceRef, cyNetworkManagerServiceRef, cyNetworkNamingServiceRef, tunableSetterServiceRef);
		
		ApplyPreferredLayoutTaskFactoryImpl applyPreferredLayoutTaskFactory = new ApplyPreferredLayoutTaskFactoryImpl(cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef, cyLayoutsServiceRef);
		DeleteColumnTaskFactoryImpl deleteColumnTaskFactory = new DeleteColumnTaskFactoryImpl(undoSupportServiceRef);
		RenameColumnTaskFactoryImpl renameColumnTaskFactory = new RenameColumnTaskFactoryImpl(undoSupportServiceRef, tunableSetterServiceRef);
		
		CopyValueToColumnTaskFactoryImpl copyValueToEntireColumnTaskFactory = new CopyValueToColumnTaskFactoryImpl(undoSupportServiceRef, false, "Apply to entire column");
		CopyValueToColumnTaskFactoryImpl copyValueToSelectedNodesTaskFactory = new CopyValueToColumnTaskFactoryImpl(undoSupportServiceRef, true, "Apply to selected nodes");
		CopyValueToColumnTaskFactoryImpl copyValueToSelectedEdgesTaskFactory = new CopyValueToColumnTaskFactoryImpl(undoSupportServiceRef, true, "Apply to selected edges");

		ConnectSelectedNodesTaskFactoryImpl connectSelectedNodesTaskFactory = new ConnectSelectedNodesTaskFactoryImpl(undoSupportServiceRef, cyEventHelperRef, visualMappingManagerServiceRef, cyNetworkViewManagerServiceRef);
		
		DynamicTaskFactoryProvisionerImpl dynamicTaskFactoryProvisionerImpl = new DynamicTaskFactoryProvisionerImpl(cyApplicationManagerServiceRef);
		registerAllServices(bc, dynamicTaskFactoryProvisionerImpl, new Properties());

		// --[ NETWORK ]------------------------------------------------------------------------------------------------
		{
			NewEmptyNetworkTaskFactoryImpl factory = new NewEmptyNetworkTaskFactoryImpl(cyNetworkFactoryServiceRef,
					cyNetworkManagerServiceRef, cyNetworkViewManagerServiceRef, cyNetworkNamingServiceRef,
					synchronousTaskManagerServiceRef, visualMappingManagerServiceRef, cyRootNetworkFactoryServiceRef,
					cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File.New.Network");
			props.setProperty(MENU_GRAVITY, "4.0");
			props.setProperty(TITLE, "Empty Network");
			props.setProperty(COMMAND, "create empty");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Create an empty network");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, NewEmptyNetworkViewFactory.class, props);
			registerServiceListener(bc, factory::addNetworkViewRenderer, factory::removeNetworkViewRenderer, NetworkViewRenderer.class);
		}
		{
			NewNetworkSelectedNodesEdgesTaskFactoryImpl factory = new NewNetworkSelectedNodesEdgesTaskFactoryImpl(
					undoSupportServiceRef, cyRootNetworkFactoryServiceRef, cyNetworkViewFactoryServiceRef,
					cyNetworkManagerServiceRef, cyNetworkViewManagerServiceRef, cyNetworkNamingServiceRef,
					visualMappingManagerServiceRef, cyApplicationManagerServiceRef, cyEventHelperRef, cyGroupManager,
					renderingEngineManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SELECTED_NODES_OR_EDGES);
			props.setProperty(PREFERRED_MENU, "File.New.Network");
			props.setProperty(MENU_GRAVITY, "2.0");
			props.setProperty(ACCELERATOR, "cmd shift n");
			props.setProperty(TITLE, "From selected nodes, selected edges");
			// props.setProperty(COMMAND, "create from selected nodes and edges");
			// props.setProperty(COMMAND_NAMESPACE, "network");
			registerService(bc, factory, NetworkTaskFactory.class, props);
			registerService(bc, factory, NewNetworkSelectedNodesAndEdgesTaskFactory.class, props);
		}
		{
			NewNetworkSelectedNodesOnlyTaskFactoryImpl factory = new NewNetworkSelectedNodesOnlyTaskFactoryImpl(
					undoSupportServiceRef, cyRootNetworkFactoryServiceRef, cyNetworkViewFactoryServiceRef,
					cyNetworkManagerServiceRef, cyNetworkViewManagerServiceRef, cyNetworkNamingServiceRef,
					visualMappingManagerServiceRef, cyApplicationManagerServiceRef, cyEventHelperRef, cyGroupManager,
					renderingEngineManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File.New.Network");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/new-from-selected-32.png").toString());
			props.setProperty(ACCELERATOR, "cmd n");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SELECTED_NODES);
			props.setProperty(TITLE, "From selected nodes, all edges");
			props.setProperty(TOOL_BAR_GRAVITY, "9.1");
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(MENU_GRAVITY, "1.0");
			props.setProperty(TOOLTIP, "New Network From Selection (all edges)");
			// props.setProperty(COMMAND, "create from selected nodes and all edges");
			// props.setProperty(COMMAND_NAMESPACE, "network");
			registerService(bc, factory, NetworkTaskFactory.class, props);
			registerService(bc, factory, NewNetworkSelectedNodesOnlyTaskFactory.class, props);
		}
		{
			NewNetworkCommandTaskFactory factory = new NewNetworkCommandTaskFactory(undoSupportServiceRef,
					cyRootNetworkFactoryServiceRef, cyNetworkViewFactoryServiceRef, cyNetworkManagerServiceRef,
					cyNetworkViewManagerServiceRef, cyNetworkNamingServiceRef, visualMappingManagerServiceRef,
					cyApplicationManagerServiceRef, cyEventHelperRef, cyGroupManager, renderingEngineManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "create");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Create a new network");
			registerService(bc, factory, NetworkTaskFactory.class, props);
		}
		{
			CloneNetworkTaskFactoryImpl factory = new CloneNetworkTaskFactoryImpl(cyNetworkManagerServiceRef,
					cyNetworkViewManagerServiceRef, visualMappingManagerServiceRef, cyNetworkFactoryServiceRef,
					cyNetworkViewFactoryServiceRef, cyNetworkNamingServiceRef, cyApplicationManagerServiceRef,
					cyNetworkTableManagerServiceRef, rootNetworkManagerServiceRef, cyGroupManager, cyGroupFactory,
					renderingEngineManagerServiceRef, nullNetworkViewFactory);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK);
			props.setProperty(PREFERRED_MENU, "File.New.Network");
			props.setProperty(MENU_GRAVITY, "3.0");
			props.setProperty(TITLE, "Clone Current Network");
			props.setProperty(COMMAND, "clone");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Make a copy of the current network");
			registerService(bc, factory, NetworkTaskFactory.class, props);
			registerService(bc, factory, CloneNetworkTaskFactory.class, props);
		}
		{
			DestroyNetworkTaskFactoryImpl factory = new DestroyNetworkTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Edit");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK);
			props.setProperty(TITLE, "Destroy Networks");
			props.setProperty(MENU_GRAVITY, "3.2");
			// props.setProperty(COMMAND, "destroy");
			// props.setProperty(COMMAND_NAMESPACE, "network");
			registerService(bc, factory, NetworkCollectionTaskFactory.class, props);
			registerService(bc, factory, DestroyNetworkTaskFactory.class, props);
			Properties destroyNetworkTaskFactoryProps2 = new Properties();
			destroyNetworkTaskFactoryProps2.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK);
			destroyNetworkTaskFactoryProps2.setProperty(COMMAND, "destroy");
			destroyNetworkTaskFactoryProps2.setProperty(COMMAND_NAMESPACE, "network");
			destroyNetworkTaskFactoryProps2.setProperty(COMMAND_DESCRIPTION, "Destroy (delete) a network");
			registerService(bc, factory, TaskFactory.class, destroyNetworkTaskFactoryProps2);
		}
		{
			LoadNetworkFileTaskFactoryImpl factory = new LoadNetworkFileTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ID, "loadNetworkFileTaskFactory");
			props.setProperty(PREFERRED_MENU, "File.Import.Network[1.0]");
			props.setProperty(ACCELERATOR, "cmd l");
			props.setProperty(TITLE, "File...");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND, "load file");
			props.setProperty(COMMAND_DESCRIPTION, "Load a network file (e.g. XGMML)");
			props.setProperty(MENU_GRAVITY, "1.0");
			props.setProperty(TOOL_BAR_GRAVITY, "2.0");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/import-net-32.png").toString());
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(TOOLTIP, "Import Network From File");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, LoadNetworkFileTaskFactory.class, props);
		}
		{
			LoadMultipleNetworkFilesTaskFactoryImpl factory = new LoadMultipleNetworkFilesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			// props.setProperty(ID, "loadMultipleNetworkFilesTaskFactory");
			// props.setProperty(COMMAND_NAMESPACE, "network");
			// props.setProperty(COMMAND, "load file");
			// props.setProperty(COMMAND_DESCRIPTION, "Load a network file (e.g. XGMML)");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, LoadMultipleNetworkFilesTaskFactory.class, props);
		}
		{
			LoadNetworkURLTaskFactoryImpl factory = new LoadNetworkURLTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ID, "loadNetworkURLTaskFactory");
			props.setProperty(PREFERRED_MENU, "File.Import.Network[1.0]");
			props.setProperty(ACCELERATOR, "cmd shift l");
			props.setProperty(MENU_GRAVITY, "2.0");
			// props.setProperty(TOOL_BAR_GRAVITY, "2.1");
			props.setProperty(TITLE, "URL...");
			// props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/import-net-url-32.png").toString());
			// props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(TOOLTIP, "Import Network From URL");
			props.setProperty(COMMAND, "load url");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Load a network file (e.g. XGMML) from a url");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, LoadNetworkURLTaskFactory.class, props);
		}
		
		// --[ PREFERENCES ]--------------------------------------------------------------------------------------------
		{
			ProxySettingsTaskFactoryImpl pfactory = new ProxySettingsTaskFactoryImpl(cyPropertyServiceRef,
					streamUtilRef, cyEventHelperRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Edit.Preferences");
			props.setProperty(MENU_GRAVITY, "3.0");
			props.setProperty(TITLE, "Proxy Settings...");
			registerService(bc, pfactory, TaskFactory.class, props);
		}
		
		// --[ TABLE ]--------------------------------------------------------------------------------------------------
		{
			LoadTableFileTaskFactoryImpl factory = new LoadTableFileTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File.Import.Table[2.0]");
			props.setProperty(MENU_GRAVITY, "1.0");
			props.setProperty(TOOL_BAR_GRAVITY, "2.2");
			props.setProperty(TITLE, "File...");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/import-table-32.png").toString());
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(TOOLTIP, "Import Table From File");
			// props.setProperty(COMMAND, "load file");
			// props.setProperty(COMMAND_NAMESPACE, "table");
			// props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK);
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, LoadTableFileTaskFactory.class, props);
		}
		{
			LoadTableURLTaskFactoryImpl factory = new LoadTableURLTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File.Import.Table[2.0]");
			props.setProperty(MENU_GRAVITY, "2.0");
			// props.setProperty(TOOL_BAR_GRAVITY, "2.3");
			props.setProperty(TITLE, "URL...");
			// props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/import-table-url-32.png").toString());
			// props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(TOOLTIP, "Import Table From URL");
			// props.setProperty(COMMAND, "load url");
			// props.setProperty(COMMAND_NAMESPACE, "table");
			// props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK);
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, LoadTableURLTaskFactory.class, props);
		}
		{
			ExportSelectedTableTaskFactoryImpl factory = new ExportSelectedTableTaskFactoryImpl(cyTableWriterManagerRef,
					cyTableManagerServiceRef, cyNetworkManagerServiceRef, cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, "table");
			props.setProperty(PREFERRED_MENU, "File.Export");
			props.setProperty(MENU_GRAVITY, "1.3");
			// props.setProperty(TOOL_BAR_GRAVITY, "3.1");
			props.setProperty(TITLE, "Table...");
			// props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/export-table-32.png").toString());
			// props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(TOOLTIP, "Export Table to File");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, ExportSelectedTableTaskFactory.class, props);
		}
		{
			ExportNoGuiSelectedTableTaskFactoryImpl factory = new ExportNoGuiSelectedTableTaskFactoryImpl(
					cyTableWriterManagerRef, cyTableManagerServiceRef, cyNetworkManagerServiceRef,
					cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "export");
			props.setProperty(COMMAND_NAMESPACE, "table");
			props.setProperty(COMMAND_DESCRIPTION, "Export a table to a file");
			registerService(bc, factory, TaskFactory.class, props);
		}
		{
			ImportTableDataTaskFactoryImpl factory = new ImportTableDataTaskFactoryImpl(serviceRegistrar);
			registerService(bc, factory, ImportDataTableTaskFactory.class);
		}
		{
			ExportTableTaskFactoryImpl factory = new ExportTableTaskFactoryImpl(cyTableWriterManagerRef,
					cyApplicationManagerServiceRef, tunableSetterServiceRef);
			registerService(bc, factory, ExportTableTaskFactory.class);
		}
		{
			MergeTablesTaskFactoryImpl factory = new MergeTablesTaskFactoryImpl(cyTableManagerServiceRef,
					cyNetworkManagerServiceRef, tunableSetterServiceRef, rootNetworkManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, "table");
			props.setProperty(PREFERRED_MENU, "Tools.Merge[2.0]");
			props.setProperty(TITLE, "Tables...");
			// props.setProperty(ServiceProperties.INSERT_SEPARATOR_AFTER, "true");
			// props.setProperty(TOOL_BAR_GRAVITY, "1.1");
			props.setProperty(MENU_GRAVITY, "5.4");
			props.setProperty(TOOLTIP, "Merge Tables");
			props.setProperty(COMMAND, "merge");
			props.setProperty(COMMAND_NAMESPACE, "table");
			props.setProperty(COMMAND_DESCRIPTION, "Merge tables together");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, MergeTablesTaskFactory.class, props);
		}
		{
			MapGlobalToLocalTableTaskFactoryImpl factory = new MapGlobalToLocalTableTaskFactoryImpl(
					cyTableManagerServiceRef, cyNetworkManagerServiceRef, tunableSetterServiceRef);
			Properties props = new Properties();
			// props.setProperty(ID, "mapGlobalToLocalTableTaskFactory");
			// props.setProperty(PREFERRED_MENU, "Tools");
			// props.setProperty(ACCELERATOR, "cmd m");
			// props.setProperty(TITLE, "Map Table to Attributes");
			// props.setProperty(MENU_GRAVITY, "1.0");
			// props.setProperty(TOOL_BAR_GRAVITY, "3.0");
			// props.setProperty(IN_TOOL_BAR, "false");
			// props.setProperty(COMMAND, "map-global-to-local");
			// props.setProperty(COMMAND_NAMESPACE, "table");
			registerService(bc, factory, TableTaskFactory.class, props);
			registerService(bc, factory, MapGlobalToLocalTableTaskFactory.class, props);
		}
		{
			MapTableToNetworkTablesTaskFactoryImpl factory = new MapTableToNetworkTablesTaskFactoryImpl(
					cyNetworkManagerServiceRef, tunableSetterServiceRef, rootNetworkManagerServiceRef);
			registerService(bc, factory, MapTableToNetworkTablesTaskFactory.class);
		}
		{
			DeleteTableTaskFactoryImpl factory = new DeleteTableTaskFactoryImpl(cyTableManagerServiceRef);
			registerService(bc, factory, TableTaskFactory.class);
			registerService(bc, factory, DeleteTableTaskFactory.class);
		}
		{
			CreateTableTaskFactory factory = new CreateTableTaskFactory(cyApplicationManagerServiceRef,
					cyTableFactoryServiceRef, cyTableManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "create table");
			props.setProperty(COMMAND_NAMESPACE, "table");
			props.setProperty(COMMAND_DESCRIPTION, "Create a new table");
			registerService(bc, factory, TaskFactory.class, props);
		}
		{
			DestroyTableTaskFactory factory = new DestroyTableTaskFactory(cyApplicationManagerServiceRef,
					cyTableManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "destroy");
			props.setProperty(COMMAND_NAMESPACE, "table");
			props.setProperty(COMMAND_DESCRIPTION, "Destroy (delete) an entire table");
			registerService(bc, factory, TaskFactory.class, props);
		}
		
		// --[ VIZMAP ]-------------------------------------------------------------------------------------------------
		createVizmapTaskFactories(bc, serviceRegistrar);
		
		{
			DeleteSelectedNodesAndEdgesTaskFactoryImpl factory = new DeleteSelectedNodesAndEdgesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Edit");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SELECTED_NODES_OR_EDGES);
			props.setProperty(TITLE, "Delete Selected Nodes and Edges");
			props.setProperty(MENU_GRAVITY, "5.0");
			props.setProperty(ACCELERATOR, "DELETE");
			props.setProperty(COMMAND, "delete");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Delete nodes or edges from a network");
			registerService(bc, factory, NetworkTaskFactory.class, props);
			registerService(bc, factory, DeleteSelectedNodesAndEdgesTaskFactory.class, props);
		}

		Properties selectAllTaskFactoryProps = new Properties();
		selectAllTaskFactoryProps.setProperty(PREFERRED_MENU,"Select");
		selectAllTaskFactoryProps.setProperty(ACCELERATOR,"cmd alt a");
		selectAllTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectAllTaskFactoryProps.setProperty(TITLE,"Select all nodes and edges");
		selectAllTaskFactoryProps.setProperty(MENU_GRAVITY,"5.0");
		selectAllTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		// selectAllTaskFactoryProps.setProperty(COMMAND,"select all");
		// selectAllTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"network");
		registerService(bc,selectAllTaskFactory,NetworkTaskFactory.class, selectAllTaskFactoryProps);
		registerService(bc,selectAllTaskFactory,SelectAllTaskFactory.class, selectAllTaskFactoryProps);

		Properties selectAllViewTaskFactoryProps = new Properties();
		selectAllViewTaskFactoryProps.setProperty(PREFERRED_MENU, NETWORK_SELECT_MENU);
		selectAllViewTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK_AND_VIEW);
		selectAllViewTaskFactoryProps.setProperty(TITLE,"All nodes and edges");
		selectAllViewTaskFactoryProps.setProperty(MENU_GRAVITY,"1.1");
		selectAllViewTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		selectAllViewTaskFactoryProps.setProperty(IN_MENU_BAR,"false");
		registerService(bc,selectAllTaskFactory,NetworkViewTaskFactory.class, selectAllViewTaskFactoryProps);

		Properties selectAllEdgesTaskFactoryProps = new Properties();
		selectAllEdgesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Edges");
		selectAllEdgesTaskFactoryProps.setProperty(ACCELERATOR,"cmd alt a");
		selectAllEdgesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectAllEdgesTaskFactoryProps.setProperty(TITLE,"Select all edges");
		selectAllEdgesTaskFactoryProps.setProperty(MENU_GRAVITY,"4.0");
		registerService(bc,selectAllEdgesTaskFactory,NetworkTaskFactory.class, selectAllEdgesTaskFactoryProps);
		registerService(bc,selectAllEdgesTaskFactory,SelectAllEdgesTaskFactory.class, selectAllEdgesTaskFactoryProps);

		Properties selectAllNodesTaskFactoryProps = new Properties();
		selectAllNodesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectAllNodesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Nodes");
		selectAllNodesTaskFactoryProps.setProperty(MENU_GRAVITY,"4.0");
		selectAllNodesTaskFactoryProps.setProperty(ACCELERATOR,"cmd a");
		selectAllNodesTaskFactoryProps.setProperty(TITLE,"Select all nodes");
		registerService(bc,selectAllNodesTaskFactory,NetworkTaskFactory.class, selectAllNodesTaskFactoryProps);
		registerService(bc,selectAllNodesTaskFactory,SelectAllNodesTaskFactory.class, selectAllNodesTaskFactoryProps);

		Properties selectAdjacentEdgesTaskFactoryProps = new Properties();
		selectAdjacentEdgesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectAdjacentEdgesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Edges");
		selectAdjacentEdgesTaskFactoryProps.setProperty(MENU_GRAVITY,"6.0");
		selectAdjacentEdgesTaskFactoryProps.setProperty(ACCELERATOR,"alt e");
		selectAdjacentEdgesTaskFactoryProps.setProperty(TITLE,"Select adjacent edges");
		// selectAdjacentEdgesTaskFactoryProps.setProperty(COMMAND,"select adjacent");
		// selectAdjacentEdgesTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"edge");
		registerService(bc,selectAdjacentEdgesTaskFactory,NetworkTaskFactory.class, selectAdjacentEdgesTaskFactoryProps);
		registerService(bc,selectAdjacentEdgesTaskFactory,SelectAdjacentEdgesTaskFactory.class, selectAdjacentEdgesTaskFactoryProps);

		Properties selectConnectedNodesTaskFactoryProps = new Properties();
		selectConnectedNodesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectConnectedNodesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Nodes");
		selectConnectedNodesTaskFactoryProps.setProperty(MENU_GRAVITY,"7.0");
		selectConnectedNodesTaskFactoryProps.setProperty(ACCELERATOR,"cmd 7");
		selectConnectedNodesTaskFactoryProps.setProperty(TITLE,"Nodes connected by selected edges");
		// selectConnectedNodesTaskFactoryProps.setProperty(COMMAND,"select by connected edges");
		// selectConnectedNodesTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"node");
		registerService(bc,selectConnectedNodesTaskFactory,NetworkTaskFactory.class, selectConnectedNodesTaskFactoryProps);
		registerService(bc,selectConnectedNodesTaskFactory,SelectConnectedNodesTaskFactory.class, selectConnectedNodesTaskFactoryProps);

		Properties selectFirstNeighborsTaskFactoryProps = new Properties();
		selectFirstNeighborsTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_SELECTED_NODES_OR_EDGES);
		selectFirstNeighborsTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Nodes.First Neighbors of Selected Nodes");
		selectFirstNeighborsTaskFactoryProps.setProperty(MENU_GRAVITY,"6.0");
		selectFirstNeighborsTaskFactoryProps.setProperty(TOOL_BAR_GRAVITY,"9.15");
		selectFirstNeighborsTaskFactoryProps.setProperty(ACCELERATOR,"cmd 6");
		selectFirstNeighborsTaskFactoryProps.setProperty(TITLE,"Undirected");
		selectFirstNeighborsTaskFactoryProps.setProperty(LARGE_ICON_URL,getClass().getResource("/images/icons/first-neighbors-32.png").toString());
		selectFirstNeighborsTaskFactoryProps.setProperty(IN_TOOL_BAR,"true");
		selectFirstNeighborsTaskFactoryProps.setProperty(TOOLTIP,"First Neighbors of Selected Nodes (Undirected)");
		// selectFirstNeighborsTaskFactoryProps.setProperty(COMMAND,"select first neighbors undirected");
		// selectFirstNeighborsTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"node");
		registerService(bc,selectFirstNeighborsTaskFactory,NetworkTaskFactory.class, selectFirstNeighborsTaskFactoryProps);
		registerService(bc,selectFirstNeighborsTaskFactory,SelectFirstNeighborsTaskFactory.class, selectFirstNeighborsTaskFactoryProps);

		Properties selectFirstNeighborsTaskFactoryInEdgeProps = new Properties();
		selectFirstNeighborsTaskFactoryInEdgeProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectFirstNeighborsTaskFactoryInEdgeProps.setProperty(PREFERRED_MENU,"Select.Nodes.First Neighbors of Selected Nodes");
		selectFirstNeighborsTaskFactoryInEdgeProps.setProperty(MENU_GRAVITY,"6.1");
		selectFirstNeighborsTaskFactoryInEdgeProps.setProperty(TITLE,"Directed: Incoming");
		selectFirstNeighborsTaskFactoryInEdgeProps.setProperty(TOOLTIP,"First Neighbors of Selected Nodes (Directed: Incoming)");
		// selectFirstNeighborsTaskFactoryInEdgeProps.setProperty(COMMAND,"select first neighbors incoming");
		// selectFirstNeighborsTaskFactoryInEdgeProps.setProperty(COMMAND_NAMESPACE,"node");
		registerService(bc,selectFirstNeighborsTaskFactoryInEdge,NetworkTaskFactory.class, selectFirstNeighborsTaskFactoryInEdgeProps);
		registerService(bc,selectFirstNeighborsTaskFactoryInEdge,SelectFirstNeighborsTaskFactory.class, selectFirstNeighborsTaskFactoryInEdgeProps);

		Properties selectFirstNeighborsTaskFactoryOutEdgeProps = new Properties();
		selectFirstNeighborsTaskFactoryOutEdgeProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectFirstNeighborsTaskFactoryOutEdgeProps.setProperty(PREFERRED_MENU,"Select.Nodes.First Neighbors of Selected Nodes");
		selectFirstNeighborsTaskFactoryOutEdgeProps.setProperty(MENU_GRAVITY,"6.2");
		selectFirstNeighborsTaskFactoryOutEdgeProps.setProperty(TITLE,"Directed: Outgoing");
		selectFirstNeighborsTaskFactoryOutEdgeProps.setProperty(TOOLTIP,"First Neighbors of Selected Nodes (Directed: Outgoing)");
		// selectFirstNeighborsTaskFactoryOutEdgeProps.setProperty(COMMAND,"select first neighbors outgoing");
		// selectFirstNeighborsTaskFactoryOutEdgeProps.setProperty(COMMAND_NAMESPACE,"node");
		registerService(bc,selectFirstNeighborsTaskFactoryOutEdge,NetworkTaskFactory.class, selectFirstNeighborsTaskFactoryOutEdgeProps);
		registerService(bc,selectFirstNeighborsTaskFactoryOutEdge,SelectFirstNeighborsTaskFactory.class, selectFirstNeighborsTaskFactoryOutEdgeProps);		

		Properties deselectAllTaskFactoryProps = new Properties();
		deselectAllTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		deselectAllTaskFactoryProps.setProperty(PREFERRED_MENU,"Select");
		deselectAllTaskFactoryProps.setProperty(MENU_GRAVITY,"5.1");
		deselectAllTaskFactoryProps.setProperty(ACCELERATOR,"cmd shift alt a");
		deselectAllTaskFactoryProps.setProperty(TITLE,"Deselect all nodes and edges");
		// deselectAllTaskFactoryProps.setProperty(COMMAND,"deselect all");
		// deselectAllTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"network");
		registerService(bc,deselectAllTaskFactory,NetworkTaskFactory.class, deselectAllTaskFactoryProps);
		registerService(bc,deselectAllTaskFactory,DeselectAllTaskFactory.class, deselectAllTaskFactoryProps);

		Properties deselectAllEdgesTaskFactoryProps = new Properties();
		deselectAllEdgesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		deselectAllEdgesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Edges");
		deselectAllEdgesTaskFactoryProps.setProperty(MENU_GRAVITY,"5.0");
		deselectAllEdgesTaskFactoryProps.setProperty(ACCELERATOR,"alt shift a");
		deselectAllEdgesTaskFactoryProps.setProperty(TITLE,"Deselect all edges");
		registerService(bc,deselectAllEdgesTaskFactory,NetworkTaskFactory.class, deselectAllEdgesTaskFactoryProps);
		registerService(bc,deselectAllEdgesTaskFactory,DeselectAllEdgesTaskFactory.class, deselectAllEdgesTaskFactoryProps);

		Properties deselectAllNodesTaskFactoryProps = new Properties();
		deselectAllNodesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		deselectAllNodesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Nodes");
		deselectAllNodesTaskFactoryProps.setProperty(MENU_GRAVITY,"5.0");
		deselectAllNodesTaskFactoryProps.setProperty(ACCELERATOR,"cmd shift a");
		deselectAllNodesTaskFactoryProps.setProperty(TITLE,"Deselect all nodes");
		registerService(bc,deselectAllNodesTaskFactory,NetworkTaskFactory.class, deselectAllNodesTaskFactoryProps);
		registerService(bc,deselectAllNodesTaskFactory,DeselectAllNodesTaskFactory.class, deselectAllNodesTaskFactoryProps);

		Properties invertSelectedEdgesTaskFactoryProps = new Properties();
		invertSelectedEdgesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		invertSelectedEdgesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Edges");
		invertSelectedEdgesTaskFactoryProps.setProperty(MENU_GRAVITY,"1.0");
		invertSelectedEdgesTaskFactoryProps.setProperty(ACCELERATOR,"alt i");
		invertSelectedEdgesTaskFactoryProps.setProperty(TITLE,"Invert edge selection");
		registerService(bc,invertSelectedEdgesTaskFactory,NetworkTaskFactory.class, invertSelectedEdgesTaskFactoryProps);
		registerService(bc,invertSelectedEdgesTaskFactory,InvertSelectedEdgesTaskFactory.class, invertSelectedEdgesTaskFactoryProps);

		Properties invertSelectedNodesTaskFactoryProps = new Properties();
		invertSelectedNodesTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_SELECTED_NODES);
		invertSelectedNodesTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Nodes");
		invertSelectedNodesTaskFactoryProps.setProperty(MENU_GRAVITY,"1.0");
		invertSelectedNodesTaskFactoryProps.setProperty(TOOL_BAR_GRAVITY,"9.2");
		invertSelectedNodesTaskFactoryProps.setProperty(ACCELERATOR,"cmd i");
		invertSelectedNodesTaskFactoryProps.setProperty(TITLE,"Invert node selection");
		invertSelectedNodesTaskFactoryProps.setProperty(IN_TOOL_BAR,"false");
		invertSelectedNodesTaskFactoryProps.setProperty(TOOLTIP,"Invert Node Selection");
		registerService(bc,invertSelectedNodesTaskFactory,NetworkTaskFactory.class, invertSelectedNodesTaskFactoryProps);
		registerService(bc,invertSelectedNodesTaskFactory,InvertSelectedNodesTaskFactory.class, invertSelectedNodesTaskFactoryProps);

		Properties selectFromFileListTaskFactoryProps = new Properties();
		selectFromFileListTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK);
		selectFromFileListTaskFactoryProps.setProperty(PREFERRED_MENU,"Select.Nodes");
		selectFromFileListTaskFactoryProps.setProperty(MENU_GRAVITY,"8.0");
		selectFromFileListTaskFactoryProps.setProperty(ACCELERATOR,"cmd i");
		selectFromFileListTaskFactoryProps.setProperty(TITLE,"From ID List file...");
		selectFromFileListTaskFactoryProps.setProperty(COMMAND,"select from file");
		selectFromFileListTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"node");
		selectFromFileListTaskFactoryProps.setProperty(COMMAND_DESCRIPTION,"Select nodes from a file");
		registerService(bc,selectFromFileListTaskFactory,NetworkTaskFactory.class, selectFromFileListTaskFactoryProps);
		registerService(bc,selectFromFileListTaskFactory,SelectFromFileListTaskFactory.class, selectFromFileListTaskFactoryProps);

		Properties selectFirstNeighborsNodeViewTaskFactoryProps = new Properties();
		selectFirstNeighborsNodeViewTaskFactoryProps.setProperty(PREFERRED_MENU,NODE_SELECT_MENU);
		selectFirstNeighborsNodeViewTaskFactoryProps.setProperty(MENU_GRAVITY,"1.0");
		selectFirstNeighborsNodeViewTaskFactoryProps.setProperty(TITLE,"Select First Neighbors (Undirected)");
		registerService(bc,selectFirstNeighborsNodeViewTaskFactory,NodeViewTaskFactory.class, selectFirstNeighborsNodeViewTaskFactoryProps);
		registerService(bc,selectFirstNeighborsNodeViewTaskFactory,SelectFirstNeighborsNodeViewTaskFactory.class, selectFirstNeighborsNodeViewTaskFactoryProps);

		{
			UnHideAllTaskFactoryImpl factory = new UnHideAllTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(PREFERRED_MENU, "Select");
			props.setProperty(MENU_GRAVITY, "3.0");
			props.setProperty(TOOL_BAR_GRAVITY, "9.6");
			props.setProperty(TITLE, factory.getDescription());
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/show-all-32.png").toString());
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(TOOLTIP, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, UnHideAllTaskFactory.class, props);
		}
		{
			HideSelectedTaskFactoryImpl factory = new HideSelectedTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SELECTED_NODES_OR_EDGES);
			props.setProperty(PREFERRED_MENU, "Select");
			props.setProperty(MENU_GRAVITY, "3.1");
			props.setProperty(TOOL_BAR_GRAVITY, "9.5");
			props.setProperty(TITLE, factory.getDescription());
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/hide-selected-32.png").toString());
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(TOOLTIP, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, HideSelectedTaskFactory.class, props);
		}
		{
			HideUnselectedTaskFactoryImpl factory = new HideUnselectedTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Select");
			props.setProperty(MENU_GRAVITY, "3.2");
			props.setProperty(TITLE, factory.getDescription());
			props.setProperty(TOOLTIP, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, HideUnselectedTaskFactory.class, props);
		}
		{
			HideSelectedNodesTaskFactoryImpl factory = new HideSelectedNodesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SELECTED_NODES);
			props.setProperty(PREFERRED_MENU, "Select.Nodes");
			props.setProperty(MENU_GRAVITY, "2.0");
			props.setProperty(TITLE, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, HideSelectedNodesTaskFactory.class, props);
		}
		{
			HideUnselectedNodesTaskFactoryImpl factory = new HideUnselectedNodesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Select.Nodes");
			props.setProperty(MENU_GRAVITY, "2.1");
			props.setProperty(TITLE, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, HideUnselectedNodesTaskFactory.class, props);
		}
		{
			HideSelectedEdgesTaskFactoryImpl factory = new HideSelectedEdgesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SELECTED_EDGES);
			props.setProperty(PREFERRED_MENU, "Select.Edges");
			props.setProperty(MENU_GRAVITY, "2.0");
			props.setProperty(TITLE, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, HideSelectedEdgesTaskFactory.class, props);
		}
		{
			HideUnselectedEdgesTaskFactoryImpl factory = new HideUnselectedEdgesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Select.Edges");
			props.setProperty(MENU_GRAVITY, "2.1");
			props.setProperty(TITLE, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, HideUnselectedEdgesTaskFactory.class, props);
		}
		{
			UnHideAllNodesTaskFactoryImpl factory = new UnHideAllNodesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(PREFERRED_MENU, "Select.Nodes");
			props.setProperty(MENU_GRAVITY, "3.0");
			props.setProperty(TITLE, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, UnHideAllNodesTaskFactory.class, props);
		}
		{
			UnHideAllEdgesTaskFactoryImpl factory = new UnHideAllEdgesTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(PREFERRED_MENU, "Select.Edges");
			props.setProperty(MENU_GRAVITY, "3.0");
			props.setProperty(TITLE, factory.getDescription());
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, UnHideAllEdgesTaskFactory.class, props);
		}

		Properties helpProps = new Properties();
// 		helpProps.setProperty(PREFERRED_MENU,"View");
// 		helpProps.setProperty(MENU_GRAVITY,"65.3");
		helpProps.setProperty(ACCELERATOR,"cmd ?");
		helpProps.setProperty(LARGE_ICON_URL,getClass().getResource("/images/icons/help-32.png").toString());
		helpProps.setProperty(TITLE,"Help");
		helpProps.setProperty(TOOLTIP,"Link to context sensitive help");
		helpProps.setProperty(TOOL_BAR_GRAVITY,"65.5");
		helpProps.setProperty(IN_TOOL_BAR,"true");
//		helpProps.setProperty(COMMAND,"zoom in");
//		helpProps.setProperty(COMMAND_NAMESPACE,"view");
		registerService(bc,helpTaskFactory,TaskFactory.class, helpProps);

		

		{
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SINGLE_NETWORK);
			props.setProperty(PREFERRED_MENU, "Edit");
			props.setProperty(MENU_GRAVITY, "5.5");
			props.setProperty(TITLE, "Rename Network...");
			props.setProperty(IN_NETWORK_PANEL_CONTEXT_MENU, "true");
			props.setProperty(COMMAND, "rename");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Rename a network");
			registerService(bc, editNetworkTitleTaskFactory, NetworkTaskFactory.class, props);
			registerService(bc, editNetworkTitleTaskFactory, EditNetworkTitleTaskFactory.class, props);
		}

		{
			ExportSelectedNetworkTaskFactoryImpl factory = new ExportSelectedNetworkTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK);
			props.setProperty(PREFERRED_MENU, "File.Export");
			props.setProperty(MENU_GRAVITY, "1.1");
			//props.setProperty(TOOL_BAR_GRAVITY, "3.0");
			props.setProperty(TITLE, "Network...");
			//props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/export-net-32.png").toString());
			//props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(IN_CONTEXT_MENU, "false");
			props.setProperty(TOOLTIP, "Export Network to File");
			props.setProperty(COMMAND, "export");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Export a network to a file");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, ExportSelectedNetworkTaskFactory.class, props);
		}
		{
			ExportNetworkImageTaskFactoryImpl factory = new ExportNetworkImageTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File");
			//props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/export-img-32.png").toString());
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(MENU_GRAVITY, "5.2");
			props.setProperty(TITLE, "Export as Image...");
			//props.setProperty(TOOL_BAR_GRAVITY, "3.2");
			//props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(IN_CONTEXT_MENU, "false");
			props.setProperty(TOOLTIP, "Export Network Image to File");
			props.setProperty(COMMAND, "export");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Export a view to a graphics file");
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, ExportNetworkImageTaskFactory.class, props);
		}
		
		{
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File.New");
			props.setProperty(MENU_GRAVITY, "1.1");
			props.setProperty(TITLE, "Session");
			props.setProperty(COMMAND, "new");
			props.setProperty(COMMAND_NAMESPACE, "session");
			props.setProperty(COMMAND_DESCRIPTION, "Create a new, empty session");
			registerService(bc, newSessionTaskFactory, TaskFactory.class, props);
			registerService(bc, newSessionTaskFactory, NewSessionTaskFactory.class, props);
		}
		{
			Properties props = new Properties();
			props.setProperty(ID, "openSessionTaskFactory");
			props.setProperty(PREFERRED_MENU, "File");
			props.setProperty(ACCELERATOR, "cmd o");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/open-file-32.png").toString());
			props.setProperty(TITLE, "Open...");
			props.setProperty(TOOL_BAR_GRAVITY, "1.0");
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(MENU_GRAVITY, "1.2");
			props.setProperty(TOOLTIP, "Open Session");
			registerService(bc, openSessionTaskFactory, OpenSessionTaskFactory.class, props);
			registerService(bc, openSessionTaskFactory, TaskFactory.class, props);
		}

		// We can't use the "normal" OpenSessionTaskFactory for commands
		// because it inserts the task with the file tunable in it, so the
		// Command processor never sees it, so we need a special OpenSessionTaskFactory
		// for commands
		// openSessionTaskFactoryProps.setProperty(COMMAND,"open");
		// openSessionTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"session");
		// registerService(bc,openSessionTaskFactory,TaskFactory.class, openSessionTaskFactoryProps);

		Properties openSessionCommandTaskFactoryProps = new Properties();
		openSessionCommandTaskFactoryProps.setProperty(COMMAND,"open");
		openSessionCommandTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"session");
		openSessionCommandTaskFactoryProps.setProperty(COMMAND_DESCRIPTION,"Open a session from a file");
		registerService(bc,openSessionCommandTaskFactory,TaskFactory.class, openSessionCommandTaskFactoryProps);

		Properties saveSessionTaskFactoryProps = new Properties();
		saveSessionTaskFactoryProps.setProperty(PREFERRED_MENU,"File");
		saveSessionTaskFactoryProps.setProperty(ACCELERATOR,"cmd s");
		saveSessionTaskFactoryProps.setProperty(LARGE_ICON_URL,getClass().getResource("/images/icons/save-32.png").toString());
		saveSessionTaskFactoryProps.setProperty(TITLE,"Save");
		saveSessionTaskFactoryProps.setProperty(TOOL_BAR_GRAVITY,"1.1");
		saveSessionTaskFactoryProps.setProperty(IN_TOOL_BAR,"true");
		saveSessionTaskFactoryProps.setProperty(MENU_GRAVITY,"3.0");
		saveSessionTaskFactoryProps.setProperty(TOOLTIP,"Save Session");
		saveSessionTaskFactoryProps.setProperty(COMMAND,"save");
		saveSessionTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"session");
		saveSessionTaskFactoryProps.setProperty(COMMAND_DESCRIPTION,"Save the session");
		registerService(bc,saveSessionTaskFactory,TaskFactory.class, saveSessionTaskFactoryProps);

		Properties saveSessionAsTaskFactoryProps = new Properties();
		saveSessionAsTaskFactoryProps.setProperty(PREFERRED_MENU,"File");
		saveSessionAsTaskFactoryProps.setProperty(ACCELERATOR,"cmd shift s");
		saveSessionAsTaskFactoryProps.setProperty(MENU_GRAVITY,"3.1");
		saveSessionAsTaskFactoryProps.setProperty(TITLE,"Save As...");
		saveSessionAsTaskFactoryProps.setProperty(COMMAND,"save as");
		saveSessionAsTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"session");
		saveSessionAsTaskFactoryProps.setProperty(COMMAND_DESCRIPTION,"Save the session to a file");
		registerService(bc,saveSessionAsTaskFactory,TaskFactory.class, saveSessionAsTaskFactoryProps);
		registerService(bc,saveSessionAsTaskFactory,SaveSessionAsTaskFactory.class, saveSessionAsTaskFactoryProps);

		Properties applyPreferredLayoutTaskFactoryProps = new Properties();
		applyPreferredLayoutTaskFactoryProps.setProperty(PREFERRED_MENU,"Layout");
		applyPreferredLayoutTaskFactoryProps.setProperty(ACCELERATOR,"fn5");
		applyPreferredLayoutTaskFactoryProps.setProperty(LARGE_ICON_URL,getClass().getResource("/images/icons/apply-layout-32.png").toString());
		applyPreferredLayoutTaskFactoryProps.setProperty(ENABLE_FOR,ENABLE_FOR_NETWORK_AND_VIEW);
		applyPreferredLayoutTaskFactoryProps.setProperty(TITLE,"Apply Preferred Layout");
		applyPreferredLayoutTaskFactoryProps.setProperty(TOOL_BAR_GRAVITY,"7.0");
		applyPreferredLayoutTaskFactoryProps.setProperty(IN_TOOL_BAR,"true");
		applyPreferredLayoutTaskFactoryProps.setProperty(MENU_GRAVITY,"5.0");
		applyPreferredLayoutTaskFactoryProps.setProperty(TOOLTIP,"Apply Preferred Layout");
		registerService(bc,applyPreferredLayoutTaskFactory,NetworkViewCollectionTaskFactory.class, applyPreferredLayoutTaskFactoryProps);
		registerService(bc,applyPreferredLayoutTaskFactory,ApplyPreferredLayoutTaskFactory.class, applyPreferredLayoutTaskFactoryProps);

		// For commands
		Properties applyPreferredLayoutTaskFactoryProps2 = new Properties();
		applyPreferredLayoutTaskFactoryProps2.setProperty(COMMAND,"apply preferred");
		applyPreferredLayoutTaskFactoryProps2.setProperty(COMMAND_NAMESPACE,"layout");
		applyPreferredLayoutTaskFactoryProps2.setProperty(COMMAND_DESCRIPTION,"Execute the preferred layout on a network");
		registerService(bc,applyPreferredLayoutTaskFactory,TaskFactory.class, applyPreferredLayoutTaskFactoryProps2);

		Properties deleteColumnTaskFactoryProps = new Properties();
		deleteColumnTaskFactoryProps.setProperty(TITLE,"Delete column");
		deleteColumnTaskFactoryProps.setProperty(COMMAND,"delete column");
		deleteColumnTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"table");
		deleteColumnTaskFactoryProps.setProperty(COMMAND_DESCRIPTION,"Delete a column from a table");
		registerService(bc,deleteColumnTaskFactory,TableColumnTaskFactory.class, deleteColumnTaskFactoryProps);
		registerService(bc,deleteColumnTaskFactory,DeleteColumnTaskFactory.class, deleteColumnTaskFactoryProps);

		Properties renameColumnTaskFactoryProps = new Properties();
		renameColumnTaskFactoryProps.setProperty(TITLE,"Rename column");
		renameColumnTaskFactoryProps.setProperty(COMMAND,"rename column");
		renameColumnTaskFactoryProps.setProperty(COMMAND_NAMESPACE,"table");
		renameColumnTaskFactoryProps.setProperty(COMMAND_DESCRIPTION,"Rename a column in a table");
		registerService(bc,renameColumnTaskFactory,TableColumnTaskFactory.class, renameColumnTaskFactoryProps);
		registerService(bc,renameColumnTaskFactory,RenameColumnTaskFactory.class, renameColumnTaskFactoryProps);

		Properties copyValueToEntireColumnTaskFactoryProps = new Properties();
		copyValueToEntireColumnTaskFactoryProps.setProperty(TITLE,copyValueToEntireColumnTaskFactory.getTaskFactoryName());
		copyValueToEntireColumnTaskFactoryProps.setProperty("tableTypes", "node,edge,network,unassigned");
		registerService(bc,copyValueToEntireColumnTaskFactory,TableCellTaskFactory.class, copyValueToEntireColumnTaskFactoryProps);

		Properties copyValueToSelectedNodesTaskFactoryProps = new Properties();
		copyValueToSelectedNodesTaskFactoryProps.setProperty(TITLE,copyValueToSelectedNodesTaskFactory.getTaskFactoryName());
		copyValueToSelectedNodesTaskFactoryProps.setProperty("tableTypes", "node");
		registerService(bc,copyValueToSelectedNodesTaskFactory,TableCellTaskFactory.class, copyValueToSelectedNodesTaskFactoryProps);
		
		Properties copyValueToSelectedEdgesTaskFactoryProps = new Properties();
		copyValueToSelectedEdgesTaskFactoryProps.setProperty(TITLE,copyValueToSelectedEdgesTaskFactory.getTaskFactoryName());
		copyValueToSelectedEdgesTaskFactoryProps.setProperty("tableTypes", "edge");
		registerService(bc,copyValueToSelectedEdgesTaskFactory,TableCellTaskFactory.class, copyValueToSelectedEdgesTaskFactoryProps);
		
		
		// Register as 3 types of service.
		Properties connectSelectedNodesTaskFactoryProps = new Properties();
		connectSelectedNodesTaskFactoryProps.setProperty(IN_MENU_BAR,"false");
		connectSelectedNodesTaskFactoryProps.setProperty(IN_TOOL_BAR,"false");
		connectSelectedNodesTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		connectSelectedNodesTaskFactoryProps.setProperty(PREFERRED_MENU, NODE_ADD_MENU);
		connectSelectedNodesTaskFactoryProps.setProperty(MENU_GRAVITY, "0.2");
		connectSelectedNodesTaskFactoryProps.setProperty(TITLE, "Edges Connecting Selected Nodes");
		connectSelectedNodesTaskFactoryProps.setProperty(COMMAND, "connect selected nodes");
		connectSelectedNodesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		connectSelectedNodesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Create new edges that connect the selected nodes");
//		registerService(bc, connectSelectedNodesTaskFactory, NetworkTaskFactory.class,
//				connectSelectedNodesTaskFactoryProps);
		registerService(bc, connectSelectedNodesTaskFactory, NodeViewTaskFactory.class,
				connectSelectedNodesTaskFactoryProps);
		registerService(bc, connectSelectedNodesTaskFactory, ConnectSelectedNodesTaskFactory.class,
				connectSelectedNodesTaskFactoryProps);

		GroupNodesTaskFactoryImpl groupNodesTaskFactory = 
			new GroupNodesTaskFactoryImpl(cyApplicationManagerServiceRef, cyGroupManager, 
			                              cyGroupFactory, undoSupportServiceRef);
		Properties groupNodesTaskFactoryProps = new Properties();
		groupNodesTaskFactoryProps.setProperty(PREFERRED_MENU,NETWORK_GROUP_MENU);
		groupNodesTaskFactoryProps.setProperty(TITLE,"Group Selected Nodes");
		groupNodesTaskFactoryProps.setProperty(TOOLTIP,"Group Selected Nodes Together");
		groupNodesTaskFactoryProps.setProperty(IN_TOOL_BAR,"false");
		groupNodesTaskFactoryProps.setProperty(MENU_GRAVITY,"0.0");
		groupNodesTaskFactoryProps.setProperty(IN_MENU_BAR,"false");
		groupNodesTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		registerService(bc,groupNodesTaskFactory,NetworkViewTaskFactory.class, groupNodesTaskFactoryProps);
		registerService(bc,groupNodesTaskFactory,GroupNodesTaskFactory.class, groupNodesTaskFactoryProps);
		// For commands
		groupNodesTaskFactoryProps = new Properties();
		groupNodesTaskFactoryProps.setProperty(COMMAND, "create");
		groupNodesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "group");
		groupNodesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Create a new group of nodes");
		registerService(bc,groupNodesTaskFactory,TaskFactory.class, groupNodesTaskFactoryProps);

		// Add Group Selected Nodes to the nodes context also
		Properties groupNodeViewTaskFactoryProps = new Properties();
		groupNodeViewTaskFactoryProps.setProperty(PREFERRED_MENU,NODE_GROUP_MENU);
		groupNodeViewTaskFactoryProps.setProperty(MENU_GRAVITY, "0.0");
		groupNodeViewTaskFactoryProps.setProperty(TITLE,"Group Selected Nodes");
		groupNodeViewTaskFactoryProps.setProperty(TOOLTIP,"Group Selected Nodes Together");
		groupNodeViewTaskFactoryProps.setProperty(IN_TOOL_BAR,"false");
		groupNodeViewTaskFactoryProps.setProperty(IN_MENU_BAR,"false");
		groupNodeViewTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		registerService(bc,groupNodesTaskFactory,NodeViewTaskFactory.class, groupNodeViewTaskFactoryProps);

		UnGroupNodesTaskFactoryImpl unGroupTaskFactory = 
			new UnGroupNodesTaskFactoryImpl(cyApplicationManagerServiceRef, cyGroupManager, 
			                                cyGroupFactory, undoSupportServiceRef);
		Properties unGroupNodesTaskFactoryProps = new Properties();
		unGroupNodesTaskFactoryProps.setProperty(PREFERRED_MENU,NETWORK_GROUP_MENU);
		unGroupNodesTaskFactoryProps.setProperty(TITLE,"Ungroup Selected Nodes");
		unGroupNodesTaskFactoryProps.setProperty(TOOLTIP,"Ungroup Selected Nodes");
		unGroupNodesTaskFactoryProps.setProperty(IN_TOOL_BAR,"false");
		unGroupNodesTaskFactoryProps.setProperty(IN_MENU_BAR,"false");
		unGroupNodesTaskFactoryProps.setProperty(MENU_GRAVITY,"1.0");
		unGroupNodesTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		registerService(bc,unGroupTaskFactory,NetworkViewTaskFactory.class, unGroupNodesTaskFactoryProps);
		registerService(bc,unGroupTaskFactory,UnGroupTaskFactory.class, unGroupNodesTaskFactoryProps);

		unGroupNodesTaskFactoryProps = new Properties();
		unGroupNodesTaskFactoryProps.setProperty(COMMAND, "ungroup");
		unGroupNodesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "group");
		unGroupNodesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Ungroup a set of previously grouped nodes");
		registerService(bc,unGroupTaskFactory,TaskFactory.class, unGroupNodesTaskFactoryProps);

		// Add Ungroup Selected Nodes to the nodes context also
		Properties unGroupNodeViewTaskFactoryProps = new Properties();
		unGroupNodeViewTaskFactoryProps.setProperty(PREFERRED_MENU,NODE_GROUP_MENU);
		unGroupNodeViewTaskFactoryProps.setProperty(MENU_GRAVITY, "1.0");
		unGroupNodeViewTaskFactoryProps.setProperty(INSERT_SEPARATOR_AFTER, "true");
		unGroupNodeViewTaskFactoryProps.setProperty(TITLE,"Ungroup Selected Nodes");
		unGroupNodeViewTaskFactoryProps.setProperty(TOOLTIP,"Ungroup Selected Nodes");
		unGroupNodeViewTaskFactoryProps.setProperty(IN_TOOL_BAR,"false");
		unGroupNodeViewTaskFactoryProps.setProperty(IN_MENU_BAR,"false");
		unGroupNodeViewTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		registerService(bc,unGroupTaskFactory,NodeViewTaskFactory.class, unGroupNodeViewTaskFactoryProps);
		registerService(bc,unGroupTaskFactory,UnGroupNodesTaskFactory.class, unGroupNodeViewTaskFactoryProps);

		GroupNodeContextTaskFactoryImpl collapseGroupTaskFactory = 
			new GroupNodeContextTaskFactoryImpl(cyApplicationManagerServiceRef, cyGroupManager, true);
		Properties collapseGroupTaskFactoryProps = new Properties();
		collapseGroupTaskFactoryProps.setProperty(PREFERRED_MENU,NODE_GROUP_MENU);
		collapseGroupTaskFactoryProps.setProperty(TITLE,"Collapse Group(s)");
		collapseGroupTaskFactoryProps.setProperty(TOOLTIP,"Collapse Grouped Nodes");
		collapseGroupTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		collapseGroupTaskFactoryProps.setProperty(MENU_GRAVITY, "2.0");
		registerService(bc,collapseGroupTaskFactory,NodeViewTaskFactory.class, collapseGroupTaskFactoryProps);
		registerService(bc,collapseGroupTaskFactory,CollapseGroupTaskFactory.class, collapseGroupTaskFactoryProps);
		collapseGroupTaskFactoryProps = new Properties();
		collapseGroupTaskFactoryProps.setProperty(COMMAND, "collapse");
		collapseGroupTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "group"); // TODO right namespace?
		collapseGroupTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Collapse a group"); // TODO right namespace?
		registerService(bc,collapseGroupTaskFactory,TaskFactory.class, collapseGroupTaskFactoryProps);

		GroupNodeContextTaskFactoryImpl expandGroupTaskFactory = 
			new GroupNodeContextTaskFactoryImpl(cyApplicationManagerServiceRef, cyGroupManager, false);
		Properties expandGroupTaskFactoryProps = new Properties();
		expandGroupTaskFactoryProps.setProperty(PREFERRED_MENU,NODE_GROUP_MENU);
		expandGroupTaskFactoryProps.setProperty(TITLE,"Expand Group(s)");
		expandGroupTaskFactoryProps.setProperty(TOOLTIP,"Expand Group");
		expandGroupTaskFactoryProps.setProperty(PREFERRED_ACTION, "NEW");
		expandGroupTaskFactoryProps.setProperty(MENU_GRAVITY, "3.0");
		registerService(bc,expandGroupTaskFactory,NodeViewTaskFactory.class, expandGroupTaskFactoryProps);
		registerService(bc,expandGroupTaskFactory,ExpandGroupTaskFactory.class, expandGroupTaskFactoryProps);
		expandGroupTaskFactoryProps = new Properties();
		expandGroupTaskFactoryProps.setProperty(COMMAND, "expand");
		expandGroupTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "group"); // TODO right namespace
		expandGroupTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Expand a collapsed group"); // TODO right namespace

		registerService(bc,expandGroupTaskFactory,TaskFactory.class, expandGroupTaskFactoryProps);

		// TODO: add to group...

		// TODO: remove from group...

		{
			ExportNetworkTaskFactoryImpl factory = new ExportNetworkTaskFactoryImpl(networkViewWriterManagerServiceRef,
					cyApplicationManagerServiceRef, tunableSetterServiceRef);
			Properties props = new Properties();
			props.setProperty(ID, "exportNetworkTaskFactory");
			registerService(bc, factory, NetworkTaskFactory.class, props);
			registerService(bc, factory, ExportNetworkTaskFactory.class, props);
		}
		
		// These are task factories that are only available to the command line

		// NAMESPACE: edge
		CreateNetworkAttributeTaskFactory createEdgeAttributeTaskFactory = 
			new CreateNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, 
		                                        cyTableManagerServiceRef, CyEdge.class);
		Properties createEdgeAttributeTaskFactoryProps = new Properties();
		createEdgeAttributeTaskFactoryProps.setProperty(COMMAND, "create attribute");
		createEdgeAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		createEdgeAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Create a new column for edges");
		registerService(bc,createEdgeAttributeTaskFactory,TaskFactory.class,createEdgeAttributeTaskFactoryProps);

		GetEdgeTaskFactory getEdgeTaskFactory = new GetEdgeTaskFactory(cyApplicationManagerServiceRef, serviceRegistrar);
		Properties getEdgeTaskFactoryProps = new Properties();
		getEdgeTaskFactoryProps.setProperty(COMMAND, "get");
		getEdgeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		getEdgeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get an edge");
		getEdgeTaskFactoryProps.setProperty(COMMAND_LONG_DESCRIPTION, "Returns an edge that matches the passed parameters. If multiple edges are found, only one will be returned, and a warning will be printed.");
		getEdgeTaskFactoryProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
		getEdgeTaskFactoryProps.setProperty(COMMAND_EXAMPLE_JSON, "101");

		registerService(bc,getEdgeTaskFactory,TaskFactory.class,getEdgeTaskFactoryProps);

		GetNetworkAttributeTaskFactory getEdgeAttributeTaskFactory = 
			new GetNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, CyEdge.class, serviceRegistrar);
		Properties getEdgeAttributeTaskFactoryProps = new Properties();
		getEdgeAttributeTaskFactoryProps.setProperty(COMMAND, "get attribute");
		getEdgeAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		getEdgeAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get the values from a column in a set of edges");
		getEdgeAttributeTaskFactoryProps.setProperty(COMMAND_LONG_DESCRIPTION, "Returns the attributes for the edges passed as parameters.");
		getEdgeAttributeTaskFactoryProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
		getEdgeAttributeTaskFactoryProps.setProperty(COMMAND_EXAMPLE_JSON, "{}");
		
		registerService(bc,getEdgeAttributeTaskFactory,TaskFactory.class,getEdgeAttributeTaskFactoryProps);

		GetPropertiesTaskFactory getEdgePropertiesTaskFactory = 
			new GetPropertiesTaskFactory(cyApplicationManagerServiceRef, CyEdge.class, 
			                             cyNetworkViewManagerServiceRef, renderingEngineManagerServiceRef);
		Properties getEdgePropertiesTaskFactoryProps = new Properties();
		getEdgePropertiesTaskFactoryProps.setProperty(COMMAND, "get properties");
		getEdgePropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		getEdgePropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get the visual properties for edges");
		registerService(bc,getEdgePropertiesTaskFactory,TaskFactory.class,getEdgePropertiesTaskFactoryProps);

		ListEdgesTaskFactory listEdges = new ListEdgesTaskFactory(cyApplicationManagerServiceRef);
		Properties listEdgesTaskFactoryProps = new Properties();
		listEdgesTaskFactoryProps.setProperty(COMMAND, "list");
		listEdgesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		listEdgesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List edges");
		registerService(bc,listEdges,TaskFactory.class,listEdgesTaskFactoryProps);

		ListNetworkAttributesTaskFactory listEdgeAttributesTaskFactory = 
			new ListNetworkAttributesTaskFactory(cyApplicationManagerServiceRef, 
		                                cyTableManagerServiceRef, CyEdge.class);
		Properties listEdgeAttributesTaskFactoryProps = new Properties();
		listEdgeAttributesTaskFactoryProps.setProperty(COMMAND, "list attributes");
		listEdgeAttributesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		listEdgeAttributesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the columns for edges");
		registerService(bc,listEdgeAttributesTaskFactory,TaskFactory.class,listEdgeAttributesTaskFactoryProps);

		ListPropertiesTaskFactory listEdgeProperties = 
			new ListPropertiesTaskFactory(cyApplicationManagerServiceRef,
		                                CyEdge.class, cyNetworkViewManagerServiceRef,
		                                renderingEngineManagerServiceRef);
		Properties listEdgePropertiesTaskFactoryProps = new Properties();
		listEdgePropertiesTaskFactoryProps.setProperty(COMMAND, "list properties");
		listEdgePropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		listEdgePropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the visual properties for edges");
		registerService(bc,listEdgeProperties,TaskFactory.class,listEdgePropertiesTaskFactoryProps);

		RenameEdgeTaskFactory renameEdge = new RenameEdgeTaskFactory();
		Properties renameEdgeTaskFactoryProps = new Properties();
		renameEdgeTaskFactoryProps.setProperty(COMMAND, "rename");
		renameEdgeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		renameEdgeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Rename an edge");
		registerService(bc,renameEdge,TaskFactory.class,renameEdgeTaskFactoryProps);

		SetNetworkAttributeTaskFactory setEdgeAttributeTaskFactory = 
			new SetNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, CyEdge.class);
		Properties setEdgeAttributeTaskFactoryProps = new Properties();
		setEdgeAttributeTaskFactoryProps.setProperty(COMMAND, "set attribute");
		setEdgeAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		setEdgeAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Change edge table values for an edge or set of edges");
		registerService(bc,setEdgeAttributeTaskFactory,TaskFactory.class,setEdgeAttributeTaskFactoryProps);

		SetPropertiesTaskFactory setEdgePropertiesTaskFactory = 
			new SetPropertiesTaskFactory(cyApplicationManagerServiceRef, CyEdge.class, 
		                               cyNetworkViewManagerServiceRef, renderingEngineManagerServiceRef);
		Properties setEdgePropertiesTaskFactoryProps = new Properties();
		setEdgePropertiesTaskFactoryProps.setProperty(COMMAND, "set properties");
		setEdgePropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "edge");
		setEdgePropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Change visual properties for a set of edges");
		registerService(bc,setEdgePropertiesTaskFactory,TaskFactory.class,setEdgePropertiesTaskFactoryProps);

		// NAMESPACE: group
		{
			AddToGroupTaskFactory factory = new AddToGroupTaskFactory(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(COMMAND, "add");
			props.setProperty(COMMAND_NAMESPACE, "group");
			props.setProperty(COMMAND_DESCRIPTION, "Add nodes or edges to a group");
			registerService(bc, factory, TaskFactory.class, props);
		}

		ListGroupsTaskFactory listGroupsTaskFactory = 
			new ListGroupsTaskFactory(cyApplicationManagerServiceRef, cyGroupManager);
		Properties listGroupsTFProps = new Properties();
		listGroupsTFProps.setProperty(COMMAND, "list");
		listGroupsTFProps.setProperty(COMMAND_NAMESPACE, "group");
		listGroupsTFProps.setProperty(COMMAND_DESCRIPTION, "List all of the groups in a network");
		registerService(bc,listGroupsTaskFactory,TaskFactory.class,listGroupsTFProps);

		{
			RemoveFromGroupTaskFactory factory = new RemoveFromGroupTaskFactory(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(COMMAND, "remove");
			props.setProperty(COMMAND_NAMESPACE, "group");
			props.setProperty(COMMAND_DESCRIPTION, "Remove nodes or edges from a group");
			registerService(bc, factory, TaskFactory.class, props);
		}

		RenameGroupTaskFactory renameGroupTaskFactory = 
			new RenameGroupTaskFactory(cyApplicationManagerServiceRef, cyGroupManager);
		Properties renameGroupTFProps = new Properties();
		renameGroupTFProps.setProperty(COMMAND, "rename");
		renameGroupTFProps.setProperty(COMMAND_NAMESPACE, "group");
		renameGroupTFProps.setProperty(COMMAND_DESCRIPTION, "Rename a group");
		registerService(bc,renameGroupTaskFactory,TaskFactory.class,renameGroupTFProps);

		// NAMESPACE: layout
		GetPreferredLayoutTaskFactory getPreferredLayoutTaskFactory = 
			new GetPreferredLayoutTaskFactory(cyLayoutsServiceRef);
		Properties getPreferredTFProps = new Properties();
		getPreferredTFProps.setProperty(COMMAND, "get preferred");
		getPreferredTFProps.setProperty(COMMAND_NAMESPACE, "layout");
		getPreferredTFProps.setProperty(COMMAND_DESCRIPTION, "Return the current preferred layout");
		registerService(bc,getPreferredLayoutTaskFactory,TaskFactory.class,getPreferredTFProps);

		SetPreferredLayoutTaskFactory setPreferredLayoutTaskFactory =
				new SetPreferredLayoutTaskFactory(cyLayoutsServiceRef);
		Properties setPreferredTFProps = new Properties();
		setPreferredTFProps.setProperty(COMMAND, "set preferred");
		setPreferredTFProps.setProperty(COMMAND_NAMESPACE, "layout");
		setPreferredTFProps.setProperty(COMMAND_DESCRIPTION, "Set the preferred layout");
		registerService(bc,setPreferredLayoutTaskFactory,TaskFactory.class,setPreferredTFProps);


		// NAMESPACE: network
		AddTaskFactory addTaskFactory = new AddTaskFactory();
		Properties addTaskFactoryProps = new Properties();
		addTaskFactoryProps.setProperty(COMMAND, "add");
		addTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		addTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Add nodes and edges to a network (they must be in the current collection)");
		registerService(bc,addTaskFactory,TaskFactory.class,addTaskFactoryProps);

		AddEdgeTaskFactory addEdgeTaskFactory = new AddEdgeTaskFactory(visualMappingManagerServiceRef,
		                                                               cyNetworkViewManagerServiceRef, cyEventHelperRef, serviceRegistrar);
		Properties addEdgeTaskFactoryProps = new Properties();
		addEdgeTaskFactoryProps.setProperty(COMMAND, "add edge");
		addEdgeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		addEdgeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Add an edge between two nodes");
		registerService(bc,addEdgeTaskFactory,TaskFactory.class,addEdgeTaskFactoryProps);

		AddNodeTaskFactory addNodeTaskFactory = new AddNodeTaskFactory(visualMappingManagerServiceRef,
		                                                               cyNetworkViewManagerServiceRef, cyEventHelperRef);
		Properties addNodeTaskFactoryProps = new Properties();
		addNodeTaskFactoryProps.setProperty(COMMAND, "add node");
		addNodeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		addNodeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Add a new node to a network");
		registerService(bc,addNodeTaskFactory,TaskFactory.class,addNodeTaskFactoryProps);

		CreateNetworkAttributeTaskFactory createNetworkAttributeTaskFactory = 
			new CreateNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, 
		                                        cyTableManagerServiceRef, CyNetwork.class);
		Properties createNetworkAttributeTaskFactoryProps = new Properties();
		createNetworkAttributeTaskFactoryProps.setProperty(COMMAND, "create attribute");
		createNetworkAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		createNetworkAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Create a new column in the network table");
		registerService(bc,createNetworkAttributeTaskFactory,TaskFactory.class,createNetworkAttributeTaskFactoryProps);

		DeselectTaskFactory deselectTaskFactory = new DeselectTaskFactory(cyNetworkViewManagerServiceRef, cyEventHelperRef);
		Properties deselectTaskFactoryProps = new Properties();
		deselectTaskFactoryProps.setProperty(COMMAND, "deselect");
		deselectTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		deselectTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Deselect nodes or edges in a network");
		registerService(bc,deselectTaskFactory,TaskFactory.class,deselectTaskFactoryProps);

		GetNetworkTaskFactory getNetwork = new GetNetworkTaskFactory(cyApplicationManagerServiceRef);
		Properties getNetworkTaskFactoryProps = new Properties();
		getNetworkTaskFactoryProps.setProperty(COMMAND, "get");
		getNetworkTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		getNetworkTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Return a network");
		registerService(bc,getNetwork,TaskFactory.class,getNetworkTaskFactoryProps);

		GetNetworkAttributeTaskFactory getNetworkAttributeTaskFactory = 
			new GetNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, CyNetwork.class, serviceRegistrar);
		Properties getNetworkAttributeTaskFactoryProps = new Properties();
		getNetworkAttributeTaskFactoryProps.setProperty(COMMAND, "get attribute");
		getNetworkAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		getNetworkAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get the value from a column for a network");
		registerService(bc,getNetworkAttributeTaskFactory,TaskFactory.class,getNetworkAttributeTaskFactoryProps);

		GetPropertiesTaskFactory getNetworkPropertiesTaskFactory = 
			new GetPropertiesTaskFactory(cyApplicationManagerServiceRef, CyNetwork.class,
		                               cyNetworkViewManagerServiceRef, renderingEngineManagerServiceRef);
		Properties getNetworkPropertiesTaskFactoryProps = new Properties();
		getNetworkPropertiesTaskFactoryProps.setProperty(COMMAND, "get properties");
		getNetworkPropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		getNetworkPropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get the visual property value for a network");
		registerService(bc,getNetworkPropertiesTaskFactory,TaskFactory.class,getNetworkPropertiesTaskFactoryProps);

		{
			HideCommandTaskFactory factory = new HideCommandTaskFactory(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(COMMAND, "hide");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Hide nodes or edges in a network");
			registerService(bc, factory, TaskFactory.class, props);
		}

		ListNetworksTaskFactory listNetworks = new ListNetworksTaskFactory(cyNetworkManagerServiceRef);
		Properties listNetworksTaskFactoryProps = new Properties();
		listNetworksTaskFactoryProps.setProperty(COMMAND, "list");
		listNetworksTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		listNetworksTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the available networks");
		registerService(bc,listNetworks,TaskFactory.class,listNetworksTaskFactoryProps);

		ListNetworkAttributesTaskFactory listNetworkAttributesTaskFactory = 
			new ListNetworkAttributesTaskFactory(cyApplicationManagerServiceRef, 
		                                cyTableManagerServiceRef, CyNetwork.class);
		Properties listNetworkAttributesTaskFactoryProps = new Properties();
		listNetworkAttributesTaskFactoryProps.setProperty(COMMAND, "list attributes");
		listNetworkAttributesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		listNetworkAttributesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the columns for networks");
		registerService(bc,listNetworkAttributesTaskFactory,TaskFactory.class,listNetworkAttributesTaskFactoryProps);

		ListPropertiesTaskFactory listNetworkProperties = 
			new ListPropertiesTaskFactory(cyApplicationManagerServiceRef,
		                                CyNetwork.class, cyNetworkViewManagerServiceRef,
		                                renderingEngineManagerServiceRef);
		Properties listNetworkPropertiesTaskFactoryProps = new Properties();
		listNetworkPropertiesTaskFactoryProps.setProperty(COMMAND, "list properties");
		listNetworkPropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		listNetworkPropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the network visual properties");
		registerService(bc,listNetworkProperties,TaskFactory.class,listNetworkPropertiesTaskFactoryProps);

		SelectTaskFactory selectTaskFactory = new SelectTaskFactory(cyApplicationManagerServiceRef,
		                                                            cyNetworkViewManagerServiceRef, cyEventHelperRef); Properties selectTaskFactoryProps = new Properties();
		selectTaskFactoryProps.setProperty(COMMAND, "select");
		selectTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		selectTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Select nodes or edges in a network");
		registerService(bc,selectTaskFactory,TaskFactory.class,selectTaskFactoryProps);

		SetNetworkAttributeTaskFactory setNetworkAttributeTaskFactory = 
			new SetNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, CyNetwork.class);
		Properties setNetworkAttributeTaskFactoryProps = new Properties();
		setNetworkAttributeTaskFactoryProps.setProperty(COMMAND, "set attribute");
		setNetworkAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		setNetworkAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Set a value in the network table");
		registerService(bc,setNetworkAttributeTaskFactory,TaskFactory.class,setNetworkAttributeTaskFactoryProps);

		SetCurrentNetworkTaskFactory setCurrentNetwork = new SetCurrentNetworkTaskFactory(cyApplicationManagerServiceRef);
		Properties setCurrentNetworkTaskFactoryProps = new Properties();
		setCurrentNetworkTaskFactoryProps.setProperty(COMMAND, "set current");
		setCurrentNetworkTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		setCurrentNetworkTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Set the current network");
		registerService(bc,setCurrentNetwork,TaskFactory.class,setCurrentNetworkTaskFactoryProps);

		SetPropertiesTaskFactory setNetworkPropertiesTaskFactory = 
			new SetPropertiesTaskFactory(cyApplicationManagerServiceRef, CyNetwork.class,
		                               cyNetworkViewManagerServiceRef, renderingEngineManagerServiceRef);
		Properties setNetworkPropertiesTaskFactoryProps = new Properties();
		setNetworkPropertiesTaskFactoryProps.setProperty(COMMAND, "set properties");
		setNetworkPropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "network");
		setNetworkPropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Set network visual properties");
		registerService(bc,setNetworkPropertiesTaskFactory,TaskFactory.class,setNetworkPropertiesTaskFactoryProps);

		{
			UnHideCommandTaskFactory factory = new UnHideCommandTaskFactory(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(COMMAND, "show");
			props.setProperty(COMMAND_NAMESPACE, "network");
			props.setProperty(COMMAND_DESCRIPTION, "Show hidden nodes and edges");
			registerService(bc, factory, TaskFactory.class, props);
		}

		// NAMESPACE: node
		CreateNetworkAttributeTaskFactory createNodeAttributeTaskFactory = 
			new CreateNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, 
		                                        cyTableManagerServiceRef, CyNode.class);
		Properties createNodeAttributeTaskFactoryProps = new Properties();
		createNodeAttributeTaskFactoryProps.setProperty(COMMAND, "create attribute");
		createNodeAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		createNodeAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Create a new column for nodes");
		registerService(bc,createNodeAttributeTaskFactory,TaskFactory.class,createNodeAttributeTaskFactoryProps);

		GetNodeTaskFactory getNodeTaskFactory = new GetNodeTaskFactory(cyApplicationManagerServiceRef, serviceRegistrar);
		Properties getNodeTaskFactoryProps = new Properties();
		getNodeTaskFactoryProps.setProperty(COMMAND, "get");
		getNodeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		getNodeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get a node from a network");
		getNodeTaskFactoryProps.setProperty(COMMAND_LONG_DESCRIPTION, "Returns a node that matches the passed parameters. If multiple nodes are found, only one will be returned, and a warning will be printed.");
		getNodeTaskFactoryProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
		getNodeTaskFactoryProps.setProperty(COMMAND_EXAMPLE_JSON, "101");
		registerService(bc,getNodeTaskFactory,TaskFactory.class,getNodeTaskFactoryProps);

		GetNetworkAttributeTaskFactory getNodeAttributeTaskFactory = 
			new GetNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, CyNode.class, serviceRegistrar);
		Properties getNodeAttributeTaskFactoryProps = new Properties();
		getNodeAttributeTaskFactoryProps.setProperty(COMMAND, "get attribute");
		getNodeAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		getNodeAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get values from the node table");
		registerService(bc,getNodeAttributeTaskFactory,TaskFactory.class,getNodeAttributeTaskFactoryProps);

		GetPropertiesTaskFactory getNodePropertiesTaskFactory = 
			new GetPropertiesTaskFactory(cyApplicationManagerServiceRef, CyNode.class,
		                               cyNetworkViewManagerServiceRef, renderingEngineManagerServiceRef);
		Properties getNodePropertiesTaskFactoryProps = new Properties();
		getNodePropertiesTaskFactoryProps.setProperty(COMMAND, "get properties");
		getNodePropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		getNodePropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get visual properties for a node");
		registerService(bc,getNodePropertiesTaskFactory,TaskFactory.class,getNodePropertiesTaskFactoryProps);


		ListNodesTaskFactory listNodes = new ListNodesTaskFactory(cyApplicationManagerServiceRef);
		Properties listNodesTaskFactoryProps = new Properties();
		listNodesTaskFactoryProps.setProperty(COMMAND, "list");
		listNodesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		listNodesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the nodes in a network");
		registerService(bc,listNodes,TaskFactory.class,listNodesTaskFactoryProps);

		ListNetworkAttributesTaskFactory listNodeAttributesTaskFactory = 
			new ListNetworkAttributesTaskFactory(cyApplicationManagerServiceRef, 
		                                cyTableManagerServiceRef, CyNode.class);
		Properties listNodeAttributesTaskFactoryProps = new Properties();
		listNodeAttributesTaskFactoryProps.setProperty(COMMAND, "list attributes");
		listNodeAttributesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		listNodeAttributesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List node columns");
		registerService(bc,listNodeAttributesTaskFactory,TaskFactory.class,listNodeAttributesTaskFactoryProps);

		ListPropertiesTaskFactory listNodeProperties = 
			new ListPropertiesTaskFactory(cyApplicationManagerServiceRef,
		                                CyNode.class, cyNetworkViewManagerServiceRef,
		                                renderingEngineManagerServiceRef);
		Properties listNodePropertiesTaskFactoryProps = new Properties();
		listNodePropertiesTaskFactoryProps.setProperty(COMMAND, "list properties");
		listNodePropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		listNodePropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List node visual properties");
		registerService(bc,listNodeProperties,TaskFactory.class,listNodePropertiesTaskFactoryProps);

		RenameNodeTaskFactory renameNode = new RenameNodeTaskFactory();
		Properties renameNodeTaskFactoryProps = new Properties();
		renameNodeTaskFactoryProps.setProperty(COMMAND, "rename");
		renameNodeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		renameNodeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Rename a node");
		registerService(bc,renameNode,TaskFactory.class,renameNodeTaskFactoryProps);

		SetNetworkAttributeTaskFactory setNodeAttributeTaskFactory = 
			new SetNetworkAttributeTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, CyNode.class);
		Properties setNodeAttributeTaskFactoryProps = new Properties();
		setNodeAttributeTaskFactoryProps.setProperty(COMMAND, "set attribute");
		setNodeAttributeTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		setNodeAttributeTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, 
		                                             "Change node table values for a node or set of nodes");
		registerService(bc,setNodeAttributeTaskFactory,TaskFactory.class,setNodeAttributeTaskFactoryProps);

		SetPropertiesTaskFactory setNodePropertiesTaskFactory = 
			new SetPropertiesTaskFactory(cyApplicationManagerServiceRef, CyNode.class,
		                               cyNetworkViewManagerServiceRef, renderingEngineManagerServiceRef);
		Properties setNodePropertiesTaskFactoryProps = new Properties();
		setNodePropertiesTaskFactoryProps.setProperty(COMMAND, "set properties");
		setNodePropertiesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "node");
		setNodePropertiesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Set node visual properties");
		registerService(bc,setNodePropertiesTaskFactory,TaskFactory.class,setNodePropertiesTaskFactoryProps);

		// NAMESPACE: table
		AddRowTaskFactory addRowTaskFactory = 
			new AddRowTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef); 
		Properties addRowTaskFactoryProps = new Properties();
		addRowTaskFactoryProps.setProperty(COMMAND, "add row");
		addRowTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		addRowTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Add a new row to a table");
		registerService(bc,addRowTaskFactory,TaskFactory.class,addRowTaskFactoryProps);

		CreateColumnTaskFactory createColumnTaskFactory = new CreateColumnTaskFactory(serviceRegistrar);
		Properties createColumnTaskFactoryProps = new Properties();
		createColumnTaskFactoryProps.setProperty(COMMAND, "create column");
		createColumnTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		createColumnTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Create a new column in a table");
		registerService(bc,createColumnTaskFactory,TaskFactory.class,createColumnTaskFactoryProps);

		DeleteColumnCommandTaskFactory deleteColumnCommandTaskFactory = 
			new DeleteColumnCommandTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef); 
		Properties deleteColumnCommandTaskFactoryProps = new Properties();
		deleteColumnCommandTaskFactoryProps.setProperty(COMMAND, "delete column");
		deleteColumnCommandTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		deleteColumnCommandTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Delete a column from a table");
		registerService(bc,deleteColumnCommandTaskFactory,TaskFactory.class,deleteColumnCommandTaskFactoryProps);

		DeleteRowTaskFactory deleteRowTaskFactory = 
			new DeleteRowTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef); 
		Properties deleteRowTaskFactoryProps = new Properties();
		deleteRowTaskFactoryProps.setProperty(COMMAND, "delete row");
		deleteRowTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		deleteRowTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Delete a row from a table");
		registerService(bc,deleteRowTaskFactory,TaskFactory.class,deleteRowTaskFactoryProps);

		GetColumnTaskFactory getColumnTaskFactory = 
			new GetColumnTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef); 
		Properties getColumnTaskFactoryProps = new Properties();
		getColumnTaskFactoryProps.setProperty(COMMAND, "get column");
		getColumnTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		getColumnTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Get the information about a table column");
		registerService(bc,getColumnTaskFactory,TaskFactory.class,getColumnTaskFactoryProps);

		GetRowTaskFactory getRowTaskFactory = 
			new GetRowTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef); 
		Properties getRowTaskFactoryProps = new Properties();
		getRowTaskFactoryProps.setProperty(COMMAND, "get row");
		getRowTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		getRowTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Return all values in a table row");
		registerService(bc,getRowTaskFactory,TaskFactory.class,getRowTaskFactoryProps);

		GetValueTaskFactory getValueTaskFactory = 
			new GetValueTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef); 
		Properties getValueTaskFactoryProps = new Properties();
		getValueTaskFactoryProps.setProperty(COMMAND, "get value");
		getValueTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		getValueTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Return a single value from a table");
		registerService(bc,getValueTaskFactory,TaskFactory.class,getValueTaskFactoryProps);

		ListColumnsTaskFactory listColumnsTaskFactory = 
			new ListColumnsTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, 
			                          cyNetworkTableManagerServiceRef);
		Properties listColumnsTaskFactoryProps = new Properties();
		listColumnsTaskFactoryProps.setProperty(COMMAND, "list columns");
		listColumnsTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		listColumnsTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the columns in a table");
		registerService(bc,listColumnsTaskFactory,TaskFactory.class,listColumnsTaskFactoryProps);

		ListRowsTaskFactory listRowsTaskFactory = 
			new ListRowsTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef);
		Properties listRowsTaskFactoryProps = new Properties();
		listRowsTaskFactoryProps.setProperty(COMMAND, "list rows");
		listRowsTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		listRowsTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the rows in a table");
		registerService(bc,listRowsTaskFactory,TaskFactory.class,listRowsTaskFactoryProps);

		ListTablesTaskFactory listTablesTaskFactory = 
			new ListTablesTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef, 
			                          cyNetworkTableManagerServiceRef);
		Properties listTablesTaskFactoryProps = new Properties();
		listTablesTaskFactoryProps.setProperty(COMMAND, "list");
		listTablesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		listTablesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "List all of the registered tables");
		registerService(bc,listTablesTaskFactory,TaskFactory.class,listTablesTaskFactoryProps);

		SetTableTitleTaskFactory setTableTitleTaskFactory = 
			new SetTableTitleTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef);
		Properties setTableTitleTaskFactoryProps = new Properties();
		setTableTitleTaskFactoryProps.setProperty(COMMAND, "set title");
		setTableTitleTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		setTableTitleTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Set the title of a table");
		registerService(bc,setTableTitleTaskFactory,TaskFactory.class,setTableTitleTaskFactoryProps);

		SetValuesTaskFactory setValuesTaskFactory = 
			new SetValuesTaskFactory(cyApplicationManagerServiceRef, cyTableManagerServiceRef);
		Properties setValuesTaskFactoryProps = new Properties();
		setValuesTaskFactoryProps.setProperty(COMMAND, "set values");
		setValuesTaskFactoryProps.setProperty(COMMAND_NAMESPACE, "table");
		setValuesTaskFactoryProps.setProperty(COMMAND_DESCRIPTION, "Set values in a table");
		registerService(bc,setValuesTaskFactory,TaskFactory.class,setValuesTaskFactoryProps);

		// NAMESPACE: view
		// ===============================
		{
			ExportNetworkViewTaskFactoryImpl factory = new ExportNetworkViewTaskFactoryImpl(
					networkViewWriterManagerServiceRef, cyApplicationManagerServiceRef, tunableSetterServiceRef);
			Properties props = new Properties();
			props.setProperty(ID, "exportNetworkViewTaskFactory");
			registerService(bc, factory, NetworkViewTaskFactory.class, props);
			registerService(bc, factory, ExportNetworkViewTaskFactory.class, props);
		}
		{
			CreateNetworkViewTaskFactoryImpl factory = new CreateNetworkViewTaskFactoryImpl(undoSupportServiceRef,
					cyNetworkViewManagerServiceRef, cyLayoutsServiceRef, cyEventHelperRef,
					visualMappingManagerServiceRef, renderingEngineManagerServiceRef, cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(ID, "createNetworkViewTaskFactory");
			// No ENABLE_FOR because that is handled by the isReady() methdod of the task factory.
			props.setProperty(PREFERRED_MENU, "Edit");
			props.setProperty(TITLE, "Create Views");
			props.setProperty(MENU_GRAVITY, "3.0");
			props.setProperty(COMMAND, "create");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Create a new view for a network"); // TODO test again: for current network or selected ones?
			registerService(bc, factory, NetworkCollectionTaskFactory.class, props);
			registerService(bc, factory, CreateNetworkViewTaskFactory.class, props);
			registerService(bc, factory, TaskFactory.class, props); // for Commands
			registerServiceListener(bc, factory::addNetworkViewRenderer, factory::removeNetworkViewRenderer, NetworkViewRenderer.class);
		}
		{
			DestroyNetworkViewTaskFactoryImpl factory = new DestroyNetworkViewTaskFactoryImpl(cyNetworkViewManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Edit");
			props.setProperty(TITLE, "Destroy Views");
			props.setProperty(MENU_GRAVITY, "3.1");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(COMMAND, "destroy");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Destroy (delete) a network view");
			registerService(bc, factory, NetworkViewCollectionTaskFactory.class, props);
			registerService(bc, factory, DestroyNetworkViewTaskFactory.class, props);
		}
		{
			ZoomInTaskFactory factory = new ZoomInTaskFactory(undoSupportServiceRef, cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "View");
			props.setProperty(TITLE, "Zoom In");
			props.setProperty(MENU_GRAVITY, "6.3");
			props.setProperty(ACCELERATOR, "cmd equals");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/zoom-in-32.png").toString());
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(TOOLTIP, "Zoom In");
			props.setProperty(TOOL_BAR_GRAVITY, "5.1");
			props.setProperty(IN_TOOL_BAR, "true");
			// props.setProperty(COMMAND, "zoom in");
			// props.setProperty(COMMAND_NAMESPACE, "view");
			registerService(bc, factory, NetworkTaskFactory.class, props);
		}
		{
			ZoomOutTaskFactory factory = new ZoomOutTaskFactory(undoSupportServiceRef, cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "View");
			props.setProperty(TITLE, "Zoom Out");
			props.setProperty(TOOLTIP, "Zoom Out");
			props.setProperty(MENU_GRAVITY, "6.4");
			props.setProperty(INSERT_SEPARATOR_AFTER, "true");
			props.setProperty(ACCELERATOR, "cmd minus");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/zoom-out-32.png").toString());
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(TOOL_BAR_GRAVITY, "5.2");
			props.setProperty(IN_TOOL_BAR, "true");
			// props.setProperty(COMMAND, "zoom out");
			// props.setProperty(COMMAND_NAMESPACE, "view");
			registerService(bc, factory, NetworkTaskFactory.class, props);
		}
		{
			FitSelectedTaskFactory factory = new FitSelectedTaskFactory(undoSupportServiceRef, cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "View");
			props.setProperty(TITLE, "Fit Selected");
			props.setProperty(TOOLTIP, "Zoom selected region");
			props.setProperty(MENU_GRAVITY, "6.2");
			props.setProperty(ACCELERATOR, "cmd 9");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/zoom-selected-32.png").toString());
			props.setProperty(ENABLE_FOR, ENABLE_FOR_SELECTED_NODES_OR_EDGES);
			props.setProperty(TOOL_BAR_GRAVITY, "5.4");
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(COMMAND, "fit selected");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Fit the selected nodes and edges into the view");
			registerService(bc, factory, NetworkTaskFactory.class, props);
		}
		{
			FitContentTaskFactory factory = new FitContentTaskFactory(undoSupportServiceRef,
					cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "View");
			props.setProperty(TITLE, "Fit Content");
			props.setProperty(TOOLTIP, "Zoom out to display all of current Network");
			props.setProperty(MENU_GRAVITY, "6.1");
			props.setProperty(ACCELERATOR, "cmd 0");
			props.setProperty(LARGE_ICON_URL, getClass().getResource("/images/icons/zoom-fit-32.png").toString());
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(TOOL_BAR_GRAVITY, "5.3");
			props.setProperty(IN_TOOL_BAR, "true");
			props.setProperty(COMMAND, "fit content");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Fit all of the nodes and edges into the view");
			registerService(bc,factory,NetworkTaskFactory.class, props);
		}
		{
			GetCurrentNetworkViewTaskFactory factory = new GetCurrentNetworkViewTaskFactory(cyApplicationManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "get current");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Get the current view");
			registerService(bc, factory, TaskFactory.class, props);
		}
		{
			ListNetworkViewsTaskFactory factory = new ListNetworkViewsTaskFactory(cyApplicationManagerServiceRef,
					cyNetworkViewManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "list");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "List all views");
			registerService(bc, factory, TaskFactory.class, props);
		}
		{
			SetCurrentNetworkViewTaskFactory factory = new SetCurrentNetworkViewTaskFactory(
					cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "set current");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Set the current view");
			registerService(bc, factory, TaskFactory.class, props);
		}
		{
			UpdateNetworkViewTaskFactory factory = new UpdateNetworkViewTaskFactory(cyApplicationManagerServiceRef,
					cyNetworkViewManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(COMMAND, "update");
			props.setProperty(COMMAND_NAMESPACE, "view");
			props.setProperty(COMMAND_DESCRIPTION, "Update (repaint) a view");
			registerService(bc, factory, TaskFactory.class, props);
		}
		{
			// New in 3.2.0: Export to HTML5 archive
			ExportAsWebArchiveTaskFactory factory = new ExportAsWebArchiveTaskFactory(cyNetworkManagerServiceRef,
					cyApplicationManagerServiceRef, cySessionManagerServiceRef);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(MENU_GRAVITY, "5.3");
			props.setProperty(TITLE, "Export as Web Page...");
			registerAllServices(bc, factory, props);
			registerServiceListener(bc, factory::registerFactory, factory::unregisterFactory, CySessionWriterFactory.class);
		}
	}

	private void createVizmapTaskFactories(BundleContext bc, CyServiceRegistrar serviceRegistrar) {
		{
			ApplyVisualStyleTaskFactoryimpl factory = new ApplyVisualStyleTaskFactoryimpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ID, "applyVisualStyleTaskFactory");
			props.setProperty(TITLE, "Apply Style...");
			props.setProperty(IN_NETWORK_PANEL_CONTEXT_MENU, "true");
			props.setProperty(MENU_GRAVITY, "6.999");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			props.setProperty(COMMAND, "apply");
			props.setProperty(COMMAND_NAMESPACE, "vizmap");
			props.setProperty(COMMAND_DESCRIPTION, "Apply a style");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "Applies the specified style to the selected views and returns the SUIDs of the affected views.");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "[ 322, 420 ]");
			registerService(bc, factory, NetworkViewCollectionTaskFactory.class, props);
			registerService(bc, factory, ApplyVisualStyleTaskFactory.class, props);
		}
		{
			ExportVizmapTaskFactoryImpl factory = new ExportVizmapTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ENABLE_FOR, "vizmap");
			props.setProperty(PREFERRED_MENU, "File.Export");
			props.setProperty(MENU_GRAVITY, "1.4");
			props.setProperty(TITLE, "Styles...");
			props.setProperty(COMMAND, "export");
			props.setProperty(COMMAND_NAMESPACE, "vizmap");
			props.setProperty(COMMAND_DESCRIPTION, "Export styles to a file");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "Exports the specified styles to a Cytoscape vizmap (XML) or a Cytoscape.js (JSON) file.");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{ }");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, ExportVizmapTaskFactory.class, props);
		}
		{
			LoadVizmapFileTaskFactoryImpl factory = new LoadVizmapFileTaskFactoryImpl(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "File.Import");
			props.setProperty(MENU_GRAVITY, "3.0");
			props.setProperty(TITLE, "Styles...");
			props.setProperty(COMMAND, "load file");
			props.setProperty(COMMAND_NAMESPACE, "vizmap");
			props.setProperty(COMMAND_DESCRIPTION, "Load styles from a file");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "Loads styles from a vizmap (XML or properties) file and returns the names of the loaded styles.");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "[ \"My Style 1\", \"My Style 2\" ]");
			registerService(bc, factory, TaskFactory.class, props);
			registerService(bc, factory, LoadVizmapFileTaskFactory.class, new Properties());
		}
		{
			// Clear edge bends - Main menu
			ClearAllEdgeBendsFactory factory = new ClearAllEdgeBendsFactory(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(ID, "clearAllEdgeBendsFactory");
			props.setProperty(TITLE, "Clear All Edge Bends");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(MENU_GRAVITY, "0.1");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			registerService(bc, factory, NetworkViewCollectionTaskFactory.class, props);
		}
		{
			// Clear edge bends - Network context menu
			ClearAllEdgeBendsFactory factory = new ClearAllEdgeBendsFactory(serviceRegistrar);
			Properties props = new Properties();
			props.setProperty(TITLE, "Clear All Edge Bends");
			props.setProperty(IN_NETWORK_PANEL_CONTEXT_MENU, "true");
			props.setProperty(MENU_GRAVITY, "6.0");
			props.setProperty(INSERT_SEPARATOR_BEFORE, "true");
			props.setProperty(ENABLE_FOR, ENABLE_FOR_NETWORK_AND_VIEW);
			registerService(bc, factory, NetworkViewCollectionTaskFactory.class, props);
		}
	}
}
