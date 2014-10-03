package view.builder;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

import model.views.ViewInfo;
import model.views.ViewLoader;
import view.I18N;
import view.MainWindow;

/**
 * Diálogo de construcción de la lista de vistas.
 */
public class ViewsListBuilderDialog extends AbstractBuilderDialog<ViewInfo> {

	/** Ventana principal. */
	private MainWindow mainWindow;
	
	/**
	 * Constructor.
	 * @param mainWindow Ventana principal.
	 */
	public ViewsListBuilderDialog(MainWindow mainWindow) {
		super(new ViewLoader());
		this.mainWindow = mainWindow;		
		
		columns = new String[]{I18N.getInstance().getString(I18N.NAME), I18N.getInstance().getString(I18N.VIEW), I18N.getInstance().getString(I18N.PARAMETERIZER), I18N.getInstance().getString(I18N.REQUIRED_ACCESS)};
		title = I18N.getInstance().getString(I18N.LISTS);
		init();
	}
	
	@Override
	protected void edit() {
		int index = table.getSelectedRow();
		new ViewInfoDialog(((ViewLoader) exportable).getAvailableViews().get(index), ViewsListBuilderDialog.this);
	}
	
	@Override
	protected void insert() {
		new ViewInfoDialog(((ViewLoader) exportable).getAvailableViews(), ViewsListBuilderDialog.this);
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
		Iterator<ViewInfo> iter = ((ViewLoader) exportable).getAvailableViews().iterator();
		ViewInfo current;
		
		while(tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}
		
		while(iter.hasNext()) {
			current = iter.next();
			Vector<String> row = new Vector<String>();
			
			row.add(current.getName());
			row.add(current.getViewName());
			row.add(current.getParameterizer());
			row.add(current.getRequiredAccess());			
			
			tableModel.addRow(row);
		}				
	}		
}
