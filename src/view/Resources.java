package view;

import java.awt.Color;

/**
 * <h1>Recursos de la aplicación</h1>
 */
public class Resources {	
	
	/**
	 * <h1>Nombre de accesos requeridos</h1>
	 */
	public class RequiredAccess {
		public static final String ADMIN_ROLE = "admin.udm_role";
		public static final String ADMIN_USER = "admin.udm_user";
		public static final String ANY_ADMIN = "admin.*";
		public static final String ANY_DEVELOPER = "developer.*";
		public static final String ANY_TABLE = "table.*";
		public static final String ANY_VIEW = "view.*";		
		public static final String DEVELOPER_BUILD = "developer.build";		
		public static final String DEVELOPER_DEBUG = "developer.debug";		
	}		
	
	/**
	 * <h1>Estilos y colores</h1>
	 */
	public static class Style {
		public final static Color SELECTION_BACKGROUND = new Color(51, 153, 255);
		public final static Color SELECTION_FOREGROUND = new Color(255, 255, 255);
		public final static Color TABLE_GRID = new Color(212, 212, 212);
	}
}
