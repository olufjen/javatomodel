package no.itpr.parser.handlers.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import no.itpr.parser.model.FileModel;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * FileParser
 * @author olj
 * This class employs the com.github.javaparser.JavaParser framework to parse Java classes and Interfaces and 
 * produces xml structures of these classes and Interfaces of XMI 2.0 format
 * The Apache Xerces framework is used to produce the xml structures.
 */
public class FileParser {
	private String filePath;
	private File selectedFile;
	public FileParser() {
		super();
		
	}

	public FileParser(String filePath,File selectedFile) {
		super();
		this.filePath = filePath;
		FileInputStream in = null;
		this.selectedFile = selectedFile;
		String fileName = this.selectedFile.getName();
		try {
			in = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File not found "+e.getMessage());
		}

		JavaParser parser = new JavaParser();
		 CompilationUnit cu = parser.parse(in);
		 NodeList<TypeDeclaration<?>> nodeclassList = cu.getTypes();
	//	 nodeclassList.
		 FileModel fileModel = new FileModel();
	      char separator = '.';
	      String newFilename = fileModel.extractString(fileName, separator, 0);
		 Optional<ClassOrInterfaceDeclaration> declaration = cu.getClassByName(newFilename);
		 if (!declaration.isPresent()){
			 declaration = cu.getInterfaceByName(newFilename);
			 fileModel.setInterfaceFlag(true);
		 }
		 
		 ClassOrInterfaceDeclaration realDeclaration = null; 
		 String code = cu.toString();
		 MethodVisitor methodVisitor = new MethodVisitor();
		 methodVisitor.visit(cu, null);
		// methodVisitor.visitClass(cu,null);
		 SimpleName method = methodVisitor.getMethods();
		 String[] lines = code.split("\r\n|\r|\n");
		 List<String> nodeList = methodVisitor.getDeclaration();
		 List<String> classlist = methodVisitor.getClassName();
		 if (declaration.isPresent()){
			 realDeclaration = declaration.get();
			 fileModel.setClassDeclaration(realDeclaration);
			 fileModel.setMethoddeclarationList(nodeList);
			 fileModel.setFileName(fileName);
			 fileModel.createXML();
		 }
		 
/*		 for (String line:lines){
			 nodeList.add(line);
//			 System.out.println(line);
		 }*/
		 Stream nodeStream = nodeList.stream();
	
		 fileModel.setFileName(fileName);
//		 fileModel.writetoFile(nodeStream);
	}
	/**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private class MethodVisitor extends VoidVisitorAdapter<Void> {
    	private SimpleName methods;
    	private List<String> methodName;
    	private List<String> className;
    	private List<String >declaration;
        public MethodVisitor() {
  			super();
  	      	methodName = new ArrayList();
  	      	declaration = new ArrayList();
			// TODO Auto-generated constructor stub
		}
		@Override
        public void visit(MethodDeclaration n, Void arg) {
            /* here you can access the attributes of the method.
             this method will be called for all methods in this 
             CompilationUnit, including inner class methods */
			   methodName.add(n.getNameAsString());
			   declaration.add(n.getDeclarationAsString());
            System.out.println(methodName);
            methods = n.getName();
         
            super.visit(n, arg);
      
        }
	       public void visitClass( ClassOrInterfaceDeclaration n, Void arg) {
	            /* here you can access the attributes of the method.
	             this method will be called for all methods in this 
	             CompilationUnit, including inner class methods */
				  className.add(n.getNameAsString());
	  //          System.out.println(methodName);
	                     
	            super.visit(n, arg);
	      
	        }
	       
		public List<String> getClassName() {
			return className;
		}
		public void setClassName(List<String> className) {
			this.className = className;
		}
		public SimpleName getMethods() {
			return methods;
		}
		public void setMethods(SimpleName methods) {
			this.methods = methods;
		}
		public List<String> getMethodName() {
			return methodName;
		}
		public void setMethodName(List<String> methodName) {
			this.methodName = methodName;
		}
		public List<String> getDeclaration() {
			return declaration;
		}
		public void setDeclaration(List<String> declaration) {
			this.declaration = declaration;
		}

		
        
    }

}
