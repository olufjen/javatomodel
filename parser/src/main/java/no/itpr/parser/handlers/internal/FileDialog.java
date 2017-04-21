/*
 * Copyright (c) 2013, Alex Blewitt, Bandlem Ltd
 * Copyright (c) 2013, Packt Publishing Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package no.itpr.parser.handlers.internal;

import java.io.File;
import java.util.TimeZone;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;



public class FileDialog extends MessageDialog {
	private File selectedFile;
	private FileParser parser;
	public FileDialog(Shell parentShell,File selectedFile) {
		super(parentShell, selectedFile.getName(), null, "Filename "
				+ selectedFile.getName(), INFORMATION,
				new String[] { IDialogConstants.OK_LABEL }, 0);
		this.selectedFile = selectedFile;
		parser = new FileParser(selectedFile.getAbsolutePath(),this.selectedFile);
	}

	protected Control createCustomArea(Composite parent) {

		return parent;
	}
}