package Application;

import javax.swing.table.AbstractTableModel;

import Application.ProjectionItem;

import java.util.ArrayList;

/**
 * An implementation of DefaultTableModel to match our needs in case of a Projection
 *
 * @author Rômulo de Carvalho Magalhães
 *
 */
@SuppressWarnings("serial")
public class ProjectionTableModel extends AbstractTableModel {

	private final int COLUMN_NAME = 0;
    private final int COLUMN_CHECK = 1;
    
    ArrayList<ProjectionItem> ProjectionList;
	
	public ProjectionTableModel() {
		this.ProjectionList = new ArrayList<ProjectionItem>();
	}

    public int getRowCount() {
        return this.ProjectionList.size();
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) 
    {
    	if(column == this.COLUMN_NAME)
        {
    		return "Vocabulary";
        }else if(column == this.COLUMN_CHECK)
        {
        	return "Choice";
        }
    	return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if(columnIndex == this.COLUMN_NAME)
        {
        	return String.class;
        }else if(columnIndex == this.COLUMN_CHECK)
        {
        	return Boolean.class;
        }
        return String.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
    	ProjectionItem p = this.ProjectionList.get(rowIndex);
    	
        if(columnIndex == this.COLUMN_NAME)
        {
            return p.getName();
        }else if(columnIndex == this.COLUMN_CHECK)
        {
        	return p.getProjection();
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
    	if(columnIndex == this.COLUMN_NAME)
        {
            return false;
        }
        return true;
    }
    
   public ArrayList<ProjectionItem> getProjectionItemList() {
        return this.ProjectionList;
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
    	ProjectionItem p = this.ProjectionList.get(rowIndex);
    	
    	if(columnIndex == this.COLUMN_CHECK)
    	{
    		if (p.getProjection().equals(Boolean.FALSE)){
                p.setProjection(Boolean.TRUE);
            }else
            {
                p.setProjection(Boolean.FALSE);
            }
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void insert(ProjectionItem p) {
    	this.ProjectionList.add(p);
        fireTableDataChanged();
    }

    public void delete(int pos) {
    	this.ProjectionList.remove(pos);
        fireTableDataChanged();
    }

    public void delete(ProjectionItem p) {
    	this.ProjectionList.remove(p);
    	fireTableDataChanged();
    }
}
