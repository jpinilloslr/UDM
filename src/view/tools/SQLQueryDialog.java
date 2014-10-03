package view.tools;

import view.I18N;
import view.Resources.Style;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import model.db.DBLink;

/**
 * <h1>Ventana de consultas SQL</h1>
 */
public class SQLQueryDialog {

	/** Diálogo. */
	private JDialog dialog;
	
	/** Resultado de la consulta. */
	private ResultSet rs;
	
	/** Tabla. */
	private JTable table;
	
	/** Modelo de la tabla. */
	private DefaultTableModel tableModel;
	
	/** Panel de texto para la consulta. */
	private JTextPane tpQuery;

	/**
	 * Constructor.
	 */
	public SQLQueryDialog() {
		initialize(null);
	}
	
	/**
	 * Crea el modelo de la tabla.
	 * @param columns Columnas.
	 */
	private void createTable(String[] columns) {
		tableModel = new DefaultTableModel(new Object[][] {}, columns);

		table.setModel(tableModel);
		
		table.setGridColor(Style.TABLE_GRID);
		table.setSelectionBackground(Style.SELECTION_BACKGROUND);
		table.setSelectionForeground(Style.SELECTION_FOREGROUND);
	}
	
	/**
	 * Ejecuta la consulta.
	 */
	private void execute() {
		String sql = tpQuery.getText();		
		
		Statement stmt;
		try {
			stmt = DBLink.getInstance().getConnection().createStatement(
			        ResultSet.TYPE_SCROLL_INSENSITIVE,
			        ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), 
					I18N.getInstance().getString(I18N.ERROR), JOptionPane.INFORMATION_MESSAGE);
		}		
		
		extractMetadata();
		updateTable();
	}
	
	/**
	 * Extrae los metadatos resultantes de la consulta
	 * para construir la tabla.
	 */
	private void extractMetadata() {		
		ArrayList<String> fieldsNames = new ArrayList<String>();
		
		if(rs != null) {
			try {
				for(int i=0; i<rs.getMetaData().getColumnCount(); i++) {
					fieldsNames.add(rs.getMetaData().getColumnName(i+1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
				
		createTable(fieldsNames.toArray(new String[0]));
	}
	
	/**
	 * Inicia el contenido del frame.
	 * @param parent Ventana padre.
	 */
	private void initialize(JFrame parent) {

		dialog = new JDialog(parent, true);
		dialog.setResizable(false);
		dialog.setTitle(I18N.getInstance().getString(I18N.SQL_QUERY));
		dialog.setBounds(50, 50, 704, 521);		
		
		dialog.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(3);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		dialog.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		tpQuery = new JTextPane();
		splitPane.setLeftComponent(tpQuery);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		splitPane.setDividerLocation(150);
		
		JToolBar toolBar = new JToolBar();
		dialog.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnExecute = new JButton("Execute");
		btnExecute.setFocusable(false);
		btnExecute.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				execute();
			}
		});
		toolBar.add(btnExecute);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	/**
	 * Actualiza el contenido de la tabla.
	 */
	private void updateTable() {
		
		while(tableModel.getRowCount() > 0)
			tableModel.removeRow(0);	
		
		if(null != rs) {
			try {
				rs.absolute(0);
				int columns = rs.getMetaData().getColumnCount();
				
				while(rs.next()) {
					Vector<String> entry = new Vector<String>();
					
					for(int i=0; i<columns; i++) {
						entry.add(String.valueOf(rs.getObject(i+1)));
					}
					
					tableModel.addRow(entry);
				}	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}
}
