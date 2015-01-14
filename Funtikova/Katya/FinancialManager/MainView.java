package Funtikova.Katya.FinancialManager;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;
import java.awt.geom.Arc2D;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


/**
 * @author fun-cat@rambler.ru
 */

public class MainView extends JFrame {

    FinancialManager fm;

    AccountsComboBox accountsComboBox;
    RestLabel restLabel;
    CategoriesComboBox categoriesComboBox;
    RecordsTable recordsTable;

    //график
    GraphicPanel graphicPanel;
    //меню
    JPopupMenu popupMenu;

    private Account account;
    private Category category;

    // остаток по категориям
    class RestLabel extends JLabel {
        float rest;

        public RestLabel(String s) {
            super(s);
        }

        public void update() {
            float rest = 0;
            if (!category.name.equals("Все")) {
                for (Record record : recordsTable.getRecords()) {
                    rest += record.amount;
                }
                this.rest = rest;
            } else {
                this.rest = account.rest;
            }
            setText(String.valueOf(this.rest));
        }

    }

    //классы для списков и таблицы
    //Счета
    class AccountsComboBox<String> extends JComboBox {
        public List<Account> accountsList;

        AccountsComboBox() {
            accountsList = new ArrayList<Account>();
            for (Account account : fm.getAccounts(fm.user))
                addItem(account);
        }

        public void addItem(Account account) {
            accountsList.add(account);
            super.addItem(account.name);
            setSelectedItem(account.name);
        }

        public void removeItem(Account account) {
            accountsList.remove(account);
            super.removeItem(account.name);
        }

        public void editItem(Account account) {
            int currentIndex = super.getSelectedIndex();
            super.insertItemAt(account.name, currentIndex);
            super.removeItem(getSelectedItem());
            accountsList.set(currentIndex, account);
        }

        public Account getAccount() {
            int selectedIndex = this.getSelectedIndex();
            if (accountsList.isEmpty())
                return Account.emptyAccount;
            if (selectedIndex >= accountsList.size())
                return accountsList.get(0);
            else
                return accountsList.get(selectedIndex);
        }

    }

    //Категории
    class CategoriesComboBox<String> extends JComboBox {
        public List<Category> categoriesList;

        CategoriesComboBox() {
            categoriesList = new ArrayList<Category>();
            for (Category category : fm.getCategories())
                addItem(category);
        }

        public void addItem(Category category) {
            categoriesList.add(category);
            super.addItem(category.name);
            setSelectedItem(category.name);
        }

        public void removeItem(Category category) {
            categoriesList.remove(category);
            super.removeItem(category.name);
        }

        public void editItem(Category category) {
            int currentIndex = super.getSelectedIndex();
            super.insertItemAt(category.name, currentIndex);
            super.removeItem(getSelectedItem());

            categoriesList.set(currentIndex, category);

            // И обновить все записи для счётов
            allRecordsRefresh();
        }

        public Category getCategory() {
            int selectedIndex = this.getSelectedIndex();
            if (categoriesList.isEmpty())
                return Category.emptyCategory;
            if (selectedIndex >= categoriesList.size())
                return categoriesList.get(0);
            else
                return categoriesList.get(selectedIndex);
        }
    }

    //Записи
    class RecordsTable extends JTable {
        private RecordsTableModel recordsTableModel;
        private List<Record> recordsList;
        Record currentRecord;
        int currentRow;
        TableRowSorter recordsSorter;

        RecordsTable() {
            super();
            recordsList = new ArrayList<Record>();
            recordsTableModel = new RecordsTableModel(recordsList);
            super.setModel(recordsTableModel);
            super.setGridColor(Color.red);
           // super.setAutoCreateRowSorter(true);
            recordsSorter = new TableRowSorter(recordsTableModel);
            recordsSorter.setComparator(0, new SortRecordByDate());
            recordsSorter.setComparator(1, new SortRecordByAmount());
            recordsSorter.setComparator(2, new SortRecordByCategory());
            recordsSorter.setComparator(3, new SortRecordByDescription());
            super.setRowSorter(recordsSorter);
            currentRow = getSelectedRow();
        }

        public void setRecordsList(List<Record> recordsList) {
            recordsTableModel.setRecords(recordsList);
            currentRow = getSelectedRow();
        }

        public List<Record> getRecords() {
            return recordsTableModel.records;
        }

        public void setTableModel(RecordsTableModel recordsTableModel) {
            this.recordsTableModel = recordsTableModel;
            super.setModel(recordsTableModel);
        }

        public void reload() {
            recordsList.clear();
            if (category.name == "Все") {
                recordsTableModel.setRecords(account.records);
            } else {
                Set<Record> recordsSet = fm.getRecords(account, category);
                for (Record record : recordsSet) {
                    recordsList.add(record);
                }
                recordsTableModel.setRecords(recordsList);
            }
            recordsSorter.setModel(recordsTableModel); // без этого не работает
            currentRow = getSelectedRow();
            if (currentRow >= getRecords().size())
                currentRow = -1;
            if (currentRow > -1)
                currentRecord = getRecords().get(currentRow);
            else currentRecord = Record.emptyRecord;
            super.revalidate();
            super.updateUI();
            super.repaint();
        }
    }

    // И обновить все записи для счетов пользователя
    public void allRecordsRefresh() {
        for (Account a : (ArrayList<Account>) accountsComboBox.accountsList) {
            a.records.clear();
            for (Record record : fm.getRecords(a)) {
                a.records.add(record);
            }
        }
    }

    //Панель с графиком
    class GraphicPanel extends JPanel {
        JLabel jLabel1;

        GraphicPanel() {
            jLabel1 = new JLabel();
            jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/i1.jpg")));//getResource("/resources/i1.jpg")));
            setLayout(new GridBagLayout());
            add(jLabel1);//, BorderLayout.CENTER);
            setBackground(Color.white);
            pack();
        }
    }

    public void addAccount(String accountName) {
        if (accountName == null)
            return;
        Account newAccount = new Account(0, accountName, 0);
        try {
            fm.addAccount(fm.user, newAccount);
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, "<html><h2>Такой счёт уже существует!");
        }
        if (newAccount.id > 0) {
            account = newAccount;
            accountsComboBox.addItem(newAccount);
            recordsTable.reload();
        }
    }

    public void deleteAccount() {
        if (fm.removeAccount(fm.user, account) != null) {
            accountsComboBox.removeItem(account);
            recordsTable.reload();
        }
    }

    public void addCategory(String categoryName) {
        if (categoryName == null)
            return;
        Category newCategory = new Category(0, categoryName);
        try {
            fm.addCategory(newCategory);
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, "<html><h2>Такая категория уже существует!");
        }
        if (newCategory.id > 0) {
            category = newCategory;
            categoriesComboBox.addItem(newCategory);
            recordsTable.reload();
        }
    }

    public void deleteCategory() {
        if (fm.removeCategory(category) != null) {
            categoriesComboBox.removeItem(category);
            recordsTable.reload();
        }
    }

    // слушатели
    ComboBoxItemListener comboBoxItemListener = new ComboBoxItemListener();

    protected void makeComponent(JComponent component,
                                 GridBagLayout gridbag,
                                 GridBagConstraints c) {
        component.setSize(c.gridwidth, c.gridheight);
        gridbag.setConstraints(component, c);
        add(component);
    }

    //Конструктор
    MainView(String titul, final FinancialManager fm) {
        super(titul);
        //this.setEmptyBorder
        this.fm = fm;

        //Layout-менеджер
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        // Счета
        accountsComboBox = new AccountsComboBox<String>();
        account = accountsComboBox.getAccount();
        restLabel = new RestLabel("rest");
        restLabel.setText(String.valueOf(account.rest));

        //Категории
        categoriesComboBox = new CategoriesComboBox<String>();
        category = new Category(0, "Все");
        categoriesComboBox.addItem(category);

        //Записи
        recordsTable = new RecordsTable();
        //сначала показать все записи
        recordsTable.setRecordsList(account.records);

        //График
        graphicPanel = new GraphicPanel();
        //

        //Добавление кнопок и полей в окно
        constraints.fill = GridBagConstraints.BOTH;
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        makeComponent(new JLabel("Счёт:"), gridbag, constraints);
        constraints.weightx = 1.0;
        constraints.weighty = 0.5;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        makeComponent(accountsComboBox, gridbag, constraints);

        constraints.gridheight = 6;
        constraints.weighty = 0;//10;
        constraints.weightx = 0.5;//10;
        constraints.gridx = 1;
        constraints.gridy = 0;
        makeComponent(graphicPanel, gridbag, constraints);

        constraints.gridheight = 1;
        constraints.gridwidth = 1;

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        makeComponent(new JLabel("Остаток:"), gridbag, constraints);

        constraints.weighty = 0.5;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 3;
        makeComponent(restLabel, gridbag, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        makeComponent(new JLabel("Категория:"), gridbag, constraints);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.weighty = 0.5;
        constraints.weightx = 1;
        makeComponent(categoriesComboBox, gridbag, constraints);

        constraints.gridy = 6;
        constraints.weighty = 3;
        constraints.weightx = 1;
        constraints.gridwidth = 2;//GridBagConstraints.SOUTH; //end row
        makeComponent(new JScrollPane(recordsTable), gridbag, constraints);

        setLayout(gridbag);

        //Окно закрывается
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // меню
        popupMenu = new JPopupMenu();
        final JMenuItem insertItem = new JMenuItem("Insert");
        final JMenuItem editItem = new JMenuItem("Edit");
        final JMenuItem deleteItem = new JMenuItem("Delete");

        popupMenu.add(insertItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        recordsTable.setComponentPopupMenu(popupMenu);
        categoriesComboBox.setComponentPopupMenu(popupMenu);
        accountsComboBox.setComponentPopupMenu(popupMenu);

        recordsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                insertItem.setActionCommand("Insert record");
                editItem.setActionCommand("Edit record");
                deleteItem.setActionCommand("Delete record");
                Point point = event.getPoint();
                int currentRow = recordsTable.rowAtPoint(point);
                recordsTable.setRowSelectionInterval(currentRow, currentRow);
                recordsTable.currentRecord = recordsTable.getRecords().get(currentRow);
            }
        });

        Component[] comps = categoriesComboBox.getComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    insertItem.setActionCommand("Insert category");
                    editItem.setActionCommand("Edit category");
                    deleteItem.setActionCommand("Delete category");
                }
            });
        }
        comps = accountsComboBox.getComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent event) {
                    insertItem.setActionCommand("Insert account");
                    editItem.setActionCommand("Edit account");
                    deleteItem.setActionCommand("Delete account");
                }
            });
        }
        //Прослушиватели списков
        accountsComboBox.addItemListener(comboBoxItemListener);
        categoriesComboBox.addItemListener(comboBoxItemListener);

        //Прослушиватели пунктов меню
        insertItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                switch (command) {
                    case "Insert account": {
                        String accountName = JOptionPane.showInputDialog(MainView.this, "<html><h2>Добавить новый счёт:");
                        addAccount(accountName);
                        break;
                    }
                    case "Insert category": {
                        String categoryName = JOptionPane.showInputDialog(MainView.this, "<html><h2>Добавить категорию:");
                        addCategory(categoryName);
                        break;
                    }
                    case "Insert record": {
                        showRecordDialog(command, command);
                        break;
                    }

                }
            }
        });
        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                switch (command) {
                    case "Edit account": {
                        String accountName = JOptionPane.showInputDialog(MainView.this, "<html><h2>Изменить счёт:", account.name);
                        if (accountName != null) {
                            Account acc = new Account(account.id, accountName, account.rest);
                            try {
                                if (fm.editAccount(acc)) {
                                    accountsComboBox.editItem(acc);
                                    account = acc;
                                    //                               account.name = acc.name;
                                }
                            } catch (SQLException e1) {
                                JOptionPane.showMessageDialog(MainView.this, "<html><h2>Такой счёт уже существует!");
                            }
                        }
                        break;
                    }
                    case "Edit category": {
                        String categoryName = JOptionPane.showInputDialog(MainView.this, "<html><h2>Изменить категорию:", category.name);
                        if (categoryName != null) {
                            Category cat = new Category(category.id, categoryName);
                            try {
                                if (fm.editCategory(cat)) {
                                    categoriesComboBox.editItem(cat);
                                    //                                category.name = cat.name;
                                    category = cat;
                                }
                            } catch (SQLException e1){
                                JOptionPane.showMessageDialog(MainView.this, "<html><h2>Такая категория уже существует!");
                            }
                        }
                        break;
                    }
                    case "Edit record": {
                        showRecordDialog(command, command);
                        break;
                    }
                }
            }
        });
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                switch (command) {
                    case "Delete account": {
                        int yes = JOptionPane.showConfirmDialog(MainView.this, "<html><h2>Удалить счёт: " + account.name, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                        if (yes == 0)
                            deleteAccount();
                    }
                    break;
                    case "Delete category": {
                        int yes = JOptionPane.showConfirmDialog(MainView.this, "<html><h2>Удалить категорию: " + category.name, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                        if (yes == 0)
                            deleteCategory();
                    }
                    break;
                    case "Delete record": {
                        showRecordDialog(command, command);
                    }
                    break;

                }
            }
        });

    }

    private void showRecordDialog(final String command, String title) {
        final JDialog dialog = new JDialog(this, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        // Формат даты и времени
        final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        dialog.getContentPane().add(new JLabel("Дата:"));
        final JTextField dateTextField = new JTextField(dateFormat.format(new java.util.Date()));
        dialog.getContentPane().add(dateTextField);

        dialog.getContentPane().add(new JLabel("Сумма:"));
        final JTextField amountTextField = new JTextField("0");
        dialog.getContentPane().add(amountTextField);

        dialog.getContentPane().add(new JLabel("Категория:"));
        final CategoriesComboBox categoriesComboBox1 = new CategoriesComboBox();
        if (!category.name.equals("Все"))
            categoriesComboBox1.setSelectedItem(category.name);
        dialog.add(categoriesComboBox1);

        dialog.getContentPane().add(new JLabel("Описание:"));
        final JTextField descriptionTextField = new JTextField("");
        dialog.getContentPane().add(descriptionTextField);

        // показать запись, котрую удаляют или изменяют
        if (command == "Delete record" || command == "Edit record") {
            dateTextField.setText(dateFormat.format(recordsTable.currentRecord.date));
            amountTextField.setText(String.valueOf(recordsTable.currentRecord.amount));
            categoriesComboBox1.setSelectedItem(recordsTable.currentRecord.category.name);
            descriptionTextField.setText(recordsTable.currentRecord.description);
        }
        // для удаления записи все поля должны быть нередактируемые
        if (command == "Delete record") {
            dateTextField.setEditable(false);
            amountTextField.setEditable(false);
            categoriesComboBox1.setEnabled(false);
            descriptionTextField.setEditable(false);
        }

        JButton okButton = new JButton("Ok");
        dialog.add(okButton);

        //Слушатель кнопки ОК
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                switch (command) {
                    case "Insert record": {
                        try {
                            Record newRecord = new Record(0, dateTextField.getText(), Float.valueOf(amountTextField.getText()), categoriesComboBox1.getCategory(), descriptionTextField.getText());
                            fm.addRecord(account, newRecord);
                            if (newRecord.recordID > 0) {
                                MainView.this.category = categoriesComboBox1.getCategory();
                                MainView.this.categoriesComboBox.setSelectedItem(MainView.this.category.name);
                                recordsTable.reload();
                                restLabel.update();
                            }
                        } catch (ParseException e) {
                            JOptionPane.showMessageDialog(dialog, "<html><h2>Не правильный формат даты!");
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(dialog, "<html><h2>Такая запись уже существует!");
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(dialog, "<html><h2>Что-то пошло не так...");
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "Edit record": {
                        try {
                            Record record = new Record(recordsTable.currentRecord.recordID, // ИД
                                    dateTextField.getText(),  // дата
                                    Float.valueOf(amountTextField.getText()), // сумма
                                    new Category(categoriesComboBox1.getCategory().id, categoriesComboBox1.getCategory().name),
                                    descriptionTextField.getText());
                            if (fm.editRecord(account, record)) {
                                MainView.this.category = categoriesComboBox1.getCategory();
                                MainView.this.categoriesComboBox.setSelectedItem(MainView.this.category.name);
                                recordsTable.reload();
                                restLabel.update();
                            } else JOptionPane.showMessageDialog(dialog, "<html><h2>Запись не изменилась!");

                        } catch (ParseException e) {
                            JOptionPane.showMessageDialog(dialog, "<html><h2>Не правильный формат даты!");
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(dialog, "<html><h2>Такая запись уже существует!");
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(dialog, "<html><h2>Что-то пошло не так...");
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "Delete record": {
                        Record record = recordsTable.currentRecord;
                        fm.removeRecord(account, record);
                        recordsTable.reload();
                        restLabel.update();
                        //чтобы удалить оставшиеся записи:
                        if (recordsTable.currentRow > -1) {
                            dateTextField.setText(recordsTable.currentRecord.stringDate);
                            amountTextField.setText(String.valueOf(recordsTable.currentRecord.amount));
                            categoriesComboBox1.setSelectedItem(recordsTable.currentRecord.category.name);
                            descriptionTextField.setText(recordsTable.currentRecord.description);
                        }
                        break;
                    }
                }
            }
        });

        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.pack();
        dialog.setBounds(350, 350, 200, 250);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public class ComboBoxItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object eventSource = e.getSource();
            if (eventSource == accountsComboBox) {
                account = accountsComboBox.getAccount();
                restLabel.update();
            } else {
                category = categoriesComboBox.getCategory();
            }
            // обновить записи
            recordsTable.reload();
            // обновить остаток
            restLabel.update();
        }
    }

    public static void main(String[] args) {

        UserView userView = new UserView("Авторизация");

        ImageIcon imageIcon = new ImageIcon("i1.jpg");
        Image svinya = imageIcon.getImage();
        userView.setIconImage(svinya);
    }

}

