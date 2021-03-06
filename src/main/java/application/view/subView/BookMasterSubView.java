package application.view.subView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.controller.BookMasterController;
import application.core.Repository;
import application.core.Texts;
import application.presentationModel.BooksPMod;
import application.presentationModel.componentModel.SearchFilterElement;
import application.view.ShortcutsManager;
import application.view.ShortcutsManager.ShortcutAction;
import application.view.helper.BooksTableContextMenuListener;
import application.view.helper.EnableCompontentOnTableSelectionListener;
import application.view.helper.HideTextOnFocusListener;
import domain.Library;

public class BookMasterSubView extends SubViewBase<Library, BookMasterController> {

    public static final String NAME_BUTTON_OPEN = "button.open";
    public static final String NAME_BUTTON_NEW = "button.new";
    public static final String NAME_TABLE_BOOKS = "table.books";
    public static final String NAME_LABEL_NUMBER_OF_BOOKS = "label.numberOfBooks";
    public static final String NAME_SEARCH_FIELD = "textField.search";
    public static final String NAME_COMBOBOX_FILTER = "comboBox.searchFilter";

    private final Logger logger = LoggerFactory.getLogger(BookMasterSubView.class);

    public static String searchDefaultText;

    public JTextField txtSearch;
    public JLabel lblNumberOfCopies;
    public JLabel lblNumberOfBooks;
    public JButton btnOpenBook;
    public JButton btnNewBook;
    public JTable tblBooks;
    public JComboBox<SearchFilterElement> comboSearchFilter;
    public JLabel lblAnzahlExemplare;
    public JLabel lblLasd;
    public JPanel pnInventory;
    public JLabel lblAllBooksHint;
    public JLabel lblNurVerfgbare;
    public JPanel pnStatistics;
    private HideTextOnFocusListener hideTextOnFocusListener;
    private JCheckBox checkBoxOnlyAvailable;

    public BooksPMod booksPMod;
    private BooksTableContextMenuListener contextMenuListener;

    public BookMasterSubView() {
        super(null);
    }

    /**
     * @wbp.parser.entryPoint
     */
    /*
     * (non-Javadoc)
     * 
     * @see application.view.subView.SubViewBase#initUIElements()
     */
    @Override
    protected void initUIElements() {
        container.setLayout(new BorderLayout());
        pnStatistics = new JPanel();
        container.add(pnStatistics, BorderLayout.NORTH);
        pnStatistics.setLayout(new MigLayout("", "[][][fill][][]", "[]"));

        lblLasd = new JLabel();
        pnStatistics.add(lblLasd, "cell 0 0");
        lblNumberOfBooks = new JLabel();
        lblNumberOfBooks.setName(NAME_LABEL_NUMBER_OF_BOOKS);
        pnStatistics.add(lblNumberOfBooks, "cell 1 0");

        lblAnzahlExemplare = new JLabel();
        pnStatistics.add(lblAnzahlExemplare, "cell 3 0");

        lblNumberOfCopies = new JLabel();
        pnStatistics.add(lblNumberOfCopies, "cell 4 0");

        updateStatistics();

        pnInventory = new JPanel();
        container.add(pnInventory, BorderLayout.CENTER);
        pnInventory.setLayout(new BorderLayout(0, 0));

        createBookMasterHeader();

        createControlsPanel();
    }

    private void createControlsPanel() {
        JPanel pnControlls = createBookOverviewPanel();
        pnControlls.setLayout(new MigLayout("", "[]", "[23px][]"));

        btnNewBook = new JButton();
        btnNewBook.setName(NAME_BUTTON_NEW);
        btnNewBook.setMnemonic('n');
        pnControlls.add(btnNewBook, "cell 0 0,growx,aligny center");

        btnOpenBook = new JButton();
        btnOpenBook.setName(NAME_BUTTON_OPEN);
        btnOpenBook.setMnemonic('o');
        pnControlls.add(btnOpenBook, "cell 0 1,growx,aligny center");
        btnOpenBook.setEnabled(false);
    }

    private JPanel createBookOverviewPanel() {
        JPanel pnBookOverview = new JPanel();
        pnInventory.add(pnBookOverview, BorderLayout.CENTER);
        pnBookOverview.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();

        pnBookOverview.add(scrollPane, BorderLayout.CENTER);

        tblBooks = new JTable(booksPMod.getBookTableModel());
        tblBooks.setRowSorter(booksPMod.getBookTableRowSorter());
        tblBooks.setName(NAME_TABLE_BOOKS);

        scrollPane.setViewportView(tblBooks);

        JPanel pnControlls = new JPanel();
        pnBookOverview.add(pnControlls, BorderLayout.EAST);

        return pnControlls;
    }

    private void createBookMasterHeader() {
        JPanel pnBookMasterHeader = new JPanel();
        pnInventory.add(pnBookMasterHeader, BorderLayout.NORTH);
        pnBookMasterHeader.setLayout(new MigLayout("", "[grow][][][]", "[][]"));

        lblAllBooksHint = new JLabel();
        pnBookMasterHeader.add(lblAllBooksHint, "cell 0 0");

        txtSearch = new JTextField();
        txtSearch.setName(NAME_SEARCH_FIELD);
        pnBookMasterHeader.add(txtSearch, "flowx,cell 0 1,growx");
        txtSearch.setColumns(10);

        comboSearchFilter = new JComboBox<SearchFilterElement>();
        comboSearchFilter.setName(NAME_COMBOBOX_FILTER);
        comboSearchFilter.setModel(booksPMod.getFilterComboBoxModel());
        pnBookMasterHeader.add(comboSearchFilter, "cell 1 1,growx");

        checkBoxOnlyAvailable = new JCheckBox();
        pnBookMasterHeader.add(checkBoxOnlyAvailable, "cell 2 1");

        lblNurVerfgbare = new JLabel();
        pnBookMasterHeader.add(lblNurVerfgbare, "cell 3 1,alignx trailing");
    }

    @Override
    protected void setTexts() {
        // panel titles
        pnStatistics.setBorder(new TitledBorder(null, Texts.get("BookMasterMainView.statisticsPanel.borderTitle"), TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        pnInventory.setBorder(new TitledBorder(null, Texts.get("BookMasterMainView.inventoryPanel.borderTitle"), TitledBorder.LEADING,
                TitledBorder.TOP, null, null));

        // components
        lblAnzahlExemplare.setText(Texts.get("BookMasterMainView.lblAnzahlExemplare.text"));
        lblLasd.setText(Texts.get("BookMasterMainView.lblLasd.text"));
        btnNewBook.setText(Texts.get("BookMasterMainView.btnNewBook.text"));
        lblAllBooksHint.setText(Texts.get("BookMasterMainView.allBooksHint.text"));
        btnOpenBook.setText(Texts.get("BookMasterMainView.btnOpenBook.text"));
        searchDefaultText = Texts.get("BookMasterMainView.searchDefault");
        txtSearch.setText(searchDefaultText);
        if (hideTextOnFocusListener != null) {
            hideTextOnFocusListener.updateText(searchDefaultText);
        }
        lblNurVerfgbare.setText(Texts.get("BookMasterMainView.lblNurVerfgbare.text"));

        // Tooltips
        btnOpenBook.setToolTipText(Texts.get("BookMasterMainView.btnOpenBook.toolTipText"));
        txtSearch.setToolTipText(Texts.get("BookMasterMainView.searchToolTip"));
        checkBoxOnlyAvailable.setToolTipText(Texts.get("BookMasterMainView.lblNurVerfgbare.tooltip"));

        // table
        booksPMod.getBookTableModel().setColumns();
        if (contextMenuListener != null) {
            contextMenuListener.updateTexts();
        }

        // filter comboBox
        booksPMod.getFilterComboBoxModel().updateTexts();

        container.revalidate();
    }

    @Override
    protected void initModel() {
        super.initModel();
        booksPMod = Repository.getInstance().getBooksPMod();
        booksPMod.addObserver(this);
    }

    @Override
    protected BookMasterController initController() {
        return new BookMasterController();
    }

    @Override
    protected void initListeners() {

        new EnableCompontentOnTableSelectionListener(tblBooks, btnOpenBook);

        tblBooks.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        tblBooks.getActionMap().put("Enter", new AbstractAction() {
            private static final long serialVersionUID = -5664120575484177305L;

            @Override
            public void actionPerformed(ActionEvent e) {
                getController().openBooks(tblBooks.getSelectedRows());
            }
        });

        comboSearchFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                search();
            }

            private void search() {
                if (!txtSearch.getText().equals(searchDefaultText)) {
                    getController().setSearchFilter(((SearchFilterElement) comboSearchFilter.getSelectedItem()).getTableModelColumn());
                }
            }
        });

        tblBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    logger.trace("doubleClick detected");
                    getController().openBooks(new int[] { tblBooks.rowAtPoint(e.getPoint()) });
                }
            }
        });

        btnOpenBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getController().openBooks(tblBooks.getSelectedRows());
            }
        });

        btnNewBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getController().openNewBook();
            }
        });

        checkBoxOnlyAvailable.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                getController().setSearchOnlyAvailableBooks(checkBoxOnlyAvailable.isSelected());
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

        });

        hideTextOnFocusListener = new HideTextOnFocusListener(txtSearch, searchDefaultText);

        contextMenuListener = new BooksTableContextMenuListener(tblBooks, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getController().openNewBook();
            }
        }, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getController().openBooks(tblBooks.getSelectedRows());
            }
        });
        tblBooks.addMouseListener(contextMenuListener);

    }

    private void search() {
        if (!txtSearch.getText().equals(searchDefaultText)) {
            getController().searchBooks(txtSearch.getText());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BooksPMod) {
            logger.debug("Updating statistics");
            updateStatistics();
        } else {
            super.update(o, arg);
        }
    }

    private void updateStatistics() {
        lblNumberOfBooks.setText(String.valueOf(booksPMod.getBooksCount()));
        lblNumberOfCopies.setText(String.valueOf(booksPMod.getCopiesCount()));
    }

    @Override
    protected void initShortcuts(ShortcutsManager shortcutsManager) {
        shortcutsManager.registerShortcut(KeyEvent.VK_B, new ShortcutAction() {

            @Override
            public void run() {
                getController().openNewBook();

            }
        });
    }
}
