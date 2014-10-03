package model;

import java.util.List;

/**
 * Describe una lista de elementos
 * exportables.
 *
 * @param <E> Tipo de dato de la lista.
 */
public interface ExportableConfig<E> {
	
	/**
	 * Exporta la lista.
	 */
	public void export();
	
	/**
	 * Devuelve la lista de elementos.
	 * @return Lista.
	 */
	public List<E> getExportableElements();
}
