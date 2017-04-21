package no.itpr.parser.handlers;

import java.io.File;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;

class FileSizeLabelProvider extends LabelProvider implements IStyledLabelProvider {

                @Override
                public StyledString getStyledText(Object element) {
                        if (element instanceof File) {
                                File file = (File) element;
                                if (file.isDirectory()) {
                                        // a directory is just a container and has no size
                                        return new StyledString("0");
                                }
                                return new StyledString(String.valueOf(file.length()));
                        }
                        return null;
                }
        }
