package view;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.PreferencesManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <h1>Internacionalización</h1>
 */
public class I18N {
	/** Variable singleton. */
	private static I18N singleton = null;

	public static final String ABOUT = "About";
	public static final String ACCEPT = "Accept";
	public static final String ACCESS = "Access";
	public static final String ACCESS_DENIED = "Access denied. Invalid user name or password.";
	public static final String ADMINISTRATION = "Administration";
	public static final String ALL = "All";
	public static final String AUTHENTICATION = "Authentication";
	public static final String AUTOINC = "Autoincrement";
	public static final String CANCEL = "Cancel";
	public static final String CASCADE = "Cascade";
	public static final String CLOSE = "Close";
	public static final String CLOSE_ALL = "Close all";
	public static final String CONNECTION_SETTINGS = "Connection settings";
	public static final String CONNECTION_SUCCESS = "Connection success.";
	public static final String CONNECTIVITY_ERROR = "Error connecting to database, check your connection settings.";
	public static final String CREDENTIALS = "Credentials";
	public static final String DATA = "Data";
	public static final String DATA_CONNECTION = "Data connection";
	public static final String DATABASE = "Database";
	public static final String DATE_AND_TIME_FORMATS = "Date and time formats";
	public static final String DATE_FORMAT = "Date format";
	public static final String DATETIME_FORMAT = "Date time format";
	public static final String DEEBUG = "Debug";
	public static final String DEFAULT_VALUE = "Default value";
	public static final String DELETE = "Delete";
	public static final String DELETE_ALL = "Delete all";
	public static final String DELETE_MULTIPLE_PROMPT = "Do you want to delete selected elements?";
	public static final String DELETE_PROMPT_TITLE = "Delete";
	public static final String DELETE_SINGLE_PROMPT = "Do you want to delete this element?";
	public static final String DELETING_DATA = "Deleting data...";
	public static final String DEPENDENCY = "Dependency";
	public static final String DEVELOP = "Develop";
	public static final String DISCONNECT = "Disconnect";
	public static final String DONE = "Done";
	public static final String EDIT = "Edit";
	public static final String EMPTY_FIELDS_ERROR = "Can not be empty fields in this form.";
	public static final String EMPTY_PASSWORD_ERROR = "Password is mandatory.";
	public static final String ENTITIES = "Entities";
	public static final String ENTITIES_LIST = "Entities list";
	public static final String ENTITY = "Entity";
	public static final String ERROR = "Error";
	public static final String EXIT = "Exit";
	public static final String EXPORT = "Export";
	public static final String FIELD = "Field";
	public static final String FIELD_NAME = "Field name";
	public static final String FILE = "File";
	public static final String FILTER = "Filter";
	public static final String FOREIGN_KEY = "Foreign key";
	public static final String FOREIGN_KEY_VIOLATION_ERROR = "Operation failure. Others entities depends on selected records.";
	public static final String HELP = "Help";
	public static final String HIDE_AUTOINC_FIELDS = "Hide autoincremental fields";
	public static final String HORIZONTAL_LAYOUT = "Horizontal layout";
	public static final String HOST = "Host";
	public static final String INSERT = "Insert";
	public static final String INTERFACE = "Interface";
	public static final String LANGUAGE = "Language";
	public static final String LIST = "List";
	public static final String LISTS = "Lists";
	public static final String LOADING = "Loading...";
	public static final String LOGIC_DELETE_MARK = "Logic delete mark";
	public static final String MANAGE = "Manage";
	public static final String MANAGER = "Manager";
	public static final String MANAGERS = "Managers";
	public static final String MANAGERS_LIST = "Managers list";
	public static final String MULTI_EDIT = "Multi edition";
	public static final String NAME = "Name";
	public static final String NEW = "New";
	public static final String NEW_ENTITY = "New entity";
	public static final String NEW_FIELD = "New field";
	public static final String NEW_PARAMETERIZER = "New parameterizer";
	public static final String NO = "No";
	public static final String NO_ENABLE_COMPONENTS = "There is no any common values in the selection.";
	public static final String NO_FIELDS_ERROR = "Can not create entity with no fields.";
	public static final String OPTIONS = "Options";
	public static final String ORDER_BY = "Order by";
	public static final String PARAMETER = "Parameter";
	public static final String PARAMETERIZER = "Parameterizer";
	public static final String PARAMETERIZERS = "Parameterizers";
	public static final String PARAMETERIZERS_LIST = "Parameterizers list";
	public static final String PASSWORD = "Password";
	public static final String PASSWORD_MATCH_ERROR = "Password match error, please check it again.";
	public static final String PASSWORD_TOO_SHORT_ERROR = "Password length must be no less than 8 characters.";
	public static final String PERMISSION = "Permission";
	public static final String PORT = "Port";
	public static final String PRIMARY_KEY = "Primary key";
	public static final String QUERY = "Query";
	public static final String READY = "Ready";
	public static final String RECORDS = "record(s)";
	public static final String REFERENCE_FIELD = "Reference field";
	public static final String REFRESH = "Refresh";
	public static final String REPEAT = "Repeat";
	public static final String REPLACE_FOREIGN_KEY_REFERENCE = "Replace foreign keys with reference";
	public static final String REPORT = "Report";
	public static final String REPORTS = "Reports";
	public static final String REQUIRED = "Required";
	public static final String REQUIRED_ACCESS = "Required access";
	public static final String RESTORE = "Restore";
	public static final String RESTORE_MULTIPLE_PROMPT = "Do you want to restore selected elements?";
	public static final String RESTORE_PROMPT_TITLE = "Restore";
	public static final String RESTORE_SINGLE_PROMPT = "Do you want to restore this element?";
	public static final String ROLE = "Role";
	public static final String ROLES = "Roles";
	public static final String SAVE = "Save";
	public static final String SAVE_CHANGES_PROMPT = "Do you want to save changes?";
	public static final String SEARCH = "Search";
	public static final String SHOULD_RESTART_MESSAGE = "Must restart program for changes to take effect.";
	public static final String SQL_QUERY = "SQL Query";
	public static final String SUBSTITUTE_FIELD = "Substitute field";
	public static final String SUBSTITUTE_NAME = "Substitute name";
	public static final String TABLE = "Table";
	public static final String TEST = "Test";
	public static final String THEME = "Theme";
	public static final String TIME_FORMAT = "Time format";
	public static final String TYPE = "Type";
	public static final String UDM = "UDM";
	public static final String UNIQUE_KEY_VIOLATION_ERROR = "Specified value can not be duplicated.";
	public static final String USER = "User";
	public static final String USER_LOGIC_DELETE = "Use logic delete";
	public static final String USERNAME = "User";
	public static final String USERS = "Users";
	public static final String VERTICAL_LAYOUT = "Vertical layout";
	public static final String VIEW = "View";
	public static final String VIEWS_LIST = "Views list";
	public static final String WINDOW = "Window";
	public static final String XML_FILE = "XML File";
	public static final String YES = "Yes";

	/** Mapa de valores registrados. */
	private HashMap<String, String> map;

	/**
	 * Constructor.
	 */
	private I18N() {
		map = new HashMap<String, String>();
		load();
	}

	/**
	 * Inicia la instancia de la variable singleton.
	 * @return I18N.
	 */
	public static I18N getInstance() {
		if (singleton == null) {
			synchronized (I18N.class) {
				if (singleton == null) {
					singleton = new I18N();
				}
			}
		}
		return singleton;
	}

	/**
	 * Carga la lista de cadenas de texto asociada
	 * al idioma actualmente configurado.
	 * 
	 * @return true si el proceso es satisfactorio, 
	 * 			false si no.
	 */
	private boolean load() {
		String value;
		String reference;
		map.clear();

		boolean success = true;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {
			File file = new File(PreferencesManager.getInstance().getLangPath()
					+ PreferencesManager.getInstance().getLanguage() + ".xml");
					
			if(file.exists()) {
				DocumentBuilder db = dbf.newDocumentBuilder();
				dom = db.parse(new FileInputStream(file));
	
				if (null != dom) {
					Element root = dom.getDocumentElement();
					NodeList nl = root.getElementsByTagName("string");
	
					if ((nl != null) && (nl.getLength() > 0)) {
						for (int i = 0; i < nl.getLength(); i++) {
							Element el = (org.w3c.dom.Element) nl.item(i);
	
							reference = el.getAttribute("reference").trim();
							value = el.getAttribute("value").trim();
							map.put(reference, value);
						}
					}
				}
			}
		} catch (Exception exc) {
			success = false;
			JOptionPane.showMessageDialog(null,
					"I18N Parse Error: " + exc.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		return success;
	}

	/**
	 * Devuelve una cadena de texto a partir de su referencia.
	 * 
	 * @param reference Referencia.
	 * @return Cadena de texto asociada si se encuentra alguna,
	 * 			de lo contrario devuelve la propia referencia.
	 */
	public String getString(String reference) {
		String str = map.get(reference);

		if (str == null) {
			str = reference;
		}

		return str;
	}
}
