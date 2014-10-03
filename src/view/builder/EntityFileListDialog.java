package view.builder;

import javax.swing.JFrame;

import view.I18N;

import model.PreferencesManager;
import model.db.entities.Entity;
import model.db.entities.EntityLoader;

/**
 *	Diálogo de configuración de entidades
 *	a partir de la lista de archivos de entidades.
 */
public class EntityFileListDialog extends AbstractConfigFileListDialog {

	/**
	 * Constructor.
	 * @param parent Ventana padre.
	 */
	public EntityFileListDialog(JFrame parent) {
		folder = PreferencesManager.getInstance().getEntitesPath();
		title = I18N.getInstance().getString(I18N.ENTITIES_LIST);
		initialize(parent);
	}

	@Override
	public void edit() {
		int i = list.getSelectedIndex();
		
		if(i > -1) {
			Entity ent = EntityLoader.getInstance().load(listModel.get(i));
			new EntityBuilderDialog(EntityFileListDialog.this, ent);
		}
	}

	@Override
	public void insert() {
		new EntityBuilderDialog(EntityFileListDialog.this, null);
	}	
}
