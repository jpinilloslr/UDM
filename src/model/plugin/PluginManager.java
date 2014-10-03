package model.plugin;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import view.I18N;

/**
 * <h1>Gestor de plugins</h1>
 */
public class PluginManager {

	/** Variable singleton. */
	private static PluginManager singleton = null;

	/** Archivo donde se describen los plugins registrados. */
	public static final String FILENAME = "plugins.xml";

	/** Lista de plugins. */
	private ArrayList<AbstractPluginInfo> pluginsInfo;

	/**
	 * Constructor.
	 */
	private PluginManager() {
		pluginsInfo = new ArrayList<AbstractPluginInfo>();
		loadPluginsInfo();
	}

	/**
	 * Inicia la instancia de la variable singleton.
	 * @return Gestor de plugins.
	 */
	public static PluginManager getInstance() {
		if (singleton == null) {
			synchronized (PluginManager.class) {
				if (singleton == null) {
					singleton = new PluginManager();
				}
			}
		}
		return singleton;
	}

	/**
	 * Carga la información de todos los plugins registrados.
	 * 
	 * @return true si el proceso es satisfactorio, 
	 * 			false si no.
	 */
	private boolean loadPluginsInfo() {
		boolean success = true;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new FileInputStream(FILENAME));

			if (null != dom) {
				Element root = dom.getDocumentElement();
				NodeList nl = root.getElementsByTagName("plugin");

				if ((nl != null) && (nl.getLength() > 0)) {
					for (int i = 0; i < nl.getLength(); i++) {
						Element el = (org.w3c.dom.Element) nl.item(i);
						parsePlugin(el);
					}
				}
			}
		} catch (Exception exc) {
			success = false;
			JOptionPane.showMessageDialog(
					null,
					"PluginManager parser error in " + FILENAME + ": "
							+ exc.getMessage(),
					I18N.getInstance().getString(I18N.ERROR),
					JOptionPane.ERROR_MESSAGE);
		}

		return success;
	}

	/**
	 * Lee la información de un plugin de un nodo del 
	 * archivo de configuración XML.
	 * 
	 * @param el Nodo XML.
	 */
	private void parsePlugin(Element el) {
		AbstractPluginInfo pluginInfo;
		String name = el.getAttribute("name");
		String type = el.getAttribute("type");
		boolean multiSelect = el.getAttribute("multiSelect").equals("true");

		if ((null != type) && type.equals("context")) {
			pluginInfo = new ContextPluginInfo();
			pluginInfo.setName(name);
			((ContextPluginInfo) pluginInfo).setWindowAccessName(el
					.getAttribute("window"));
			((ContextPluginInfo) pluginInfo).setMultiSelect(multiSelect);
		} else {
			pluginInfo = new MenuPluginInfo();
			pluginInfo.setName(name);
			((MenuPluginInfo) pluginInfo).setMenuName(el.getAttribute("parent"));

		}

		pluginsInfo.add(pluginInfo);
	}

	/**
	 * Devuelve la lista de plugins del menú principal 
	 * asociados a un menú a partir de su nombre.
	 * 
	 * @param menuName Nombre del menú.
	 * @return Lista de plugins del menú principal.
	 */
	public ArrayList<Plugin> getPluginsForMenu(String menuName) {
		ArrayList<Plugin> plugins = new ArrayList<Plugin>();
		Iterator<AbstractPluginInfo> iter = pluginsInfo.iterator();
		AbstractPluginInfo current;

		while (iter.hasNext()) {
			current = iter.next();

			if (current instanceof MenuPluginInfo) {
				String pluginMenuName = I18N.getInstance().getString(
						((MenuPluginInfo) current).getMenuName());
				
				if (pluginMenuName.equals(menuName)) {
					Plugin p = new Plugin(current);
					plugins.add(p);
				}
			}
		}

		return plugins;
	}

	/**
	 * Devuelve la lista de plugins contextuales 
	 * asociados a una ventana a partir de su nombre 
	 * de acceso asociado.
	 * 
	 * @param windowAccessName Nombre de acceso asociado a la ventana.
	 * @return Lista de plugins contextuales.
	 */
	public ArrayList<Plugin> getPluginsForWindow(String windowAccessName) {
		ArrayList<Plugin> plugins = new ArrayList<Plugin>();
		Iterator<AbstractPluginInfo> iter = pluginsInfo.iterator();
		AbstractPluginInfo current;

		while (iter.hasNext()) {
			current = iter.next();

			if (current instanceof ContextPluginInfo) {
				if (((ContextPluginInfo) current).getWindowAccessName().equals(windowAccessName)) {
					Plugin p = new Plugin(current);
					plugins.add(p);
				}
			}
		}

		return plugins;
	}
}
