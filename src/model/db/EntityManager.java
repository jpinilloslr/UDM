package model.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import model.db.entities.Entity;
import model.db.entities.EntityLoader;
import model.db.entities.Field;
import view.I18N;

/**
 * <h1>Administrador de entidades</h1>
 * 
 * Representa el punto de conexión entre la capa modelo de la aplicación y la
 * base de datos. Utiliza el patrón Strategy mediante un StatementsGenerator
 * para definir la forma en que genera las consultas de datos. Esta clase es
 * responsable de manipular la interacción de las entidades con la base de
 * datos, por lo que gestiona cualquier instancia de Entity de manera
 * dinámica independientemente de su estructura concreta.
 */
public class EntityManager {

	/** Generador de consultas. */
	private StatementsGenerator statGen;

	/**
	 * Constructor.
	 * @param sg Generador de consultas.
	 */
	public EntityManager(StatementsGenerator sg) {
		statGen = sg;
	}

	/**
	 * Elimina una entidad de su tabla correspondiente en la base de datos.
	 * 
	 * @param entity Entidad.
	 * @return Devuelve true si la operación es satisfactoria, false si no.
	 */
	public boolean delete(Entity entity) {
		boolean success = false;

		try {
			PreparedStatement ps = statGen.getDeleteStatement(entity, entity.useLogicDelete());
			ps.execute();
			success = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return success;
	}

	/**
	 * Devuelve una entidad de la base de datos a partir de los campos definidos
	 * como referencia para búsqueda (searchables).
	 * 
	 * @param entity Entidad.
	 * @return Entidad obtenida o null si no se ha encontrado ninguna
	 *         coincidencia.
	 */
	public Entity get(Entity entity) {
		Entity result = null;

		try {
			PreparedStatement ps = statGen.getSelectStatement(entity);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Iterator<Field> iter = entity.getFields().iterator();
				Field current;

				while (iter.hasNext()) {
					current = iter.next();
					current.setValue(rs.getObject(current.getName()));
				}
				result = entity;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return result;
	}

	/**
	 * Devuelve una lista de entidades pertenecientes a una tabla de la base de
	 * datos.
	 * 
	 * @param tablename Nombre de la tabla.
	 * @param orderBy Elemento de ordenamiento de la consulta si se necesita, de lo
	 *            contrario puede quedar en blanco.
	 * @param creator Creador de entidad.
	 * @return Lista de entidades obtenidas de la tabla especificada.
	 */
	public LinkedList<Entity> get(String tablename, String orderBy,
			EntityCreator creator) {
		LinkedList<Entity> list = new LinkedList<Entity>();

		try {
			PreparedStatement ps = statGen.getSelectStatement(tablename, orderBy);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Entity mun = creator.create();
				Iterator<Field> iter = mun.getFields().iterator();
				Field current;

				while (iter.hasNext()) {
					current = iter.next();
					current.setValue(rs.getObject(current.getName()));
				}

				list.add(mun);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return list;
	}

	/**
	 * Devuelve una lista de entidades pertenecientes a una tabla de la base de
	 * datos.
	 * 
	 * @param tablename Nombre de la tabla.
	 * @param orderBy Elemento de ordenamiento de la consulta si se necesita, de lo
	 *            contrario puede quedar en blanco.
	 * @param entityXML Archivo XML de descripción de la entidad.
	 * @return Lista de entidades obtenidas de la tabla especificada.
	 */
	public LinkedList<Entity> get(String tablename, String orderBy,
			String entityXML) {
		LinkedList<Entity> list = new LinkedList<Entity>();

		try {
			PreparedStatement ps = statGen.getSelectStatement(tablename, orderBy);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Entity mun = EntityLoader.getInstance().load(entityXML);
				Iterator<Field> iter = mun.getFields().iterator();
				Field current;

				while (iter.hasNext()) {
					current = iter.next();
					current.setValue(rs.getObject(current.getName()));
				}

				list.add(mun);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return list;
	}

	/**
	 * Inserta una entidad en su tabla correspondiente de la base de datos.
	 * 
	 * @param entity Entidad.
	 * @param ignoreAutoinc Ignorar campos de autoincremento.
	 * @return Devuelve true si la operación es satisfactoria, false si no.
	 */
	public boolean insert(Entity entity, boolean ignoreAutoinc) {
		boolean success = false;

		try {
			PreparedStatement ps = statGen.getInsertStatement(entity, ignoreAutoinc);
			ps.execute();
			success = true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
		return success;
	}

	/**
	 * Inserta una entidad ignorando sus campos autoincrementables y obtiene los
	 * valores autogenerados.
	 * 
	 * @param entity Entidad.
	 * @return Entidad o null si ocurre algún error.
	 */
	public Entity insertAndRetrieve(Entity entity) {
		Entity result = null;

		if (insert(entity, true)) {
			for (int i = 0; i < entity.getFields().size(); i++) {
				if (!entity.getFields().get(i).isPrimaryKey()) {
					entity.getFields().get(i).setSearchable(true);
				}
			}

			result = searchLast(entity);
		}

		return result;
	}

	/**
	 * Ejecuta una consulta a partir de una sentencia SQL. Esta función debería
	 * usarse lo menos posible para no saltar una de las capas de abstracción
	 * del modelo, en cualquier caso es preferible a realizar una consulta
	 * directa desde un punto arbitrario de la aplicación.
	 * 
	 * @param sql Sentencia SQL.
	 * @return Resultado de la consulta.
	 */
	public ResultSet rawExecute(String sql) {
		ResultSet rs = null;

		try {
			rs = DBLink.getInstance().getConnection().createStatement()
					.executeQuery(sql);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return rs;
	}

	/**
	 * Ejecuta una consulta de actualización. 
	 * @param sql Sentencia SQL.
	 */
	public void rawUpdate(String sql) {

		try {
			DBLink.getInstance().getConnection().createStatement()
					.executeUpdate(sql);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Devuelve las coincidencias de una entidad de la base de datos a partir de
	 * sus campos definidos como referencia para búsqueda (searchables).
	 * 
	 * @param entity Entidad
	 * @param creator Creador de entidad.
	 * @return Lista de entidades.
	 */
	public LinkedList<Entity> search(Entity entity, EntityCreator creator) {
		LinkedList<Entity> list = new LinkedList<Entity>();

		try {
			PreparedStatement ps = statGen.getSearchStatement(entity);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Entity mun = creator.create();
				Iterator<Field> iter = mun.getFields().iterator();
				Field current;

				while (iter.hasNext()) {
					current = iter.next();
					current.setValue(rs.getObject(current.getName()));
				}

				list.add(mun);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return list;
	}

	/**
	 * Devuelve las coincidencias de una entidad de la base de datos a partir de
	 * sus campos definidos como referencia para búsqueda (searchables).
	 * 
	 * @param entity Entidad
	 * @param entityXML Archivo XML que describe a la entidad.
	 * @return Lista de entidades.
	 */
	public LinkedList<Entity> search(Entity entity, String entityXML) {
		LinkedList<Entity> list = new LinkedList<Entity>();

		try {
			PreparedStatement ps = statGen.getSearchStatement(entity);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Entity mun = EntityLoader.getInstance().load(entityXML);
				Iterator<Field> iter = mun.getFields().iterator();
				Field current;

				while (iter.hasNext()) {
					current = iter.next();
					current.setValue(rs.getObject(current.getName()));
				}

				list.add(mun);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return list;
	}

	/**
	 * Devuelve la última coincidencia de una entidad de la base de datos a
	 * partir de los campos definidos como referencia para búsqueda
	 * (searchables).
	 * 
	 * @param entity Entidad.
	 * @return Entidad obtenida o null si no se ha encontrado ninguna
	 *         coincidencia.
	 */
	public Entity searchLast(Entity entity) {
		Entity result = null;

		try {
			PreparedStatement ps = statGen.getSearchStatement(entity);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Iterator<Field> iter = entity.getFields().iterator();
				Field current;

				while (iter.hasNext()) {
					current = iter.next();
					current.setValue(rs.getObject(current.getName()));
				}

				result = entity;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return result;
	}

	/**
	 * Actualiza una entidad en su tabla correspondiente de la base de datos.
	 * 
	 * @param entity Entidad.
	 * @return Devuelve true si la operación es satisfactoria, false si no.
	 */
	public boolean update(Entity entity) {
		boolean success = false;

		try {
			PreparedStatement ps = statGen.getUpdateStatement(entity);
			ps.execute();
			success = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, MessageManager
					.getFriendlyMessage(e.getMessage()), I18N.getInstance()
					.getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}

		return success;
	}
}
