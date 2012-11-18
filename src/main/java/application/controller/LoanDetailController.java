package application.controller;

import application.core.Repository;
import application.core.Texts;

import com.jgoodies.validation.ValidationResult;

import domain.Copy;
import domain.Customer;
import domain.Library;
import domain.Loan;

public class LoanDetailController extends ControllerBase {

    public void filterCustomers(String text) {
        getRepository().getCustomerPMod().getCustomerComboBoxModel().filterContent(text);
    }

    public ValidationResult validateLoan(Long copyNr, Customer customer) {
        ValidationResult result = new ValidationResult();
        Library library = getRepository().getLibrary();

        Copy copy = null;
        if (copyNr != null) {
            copy = library.getCopyByInventoryNr(copyNr);
        }

        if (copy == null) {
            result.addError(Texts.get("validation.noCopyFound"));
        } else if (library.isCopyLent(copy)) {
            result.addError(Texts.get("validation.copyLent"));
        } else if (!library.canCustomerMakeMoreLoans(customer)) {
            result.addError(Texts.get("validation.noMoreLoansAllowed"));
        } else if (customer == null) {
            result.addError(Texts.get("validation.noCustomerSelected"));
        }

        return result;

    }

    public Loan saveLoan(Long copyId, Customer customer) {
        Copy copy = getRepository().getLibrary().getCopyByInventoryNr(copyId);
        Loan loan = getRepository().getLibrary().createAndAddLoan(customer, copy);
        getRepository().getLoansPMod().addLoan(loan);
        return loan;
    }

    public Copy searchCopy(Long copyId) {
        return getRepository().getLibrary().getCopyByInventoryNr(copyId);
    }

    public String returnCopies(int[] selectedRows) {
        String returnedCopies = "";
        for (int row : selectedRows) {
            Loan loan = Repository.getInstance().getLoansPMod().getLoanDetailTableModel().getLoan(row);
            if (loan.returnCopy()) {
                returnedCopies += loan.getCopy().getInventoryNumber() + " ";
                getRepository().getLoansPMod().updateLoan(loan);
                getRepository().getCustomerPMod().updateCustomer(loan.getCustomer());
            }
        }
        return returnedCopies;
    }
}
