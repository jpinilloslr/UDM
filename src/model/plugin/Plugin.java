package model.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import model.PreferencesManager;
import model.db.entities.Entity;

/**
 * <h1>Plugin</h1>
 * 
 * Utiliza reflection para acceder m�todos p�blicos conocidos
 * en la clase PluginWindow de un archivo jar externo a la 
 * aplicaci�n.
 */
public class Plugin {
	
	/** M�todo getText. Devuelve el texto del plugin. */
	private Method getTextMethod;

	/** M�todo load. Ejecuta el payload del plugin. */
	private Method loadMethod;

	/** Objeto instanciado del jar externo. */
	private Object plugin;

	/** Informaci�n del plugin. */
	private AbstractPluginInfo pluginInfo;

	/** M�todo setEntities. Env�a la lista de elementos seleccionadas. */
	private Method setEntitiesMethod;

	/**
	 * Constructor.
	 * 
	 * @param pi Informaci�n del plugin.
	 */
	public Plugin(AbstractPluginInfo pi) {
		pluginInfo = pi;
		loadFromInfo();
	}

	/**
	 * Carga el plugin a partir de su informaci�n.
	 * 
	 * @return true si el proceso es satisfactorio, 
	 * 			false si no.
	 */
	private boolean loadFromInfo() {
		boolean success = true;

		try {
			File file = new File(PreferencesManager.getInstance().getPluginsPath() 
					+ pluginInfo.getName());
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader cl = new URLClassLoader(urls);
			Class<?> cls = cl.loadClass("view.PluginWindow");

			plugin = cls.newInstance();
			Method[] methods = cls.getMethods();

			for (Method method : methods) {
				if (method.getName().contains("load")) {
					loadMethod = method;
				}
				if (method.getName().contains("getText")) {
					getTextMethod = method;
				}
				if (method.getName().contains("setEntities")) {
					setEntitiesMethod = method;
				}
			}

		} catch (Exception e) {
			success = false;
		}

		return success;
	}

	/**
	 * Devuelve la informaci�n del plugin.
	 * @return Informaci�n del plugin.
	 */
	public AbstractPluginInfo getPluginInfo() {
		return pluginInfo;
	}

	/**
	 * Devuelve el texto que debe representar al 
	 * plugin en su men�.
	 * 
	 * @return Texto.
	 */
	public String getText() {
		String text = "";
		try {
			if (null != getTextMethod) {
				text = (String) getTextMethod.invoke(plugin, (Object[]) null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * Ejecuta el payload del plugin.
	 */
	public void load() {
		try {
			if (null != loadMethod) {
				loadMethod.invoke(plugin, (Object[]) null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Define las entidades seleccionadas para un 
	 * plugin contextual.
	 * 
	 * @param batch Conjunto de entidades seleccionadas.
	 * @return true si la asignaci�n fue satisfactoria, 
	 * 			false si no.
	 */
	public boolean setEntities(ArrayList<Entity> batch) {
		boolean success = true;

		try {
			if (null != setEntitiesMethod) {
				setEntitiesMethod.invoke(plugin, new Object[] { batch });
			}
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}

		return success;
	}
}
