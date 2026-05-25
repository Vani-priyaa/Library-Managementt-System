package library.model;

/**
 * User - Entity class representing a library member (student/staff).
 * Acts as a base class — can be extended to StudentUser, FacultyUser, etc.
 * Demonstrates Encapsulation and OOP design for future Inheritance.
 */
public class User {

    // ── Fields ────────────────────────────────────────────────────────────────
    private int    userId;
    private String name;
    private String email;
    private String phone;
    private String userType;   // e.g., "Student", "Faculty", "Staff"
    private String joinedDate; // Stored as VARCHAR in DB, formatted on display

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Full constructor — used when reading from the database */
    public User(int userId, String name, String email,
                String phone, String userType, String joinedDate) {
        this.userId     = userId;
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
        this.userType   = userType;
        this.joinedDate = joinedDate;
    }

    /** Partial constructor — used when registering a new user (ID auto-generated) */
    public User(String name, String email, String phone, String userType) {
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.userType = userType;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getUserId()             { return userId; }
    public void   setUserId(int id)       { this.userId = id; }

    public String getName()               { return name; }
    public void   setName(String n)       { this.name = n; }

    public String getEmail()              { return email; }
    public void   setEmail(String e)      { this.email = e; }

    public String getPhone()              { return phone; }
    public void   setPhone(String p)      { this.phone = p; }

    public String getUserType()           { return userType; }
    public void   setUserType(String t)   { this.userType = t; }

    public String getJoinedDate()         { return joinedDate; }
    public void   setJoinedDate(String d) { this.joinedDate = d; }

    // ── Display Helper ────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-20s | %-25s | %-12s | %-10s | %-12s |",
            userId, name, email, phone, userType, joinedDate
        );
    }

    public static String tableHeader() {
        return String.format(
            "| %-4s | %-20s | %-25s | %-12s | %-10s | %-12s |",
            "ID", "Name", "Email", "Phone", "Type", "Joined"
        );
    }
}
