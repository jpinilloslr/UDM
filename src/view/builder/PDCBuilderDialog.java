package view.builder;

import view.I18N;
import view.utils.GUIUtils;
import view.views.ParameterizeDialogConfig;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import model.views.Parameter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Diálogo de construcción de parametrizador.
 */
public class PDCBuilderDialog extends AbstractBuilderDialog<Parameter> {

	/** Diálogo de configuración de parametrizadores a partir de sus archivos. */
	private AbstractConfigFileListDialog listDialog;
	
	/** Campo de texto para el nombre del archivo. */
	private JTextField tfFilename;	
	
	/**
	 * Constrctor.
	 * 
	 * @param pdcListDialog Diálogo de configuración de parametrizadores a partir de sus archivos.
	 * @param pdc Parametrizador. Si es null la ventana se crea en modo inserción, de lo contrario	
	 * 				se crea en modo edición.
	 */
	public PDCBuilderDialog(AbstractConfigFileListDialog pdcListDialog, ParameterizeDialogConfig pdc) {
		super(pdc);
		
		this.listDialog = pdcListDialog;		
		if(null == pdc) {
			exportable = new ParameterizeDialogConfig();
			editMode = false;
			title = I18N.getInstance().getString(I18N.NEW_PARAMETERIZER);
		} else { 
			exportable = pdc;
			editMode = true;
			title = I18N.getInstance().getString(I18N.EDIT) + " " + pdc.getFilename();
		}

		columns = new String[]{I18N.getInstance().getString(I18N.NAME), I18N.getInstance().getString(I18N.TYPE)};
		init();
	}
	
	/**
	 * Valida los componente.
	 * @return true si la validación es satisfactoria, 
	 * 			false si no.
	 */
	private boolean validate() {
		boolean b = true;
		b &= GUIUtils.checkTextField(tfFilename);

		if(table.getRowCount() == 0) {
			b = false;
			JOptionPane.showMessageDialog(null, I18N.getInstance().getString(I18N.NO_FIELDS_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
		
		if(!b) {
			JOptionPane.showMessageDialog(null, I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
		
		return b;
	}
	
	@Override
	protected void edit() {
		int index = table.getSelectedRow();
		new ParameterDialog(((ParameterizeDialogConfig) exportable), index, this);
	}
	

	@Override
	protected void insert() {
		new ParameterDialog(((ParameterizeDialogConfig) exportable), -1, this);
	}
	
	@Override
	protected void load() {						
		if(((ParameterizeDialogConfig) exportable).getFilename() != null)
			tfFilename.setText(((ParameterizeDialogConfig) exportable).getFilename());
		
		Iterator<Parameter> iter = ((ParameterizeDialogConfig) exportable).getParams().iterator();
		Parameter current;
		
		while(tableModel.getRowCount() > 0)
			tableModel.removeRow(0);
		
		while(iter.hasNext()) {
			current = iter.next();
			Vector<String> row = new Vector<String>();
			
			row.add(current.getName());
			row.add(current.getType().toString());
			
			tableModel.addRow(row);
		}						
		
		setModified(false);
	}
		
	@Override
	protected boolean save() {
		boolean success = false;
		
		if(validate()) {
			((ParameterizeDialogConfig) exportable).setFilename(tfFilename.getText());
			exportable.export();			
			
			listDialog.refreshList();
			setModified(false);
			success = true;
		}
		
		return success;
	}
	
	@Override
	public void createTopPanel(JPanel topPanel) {
		tfFilename = new JTextField();		
		tfFilename.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				setModified(true);
			}
		});
		tfFilename.setColumns(10);
		tfFilename.setBounds(10, 29, 217, 20);
		topPanel.add(tfFilename);
		
		JLabel lblFilename = new JLabel(I18N.getInstance().getString(I18N.XML_FILE));
		lblFilename.setBounds(10, 11, 145, 14);
		topPanel.add(lblFilename);
	}
}
