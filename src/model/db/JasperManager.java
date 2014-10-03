package model.db;

import java.sql.Connection;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * <h1>Gestión de reportes</h1>
 * 
 * Wrapper a la API de iReport.
 */
public class JasperManager {

	/**
	 * Muestra un reporte.
	 * 
	 * @param parameters Parámetros a pasar al reporte.
	 * @param reportFile Archivo jasper compilado.
	 * @param connection Conexión a la base de datos.
	 */
	@SuppressWarnings("rawtypes")
	public static void showReport(Map parameters, String reportFile,
			Connection connection) {

		JasperPrint print;
		try {
			print = JasperFillManager.fillReport(reportFile, parameters,
					connection);

			JasperViewer.viewReport(print, false);
		} catch (JRException e) {
			e.printStackTrace();
		}

	}

}