package model.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import model.db.entities.Entity;
import model.db.entities.Field;

/**
 * <h1>Generador de consultas estáticas</h1>
 * 
 * Genera consultas para el procesamiento de los objetos 
 * Entity en la base de datos.
 */
public class StaticStatementsGenerator implements StatementsGenerator {
	
	private void setParam(int index, Field field, PreparedStatement ps) throws SQLException {
		switch (field.getFieldType()) {
		case FT_BOOLEAN:
			ps.setBoolean(index, (Boolean) field.getValue());
			break;
		case FT_INT:
			ps.setInt(index, (Integer) field.getValue());
			break;
		case FT_DOUBLE:
			ps.setDouble(index, (Double) field.getValue());
			break;
		case FT_STRING:
			ps.setString(index, (String) field.getValue());
			break;
		case FT_DATE:
			ps.setDate(index, (Date) field.getValue());
			break;
		case FT_TIME:
			ps.setTime(index, (Time) field.getValue());
			break;
		case FT_DATETIME:
			ps.setTimestamp(index, (Timestamp) field.getValue());
			break;			
		}
	}

	/**
	 * Define los parámetros de campos no llave de un 
	 * objeto PreparedStatement.
	 * 
	 * @param fields Campos de la entidad.
	 * @param ps PreparedStatement.
	 * @throws SQLException Excepción SQL.
	 */
	private void setNPKParams(List<Field> fields, PreparedStatement ps)
			throws SQLException {
		int i = 1;
		boolean process;
		Iterator<Field> iter = fields.iterator();
		Field current;

		while (iter.hasNext()) {
			current = iter.next();
			process = false;

			if (!current.isPrimaryKey()) {
				process = true;
			} else {
				process = false;
			}

			if (process) {
				setParam(i, current, ps);
				i++;
			}
		}
	}

	/**
	 * Define los parámetros de un objeto PreparedStatement.
	 * 
	 * @param fields Campos de la entidad.
	 * @param ps PreparedStatement.
	 * @param ignoreAutoinc Ignorar los campos autoincrementables.
	 * @throws SQLException Excepción SQL.
	 */
	private void setParams(List<Field> fields, PreparedStatement ps,
			boolean ignoreAutoinc) throws SQLException {
		int i = 1;
		boolean process;
		Iterator<Field> iter = fields.iterator();
		Field current;

		while (iter.hasNext()) {
			current = iter.next();
			process = false;

			if (ignoreAutoinc) {
				if (!current.isAutoincremental()) {
					process = true;
				}
			} else {
				process = true;
			}

			if (process) {
				setParam(i, current, ps);
				i++;
			}
		}
	}

	/**
	 * Define los parámetros definidos como llave primaria en un 
	 * objeto PreparedStatement.
	 * 
	 * @param fields Campos de la entidad.
	 * @param ps PreparedStatement
	 * @param startIndex Índice inicial de los parámetros del objeto 
	 * 						PreparedStatement.
	 * @throws SQLException Excepción SQL.
	 */
	private void setPKParams(List<Field> fields, PreparedStatement ps,
			int startIndex) throws SQLException {
		int i = startIndex;
		Iterator<Field> iter = fields.iterator();
		Field current;

		while (iter.hasNext()) {
			current = iter.next();

			if (current.isPrimaryKey()) {
				setParam(i, current, ps);
				i++;
			}
		}
	}

	/**
	 * Define los parámetros definidos como referencias de búsqueda 
	 * en un objeto PreparedStatement.
	 * 
	 * @param fields  Campos de la entidad.
	 * @param ps PreparedStatement
	 * @throws SQLException Excepción SQL.
	 */
	private void setSearchableParams(List<Field> fields, PreparedStatement ps)
			throws SQLException {
		int i = 1;
		Iterator<Field> iter = fields.iterator();
		Field current;

		while (iter.hasNext()) {
			current = iter.next();

			if (current.isSearchable()) {
				setParam(i, current, ps);
				i++;
			}
		}
	}

	@Override
	public PreparedStatement getDeleteStatement(Entity entity,
			boolean logicDelete) throws SQLException {
		PreparedStatement ps = null;

		if (logicDelete) {
			entity.getLogicDeleteMarkerField().setValue(false);
			ps = getUpdateStatement(entity);
		} else {
			Field current;
			Iterator<Field> iter = entity.getFields().iterator();
			String statement = "DELETE FROM \"" + entity.getTableName()
					+ "\" WHERE ";

			while (iter.hasNext()) {
				current = iter.next();

				if (current.isPrimaryKey()) {
					statement += "\"" + current.getName() + "\"=? AND ";
				}
			}

			if (statement.endsWith("? AND ")) {
				statement = statement.substring(0, statement.length() - 5);
			}

			iter = entity.getFields().iterator();

			ps = DBLink.getInstance().getConnection()
					.prepareStatement(statement);
			setPKParams(entity.getFields(), ps, 1);
		}
		return ps;
	}

	@Override
	public PreparedStatement getInsertStatement(Entity entity,
			boolean ignoreAutoinc) throws SQLException {
		boolean process;
		Field current;
		Iterator<Field> iter = entity.getFields().iterator();
		String statement = "INSERT INTO \"" + entity.getTableName() + "\" (";
		String params = "(";

		while (iter.hasNext()) {
			current = iter.next();
			process = false;

			if (ignoreAutoinc) {
				if (!current.isAutoincremental()) {
					process = true;
				}
			} else {
				process = true;
			}

			if (process) {
				statement += "\"" + current.getName() + "\"";
				params += "?";
				statement += ", ";
				params += ", ";
			}
		}

		statement = statement.substring(0, statement.length() - 2) + ")";
		params = params.substring(0, params.length() - 2) + ")";

		statement += " VALUES " + params;
		PreparedStatement ps = null;

		ps = DBLink.getInstance().getConnection().prepareStatement(statement);
		setParams(entity.getFields(), ps, ignoreAutoinc);
		return ps;
	}

	@Override
	public PreparedStatement getSearchStatement(Entity entity)
			throws SQLException {
		Field current;
		Iterator<Field> iter = entity.getFields().iterator();
		String statement = "SELECT * FROM \"" + entity.getTableName()
				+ "\" WHERE ";

		while (iter.hasNext()) {
			current = iter.next();

			if (current.isSearchable()) {
				statement += "\"" + current.getName() + "\"=? AND ";
			}
		}

		if (statement.endsWith("? AND ")) {
			statement = statement.substring(0, statement.length() - 5);
		}

		PreparedStatement ps = null;
		iter = entity.getFields().iterator();

		ps = DBLink.getInstance().getConnection().prepareStatement(statement);
		setSearchableParams(entity.getFields(), ps);
		return ps;
	}

	@Override
	public PreparedStatement getSelectStatement(Entity entity)
			throws SQLException {
		Field current;
		Iterator<Field> iter = entity.getFields().iterator();
		String statement = "SELECT * FROM \"" + entity.getTableName()
				+ "\" WHERE ";

		while (iter.hasNext()) {
			current = iter.next();

			if (current.isPrimaryKey()) {
				statement += "\"" + current.getName() + "\"=? AND ";
			}
		}

		if (statement.endsWith("? AND ")) {
			statement = statement.substring(0, statement.length() - 5);
		}

		PreparedStatement ps = null;
		iter = entity.getFields().iterator();

		ps = DBLink.getInstance().getConnection().prepareStatement(statement);
		setPKParams(entity.getFields(), ps, 1);
		return ps;
	}

	@Override
	public PreparedStatement getSelectStatement(String tableName, String orderBy)
			throws SQLException {
		String statement = "SELECT * FROM \"" + tableName + "\"";

		if ((null != orderBy) && (orderBy.length() > 0)) {
			statement += " ORDER BY " + orderBy;
		}
		return DBLink.getInstance().getConnection().prepareStatement(statement);
	}

	@Override
	public PreparedStatement getUpdateStatement(Entity entity)
			throws SQLException {
		Field current;
		Iterator<Field> iter = entity.getFields().iterator();
		String statement = "UPDATE \"" + entity.getTableName() + "\" SET ";
		String where = "";
		int paramsCount = 0;

		while (iter.hasNext()) {
			current = iter.next();

			if (!current.isPrimaryKey()) {
				statement += "\"" + current.getName() + "\"=?, ";
				paramsCount++;
			} else {
				where += "\"" + current.getName() + "\"=? AND ";
			}
		}

		if (where.endsWith("? AND ")) {
			where = where.substring(0, where.length() - 5);
		}

		if (statement.endsWith(", ")) {
			statement = statement.substring(0, statement.length() - 2)
					+ " WHERE " + where;
		}

		PreparedStatement ps = null;
		iter = entity.getFields().iterator();

		ps = DBLink.getInstance().getConnection().prepareStatement(statement);
		setNPKParams(entity.getFields(), ps);
		setPKParams(entity.getFields(), ps, paramsCount + 1);
		return ps;
	}
}
