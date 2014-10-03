package view.builder;

import view.I18N;
import view.MainWindow;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

import model.managers.ManagerInfo;
import model.managers.ManagerLoader;

/**
 * Diálogo de construcción de la lista de gestores.
 */
public class ManagersListBuilderDialog extends AbstractBuilderDialog<ManagerInfo> {

	/** Ventana principal. */
	private MainWindow mainWindow;	
	
	/**
	 * Constructor.
	 * 
	 * @param mainWindow Ventana principal.
	 */
	public ManagersListBuilderDialog(MainWindow mainWindow) {
		super(new ManagerLoader());
		this.mainWindow = mainWindow;		
		
		columns = new String[]{I18N.getInstance().getString(I18N.NAME), I18N.getInstance().getString(I18N.ENTITY), I18N.getInstance().getString(I18N.INSERT), I18N.getInstance().getString(I18N.EDIT), I18N.getInstance().getString(I18N.DELETE), I18N.getInstance().getString(I18N.REQUIRED_ACCESS)};	
		title = I18N.getInstance().getString(I18N.MANAGERS);
		init();
	}		
	
	@Override
	protected void edit() {
		int index = table.getSelectedRow();
		ManagerInfo mi = ((ManagerLoader) exportable).getAvailableManagers().get(index);
		new ManagerInfoDialog(mi, this);
	}
	
	@Override
	protected void insert() {
		new ManagerInfoDialog(((ManagerLoader) exportable).getAvailableManagers(), this);
	}
	
	@Override
	protected boolean save() {
		boolean success = false;
				
		exportable.export();
		setModified(false);
		success = true;
		mainWindow.refreshNavigationTree();
		
		return success;
	}	
	
	@Override
	public void createTopPanel(JPanel topPanel) {
		splitPane.setDividerLocation(0);
	}
		
	@Override
	public void load() {								
		Iterator<ManagerInfo> iter = ((ManagerLoader) exportable).getAvailableManagers().iterator();
		ManagerInfo current;
		
		while(tableModel.getRowCount() > 0)
			tableModel.removeRow(0);
		
		while(iter.hasNext()) {
			current = iter.next();
			Vector<String> row = new Vector<String>();
			
			row.add(current.getName());
			row.add(current.getEntityXML());			
			if(current.showInsertOption())	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));
			if(current.showEditOption())	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));
			if(current.showDeleteOption())	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));
			row.add(current.getRequiredAccess());
			
			tableModel.addRow(row);
		}
	}	
}
