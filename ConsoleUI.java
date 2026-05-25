package library.ui;

import library.model.Book;
import library.model.IssuedBook;
import library.model.User;

import java.util.List;

/**
 * ConsoleUI - Utility class for all console display operations.
 *
 * Centralises all print logic so Main.java stays clean.
 * Demonstrates Single Responsibility Principle.
 */
public class ConsoleUI {

    // ── Divider widths ────────────────────────────────────────────────────────
    private static final String THIN  = "─".repeat(80);
    private static final String THICK = "═".repeat(80);

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN MENU
    // ─────────────────────────────────────────────────────────────────────────

    public static void showMainMenu() {
        System.out.println("\n" + THICK);
        System.out.println("         📚  LIBRARY MANAGEMENT SYSTEM  📚");
        System.out.println(THICK);
        System.out.println("  [1]  Add Book");
        System.out.println("  [2]  View All Books");
        System.out.println("  [3]  Search Book (by ID or Title)");
        System.out.println("  [4]  Issue Book");
        System.out.println("  [5]  Return Book");
        System.out.println("  [6]  Delete Book");
        System.out.println("  [7]  Update Book Details");
        System.out.println("  [8]  Add Student / User");
        System.out.println("  [9]  View All Issued Books");
        System.out.println("  [0]  Exit System");
        System.out.println(THICK);
        System.out.print("  ➤  Enter your choice: ");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BOOK TABLE
    // ─────────────────────────────────────────────────────────────────────────

    public static void showBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("\n  [!] No books found.");
            return;
        }
        System.out.println("\n" + THIN);
        System.out.println(Book.tableHeader());
        System.out.println(THIN);
        books.forEach(b -> System.out.println(b));
        System.out.println(THIN);
        System.out.println("  Total: " + books.size() + " book(s).");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ISSUED BOOKS TABLE
    // ─────────────────────────────────────────────────────────────────────────

    public static void showIssuedBooks(List<IssuedBook> issued) {
        if (issued == null || issued.isEmpty()) {
            System.out.println("\n  [!] No books are currently issued.");
            return;
        }
        System.out.println("\n" + THIN);
        System.out.println(IssuedBook.tableHeader());
        System.out.println(THIN);
        issued.forEach(ib -> System.out.println(ib));
        System.out.println(THIN);
        System.out.println("  Total active issues: " + issued.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RESULT MESSAGES
    // ─────────────────────────────────────────────────────────────────────────

    public static void showResult(String message) {
        System.out.println();
        if (message.startsWith("SUCCESS")) {
            System.out.println("  ✅  " + message);
        } else if (message.startsWith("ERROR")) {
            System.out.println("  ❌  " + message);
        } else {
            System.out.println("  ℹ️   " + message);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION HEADERS
    // ─────────────────────────────────────────────────────────────────────────

    public static void showHeader(String title) {
        System.out.println("\n" + THICK);
        System.out.println("  " + title);
        System.out.println(THICK);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FAREWELL
    // ─────────────────────────────────────────────────────────────────────────

    public static void showGoodbye() {
        System.out.println("\n" + THICK);
        System.out.println("  👋  Thank you for using Library Management System. Goodbye!");
        System.out.println(THICK + "\n");
    }
}
