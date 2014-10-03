package view.builder;


import javax.swing.JFrame;

import model.PreferencesManager;

import view.I18N;
import view.views.ParameterizeDialogConfig;

/**
 *	Diálogo de configuración de parametrizadores
 *	a partir de la lista de archivos de parametrizadores.
 */
public class PDCFileListDialog extends AbstractConfigFileListDialog {

	/**
	 * Constructor.
	 * @param parent Ventana padre.
	 */
	public PDCFileListDialog(JFrame parent) {
		folder = PreferencesManager.getInstance().getParamsSelectorPath();
		title = I18N.getInstance().getString(I18N.PARAMETERIZERS);
		initialize(parent);		
	}

	@Override
	public void edit() {
		int i = list.getSelectedIndex();
		
		if(i > -1) {
			ParameterizeDialogConfig pdconfig = new ParameterizeDialogConfig(listModel.get(i));
			new PDCBuilderDialog(PDCFileListDialog.this, pdconfig);
		}
	}

	@Override
	public void insert() {
		new PDCBuilderDialog(PDCFileListDialog.this, null);
	}
}
