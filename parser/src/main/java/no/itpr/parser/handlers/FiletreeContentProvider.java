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
import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides a tree view of file system.
 */
public class FiletreeContentProvider implements ITreeContentProvider {

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	 @Override
     public void dispose() {
     }

     @Override
     public Object[] getElements(Object inputElement) {
    	File[]files = (File[]) inputElement;
       	return files; 
     }

     @Override
     public Object[] getChildren(Object parentElement) {
             File file = (File) parentElement;
             return file.listFiles();
     }

     @Override
     public Object getParent(Object element) {
             File file = (File) element;
             return file.getParentFile();
     }

     @Override
     public boolean hasChildren(Object element) {
             File file = (File) element;
             if (file.isDirectory()) {
                     return true;
             }
             return false;
     }

}
