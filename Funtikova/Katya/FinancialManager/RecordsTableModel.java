package Funtikova.Katya.FinancialManager;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
//import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * @author fun-cat@rambler.ru
 */
class RecordsTableModel implements TableModel {

    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
    protected List<Record> records;
    private final List<Record> emptyList = Arrays.asList(Record.emptyRecord);

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public RecordsTableModel(List<Record> records) {
        super();
        if (records.isEmpty()) {
            this.records = emptyList;
        }
        else {
            this.records = records;
        }
    }

    public RecordsTableModel() {
        super();
    }

    public void setRecords (List<Record> records) {
        if (records.isEmpty()) {
            this.records = emptyList;
        }
        else {
            this.records = records;
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 1:
                return Float.class;
            case 4:
                return Double.class;
        }
        return String.class; // 1, 2 and 3 (Date, Category and Description)
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Дата";
            case 1:
                return "Сумма";
            case 2:
                return "Категория";
            case 3:
                return "Описание";
            case 4:
                return "ID";
        }
        return "";
    }

    public int getRowCount() {
        return records.isEmpty() ? 1:records.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Record record = (Record)records.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return record.stringDate;
            case 1:
                return record.amount;
            case 2:
                return record.category.name;
            case 3:
                return record.description;
            case 4:
                return record.recordID;
        }
        return "";
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {

    }

}