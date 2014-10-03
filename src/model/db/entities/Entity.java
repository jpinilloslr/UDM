package model.db.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.ExportableConfig;
import model.PreferencesManager;
import model.db.EntityManager;
import model.db.StaticStatementsGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import view.I18N;

/**
 * <h1>Entidad</h1> 
 * Representa una entidad de la base de datos.
 */
public class Entity implements ExportableConfig<Field> {
	
	/** Nombre del campo utilizado para ordenar la lista de elementos. */
	private String defaultOrderField;

	/** Lista de campos. */
	private List<Field> fields;

	/** Usa borrado lógico. */
	private boolean logicDelete;

	/** Nombre de la tabla a la que se asocia la entidad. */
	private String tableName;

	/** Archivo XML que describe a la entidad. */
	private String xmlFile;

	/**
	 * Constructor.
	 */
	public Entity() {
		fields = new ArrayList<Field>();
		logicDelete = false;
	}

	/**
	 * Constructor copia.
	 * @param clone Clon.
	 */
	public Entity(Entity clone) {
		clone(clone);
	}

	/**
	 * Exporta un campo.
	 * 
	 * @param data Datos XML.
	 * @param field Campo.
	 * @return Contenido XML con el nuevo nodo agregado.
	 */
	private String exportField(String data, Field field) {
		String type = "";
		String defaultValue = "";
		
		switch (field.getFieldType()) {
		case FT_BOOLEAN:
			type = "boolean";
			break;
		case FT_INT:
			type = "integer";
			break;
		case FT_DOUBLE:
			type = "double";			
			break;
		case FT_DATE:
			type = "date";	
			break;	
		case FT_TIME:
			type = "time";	
			break;
		case FT_DATETIME:
			type = "datetime";	
			break;
		default:
			type = "string";
			break;
		}
		
		if(field.getFieldType() != FieldType.FT_DATETIME && 
				field.getFieldType() != FieldType.FT_DATE &&
				field.getFieldType() != FieldType.FT_TIME)
			defaultValue = String.valueOf(field.getValue());
		else
			defaultValue = "0";

		data += "\t<field name=\"" + field.getName() + "\"\r\n"
				+ "\t\t\tdefaultValue=\"" + defaultValue + "\"\r\n"
				+ "\t\t\ttype=\"" + type + "\"\r\n"
				+ "\t\t\tprimaryKey=\"" + String.valueOf(field.isPrimaryKey()) + "\"\r\n"
				+ "\t\t\trequired=\"" + String.valueOf(field.isRequired()) + "\"\r\n"
				+ "\t\t\tautoinc=\"" + String.valueOf(field.isAutoincremental()) + "\"\r\n"
				+ "\t\t\tlogicDeleteMark=\"" + String.valueOf(field.isLogicDeleteMarker()) + "\">\r\n";

		if (field.getForeignKeyInfo() != null) {
			ForeignKeyInfo fkInfo = field.getForeignKeyInfo();

			data += "\t\t\t<fk_info entity=\"" + fkInfo.getEntityXML()
					+ "\"\r\n" + "\t\t\t\t\t\treferencedField=\""
					+ fkInfo.getReferencedField() + "\"\r\n"
					+ "\t\t\t\t\t\tsubstituteField=\""
					+ fkInfo.getSubstituteField() + "\">\r\n";
			data += "\t\t\t</fk_info>";
		}

		data += "\t</field>\r\n";
		return data;
	}

	/**
	 * Parsea un campo del archivo de metadatos.
	 * @param element Nodo que describe al campo.
	 */
	private void parseField(Element element) {
		Field field = new Field();
		field.setName(element.getAttribute("name").trim());

		String type = element.getAttribute("type").trim();

		if (type.equalsIgnoreCase("integer")) {
			field.setFieldType(FieldType.FT_INT);
		} else 
		if (type.equalsIgnoreCase("boolean")) {
			field.setFieldType(FieldType.FT_BOOLEAN);
		} else 
		if (type.equalsIgnoreCase("double")) {
			field.setFieldType(FieldType.FT_DOUBLE);
		} else 
		if (type.equalsIgnoreCase("date")) {
			field.setFieldType(FieldType.FT_DATE);
		} else
		if (type.equalsIgnoreCase("time")) {
			field.setFieldType(FieldType.FT_TIME);
		} else
		if (type.equalsIgnoreCase("datetime")) {
			field.setFieldType(FieldType.FT_DATETIME);
		} else {
			field.setFieldType(FieldType.FT_STRING);
		}

		String value = element.getAttribute("defaultValue").trim();

		switch (field.getFieldType()) {
		case FT_BOOLEAN:
			field.setValue(value.equalsIgnoreCase("true"));
			break;
		case FT_INT:
			field.setValue(Integer.valueOf(value));
			break;
		case FT_DOUBLE:
			field.setValue(Double.valueOf(value));
			break;		
		case FT_DATE:
			field.setValue(new Date(System.currentTimeMillis()));
			break;	
		case FT_TIME:
			field.setValue(new Time(System.currentTimeMillis()));
			break;
		case FT_DATETIME:
			field.setValue(new Timestamp(System.currentTimeMillis()));
			break;			
		default:
			field.setValue(value);
			break;
		}

		field.setPrimaryKey(element.getAttribute("primaryKey").trim().equalsIgnoreCase("true"));
		field.setAutoincremental(element.getAttribute("autoinc").trim().equalsIgnoreCase("true"));
		field.setLogicDeleteMarker(element.getAttribute("logicDeleteMark").trim().equalsIgnoreCase("true"));
		field.setRequired(element.getAttribute("required").trim().equalsIgnoreCase("true"));

		NodeList nl = element.getElementsByTagName("fk_info");

		if ((nl != null) && (nl.getLength() > 0)) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				parseFkInfo(field, el);
			}
		}

		fields.add(field);
	}

	/**
	 * Parsea la información que describe al campo 
	 * como llave foránea.
	 * 
	 * @param field Campo.
	 * @param element Elemento.
	 */
	private void parseFkInfo(Field field, Element element) {
		ForeignKeyInfo fkInfo = new ForeignKeyInfo();
		fkInfo.setEntityXML(element.getAttribute("entity"));
		fkInfo.setReferencedField(element.getAttribute("referencedField"));
		fkInfo.setSubstituteField(element.getAttribute("substituteField"));
		field.setForeignKeyInfo(fkInfo);
	}

	/**
	 * Vacía los campos que describen la entidad.
	 */
	public void clear() {
		Field current;
		Iterator<Field> iter = fields.iterator();

		while (iter.hasNext()) {
			current = iter.next();
			current.reset();
		}
	}

	/**
	 * Clonar.
	 * @param clone Clon.
	 */
	public void clone(Entity clone) {
		fields = new ArrayList<Field>();

		for (int i = 0; i < clone.getFields().size(); i++) {
			Field field = new Field();
			field.clone(clone.getFields().get(i));
			fields.add(field);
		}

		tableName = clone.getTableName();
		logicDelete = clone.useLogicDelete();
		defaultOrderField = clone.getDefaultOrderField();
		xmlFile = clone.getXmlFile();
	}

	@Override
	public void export() {
		exportToXml();
	}

	/**
	 * Exporta los datos de la entidad a un archivo en formato XML.
	 */
	public void exportToXml() {
		String data = "<entity tablename=\"" + getTableName()
				+ "\" defaultOrderField=\"" + getDefaultOrderField()
				+ "\" logicDelete=\"" + String.valueOf(logicDelete) + "\">\r\n";

		Iterator<Field> iter = fields.iterator();
		Field current;

		while (iter.hasNext()) {
			current = iter.next();
			data = exportField(data, current);
		}

		data += "</entity>";

		try {
			File file = new File(PreferencesManager.getInstance()
					.getEntitesPath() + xmlFile);

			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter pw = new PrintWriter(file, "UTF-8");
			pw.print(data);
			pw.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Devuelve el valor de un campo como un Boolean.
	 * 
	 * @param name Nombre del campo.
	 * @return El valor como un Boolean o null si no se 
	 * 			encuentra el campo con el nombre dado.
	 */
	public Boolean getAsBoolean(String name) {
		Field field = getByName(name);
		Object value = null;

		if (null != field) {
			value = field.getValue();
		}

		return (Boolean) value;
	}

	/**
	 * Devuelve el valor de un campo como un Integer.
	 * 
	 * @param name Nombre del campo.
	 * @return El valor como un Integer o null si no se 
	 * 			encuentra el campo con el nombre dado.
	 */
	public Integer getAsInteger(String name) {
		Field field = getByName(name);
		Object value = null;

		if (null != field) {
			value = field.getValue();
		}

		return (Integer) value;
	}

	/**
	 * Devuelve el valor de un campo como un String.
	 * 
	 * @param name Nombre del campo.
	 * @return El valor como un String o null si no se 
	 * 			encuentra el campo con el nombre dado.
	 */
	public String getAsString(String name) {
		Field field = getByName(name);
		Object value = null;

		if (null != field) {
			value = field.getValue();
		}

		return String.valueOf(value);
	}

	/**
	 * Devuelve un campo a partir de su nombre.
	 * 
	 * @param name Nombre del campo.
	 * @return El campo si se encuentra, null si no.
	 */
	public Field getByName(String name) {
		Field field = null;
		Field current;
		Iterator<Field> iter = fields.iterator();

		while ((null == field) && iter.hasNext()) {
			current = iter.next();

			if (current.getName().equals(name)) {
				field = current;
			}
		}

		return field;
	}

	/**
	 * Obtiene el campo por el que se debe ordenar la 
	 * lista de este elemento.
	 * 
	 * @return Nombre del campo.
	 */
	public String getDefaultOrderField() {
		String fieldName = "";

		if ((defaultOrderField == null) || (defaultOrderField.length() == 0)) {
			Iterator<Field> iter = fields.iterator();
			Field current;

			while (iter.hasNext() && (fieldName.length() == 0)) {
				current = iter.next();

				if (current.isAutoincremental()) {
					fieldName = current.getName();
				}
			}
		} else {
			fieldName = defaultOrderField;
		}

		return fieldName;
	}

	@Override
	public List<Field> getExportableElements() {
		return getFields();
	}

	/**
	 * Devuelve la lista de campos.
	 * @return Lista de campos.
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * Devuelve el campo que funciona como marcador de 
	 * borrado lógico.
	 * 
	 * @return El campo si existe alguno que funcione 
	 * 			como marcador de borrado lógico, de lo 
	 * 			contrario devuelve null.
	 */
	public Field getLogicDeleteMarkerField() {
		Field field = null;
		Iterator<Field> iter = fields.iterator();
		Field current;

		while ((field == null) && iter.hasNext()) {
			current = iter.next();

			if (current.isLogicDeleteMarker()) {
				field = current;
			}
		}

		return field;
	}

	/**
	 * Devuelve la entidad referida por un campo 
	 * llave foránea.
	 * 
	 * @param fieldName Nombre del campo llave foránea.
	 * @return Entidad referida.
	 */
	public Entity getReference(String fieldName) {
		Entity refEnt = null;
		Field field = getByName(fieldName);
		EntityManager manager = new EntityManager(
				new StaticStatementsGenerator());

		if ((null != field) && (null != field.getForeignKeyInfo())) {
			ForeignKeyInfo fkInfo = field.getForeignKeyInfo();
			refEnt = EntityLoader.getInstance().load(fkInfo.getEntityXML());
			refEnt.setField(fkInfo.getReferencedField(), field.getValue());
			refEnt = manager.get(refEnt);
		}

		return refEnt;
	}

	/**
	 * Devuelve el nombre de accesso requerido para acceder 
	 * a la tabla de gestión de esta entidad.
	 * 
	 * @return Nombre de acceso.
	 */
	public String getRequiredAccess() {
		String access = null;
		if (getTableName() != null) {
			access = "table." + getTableName();
		}
		return access;
	}

	/**
	 * Devuelve el nombre de la tabla asociada a esta entidad.
	 * @return Nombre de la tabla.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Obtiene el nombre del archivo XML.
	 * @return Nombre del archivo.
	 */
	public String getXmlFile() {
		String filename = "";
		if (xmlFile != null) {
			if (xmlFile.contains("/")) {
				filename = xmlFile.substring(xmlFile.lastIndexOf("/") + 1);
			} else if (xmlFile.contains("\\")) {
				filename = xmlFile.substring(xmlFile.lastIndexOf("\\") + 1);
			} else {
				filename = xmlFile;
			}
		}
		return filename;
	}

	/**
	 * Carga los metadatos de esta entidad desde un archivo XML.
	 * 
	 * @param filename Archivo.
	 * @return true si el proceso es satisfactorio, false si no.
	 */
	public boolean loadFromXML(String filename) {
		boolean success = true;
		xmlFile = filename;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new FileInputStream(filename));

			if (null != dom) {
				Element root = dom.getDocumentElement();
				tableName = root.getAttribute("tablename").trim();
				defaultOrderField = root.getAttribute("defaultOrderField")
						.trim();
				logicDelete = root.getAttribute("logicDelete").trim()
						.equalsIgnoreCase("true");

				NodeList nl = root.getElementsByTagName("field");

				if ((nl != null) && (nl.getLength() > 0)) {
					for (int i = 0; i < nl.getLength(); i++) {
						Element el = (org.w3c.dom.Element) nl.item(i);
						parseField(el);
					}
				}
			}
		} catch (Exception exc) {
			success = false;
			JOptionPane.showMessageDialog(null, "Entity parser error in "
					+ filename + ": " + exc.getMessage(), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return success;
	}

	/**
	 * Elimina todas las referencias a búsqueda en los 
	 * campos de la entidad.
	 */
	public void resetSearchables() {
		for (int i = 0; i < fields.size(); i++) {
			fields.get(i).setSearchable(false);
		}
	}

	/**
	 * Define el campo por el que se debe ordenar la lista de 
	 * este elemento.
	 * 
	 * @param defaultOrderField Campo.
	 */
	public void setDefaultOrderField(String defaultOrderField) {
		this.defaultOrderField = defaultOrderField;
	}

	/**
	 * Define el valor de un campo.
	 * 
	 * @param name Nombre del campo.
	 * @param value Valor que se desea asignar.
	 * @return Devuelve true si el proceso se realiza 
	 * 			satisfactoriamente, false si no.
	 */
	public boolean setField(String name, Object value) {
		boolean success = false;
		Field field = getByName(name);

		if (null != field) {
			success = true;
			field.setValue(value);
		}

		return success;
	}

	/**
	 * Define si la entidad usa borrado lógico.
	 * @param logicDelete Borrado lógico.
	 */
	public void setLogicDelete(boolean logicDelete) {
		this.logicDelete = logicDelete;
	}

	/**
	 * Define un campo como referencia de búsqueda. Esto
	 * es necesario si se quiere utilizar EntityManager para
	 * realizar búsquedas usando el contenido de uno o varios
	 * campos de una entidad como referencia.
	 * 
	 * @param fieldName Nombre del campo.
	 */
	public void setSearchable(String fieldName) {
		Field field = getByName(fieldName);

		if (null != field) {
			field.setSearchable(true);
		}
	}

	/**
	 * Define el nombre de la tabla asociada a esta entidad.
	 * @param tableName Nombre de la tabla.
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Define el archivo XML.
	 * @param xmlFile Archivo XML.
	 */
	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	@Override
	public String toString() {
		String text = "";
		Field field;
		for (int i = 0; i < fields.size(); i++) {
			field = fields.get(i);
			text += "[" + field.getName() + "=" + field.getValue().toString()
					+ "]";
		}

		return text;
	}

	/**
	 * Indica si la entidad usa borrado lógico.
	 * 
	 * @return true si usa borrado lógico, false si 
	 * 			usa borrado físico.
	 */
	public boolean useLogicDelete() {
		return logicDelete;
	}

	/**
	 * Agrega un campo a la entidad.
	 * 
	 * @param name Nombre del campo.
	 * @param value Valor inicial.
	 * @param fieldType Tipo del campo.
	 * @param primaryKey Es llave primaria.
	 * @param autoincremental Es autoincrementable.
	 */
	protected void addField(String name, Object value, FieldType fieldType,
			boolean primaryKey, boolean autoincremental) {
		fields.add(new Field(name, value, fieldType, primaryKey,
				autoincremental));
	}

	/**
	 * Agrega un campo a la entidad.
	 * 
	 * @param name Nombre del campo.
	 * @param value Valor inicial.
	 * @param fieldType Tipo del campo.
	 * @param primaryKey Es llave primaria.
	 * @param autoincremental Es autoincrementable.
	 * @param logicDeleteMarker Marca de borrado lógico.
	 */
	protected void addField(String name, Object value, FieldType fieldType,
			boolean primaryKey, boolean autoincremental, boolean logicDeleteMarker) {
		fields.add(new Field(name, value, fieldType, primaryKey,
				autoincremental, logicDeleteMarker));
	}

	/**
	 * Agrega un campo a la entidad.
	 * 
	 * @param name Nombre del campo.
	 * @param value Valor inicial.
	 * @param fieldType Tipo del campo.
	 * @param primaryKey Es llave primaria.
	 * @param autoincremental Es autoincrementable.
	 * @param foreignKeyInfo Información de la llave foránea.
	 */
	protected void addField(String name, Object value, FieldType fieldType,
			boolean primaryKey, boolean autoincremental,
			ForeignKeyInfo foreignKeyInfo) {
		fields.add(new Field(name, value, fieldType, primaryKey,
				autoincremental, foreignKeyInfo));
	}
}
