package model;

import java.io.File;
import java.io.PrintWriter;

import model.managers.ManagerLoader;
import model.plugin.PluginManager;
import model.views.ViewLoader;

/**
 * Chequea la estructura de directorios y la presencia de 
 * los archivos necesarios del sistema.  
 */
public class ConfigFilesIntegrityChecker {

	/**
	 * Chequea todos los archivos. Si no existe
	 * alguno lo crea con el contenido por defecto. 
	 */
	private void checkAllFiles() {
		PreferencesManager pm = PreferencesManager.getInstance();
		checkFile(pm.getDataPath() + ManagerLoader.FILE_NAME, "<managersList>\r\n</managersList>");
		checkFile(pm.getDataPath() + ViewLoader.FILENAME, "<viewsList>\r\n</viewsList>");
		checkFile(PluginManager.FILENAME, "<pluginsList>\r\n</pluginsList>");
	}

	/**
	 * Chequea todos los caminos.
	 */
	private void checkAllPaths() {
		PreferencesManager pm = PreferencesManager.getInstance();

		checkPath(pm.getEditorsPath());
		checkPath(pm.getEntitesPath());
		checkPath(pm.getParamsSelectorPath());
		checkPath(pm.getPluginsPath());
		checkPath(pm.getLangPath());
	}

	/**
	 * Verifica que un archivo exista, de no existir se crea.
	 * 
	 * @param name Nombre del archivo.
	 * @param text Contenido con que debe crearse si no existe.
	 */
	private void checkFile(String name, String text) {
		File file = new File(name);

		try {
			if (!file.exists()) {
				file.createNewFile();
				PrintWriter pw = new PrintWriter(file, "UTF-8");
				pw.print(text);
				pw.close();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Verifica que un camino exista, de no 
	 * existir se crea.
	 * 
	 * @param path Camino.
	 */
	private void checkPath(String path) {
		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * Chequea que existan todos los caminos y archivos 
	 * necesarios para el funcionamiento del sistema, de no 
	 * existir los crea.
	 */
	public void checkAll() {
		checkAllPaths();
		checkAllFiles();
	}
}
