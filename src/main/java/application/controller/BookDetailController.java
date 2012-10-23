package application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Book;

public class BookDetailController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(BookDetailController.class);

    /**
     * @return <code>true</code> if the book was successfully persisted and the
     *         calling dialog can be closed
     */
    public boolean saveBook(Book book) {
        // TODO implement
        logger.info("Persisting book update: {}", book.toString());
        return true;
    }

}
