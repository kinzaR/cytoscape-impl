package org.cytoscape.work.internal.tunables;

/*
 * #%L
 * Cytoscape Work Swing Impl (work-swing-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
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


import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Insets;
import java.awt.Window;

public final class TunableDialog extends JDialog {

	private static final long serialVersionUID = 7438623438647443009L;

	protected JPanel parentPanel = null;
	protected Component optionPanel = null;
	private String userInput = "";

	/**
	 * Construct this TunableDialog.
	 * @param parent The parent Window of this TunableDialog.
	 */
	public TunableDialog(final Window parent, final Component optionPanel) {
		super(parent);
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.optionPanel = optionPanel;
		initComponents();
	}

	/** Set the text to replace the "OK" string on OK button. 
	 * @param okText the text to replace "OK" on the OK button.
	 * */
	public void setOKtext(String okText) {
		this.btnOK.setText(okText);
	}

	private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {
		this.userInput = "OK";
		this.jScrollPane1.removeAll();
		this.dispose();
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		this.userInput = "CANCEL";
		this.jScrollPane1.removeAll();
		this.dispose();
	}

	public String getUserInput() {
		return userInput;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		jScrollPane1 = new javax.swing.JScrollPane();
		pnlButtons = new javax.swing.JPanel();
		btnOK = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("Form"); // NOI18N
		getContentPane().setLayout(new java.awt.GridBagLayout());

		jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		jScrollPane1.setName("jScrollPane1"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		getContentPane().add(jScrollPane1, gridBagConstraints);

		pnlButtons.setName("pnlButtons"); // NOI18N

		btnOK.setText("OK"); // NOI18N
		btnOK.setName("btnOK"); // NOI18N
		btnOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnOKActionPerformed(evt);
			}
		});
		pnlButtons.add(btnOK);

		btnCancel.setText("Cancel"); // NOI18N
		btnCancel.setName("btnCancel"); // NOI18N
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
			}
		});
		pnlButtons.add(btnCancel);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
		getContentPane().add(pnlButtons, gridBagConstraints);
		jScrollPane1.setViewportView(optionPanel);
		pack();
		// Shouldn't call setSize after we pack.  Leads to really ugly dialogs if we only
		// have a single tunable
		// setSize(this.getSize().width + 30, this.getSize().height + 30);
	}// </editor-fold>

	// Variables declaration - do not modify
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnOK;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JPanel pnlButtons;
	// End of variables declaration
}
