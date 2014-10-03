package view.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.ExportableConfig;
import model.PreferencesManager;
import model.db.entities.FieldType;
import model.views.Parameter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import view.I18N;

/**
 *  <h1>Configuraci�n del di�logo de parametrizaci�n de vistas</h1>
 *  
 *  Almacena la configuraci�n de parametrizaci�n luego de ser cargada
 *  desde un archivo XML. 
 */
public class ParameterizeDialogConfig implements ExportableConfig<Parameter> {
	
	/** Lista de par�metros */
	private ArrayList<Parameter> params;
	
	/** Archivo de configuraci�n */
	private String filename;
	
	/**
	 * Constructor.
	 */
	public ParameterizeDialogConfig() {
		params = new ArrayList<Parameter>();;
	}
	
	/**
	 * Constructor.
	 * @param parameterizerFile Archivo del parametrizador.
	 */
	public ParameterizeDialogConfig(String parameterizerFile) {
		this.setFilename(parameterizerFile);
		params = new ArrayList<Parameter>();
		load(parameterizerFile);
	}
	
	/**
	 * Carga la configuraci�n desde el archivo especificado.
	 * 
	 * @param parameterizerFile Archivo XML con la descripci�n 
	 * 							de los par�metros a usar.
	 */
	private void load(String parameterizerFile) {
		String filename = PreferencesManager.getInstance().getParamsSelectorPath() + parameterizerFile;
		File file = new File(filename);

		if(file.exists()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();	
			Document dom = null;

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				dom = db.parse(new FileInputStream(filename));		

				if(null != dom) {
					Element root = dom.getDocumentElement();					

					NodeList nl = root.getElementsByTagName("parameter");

					if(nl != null && nl.getLength() > 0) {
						for(int i = 0 ; i < nl.getLength();i++) {
							Element el = (org.w3c.dom.Element)nl.item(i);
							String compName = el.getAttribute("name");
							String type = el.getAttribute("type");
							String depCompName = el.getAttribute("dependency");
							String query = el.getTextContent().trim();
							
							FieldType ft = FieldType.FT_STRING;
							if(type.equalsIgnoreCase("integer")) ft = FieldType.FT_INT;
							if(type.equalsIgnoreCase("boolean")) ft = FieldType.FT_BOOLEAN;
							
							params.add(new Parameter(compName, depCompName, ft, query));							
						}
					}
				}
			}catch(Exception exc) {
				JOptionPane.showMessageDialog(null, "ParameterizeDialogConfig Error: " + exc.getMessage(), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Exporta un par�metro a formato XML.
	 * 
	 * @param data Contenido XML.
	 * @param param Par�metro.
	 * @return Devuelve el contenido XML con la informaci�n
	 * 			agregada del nuevo par�metro.
	 */
	private String exportParam(String data, Parameter param) {
		String type = "";
		
		switch (param.getType()) {
		case FT_BOOLEAN:
			type = "boolean";
			break;
		case FT_INT:
			type = "integer";
			break;
		default:
			type = "string";
			break;
		}
		
		data += "\t<parameter name=\"" + param.getName() + "\" " +
				"dependency=\"" + param.getDependency() + "\" " +
				"type=\"" + type + "\">\r\n\t\t" + param.getQuery() + "\r\n\t</parameter>\r\n";
		return data;
	}
	
	/**
	 * Devuelve los par�metros.
	 * @return Par�metros.
	 */
	public ArrayList<Parameter> getParams() {
		return params;
	}

	/**
	 * Define los par�metros.
	 * @param params Par�metros.
	 */
	public void setParams(ArrayList<Parameter> params) {
		this.params = params;
	}
	
	/**
	 * Exporta la configuraci�n al archivo
	 * XML.
	 */
	public void exportToXml() {
		String data = " <parametersSelector>\r\n";
		
		Iterator<Parameter> iter = getParams().iterator();
		Parameter current;
		
		while(iter.hasNext()) {
			current = iter.next();
			data = exportParam(data, current);
		}
		
		data += " </parametersSelector>";
						
		try {
			File file = new File(PreferencesManager.getInstance().getParamsSelectorPath() + filename);
			
			if(!file.exists())
				file.createNewFile();
			
			PrintWriter pw = new PrintWriter(file, "UTF-8");
			pw.print(data);
			pw.close();
		} catch (Exception e) {}
	}
		
	/**
	 * Obtiene el nombre de archivo utilizado 
	 * para guardar/leer la configuraci�n.
	 * 
	 * @return Nombre de archivo.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Define el nombre de archivo utilizado 
	 * para guardar/leer la configuraci�n.
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}


	@Override
	public void export() {
		exportToXml();
	}

	@Override
	public List<Parameter> getExportableElements() {
		return params;
	}
}
