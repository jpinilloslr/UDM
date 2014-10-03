package model.db;

import view.I18N;

/**
 * <h1>Manipulador de mensajes</h1>
 * 
 * Convierte los mensajes de excepciones de la base de datos en mensajes m�s
 * amigables.
 */
public class MessageManager {
	
	/**
	 * Genera un mensaje personalizado a partir
	 * de un mensaje del sistema de base de datos.
	 * 
	 * @param message Mensaje del sistema de base de datos.
	 * @return Mensaje personalizado.
	 */
	public static String getFriendlyMessage(String message) {
		String fMsg = "";

		if (null != message) {
			fMsg = message;

			if (message.contains("ERROR: update o delete")
					&& message.contains("viola la llave for�nea")) {
				fMsg = I18N.getInstance().getString(
						I18N.FOREIGN_KEY_VIOLATION_ERROR);
			} else if (message.contains("ERROR: llave duplicada")
					&& message.contains("restricci�n de unicidad")) {
				fMsg = I18N.getInstance().getString(
						I18N.UNIQUE_KEY_VIOLATION_ERROR);
			}
		}
		return fMsg;
	}
}
