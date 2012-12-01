package application.view.mainView.dialogView;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.controller.LoanDetailController;
import application.core.Repository;
import application.core.Texts;
import application.presentationModel.CopyPMod;
import application.presentationModel.LoansPMod;
import application.presentationModel.componentModel.LoanDetailTableModel;
import application.util.IconUtil;
import application.view.component.JNumberTextField;
import application.view.helper.CustomerListRenderer;
import application.view.helper.DueDateTableCellRenderer;
import application.view.helper.EnableCompontentOnTableSelectionListener;
import application.view.helper.HideTextOnFocusListener;

import com.google.common.base.Joiner;
import com.jgoodies.validation.ValidationResult;

import domain.Copy;
import domain.Copy.Condition;
import domain.Customer;
import domain.Loan;

public class LoanDetailMainView extends DialogViewBase<Loan, LoanDetailController> implements Observer {

    private static String defaultSearchValue;

    private JPanel pnMain;
    private JPanel pnNewLoan;
    private JPanel pnLoanOverview;
    private JLabel lblSearch;
    private JLabel lblCopyId;
    private JLabel lblCustomer;
    private JLabel lblCondition;
    private JLabel lblCopyTitle;
    private JLabel lblAvailabilityValue;
    private JLabel lblNumberOfLoans;
    private JLabel lblConditionValue;
    private JLabel lblValCopyTitle;
    private JLabel lblValNumberOfLoans;
    private JTable tblLoans;
    private JLabel lblCopyStatus;
    private JLabel lblAvailability;
    private JButton btnCreateLoan;
    private JButton btnReturnButton;
    private JButton btnLinkToLoan;
    private JTextField txtCustomerSearch;
    private JNumberTextField txtCopyId;
    private JComboBox<Condition> comboStatus;
    private JList<Customer> listCustomer;
    private JLabel lblReturnFeedbackLabel;

    private Copy currentSelectedCopy;
    private HideTextOnFocusListener hideTextOnFocusListener;

    private static final Logger logger = LoggerFactory.getLogger(LoanDetailMainView.class);

    private LoansPMod pMod;
    private CopyPMod copyPMod;

    public LoanDetailMainView(Loan loan) {
        super(loan, "loan.gif");
    }

    public void switchToLoan(Loan loan, boolean reloadAll) {
        logger.debug("switch to loan {}", loan);
        setReferenceObject(loan);
        updateLoanOverViewSection();
        if (reloadAll) {
            updateCustomerSelectionSection();
            clearNewLoanSection();
        }
        updateMakeLoanButtonVisibility();
    }

    @Override
    protected void addObservables() {
        observables.add(Repository.getInstance().getCopyPMod());
    }

    @Override
    protected void initModel() {
        super.initModel();
        pMod = Repository.getInstance().getLoansPMod();
        copyPMod = Repository.getInstance().getCopyPMod();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // set initial data
                if (getReferenceObject() != null) {
                    updateLoanOverViewSection();
                    updateCustomerSelectionSection();
                }
            }
        });
    }

    @Override
    protected void setTexts() {
        // title
        getContainer().setTitle(Texts.get("LoanDetailMainView.this.title")); //$NON-NLS-1$

        defaultSearchValue = Texts.get("LoanDetailMainViewBase.defaultSearchValue");
        if (hideTextOnFocusListener != null) {
            hideTextOnFocusListener.updateText(defaultSearchValue);
        }

        // border of panels
        pnNewLoan.setBorder(new TitledBorder(null, Texts.get("LoanDetailMainViewBase.newLoan.title"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
        pnMain.setBorder(new TitledBorder(null, Texts.get("LoanDetailMainViewBase.customerSelection.title"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

        // components
        lblCopyTitle.setText(Texts.get("LoanDetailMainViewBase.newLoan.bookDescriptionLabel"));
        lblNumberOfLoans.setText(Texts.get("LoanDetailMainViewBase.loansOverview.numberOfLoans"));
        lblCopyId.setText(Texts.get("LoanDetailMainViewBase.newLoan.copyId"));
        btnCreateLoan.setText(Texts.get("LoanDetailMainViewBase.newLoan.lendCopyButton"));
        lblCustomer.setText(Texts.get("LoanDetailMainViewBase.customerSelection.customerLabel"));
        lblSearch.setText(Texts.get("LoanDetailMainViewBase.customerSelection.searchLabel"));
        lblCondition.setText(Texts.get("LoanDetailMainViewBase.newLoan.copyCondition"));
        btnReturnButton.setText(Texts.get("LoanDetailMainViewBase.loansOverview.returnButton"));
        btnLinkToLoan.setText(Texts.get("LoanDetailMainViewBase.newLoan.VisitLoan"));
        lblReturnFeedbackLabel.setText("");
        lblAvailability.setText(Texts.get("LoanDetailMainViewBase.newLoan.availability"));
    }

    /**
     * Initialize the contents of the frame.
     * 
     * @wbp.parser.entryPoint
     */
    @Override
    protected void initUIElements() {
        super.initUIElements();

        Container contentPane = getContainer().getContentPane();
        getContainer().setBounds(100, 100, 600, 500);

        JPanel mainPanel = new JPanel();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new MigLayout("", "[grow]", "[70.00][89.00,top][grow]"));

        createCustomerSelectionSection(mainPanel);

        createNewLoanSection(mainPanel);

        createLoanOverviewSection(mainPanel);

        updateCustomerSelectionSection();
        updateLoanOverViewSection();
    }

    private void createCustomerSelectionSection(JPanel panel_4) {
        pnMain = new JPanel();
        panel_4.add(pnMain, "cell 0 0,growx,aligny top");
        pnMain.setLayout(new MigLayout("", "[][193.00][grow]", "[50.00,grow][26.00,grow]"));

        lblSearch = new JLabel();
        pnMain.add(lblSearch, "cell 0 0,alignx trailing");

        txtCustomerSearch = new JTextField();
        pnMain.add(txtCustomerSearch, "cell 1 0,growx");
        txtCustomerSearch.setColumns(10);
        txtCustomerSearch.setToolTipText(defaultSearchValue);

        lblCustomer = new JLabel();
        pnMain.add(lblCustomer, "cell 0 1,alignx trailing");

        listCustomer = new JList<Customer>(Repository.getInstance().getCustomerPMod().getCustomerListModel());

        listCustomer.setCellRenderer(new CustomerListRenderer());
        listCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPaneCustomerList = new JScrollPane(listCustomer);

        scrollPaneCustomerList.setMinimumSize(new Dimension(200, 100));
        scrollPaneCustomerList.setPreferredSize(new Dimension(200, 100));

        pnMain.add(scrollPaneCustomerList, "cell 1 1 2 1,growx");
    }

    private void updateCustomerSelectionSection() {
        txtCustomerSearch.setText(getReferenceObject().getCustomer().getFullName());
        listCustomer.setSelectedIndex(0);

        updateMakeLoanButtonVisibility();
    }

    private void createLoanOverviewSection(JPanel panel_4) {
        pnLoanOverview = new JPanel();
        panel_4.add(pnLoanOverview, "cell 0 2,grow");
        pnLoanOverview.setLayout(new MigLayout("", "[][][][grow]", "[grow][grow][][]"));

        lblNumberOfLoans = new JLabel();
        pnLoanOverview.add(lblNumberOfLoans, "cell 0 0");

        lblValNumberOfLoans = new JLabel("1");
        pnLoanOverview.add(lblValNumberOfLoans, "cell 1 0,alignx leading");

        LoanDetailTableModel loanDetailTableModel = pMod.getLoanDetailTableModel();
        tblLoans = new JTable(loanDetailTableModel);
        tblLoans.setDefaultRenderer(Date.class, new DueDateTableCellRenderer());

        pnLoanOverview.add(new JScrollPane(tblLoans), "cell 0 1 4 1,grow");

        JPanel panel_5 = new JPanel();
        pnLoanOverview.add(panel_5, "cell 0 2 4 1,grow");
        panel_5.setLayout(new MigLayout("", "[][][][]", "[][]"));

        lblCopyStatus = new JLabel("Status:");
        panel_5.add(lblCopyStatus, "cell 0 0");

        btnReturnButton = new JButton();
        panel_5.add(btnReturnButton, "cell 2 0");
        btnReturnButton.setEnabled(false);

        comboStatus = new JComboBox<Condition>(copyPMod.getCopyStatusModel());
        panel_5.add(comboStatus, "cell 1 0");

        lblReturnFeedbackLabel = new JLabel();
        panel_5.add(lblReturnFeedbackLabel, "cell 3 0");
    }

    private void updateLoanOverViewSection() {
        String overViewTitle = "";
        Customer customer;

        customer = getCurrentCustomer();
        if (customer != null) {
            // update Table
            pMod.getLoanDetailTableModel().updateLoans(customer);
            int curLoanIndex = pMod.getLoanDetailTableModel().getRowIndex(getReferenceObject());
            // set selection
            if (curLoanIndex >= 0) {
                tblLoans.getSelectionModel().setSelectionInterval(curLoanIndex, curLoanIndex);
            }

            // update border
            overViewTitle = Texts.get("LoanDetailMainViewBase.loansOverview.title") + " " + customer.getFullName();
        } else {
            pMod.getLoanDetailTableModel().updateLoans((Customer) null);
            overViewTitle = Texts.get("LoanDetailMainViewBase.loansOverview.titleAlone");
        }
        // update statistic
        List<Loan> openLoans = Repository.getInstance().getCustomerPMod().getCustomerOpenLoans(customer);
        lblValNumberOfLoans.setText("" + openLoans.size());
        pnLoanOverview.setBorder(new TitledBorder(null, overViewTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));

    }

    private void createNewLoanSection(JPanel panel_4) {
        pnNewLoan = new JPanel();
        panel_4.add(pnNewLoan, "cell 0 1,growx,aligny top");
        pnNewLoan.setLayout(new MigLayout("", "[][][][]", "[][][][][][][]"));

        lblCopyId = new JLabel();
        pnNewLoan.add(lblCopyId, "cell 0 0,grow");

        txtCopyId = new JNumberTextField();
        txtCopyId.setMaximumSize(new Dimension(40, 20));
        txtCopyId.setPreferredSize(new Dimension(40, 20));
        txtCopyId.setMinimumSize(new Dimension(40, 20));
        pnNewLoan.add(txtCopyId, "cell 1 0,alignx left,growy");
        txtCopyId.setColumns(10);

        btnCreateLoan = new JButton();
        pnNewLoan.add(btnCreateLoan, "cell 2 0,alignx left,growy");

        lblCopyTitle = new JLabel();
        pnNewLoan.add(lblCopyTitle, "cell 0 1,grow");

        lblValCopyTitle = new JLabel();
        // lblCopyDescription.setMaximumSize(new Dimension(250, 20));
        pnNewLoan.add(lblValCopyTitle, "cell 1 1 3,alignx left,growy");

        btnLinkToLoan = new JButton("");
        btnLinkToLoan.setVisible(false);
        pnNewLoan.add(btnLinkToLoan, "cell 3 3,alignx left");

        lblCondition = new JLabel();
        pnNewLoan.add(lblCondition, "cell 0 2,alignx left,growy");

        lblConditionValue = new JLabel();
        pnNewLoan.add(lblConditionValue, "cell 1 2,alignx left,growy");

        lblAvailabilityValue = new JLabel();
        pnNewLoan.add(lblAvailabilityValue, "cell 1 3 2 1,alignx left,growy");

        lblAvailability = new JLabel("");
        pnNewLoan.add(lblAvailability, "cell 0 3");
    }

    private Condition getSelectedCondition() {
        return (Condition) comboStatus.getSelectedItem();
    }

    private void clearNewLoanSection() {
        txtCopyId.setText("");
        currentSelectedCopy = null;
        updateNewLoanSection();
    }

    private void updateNewLoanSection() {

        btnLinkToLoan.setVisible(false);
        if (currentSelectedCopy == null) {
            lblValCopyTitle.setText("");
            lblConditionValue.setText("");
            lblConditionValue.setIcon(null);
            lblAvailabilityValue.setIcon(null);
            lblAvailabilityValue.setText("");
        } else {
            String titleName = currentSelectedCopy.getTitle().getName();
            lblValCopyTitle.setText(titleName);
            // set it as title as well in case the content is too long
            lblValCopyTitle.setToolTipText(titleName);
            lblConditionValue.setText(currentSelectedCopy.getCondition().name());
            lblConditionValue.setIcon(IconUtil.loadIcon(currentSelectedCopy.getCondition().getIcon()));
            // lblLoanStatus
            boolean isCopyLent = Repository.getInstance().getCopyPMod().isCopyLent(currentSelectedCopy);
            if (currentSelectedCopy.getCondition().equals(Condition.LOST)) {
                lblAvailabilityValue.setText(Texts.get("validation.copy.lost"));
                lblAvailabilityValue.setIcon(IconUtil.loadIcon("warning.png"));
            } else {
                if (!isCopyLent) {
                    lblAvailabilityValue.setIcon(IconUtil.loadIcon("check.png"));
                    lblAvailabilityValue.setText(Texts.get("validation.copy.isLent.false"));
                } else {
                    String lender = copyPMod.getLender(currentSelectedCopy).getFullName();
                    lblAvailabilityValue.setText(Texts.get("validation.copy.isLent.true") + lender + ".");
                    lblAvailabilityValue.setIcon(IconUtil.loadIcon("warning.png"));
                    btnLinkToLoan.setVisible(true);
                }
            }
        }

        updateMakeLoanButtonVisibility();
    }

    private void updateMakeLoanButtonVisibility() {
        boolean hasErrors = true;
        if (currentSelectedCopy != null) {
            hasErrors = getController().validateLoan(currentSelectedCopy, getCurrentCustomer()).hasErrors();
        }
        btnCreateLoan.setEnabled(!hasErrors);

    }

    @Override
    protected LoanDetailController initController() {
        return new LoanDetailController();
    }

    @Override
    protected void initListeners() {

        new EnableCompontentOnTableSelectionListener(tblLoans, btnReturnButton);
        new EnableCompontentOnTableSelectionListener(tblLoans, comboStatus, true);

        getContainer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                lblReturnFeedbackLabel.setText("");
            }
        });

        tblLoans.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                Long copyId = pMod.getLoanDetailTableModel().getCopyOfRow(tblLoans.getSelectedRow());
                if (copyId != null) {
                    Copy copy = copyPMod.searchCopy(copyId);
                    comboStatus.setSelectedItem(copy.getCondition());
                }
            }
        });

        btnReturnButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<Long> returnedCopies;
                Condition newCondition = null;
                if (tblLoans.getSelectedRowCount() == 1) {
                    newCondition = getSelectedCondition();
                    getController().changeCondition(tblLoans.getSelectedRow(), newCondition);
                }
                returnedCopies = getController().returnCopies(tblLoans.getSelectedRows());
                String key = "LoanDetailMainViewBase.loansOverview.returnMessage";
                if (returnedCopies.size() > 1) {
                    key += "s";
                }
                String message = Texts.get(key, Joiner.on(", ").join(returnedCopies));
                if (newCondition != null) {
                    message = message + " " + Texts.get("LoanDetailMainViewBase.loansOverview.conditionChanged", Texts.get(newCondition.getKey()));
                }
                lblReturnFeedbackLabel.setText(message);
                updateLoanOverViewSection();
                updateMakeLoanButtonVisibility();
            }
        });

        txtCustomerSearch.getDocument().addDocumentListener(new DocumentListener() {
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

            private void search() {
                if (!txtCustomerSearch.getText().equals(defaultSearchValue)) {
                    logger.debug("search for value {}", txtCustomerSearch.getText());
                    getController().filterCustomers(txtCustomerSearch.getText());
                }
            }
        });

        hideTextOnFocusListener = new HideTextOnFocusListener(txtCustomerSearch, defaultSearchValue);

        txtCustomerSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                txtCustomerSearch.selectAll();
            }
        });

        Repository.getInstance().getCustomerPMod().getCustomerListModel().addListDataListener(new ListDataListener() {

            @Override
            public void intervalRemoved(ListDataEvent arg0) {
            }

            @Override
            public void intervalAdded(ListDataEvent arg0) {
            }

            @Override
            public void contentsChanged(ListDataEvent arg0) {
                updateList(arg0);
            }

            private void updateList(ListDataEvent e) {
                // bit hackish.... don't update if only one cell was updated
                if (!(e.getIndex0() != 0 && e.getIndex0() == e.getIndex1())) {
                    // call later, cause no mutation allowed in Listener
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            // update ReferencedObject
                            listCustomer.setSelectedIndex(0);
                            Loan firstLoan = pMod.getFirstLoanOfCustomer(listCustomer.getSelectedValue());
                            switchToLoan(firstLoan, false);
                        }
                    });
                }
            }

        });

        listCustomer.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                Loan firstLoanOfCustomer = pMod.getFirstLoanOfCustomer(listCustomer.getSelectedValue());
                switchToLoan(firstLoanOfCustomer, false);
            }
        });

        btnCreateLoan.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveLoan();
            }

        });

        txtCopyId.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchCopy(txtCopyId.getNumber());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                searchCopy(txtCopyId.getNumber());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchCopy(txtCopyId.getNumber());
            }

            private void searchCopy(Long copyId) {
                if (copyId != null) {
                    currentSelectedCopy = copyPMod.searchCopy(copyId);
                    updateNewLoanSection();
                    updateMakeLoanButtonVisibility();
                }
            }
        });

        btnLinkToLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (copyPMod.isCopyLent(currentSelectedCopy)) {
                    switchToLoan(copyPMod.getCurrentLoan(currentSelectedCopy), true);
                }
            }
        });
    }

    private Customer getCurrentCustomer() {
        if (getReferenceObject() != null) {
            return getReferenceObject().getCustomer();
        }
        return listCustomer.getSelectedValue();
    }

    private void saveLoan() {
        if (currentSelectedCopy == null) {
            lblAvailabilityValue.setText(Texts.get("LoanDetailMainViewBase.newLoan.noInventoryNumber"));
        } else {
            ValidationResult validationResult = getController().validateLoan(currentSelectedCopy, getCurrentCustomer());
            if (!validationResult.hasErrors()) {
                getController().saveLoan(currentSelectedCopy, getCurrentCustomer());
                txtCopyId.setText("");
            }
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if (arg0.getClass().equals(CopyPMod.class)) {
            updateNewLoanSection();
            updateLoanOverViewSection();

            Long copyId = pMod.getLoanDetailTableModel().getCopyOfRow(tblLoans.getSelectedRow());
            if (copyId != null) {
                Copy copy = copyPMod.searchCopy(copyId);
                comboStatus.setSelectedItem(copy.getCondition());
            }
        }
    }
}
