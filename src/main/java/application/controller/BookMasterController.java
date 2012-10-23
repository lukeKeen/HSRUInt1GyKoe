package application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.core.Repository;
import application.view.BookDetailMainView;
import application.viewModel.BookListModel;
import domain.Book;

public class BookMasterController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(BookMasterController.class);

    public void openBooks(int[] selectedIndices) {
        BookListModel booksListModel = Repository.getInstance().getBooksPMod().getBookListModel();

        for (int index : selectedIndices) {
            Book book = booksListModel.getElementAt(index);
            logger.debug("opening book view " + book.getName());
            try {
                new BookDetailMainView((Book) book.clone());
            } catch (CloneNotSupportedException e) {
                logger.error("Clone for Book.java is not possible", e);
            }
        }

    }

    public void openNewBook() {
        new BookDetailMainView(null);
    }

}
