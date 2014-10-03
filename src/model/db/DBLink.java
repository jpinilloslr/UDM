package model.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <h1>Enlace con la base de datos</h1>
 */
public class DBLink {
	
	/** Variable singleton. */
	private static DBLink singleton = null;

	/** Nombre del archivo que contiene la configuración de la conexión. */
	public static final String CONFIG_FILENAME = "connection.xml";

	/** Conexión a la base de datos. */
	private Connection connection;

	/** Driver de conexión a la base de datos. */
	private String driver;

	/** Contraseña para la conexión a la base de datos. */
	private String password;

	/** URL para la conexión a la base de datos. */
	private String url;

	/** Nombre de usuario para la conexión a la base de datos. */
	private String username;

	/**
	 * Constructor.
	 */
	private DBLink() {
		loadConfig();
		initialize();
	}

	/**
	 * Inicia la instancia de la variable singleton.
	 * @return Enlace a la base de datos.
	 */
	public static DBLink getInstance() {
		if (singleton == null) {
			synchronized (DBLink.class) {
				if (singleton == null) {
					singleton = new DBLink();
				}
			}
		}
		return singleton;
	}

	/**
	 * Devuelve la conexión con la base de datos.
	 * @return Conexión.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Devuelve el driver.
	 * @return Driver.
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Devuelve la contraseña de la base de datos.
	 * @return Contraseña.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Devuelve la URL.
	 * @return URL.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Devuelve el nombre de usuario de la base de datos.
	 * @return Nombre de usuario.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Inicia la conexión a la base de datos.
	 */
	public void initialize() {
		try {
			Class.forName(driver);
			connection = null;
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {

		}
	}

	/**
	 * Carga la configuración.
	 */
	public void loadConfig() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new FileInputStream(CONFIG_FILENAME));
		} catch (Exception exc) {
		}

		if (null != dom) {
			Element root = dom.getDocumentElement();
			driver = root.getAttribute("driver").trim();
			url = root.getAttribute("url").trim();
			username = root.getAttribute("username").trim();
			password = root.getAttribute("password").trim();
		}
	}

	/**
	 * Define el driver.
	 * @param driver Driver.
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * Define la contraseña a la base de datos.
	 * @param password Contraseña.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Define la URL.
	 * @param url URL.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Define el nombre de usuario a la base de datos.
	 * @param username Nombre de usuario.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
