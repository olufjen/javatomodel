package no.itpr.parser.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import no.itpr.parser.model.FileModel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Name;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Parser",
				"Hello, my Eclipse world");
		
		FileInputStream in = null;
		try {
			in = new FileInputStream("C:\\svnroot2\\nokc\\biovigilans\\trunk\\biovigilans_felles\\src\\main\\java\\no\\naks\\biovigilans\\felles\\server\\resource\\ProsedyreServerResource.java");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File not found "+e.getMessage());
		}
		
		JavaParser parser = new JavaParser();
		 CompilationUnit cu = parser.parse(in);
		 System.out.println(cu.toString());
		 String code = cu.toString();
		 String[] lines = code.split("\r\n|\r|\n");
		 List<String> nodeList = new ArrayList();
		 for (String line:lines){
			 nodeList.add(line);
			 System.out.println("line "+line);
		 }
		 Stream nodeStream = nodeList.stream();
		 FileModel fileModel = new FileModel();
		 fileModel.setFileName("test.txt");
		 fileModel.writetoFile(nodeStream);
//		 System.out.println(cu.toString());
		return null;
	}
}
