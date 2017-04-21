package no.itpr.parser.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node; 
 





import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

import no.itpr.parser.model.FileProcess.ReadFile;

/**
 * @author oluf
 *
 * Denne klassen benyttes til File og java 8 Stream operasjoner
 * Den benyttes også til å lage xmi/xml representasjoner av java klasser
 * Et eget rammeverk for java parser benyttes til dette:
 * http://www.javadoc.io/doc/com.github.javaparser/javaparser-core/3.1.2
 */
public class FileModel {
	private Stream<String> lines;
	private String filePath;
	private List<String> linesFromfile;
	private String fileName;
	private ClassOrInterfaceDeclaration classDeclaration;
	List<String> methoddeclarationList;
	private boolean interfaceFlag = false;
	
	public FileModel() {
		super();
		
	}
	
	public boolean isInterfaceFlag() {
		return interfaceFlag;
	}

	public void setInterfaceFlag(boolean interfaceFlag) {
		this.interfaceFlag = interfaceFlag;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getLinesFromfile() {
		return linesFromfile;
	}

	public void setLinesFromfile(List<String> linesFromfile) {
		this.linesFromfile = linesFromfile;
	}

	public Stream<String> getLines() {
		return lines;
	}
	public void setLines(Stream<String> lines) {
		this.lines = lines;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public ClassOrInterfaceDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void setClassDeclaration(ClassOrInterfaceDeclaration classDeclaration) {
		this.classDeclaration = classDeclaration;
	}

	public List<String> getMethoddeclarationList() {
		return methoddeclarationList;
	}

	public void setMethoddeclarationList(List<String> methoddeclarationList) {
		this.methoddeclarationList = methoddeclarationList;
	}

	/**
	 * createLines
	 * Denne rutinen åpner en file som en Stream
	 */
	public void createLines(){
		try {
			lines = Files.lines(Paths.get(filePath), Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * readlines
	 * This function Reads a line from a file using a functional interface ReadFile
	 * A functional interface  passes behavior
	 * This function executes the behavior
	 * The behavior of this functional interface is: BufferedReader -> String
	 * @param r a functional interface ReadFile
	 * @return String a line from the file
	 * @throws IOException
	 */
	public String readlines(ReadFile r,BufferedReader br) throws IOException{

			return r.readFile(br); // Processing the buffered reader object

		
	}
	/**
	 * fillLines
	 * This routine removes empty lines from the list
	 * @param list of lines from file
	 * @param p The predicate
	 * @return a nonempty list
	 */
	public <T> List<T> fillLines (List<T> list,Predicate<T> p){
		List<T> results = new ArrayList<>();
		String line = null;
		for(T s:list)
		if (p.test(s)){ // calls the lambda expression
			results.add(s);
		}
		return results;
	}
	/**
	 * sendeListe 
	 * This routine reads a file line by line and places it in a List<String>
	 * @return List<String> lines from a file
	 */
	public List sendeListe(){
/*
 * Define the lambda expression using predicate
 * Keep a string if it is nonempty
 */
		Predicate<String> nonemptyP = (String s) -> (s != null && !s.isEmpty()); // Is executed for the whole list from the call to fillLines!!
/*
 * Define the lambda expression using functional interface ReadFile	
 * The target type is ReadFile which must be a functional interface	
 */
		ReadFile r = (BufferedReader brx) -> brx.readLine(); // Is executed from the routine readlines
		String line = "";
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		linesFromfile = new ArrayList<String>();
		while(line != null ){
			try {
				line = readlines(r, br);
				linesFromfile.add(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}			
		}
		List<String> nonEmptylines = fillLines(linesFromfile,nonemptyP);
		return nonEmptylines;
	}
	public  String extract(String s,Function <String,String> f){
		return f.apply(s);
	}
	/**
	 * extractString
	 * This routine extracts a substring form a string  using the Function interface
	 * It finds the last index of a string using separator
	 * @param line The original string
	 * @param separator The separator
	 * @param startindex The startindex
	 * @return the substring
	 */
	public String extractString(String line,char separator,int startindex){
		int index = line.lastIndexOf(separator);
		Function<String,String> f = (String s) -> line.substring(startindex,index);
		return extract(line,f);

	}
	
	/**
	 * createMethods
	 * This routine creates all methods to the xmi classtructure from the Java Class being parsed
	 * @param packagedElement
	 * @param doc
	 */
	public void createMethods(Element packagedElement,Document doc){
		
		for (String method:methoddeclarationList ){
			String[] lines = method.split(" ");
			char separator = '(';

			Element ownedAttribute = doc.createElement("ownedOperation");
			ownedAttribute.setAttribute("isAbstract", "false");
			ownedAttribute.setAttribute("ownerScope", "instance");
			ownedAttribute.setAttribute("isQuery", "false");
			ownedAttribute.setAttribute("isPolymorphic", "false");
			ownedAttribute.setAttribute("xmi:type", "uml:Operation");
			
			ownedAttribute.setAttribute("visibility", lines[0]);
			int namect = 0;
			for (String line:lines){
				if (line.contains("("))
					break;
				namect++;
			}
			String name = extractString(lines[namect], separator,0);
			namect = 2;
			ownedAttribute.setAttribute("name", name);
			Element ownedParameter = doc.createElement("ownedParameter");
			ownedParameter.setAttribute("xmi:type","uml:Parameter");
			ownedParameter.setAttribute("kind","return");
			ownedParameter.setAttribute("name", name);
			ownedParameter.setAttribute("visibility", lines[0]);
			ownedParameter.setAttribute("type", lines[1]);
			
			Element extension = doc.createElement("xmi:Extension");
	        extension.setAttribute("extender","Select Architect");
	    	Element attributeExtension = doc.createElement("attributeExtension");
			Element attributePersist = (Element) attributeExtension.cloneNode(false);
			Element attributeStructure = (Element) attributeExtension.cloneNode(false);
			Element attributeFinal = (Element) attributeExtension.cloneNode(false);
			Element attributeStatic = (Element) attributeExtension.cloneNode(false);
			Element attributeSync = (Element) attributeExtension.cloneNode(false);

			Element taggedValue = doc.createElement("taggedValue");
			taggedValue.setAttribute("tag", "IsVirtual");
			taggedValue.setAttribute("value", "FALSE");
			Element derived = (Element) taggedValue.cloneNode(false);
			Element transientKey = (Element) taggedValue.cloneNode(false);
			Element volatileKey = (Element) taggedValue.cloneNode(false);
			Element finalKeyword = (Element) taggedValue.cloneNode(false);
			Element syncronizedKeyword = (Element) taggedValue.cloneNode(false);
			derived.setAttribute("tag", "IsOverride");
			derived.setAttribute("value", "FALSE");
			transientKey.setAttribute("tag", "Java Synchronized Keyword");
			transientKey.setAttribute("value", "FALSE");
			volatileKey.setAttribute("tag", "Java Native Keyword");
			volatileKey.setAttribute("value", "FALSE");
			finalKeyword.setAttribute("tag", "Java Final Keyword");
			finalKeyword.setAttribute("value", "FALSE");
			syncronizedKeyword.setAttribute("tag", "Asynchronous");
			syncronizedKeyword.setAttribute("value", "FALSE");
			
			attributeExtension.appendChild(taggedValue);
			attributePersist.appendChild(derived);
			attributeFinal.appendChild(finalKeyword);
			attributeStructure.appendChild(volatileKey);
			attributeStatic.appendChild(transientKey);
			attributeSync.appendChild(syncronizedKeyword);
			
			extension.appendChild(attributeExtension);
			extension.appendChild(attributePersist);
			extension.appendChild(attributeFinal);
			extension.appendChild(attributeStructure);
			extension.appendChild(attributeStatic);
			extension.appendChild(attributeSync);
			
			ownedAttribute.appendChild(extension);
			ownedAttribute.appendChild(ownedParameter);
			packagedElement.appendChild(ownedAttribute);			
		}


	}
	/**
	 * createAttributes
	 * This routine creates all attributes to the xmi classtructure from the Java Class being parsed
	 * @param packagedElement
	 * @param doc
	 */
	public void createAttributes(Element packagedElement,Document doc){
		
		
		
		NodeList<BodyDeclaration<?>> nodes =  classDeclaration.getMembers();
		if (!nodes.isEmpty()){
			for (BodyDeclaration body : nodes){
				if (body instanceof FieldDeclaration){
					Element extension = doc.createElement("xmi:Extension");
			        extension.setAttribute("extender","Select Architect");
			    	Element attributeExtension = doc.createElement("attributeExtension");
					Element attributePersist = (Element) attributeExtension.cloneNode(false);
					Element attributeStructure = (Element) attributeExtension.cloneNode(false);
					Element attributeFinal = (Element) attributeExtension.cloneNode(false);
					Element attributeStatic = (Element) attributeExtension.cloneNode(false);

					Element taggedValue = doc.createElement("taggedValue");
					taggedValue.setAttribute("tag", "Is Unique");
					taggedValue.setAttribute("value", "FALSE");
					Element derived = (Element) taggedValue.cloneNode(false);
					Element transientKey = (Element) taggedValue.cloneNode(false);
					Element volatileKey = (Element) taggedValue.cloneNode(false);
					Element finalKeyword = (Element) taggedValue.cloneNode(false);
					derived.setAttribute("tag", "Is Derived");
					derived.setAttribute("value", "FALSE");
					transientKey.setAttribute("tag", "Java Transient Keyword");
					transientKey.setAttribute("value", "FALSE");
					volatileKey.setAttribute("tag", "Java Volatile Keyword");
					volatileKey.setAttribute("value", "FALSE");
					finalKeyword.setAttribute("tag", "Java Final Keyword");
					finalKeyword.setAttribute("value", "FALSE");
					attributeExtension.appendChild(taggedValue);
					attributeFinal.appendChild(finalKeyword);
					attributePersist.appendChild(derived);
					attributeStructure.appendChild(volatileKey);
					attributeStatic.appendChild(transientKey);
					String variable = body.toString();
					Element ownedAttribute = doc.createElement("ownedAttribute");
					ownedAttribute.setAttribute("changeability", "changeable");
					ownedAttribute.setAttribute("ownerScope", "instance");
					
					ownedAttribute.setAttribute("xmi:type", "uml:Property");
					extension.appendChild(attributeExtension);
					extension.appendChild(attributePersist);
					extension.appendChild(attributeStructure);
					extension.appendChild(attributeFinal);
					extension.appendChild(attributeStatic);
					ownedAttribute.appendChild(extension);
					
					String[] lines = variable.split("\r\n|\r|\n");
					packagedElement.appendChild(ownedAttribute);
					String varableDeclaration = null;
				    char separator = ' ';
					for (String line:lines){
						if (line.contains("private") || line.contains("protected")){
							int index= line.indexOf(separator,1);
							String temp = extractString(line, separator,index);
							if (temp.contains("="))
								temp = extractString(temp,'=',0);
							String empty = "";
							String[]variables = temp.split(" ");
							int ctn = variables.length;
							int ct = 0;
							for (String tempVar:variables){
								if (tempVar.isEmpty() || tempVar.equals(""))
									ct = 1;
							}
							varableDeclaration = variables[ctn-1];
							String type = variables[ct];
							if (line.contains("private")){
								ownedAttribute.setAttribute("visibility", "private");
								ownedAttribute.setAttribute("name",varableDeclaration);
								ownedAttribute.setAttribute("type",type);
							}
							if (line.contains("protected")){
								ownedAttribute.setAttribute("visibility", "protected");
								ownedAttribute.setAttribute("name",varableDeclaration);
								ownedAttribute.setAttribute("type",type);
							}				
						}
					}
					
				}
			}
		}


	}
	/**
	 * createClasstructure
	 * This method creates an outer xmi classtructure from the Java Class being parsed
	 * @param extension Outer element
	 * @param doc The created xml document
	 */
	public void createClasstructure(Element extension, Document doc){
		Element attributeExtension = doc.createElement("attributeExtension");
		Element attributePersist = (Element) attributeExtension.cloneNode(false);
		Element attributeStructure = (Element) attributeExtension.cloneNode(false);
		Element attributeTemplate = (Element) attributeExtension.cloneNode(false);
		Element attributeFinal = (Element) attributeExtension.cloneNode(false);
		Element attributeStatic = (Element) attributeExtension.cloneNode(false);
		Element taggedValue = doc.createElement("taggedValue");
		taggedValue.setAttribute("tag", "Description");
		if (classDeclaration.getComment() != null)
			taggedValue.setAttribute("value", classDeclaration.getComment().getContent());
		Element persistent = (Element) taggedValue.cloneNode(false);
		Element structure = (Element) taggedValue.cloneNode(false);
		Element template = (Element) taggedValue.cloneNode(false);
		Element finalKeyword = (Element) taggedValue.cloneNode(false);
		Element staticKeyword = (Element) taggedValue.cloneNode(false);
		persistent.setAttribute("tag", "Is Persistent");
		persistent.setAttribute("value", "TRUE");
		structure.setAttribute("tag", "Is Structure");
		structure.setAttribute("value", "FALSE");
		template.setAttribute("tag", "Template Class");
		template.setAttribute("value", "FALSE");
		finalKeyword.setAttribute("tag", "Java Final Keyword");
		finalKeyword.setAttribute("value", "FALSE");
		staticKeyword.setAttribute("tag", "Java Static Keyword");
		staticKeyword.setAttribute("value", "FALSE");
		attributeExtension.appendChild(taggedValue);
		attributePersist.appendChild(persistent);
		attributeStructure.appendChild(structure);
		attributeTemplate.appendChild(template);
		attributeFinal.appendChild(finalKeyword);
		attributeStatic.appendChild(staticKeyword);
		extension.appendChild(attributeExtension);
		extension.appendChild(attributePersist);
		extension.appendChild(attributeStructure);
		extension.appendChild(attributeTemplate);
		extension.appendChild(attributeFinal);
		extension.appendChild(attributeStatic);
	}
	/**
	 * writetoFile
	 * Denne rutinen skriver en Java Stream til file.
	 * Denne stream inneholder parsed data fra en java klasse
	 * @param fileData
	 */
	public void writetoFile(Stream fileData){
	
		Path wiki_path = Paths.get("c:\\svnroot2\\nokc\\plugin\\", fileName);

        Charset charset = Charset.forName("UTF-8");
 //       String text = "\nfrom java2s.com!";
        try (BufferedWriter writer = Files.newBufferedWriter(wiki_path, charset, StandardOpenOption.CREATE_NEW)) {
           // writer.write(text);
            fileData.forEach(text -> writeToFile(writer,text));
            writer.close();
            fileData.close();
        } catch (IOException e) {
            System.err.println(e);
        }
//		createXML();
	
	}
    private void writeToFile(BufferedWriter fw, Object text) {
    	String tx = "";
    	if (text != null && text.toString() != null)
    		tx = text.toString();
        try {
            fw.write(String.format("%s%n", tx));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * createXML
     * This routine creates an xml file in xmi format from a chosen Java class
     */
    public void createXML(){
    	DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        FileOutputStream fileOut = null;
        char separator = '.';
        String newFilename = extractString(fileName, separator, 0);
        File file = new File("c:\\svnroot2\\nokc\\plugin\\"+ newFilename+".xml");
        try {
			fileOut = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
            icBuilder = icFactory.newDocumentBuilder();
            String classtype = "Class";
            if (interfaceFlag)
            	classtype = "Interface";
            Document doc = icBuilder.newDocument();
          
            Element mainRootElement = doc.createElementNS("http://schema.omg.org/spec/XMI/2.1", "xmi:XMI");
 
            mainRootElement.setAttribute("xmlns:xmi", "http://schema.omg.org/spec/XMI/2.1");
             mainRootElement.setAttribute("xmlns:uml", "http://schema.omg.org/spec/UML/2.0");
 
             mainRootElement.setAttribute("xmi:version", "2.1");  
//       
            doc.appendChild(mainRootElement);
            Element model = doc.createElement("uml:Model");
            mainRootElement.appendChild(model);
            model.setAttribute("visibility", "public");        
            model.setAttribute("name", "Biovigilans");
            model.setAttribute("xmi:id", "Modeller");
            Element packageElement = doc.createElement("PackagedElement");
          
            packageElement.setAttribute("isAbstract", "false");
            packageElement.setAttribute("visibility", "public");
            packageElement.setAttribute("name", newFilename);
            packageElement.setAttribute("xmi:type", "uml:"+classtype);
            Element extension = doc.createElement("xmi:Extension");
            extension.setAttribute("extender","Select Architect");
            packageElement.appendChild(extension);
            model.appendChild(packageElement);
            createClasstructure(extension, doc);
            createAttributes(packageElement, doc);
            createMethods(packageElement, doc);
            // output DOM XML to console 
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
            DOMSource source = new DOMSource(doc);
            StreamResult console = new StreamResult(fileOut);
            transformer.transform(source, console);
 
            System.out.println("\nXML DOM Created Successfully..");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
