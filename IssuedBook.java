package library.model;

/**
 * IssuedBook - Entity class representing a book-issue transaction.
 * Links a Book to a User with issue/return date tracking.
 */
public class IssuedBook {

    // ── Fields ────────────────────────────────────────────────────────────────
    private int    issueId;
    private int    bookId;
    private int    userId;
    private String bookTitle;   // Denormalized for easy display (JOIN result)
    private String userName;    // Denormalized for easy display (JOIN result)
    private String issueDate;
    private String returnDate;  // NULL if not yet returned
    private String status;      // "ISSUED" or "RETURNED"

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Full constructor — used when reading from the database with JOINs */
    public IssuedBook(int issueId, int bookId, int userId,
                      String bookTitle, String userName,
                      String issueDate, String returnDate, String status) {
        this.issueId    = issueId;
        this.bookId     = bookId;
        this.userId     = userId;
        this.bookTitle  = bookTitle;
        this.userName   = userName;
        this.issueDate  = issueDate;
        this.returnDate = returnDate;
        this.status     = status;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getIssueId()              { return issueId; }
    public int    getBookId()               { return bookId; }
    public int    getUserId()               { return userId; }
    public String getBookTitle()            { return bookTitle; }
    public String getUserName()             { return userName; }
    public String getIssueDate()            { return issueDate; }
    public String getReturnDate()           { return returnDate; }
    public String getStatus()               { return status; }
    public void   setReturnDate(String d)   { this.returnDate = d; }
    public void   setStatus(String s)       { this.status = s; }

    // ── Display Helper ────────────────────────────────────────────────────────

    @Override
    public String toString() {
        String ret = (returnDate == null || returnDate.isEmpty()) ? "Pending" : returnDate;
        return String.format(
            "| %-5d | %-4d | %-4d | %-25s | %-15s | %-12s | %-12s | %-8s |",
            issueId, bookId, userId, bookTitle, userName, issueDate, ret, status
        );
    }

    public static String tableHeader() {
        return String.format(
            "| %-5s | %-4s | %-4s | %-25s | %-15s | %-12s | %-12s | %-8s |",
            "TxnID", "BkID", "UsrID", "Book Title", "User Name",
            "Issue Date", "Return Date", "Status"
        );
    }
}
