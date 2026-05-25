package library.service;

import library.dao.BookDAO;
import library.dao.IssuedBookDAO;
import library.dao.UserDAO;
import library.model.Book;
import library.model.IssuedBook;
import library.model.User;

import java.util.List;

/**
 * LibraryService - Service/Business Logic Layer.
 *
 * Sits between the UI (Main/ConsoleUI) and the DAO layer.
 * Coordinates multi-DAO operations, applies business rules,
 * and returns human-readable success/error messages.
 *
 * This separation ensures:
 *   - DAOs remain purely data-access focused
 *   - Business rules live in one place (easy to change)
 *   - UI layer stays thin (no SQL, no business logic)
 */
public class LibraryService {

    // ── DAO dependencies (injected via constructor) ───────────────────────────
    private final BookDAO       bookDAO;
    private final UserDAO       userDAO;
    private final IssuedBookDAO issuedBookDAO;

    public LibraryService() {
        this.bookDAO       = new BookDAO();
        this.userDAO       = new UserDAO();
        this.issuedBookDAO = new IssuedBookDAO();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BOOK OPERATIONS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Validates and adds a book.
     * Business Rule: title and author must be non-empty; copies must be > 0.
     */
    public String addBook(String title, String author, String genre, int copies, int year) {
        // ── Input validation ──
        if (title == null || title.trim().isEmpty())
            return "ERROR: Book title cannot be empty.";
        if (author == null || author.trim().isEmpty())
            return "ERROR: Author name cannot be empty.";
        if (copies <= 0)
            return "ERROR: Number of copies must be greater than zero.";
        if (year < 1000 || year > 2100)
            return "ERROR: Published year seems invalid.";

        Book book = new Book(title.trim(), author.trim(), genre.trim(), copies, year);
        boolean success = bookDAO.addBook(book);
        return success ? "SUCCESS: Book '" + title + "' added to library."
                       : "ERROR: Could not add book. Please try again.";
    }

    /** Returns all books. Returns empty list if none exist. */
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    /** Searches by ID first; if not found, falls back to title search. */
    public List<Book> searchBook(String query) {
        List<Book> results = new java.util.ArrayList<>();

        // Try numeric ID search first
        try {
            int id = Integer.parseInt(query.trim());
            Book b = bookDAO.getBookById(id);
            if (b != null) {
                results.add(b);
                return results;
            }
        } catch (NumberFormatException ignored) {
            // Not a number — proceed to title search
        }

        // Fall back to title search
        return bookDAO.searchByTitle(query.trim());
    }

    /**
     * Updates book details.
     * Business Rule: Fields left blank retain their current values.
     */
    public String updateBook(int bookId, String title, String author,
                             String genre, int copies, int year) {
        Book existing = bookDAO.getBookById(bookId);
        if (existing == null)
            return "ERROR: No book found with ID " + bookId + ".";

        // Only update fields that were provided (non-empty / non-zero)
        if (title  != null && !title.trim().isEmpty())  existing.setTitle(title.trim());
        if (author != null && !author.trim().isEmpty()) existing.setAuthor(author.trim());
        if (genre  != null && !genre.trim().isEmpty())  existing.setGenre(genre.trim());
        if (copies  > 0)  existing.setTotalCopies(copies);
        if (year    > 0)  existing.setPublishedYear(year);

        boolean success = bookDAO.updateBook(existing);
        return success ? "SUCCESS: Book ID " + bookId + " updated."
                       : "ERROR: Update failed. Please try again.";
    }

    /**
     * Deletes a book.
     * Business Rule: Cannot delete a book that is currently issued.
     */
    public String deleteBook(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null)
            return "ERROR: No book found with ID " + bookId + ".";

        // Check for active issue records
        List<IssuedBook> active = issuedBookDAO.getHistoryByBook(bookId);
        boolean hasActive = active.stream().anyMatch(ib -> "ISSUED".equals(ib.getStatus()));
        if (hasActive)
            return "ERROR: Cannot delete '" + book.getTitle() + "' — it has active issue records. Return all copies first.";

        boolean success = bookDAO.deleteBook(bookId);
        return success ? "SUCCESS: Book '" + book.getTitle() + "' deleted."
                       : "ERROR: Deletion failed. Please try again.";
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  USER OPERATIONS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Validates and registers a new user.
     */
    public String addUser(String name, String email, String phone, String type) {
        if (name == null || name.trim().isEmpty())
            return "ERROR: Name cannot be empty.";
        if (email == null || !email.contains("@"))
            return "ERROR: Please enter a valid email address.";
        if (phone == null || phone.trim().length() < 8)
            return "ERROR: Phone number must be at least 8 digits.";

        String userType = (type == null || type.trim().isEmpty()) ? "Student" : type.trim();
        User user = new User(name.trim(), email.trim(), phone.trim(), userType);
        boolean success = userDAO.addUser(user);
        return success ? "SUCCESS: User '" + name + "' registered successfully."
                       : "ERROR: Could not register user. Email may already be in use.";
    }

    /** Returns all registered users. */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ISSUE / RETURN OPERATIONS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Issues a book to a user.
     * Business Rules enforced:
     *   1. Book must exist
     *   2. User must exist
     *   3. At least 1 copy must be available
     *   4. User must not already have this book issued
     */
    public String issueBook(int bookId, int userId) {
        // Rule 1 – Book exists?
        Book book = bookDAO.getBookById(bookId);
        if (book == null)
            return "ERROR: No book found with ID " + bookId + ".";

        // Rule 2 – User exists?
        if (!userDAO.userExists(userId))
            return "ERROR: No user found with ID " + userId + ".";

        // Rule 3 – Copies available?
        if (book.getAvailableCopies() <= 0)
            return "ERROR: No available copies of '" + book.getTitle() + "'. All copies are issued.";

        // Rule 4 – Duplicate issuance?
        if (issuedBookDAO.isAlreadyIssued(bookId, userId))
            return "ERROR: User " + userId + " already has '" + book.getTitle() + "' issued.";

        // Both DAO operations must succeed (pseudo-transaction)
        boolean recorded  = issuedBookDAO.issueBook(bookId, userId);
        boolean decremented = recorded && bookDAO.decrementAvailableCopies(bookId);

        if (decremented)
            return "SUCCESS: '" + book.getTitle() + "' issued to User ID " + userId + ".";
        else
            return "ERROR: Issue operation failed. Please try again.";
    }

    /**
     * Processes a book return.
     * Business Rules:
     *   1. Book must exist
     *   2. User must exist
     *   3. An active issue record (ISSUED) must exist for this pair
     */
    public String returnBook(int bookId, int userId) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null)
            return "ERROR: No book found with ID " + bookId + ".";

        if (!userDAO.userExists(userId))
            return "ERROR: No user found with ID " + userId + ".";

        if (!issuedBookDAO.isAlreadyIssued(bookId, userId))
            return "ERROR: No active issue record found for this book-user pair.";

        boolean returned     = issuedBookDAO.returnBook(bookId, userId);
        boolean incremented  = returned && bookDAO.incrementAvailableCopies(bookId);

        if (incremented)
            return "SUCCESS: '" + book.getTitle() + "' returned by User ID " + userId + ".";
        else
            return "ERROR: Return operation failed. Please try again.";
    }

    /** Returns all currently-issued books with user/book details. */
    public List<IssuedBook> getIssuedBooks() {
        return issuedBookDAO.getAllIssuedBooks();
    }
}
