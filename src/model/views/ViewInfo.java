package model.views;

/**
 * <h1>Información de una vista</h1>
 */
public class ViewInfo {
	
	/** Nombre que se muestra al usuario. */
	private String name;

	/** Nombre de archivo del parametrizador. */
	private String parameterizer;

	/** Nombre de la vista en la base de datos. */
	private String viewName;

	/**
	 * Constructor.
	 */
	public ViewInfo() {
	}

	/**
	 * Constructor.
	 * 
	 * @param viewName Nombre de la vista en la base de datos.
	 * @param name Nombre que se muestra al usuario o que se 
	 * 				pasa al motor i18n.
	 * @param parameterizer  Nombre de archivo del parametrizador. 
	 * 				Puede estar vacío si la vista no necesita 
	 * 				parametrización.
	 */
	public ViewInfo(String viewName, String name, String parameterizer) {
		super();
		this.viewName = viewName;
		this.name = name;
		this.parameterizer = parameterizer;
	}

	/**
	 * Devuelve el nombre que se muestra al usuario o 
	 * que se pasa al motor i18n.
	 * 
	 * @return Nombre que se muestra al usuario.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuelve el nombre de archivo del parametrizador. 
	 * Puede estar vacío si la vista no necesita parametrización.
	 * 
	 * @return Nombre de archivo del parametrizador.
	 */
	public String getParameterizer() {
		return parameterizer;
	}

	/**
	 * Devuelve el nombre de acceso requerido para la vista.
	 * @return Nombre de acceso requerido.
	 */
	public String getRequiredAccess() {
		return "view." + getViewName();
	}

	/**
	 * Devuelve el nombre de la vista en la base de datos.
	 * @return Nombre de la vista.
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * Define el nombre que se muestra al usuario o que se 
	 * pasa al motor i18n.
	 * 
	 * @param name Nombre que se muestra al usuario.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Define el nombre de archivo del parametrizador. 
	 * Puede estar vacío si la vista no necesita parametrización.
	 * 
	 * @param parameterizer Nombre de archivo del parametrizador.
	 */
	public void setParameterizer(String parameterizer) {
		this.parameterizer = parameterizer;
	}

	/**
	 * Define el nombre de la vista en la base de datos.
	 *  
	 * @param name Nombre de la vista en la base de datos.
	 */
	public void setViewName(String name) {
		this.viewName = name;
	}

	/**
	 * Indica si la vista está parametrizada.
	 * @return true si usa un parametrizador, false si no.
	 */
	public boolean useParameterizer() {
		return ((parameterizer != null) && (parameterizer.length() > 0));
	}
}
