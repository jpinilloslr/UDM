package model.plugin;

/**
 * <h1>Informaci�n de un plugin contextual</h1>
 */
public class ContextPluginInfo extends AbstractPluginInfo {
	
	/** Admite multiselecci�n. */
	private boolean multiSelect;

	/** Nombre de acceso de la ventana a la que se asocia. */
	private String windowAccessName;

	/**
	 * Devuelve el nombre de acceso de la ventana asociada.
	 * @return Nombre de acceso a la ventana.
	 */
	public String getWindowAccessName() {
		return windowAccessName;
	}

	/**
	 * Indica si permite multiselecci�n.
	 * @return Permite multiselecci�n.
	 */
	public boolean isMultiSelect() {
		return multiSelect;
	}

	/**
	 * Define si permitir� multiselecci�n.
	 * @param multiSelect Permitir� multiselecci�n.
	 */
	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	/**
	 * Define el nombre de acceso de la ventana a la 
	 * que se desea asociar el plugin.
	 * 
	 * @param windowAccessName Nombre de acceso a la ventana.
	 */
	public void setWindowAccessName(String windowAccessName) {
		this.windowAccessName = windowAccessName;
	}
}
