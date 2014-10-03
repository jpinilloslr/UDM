package model.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.db.entities.Entity;

/**
 * <h1>Generador de consultas</h1>
 * 
 * Es la interface que describe el comportamiento a implementar por todos los
 * generadores de consulta de la aplicación. Las clases que usen esta interface
 * deben implementar el comportamiento relativo a la generación de consultas
 * para la inserción, edición, eliminación, listado y búsqueda de cualquier
 * entidad de manera dinámica independientemente de su estructura concreta.
 */
public interface StatementsGenerator {

	/**
	 * Devuelve un PreparedStatement para la eliminación de una 
	 * entidad de su tabla correspondiente.
	 * 
	 * @param entity Entidad.
	 * @param logicDelete Usar borrado lógico.
	 * @return PreparedStatement.
	 * @throws SQLException  Excepción SQL.
	 */
	public abstract PreparedStatement getDeleteStatement(Entity entity,
			boolean logicDelete) throws SQLException;

	/**
	 * Devuelve un PreparedStatement para la inserción de una 
	 * entidad en su tabla correspondiente.
	 * 
	 * @param entity Entidad.
	 * @param ignoreAutoinc Ignorar campos autoincrementables.
	 * @return PreparedStatement.
	 * @throws SQLException Excepción SQL.
	 */
	public abstract PreparedStatement getInsertStatement(Entity entity,
			boolean ignoreAutoinc) throws SQLException;

	/**
	 * Devuelve un PreparedStatement para la búsqueda de una 
	 * entidad en su tabla correspondiente.
	 * 
	 * @param entity Entidad.
	 * @return PreparedStatement.
	 * @throws SQLException Excepción SQL.
	 */
	public abstract PreparedStatement getSearchStatement(Entity entity)
			throws SQLException;

	/**
	 * Devuelve un PreparedStatement para la selección de una 
	 * entidad en su tabla correspondiente.
	 * 
	 * @param entity Entidad.
	 * @return PreparedStatement.
	 * @throws SQLException Excepción SQL.
	 */
	public abstract PreparedStatement getSelectStatement(Entity entity)
			throws SQLException;

	/**
	 * Devuelve un PreparedStatement para la selección de una 
	 * entidad en su tabla correspondiente.
	 * 
	 * @param tableName Nombre de la tabla.
	 * @param orderBy Elemento de ordenamiento de la consulta si se 
	 * 					necesita, de lo contrario puede quedar en blanco.
	 * @return PreparedStatement.
	 * @throws SQLException Excepción SQL.
	 */
	public abstract PreparedStatement getSelectStatement(String tableName,
			String orderBy) throws SQLException;

	/**
	 * Devuelve un PreparedStatement para la actualización de una 
	 * entidad en su tabla correspondiente.
	 * 
	 * @param entity Entidad.
	 * @return PreparedStatement.
	 * @throws SQLException  Excepción SQL.
	 */
	public abstract PreparedStatement getUpdateStatement(Entity entity)
			throws SQLException;

}