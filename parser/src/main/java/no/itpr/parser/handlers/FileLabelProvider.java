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
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Provides a means to convert an object into text. Think of it as an
 * externalised toString representation for an object that is passed in as the
 * argument.
 */
public class FileLabelProvider extends LabelProvider implements
		IStyledLabelProvider, IFontProvider {

	private ResourceManager resourceManager;
	private ImageDescriptor directoryImage;


	public FileLabelProvider(ImageDescriptor directoryImage){
		this.directoryImage = directoryImage;
	}
	@SuppressWarnings("rawtypes")
	public String getText(Object element) {
		String ss = "";
		 if (element instanceof File) {
            File file = (File) element;
            StyledString styledString = new StyledString(getFileName(file));
            String[] files = file.list();
            if (files != null) {
                    styledString.append(" ( " + files.length + " ) ", StyledString.COUNTER_STYLER);
            }
            return ss;
		 }
		 return ss;
	}

	public Image getImage(Object element) {
		if (element instanceof File) {
            if (((File) element).isDirectory()) {
                    return getResourceManager().createImage(directoryImage);
            }
    }

    return super.getImage(element);
	}

	public StyledString getStyledText(Object element) {
		String text = getText(element);
		StyledString ss = new StyledString(text);
		 if (element instanceof File) {
             File file = (File) element;
             StyledString styledString = new StyledString(getFileName(file));
             String[] files = file.list();
             if (files != null) {
                     styledString.append(" ( " + files.length + " ) ", StyledString.COUNTER_STYLER);
             }
             return styledString;
     }
     return null;
     
	}


	 protected ResourceManager getResourceManager() {
         if (resourceManager == null) {
                 resourceManager = new LocalResourceManager(JFaceResources.getResources());
         }
         return resourceManager;
	 }
	 private String getFileName(File file) {
         String name = file.getName();
         return name.isEmpty() ? file.getPath() : name;
	 }
	@Override
	public Font getFont(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
