package application.presentationModel;

import java.util.ArrayList;
import java.util.List;

import application.presentationModel.componentModel.CustomerListModel;
import domain.Customer;
import domain.Loan;

public class CustomerPMod extends AbstractPresentationModel {

    private CustomerListModel customerListModel = new CustomerListModel();

    public CustomerPMod() {
        super();
    }

    public CustomerListModel getCustomerListModel() {
        return customerListModel;
    }

    public void setCustomerListModel(CustomerListModel customerListModel) {
        this.customerListModel = customerListModel;
    }

    public void updateCustomer(Customer customer) {
        customerListModel.updateCustomer(customer);
    }

    public boolean canCustomerMakeMoreLoans(Customer customer) {
        return library.canCustomerMakeMoreLoans(customer);
    }

    public List<Loan> getCustomerOpenLoans(Customer customer) {
        List<Loan> result = new ArrayList<Loan>();
        for (Loan loan : library.getCustomerLoans(customer)) {
            if (loan.isLent()) {
                result.add(loan);
            }
        }
        return result;
    }

}
