package model;

import java.io.FileInputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import view.ThemeManager;

/**
 * <h1>Gestor de preferencias</h1>
 * 
 * Gestiona globalmente las preferencias del sistema.
 */
public class PreferencesManager {
	
	/** Variabla singleton */
	private static PreferencesManager singleton = null;
	
	/** Archivo de preferencias. */
	public static final String PREFERENCES_FILENAME = "preferences.xml";

	/** Camino a la carpeta de datos. */
	private String dataPath;
	
	/** Camino a la carpeta de editores. */
	private String editorsPath;
	
	/** Camino a la carpeta de entidades. */
	private String entitesPath;
	
	/** Ocultar campos autoincrementables de los gestores. */
	private boolean hideAutoincrementalFields;
	
	/** Camino a la carpeta de idiomas. */
	private String langPath;
	
	/** Idioma actual. */
	private String language;
	
	/** Nombre de usuario del último login satisfactorio. */
	private String lastLoggedUsername;
	
	/** Camino a la carpeta de parametrizadores. */
	private String paramsSelectorPath;
	
	/** Camino a la carpeta de plugins. */
	private String pluginsPath;
	
	/** Sustituir valores de llaves foráneas por sus referencias en los gestores. */
	private boolean replaceForeignKeyValues;
	
	/** Nombre del tema actual. */
	private String themeName;
	
	/** Formato de fecha y hora. */
	private String dateTimeFormat;
	
	/** Formato de fecha. */
	private String dateFormat;
	
	/** Formato de hora. */
	private String timeFormat;

	/**
	 * Constructor.
	 */
	private PreferencesManager() {
		loadPreferences();
	}

	/**
	 * Inicia la instancia de la variable singleton.
	 * @return Gestor de preferencias.
	 */
	public static PreferencesManager getInstance() {
		if (singleton == null) {
			synchronized (PreferencesManager.class) {
				if (singleton == null) {
					singleton = new PreferencesManager();
				}
			}
		}
		return singleton;
	}

	/**
	 * Devuelve el camino a la carpeta de datos.
	 * @return Camino a la carpeta de datos.
	 */
	public String getDataPath() {
		return dataPath;
	}

	/**
	 * Devuelve el camino a la carpeta de editores.
	 * @return Camino a la carpeta de editores.
	 */
	public String getEditorsPath() {
		return dataPath + editorsPath;
	}

	/**
	 * Devuelve el camino a la carpeta de entidades.
	 * @return Camino a la carpeta de entidades.
	 */
	public String getEntitesPath() {
		return dataPath + entitesPath;
	}

	/**
	 * Devuelve el camino a la carpeta de idioma.
	 * @return Camino a la carpeta de idioma.
	 */
	public String getLangPath() {
		return langPath;
	}

	/**
	 * Devuelve el idioma actual. Por defecto es inglés.
	 * @return Idioma.
	 */
	public String getLanguage() {
		if ((language == null) || (language.length() == 0)) {
			language = "English";
		}

		return language;
	}

	/**
	 * Devuelve el nombre del usuario del último
	 * login satisfactorio.
	 * 
	 * @return Nombre de usuario.
	 */
	public String getLastLoggedUsername() {
		return lastLoggedUsername;
	}

	/**
	 * Devuelve el camino a la carpeta de parametrizadores.
	 * @return Camino a la carpeta de parametrizadores.
	 */
	public String getParamsSelectorPath() {
		return dataPath + paramsSelectorPath;
	}

	/**
	 * Devuelve el camino a la carpeta de plugins.
	 * @return Camino a la carpeta de plugins.
	 */
	public String getPluginsPath() {
		return pluginsPath;
	}

	/**
	 * Devuelve el tema actual.
	 * @return Nombre del tema.
	 */
	public String getThemeName() {
		if ((themeName == null) || (themeName.length() == 0)) {
			themeName = ThemeManager.DEFAULT_THEME;
		}

		return themeName;
	}

	/**
	 * Indica si se deben ocultar los campos autoincrementables en los gestores.
	 * @return true si se deben ocultar, false si no.
	 */
	public boolean hideAutoincrementalFields() {
		return hideAutoincrementalFields;
	}

	/**
	 * Carga las preferencias del archivo de preferencias.
	 */
	public void loadPreferences() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new FileInputStream(PREFERENCES_FILENAME));
		} catch (Exception exc) {
		}

		if (null != dom) {
			Element root = dom.getDocumentElement();
			hideAutoincrementalFields = root
					.getAttribute("hideAutoincrementalFields").trim()
					.equalsIgnoreCase("true");
			replaceForeignKeyValues = root
					.getAttribute("replaceForeignKeyValues").trim()
					.equalsIgnoreCase("true");
			lastLoggedUsername = root.getAttribute("lastLoggedUsername").trim();
			language = root.getAttribute("language").trim();
			themeName = root.getAttribute("currentThemeName").trim();
			dateTimeFormat = root.getAttribute("dateTimeFormat").trim();
			dateFormat = root.getAttribute("dateFormat").trim();
			timeFormat = root.getAttribute("timeFormat").trim();
		}

		dataPath = "data/";
		editorsPath = "editors/";
		entitesPath = "entities/";
		paramsSelectorPath = "parameters_selector/";
		pluginsPath = "plugins/";
		langPath = "lang/";
		
		if(dateTimeFormat == null || dateTimeFormat.length() == 0)
			dateTimeFormat = "dd/MMMMM/yyyy h:mm a";
		
		if(dateFormat == null || dateFormat.length() == 0)
			dateFormat = "dd/MMMMM/yyyy";
		
		if(timeFormat == null || timeFormat.length() == 0)
			timeFormat = "h:mm a";
	}

	/**
	 * Indica si se deben sustituir los valores de las llaves foráneas de los
	 * gestores por sus referencias.
	 * 
	 * @return true si se deben sustituir, false si no.
	 */
	public boolean replaceForeignKeyValues() {
		return replaceForeignKeyValues;
	}

	/**
	 * Guarda el archivo de preferencias.
	 */
	public void savePreferences() {
		String params = "";
		params = "<preferences \r\n" + "replaceForeignKeyValues=\""
				+ String.valueOf(replaceForeignKeyValues) + "\"\r\n"
				+ "hideAutoincrementalFields=\""
				+ String.valueOf(hideAutoincrementalFields) + "\"\r\n"
				+ "currentThemeName=\"" + String.valueOf(themeName) + "\"\r\n"
				+ "dateTimeFormat=\"" + dateTimeFormat + "\"\r\n"
				+ "dateFormat=\"" + dateFormat + "\"\r\n"
				+ "timeFormat=\"" + timeFormat + "\"\r\n"
				+ "language=\"" + language + "\"\r\n" + "lastLoggedUsername=\""
				+ String.valueOf(lastLoggedUsername) + "\">\r\n"
				+ "</preferences>";

		try {
			PrintWriter pw = new PrintWriter(PREFERENCES_FILENAME, "UTF-8");
			pw.print(params);
			pw.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Define el camino a la carpeta de datos.
	 * @param dataPath Camino a la carpeta de datos.
	 */
	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	/**
	 * Define el camino a la carpeta de editores.
	 * @param editorsPath Camino a la carpeta de editores.
	 */
	public void setEditorsPath(String editorsPath) {
		this.editorsPath = editorsPath;
	}

	/**
	 * Define el camino a la carpeta de entidades.
	 * @param entitesPath Camino a la carpeta de entidades.
	 */
	public void setEntitesPath(String entitesPath) {
		this.entitesPath = entitesPath;
	}

	/**
	 * Define si se ocultarán los campos autoincrementables 
	 * en los gestores.
	 * 
	 * @param hideAutoincrementalFields Ocultar campos autoincrementables.
	 */
	public void setHideAutoincrementalFields(boolean hideAutoincrementalFields) {
		this.hideAutoincrementalFields = hideAutoincrementalFields;
	}

	/**
	 * Define el camino a la carpeta de idiomas.
	 * @param langPath Camino a la carpeta de idiomas.
	 */
	public void setLangPath(String langPath) {
		this.langPath = langPath;
	}

	/**
	 * Define el idioma que se cargará al inicio de la aplicación.
	 * @param language Idioma.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Define el nombre de usuario del último login satisfactorio.
	 * @param lastLoggedUsername Nombre de usuario.
	 */
	public void setLastLoggedUsername(String lastLoggedUsername) {
		this.lastLoggedUsername = lastLoggedUsername;
	}

	/**
	 * Define el camino a la carpeta de parametrizadores.
	 * @param paramsSelectorPath Camino a la carpeta de parametrizadores.
	 */
	public void setParamsSelectorPath(String paramsSelectorPath) {
		this.paramsSelectorPath = paramsSelectorPath;
	}

	/**
	 * Define el camino a la carpeta de plugins.
	 * @param pluginsPath Camino a la carpeta de plugins.
	 */
	public void setPluginsPath(String pluginsPath) {
		this.pluginsPath = pluginsPath;
	}

	/**
	 * Define si se sustituirán los valores de las llaves foráneas en los
	 * gestores.
	 * 
	 * @param replaceForeignKeyValues Sustituir.
	 */
	public void setReplaceForeignKeyValues(boolean replaceForeignKeyValues) {
		this.replaceForeignKeyValues = replaceForeignKeyValues;
	}

	/**
	 * Define el nombre del tema actual.
	 * @param themeName Nombre del tema.
	 */
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
}
