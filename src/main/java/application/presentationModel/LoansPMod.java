package application.presentationModel;

import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.core.Repository;
import application.viewModel.LoanDetailTableModel;
import application.viewModel.LoanTableModel;
import domain.Loan;

public class LoansPMod extends pModBase {
    private final LoanTableModel loanTableModel;
    private final TableRowSorter<LoanTableModel> loanTableRowSorter;
    private final Logger logger = LoggerFactory.getLogger(LoansPMod.class);
    private final LoanDetailTableModel loanDetailTableModel;

    public LoansPMod() {
        loanTableModel = new LoanTableModel(Repository.getInstance().getLibrary().getLoans());
        loanTableRowSorter = new TableRowSorter<LoanTableModel>(loanTableModel);
        loanDetailTableModel = new LoanDetailTableModel(null);
    }

    public LoanDetailTableModel getLoanDetailTableModel() {
        return loanDetailTableModel;
    }

    public LoanTableModel getLoanTableModel() {
        return loanTableModel;
    }

    public TableRowSorter<LoanTableModel> getLoanTableRowSorter() {
        return loanTableRowSorter;
    }

    public void setSearchString(String searchText) {
        logger.debug("Filter loans table for \"{}\"", searchText);
        loanTableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));

    }

    public void addLoan(Loan loan) {
        loanTableModel.addLoan(loan);
    }

    public void updateLoan(Loan loan) {
        loanTableModel.updateLoan(loan);
        loanDetailTableModel.updateLoans(loan);
    }
}
