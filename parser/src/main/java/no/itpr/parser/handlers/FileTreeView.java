/*
 * Copyright (c) 2013, Alex Blewitt, Bandlem Ltd
 * Copyright (c) 2013, Packt Publishing Ltd
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package no.itpr.parser.handlers;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.util.TimeZone;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.dialogs.ViewContentProvider;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import no.itpr.parser.handlers.internal.FileDialog;
import no.itpr.parser.handlers.internal.TimeZoneComparator;
import no.itpr.parser.handlers.internal.TimeZoneDialog;
import no.itpr.parser.handlers.internal.TimeZoneSelectionListener;
import no.itpr.parser.handlers.internal.TimeZoneViewerComparator;
import no.itpr.parser.handlers.internal.TimeZoneViewerFilter;

public class FileTreeView extends ViewPart {
	private TreeViewer treeViewer;
	private static final DateFormat dateFormat = DateFormat.getDateInstance();
	private TimeZoneSelectionListener selectionListener;

	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		 treeViewer.setContentProvider(new FiletreeContentProvider());
         treeViewer.getTree().setHeaderVisible(true);
         treeViewer.getTree().setLinesVisible(true);
         TreeViewerColumn mainColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
         mainColumn.getColumn().setText("Name");
         mainColumn.getColumn().setWidth(300);
         mainColumn.setLabelProvider(
                         new DelegatingStyledCellLabelProvider(
                                         (IStyledLabelProvider) new FileLabelProvider(createImageDescriptor())));

         TreeViewerColumn modifiedColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
         modifiedColumn.getColumn().setText("Last Modified");
         modifiedColumn.getColumn().setWidth(100);
         modifiedColumn.getColumn().setAlignment(SWT.RIGHT);
         modifiedColumn
                         .setLabelProvider(new DelegatingStyledCellLabelProvider(
                                         (IStyledLabelProvider) new FileModifiedLabelProvider(dateFormat)));

         TreeViewerColumn fileSizeColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
         fileSizeColumn.getColumn().setText("Size");
         fileSizeColumn.getColumn().setWidth(100);
         fileSizeColumn.getColumn().setAlignment(SWT.RIGHT);
         fileSizeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
                         new FileSizeLabelProvider()));

 		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Viewer viewer = event.getViewer();
				Shell shell = viewer.getControl().getShell();
				ISelection sel = viewer.getSelection();
				Object selectedValue;
				if (!(sel instanceof IStructuredSelection) || sel.isEmpty()) {
					selectedValue = null;
				} else {
					selectedValue = ((IStructuredSelection) sel)
							.getFirstElement();
				}
				if (selectedValue instanceof File) {
					File selectedFile = (File) selectedValue;
					new FileDialog(shell, selectedFile).open();
				}
			}
		});
 //        treeViewer.getContentProvider()
         File[] files = File.listRoots();
         File[] otherFiles = {files[0]};
        File fileC = files[0];
         treeViewer.setInput(otherFiles);
         
  //       String tree = treeViewer.getTree().getData().toString();
         System.out.println("List roots: "+fileC.getAbsolutePath());
	}
	  private ImageDescriptor createImageDescriptor() {
          Bundle bundle = FrameworkUtil.getBundle(FileLabelProvider.class);
//          Path path = (Path) Paths.get("icons/folder.png");
          URL url = FileLocator.find(bundle, (IPath)new Path("icons/folder.png"), null);
          return ImageDescriptor.createFromURL(url);
  }
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		if (selectionListener != null) {
			getSite().getWorkbenchWindow()
					.getSelectionService().removeSelectionListener(selectionListener);
			selectionListener = null;
		}
		super.dispose();
	}
}
