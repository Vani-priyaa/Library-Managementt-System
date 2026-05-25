package library.dao;

import library.db.DBConnection;
import library.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Data Access Object for User (student/member) CRUD operations.
 * All queries use PreparedStatement to prevent SQL injection.
 */
public class UserDAO {

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Inserts a new user into the users table.
     *
     * @param user User object (without userId — auto-assigned by DB)
     * @return true if insert succeeded
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (name, email, phone, user_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getUserType());

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Duplicate email — friendly message instead of stack trace
            System.err.println("[UserDAO] Email already registered: " + user.getEmail());
            return false;
        } catch (SQLException e) {
            System.err.println("[UserDAO] addUser error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ – ALL
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retrieves all registered users ordered by user_id.
     *
     * @return ArrayList of User objects
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers error: " + e.getMessage());
        }
        return users;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ – BY ID
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retrieves a user by their primary key.
     *
     * @param userId the ID to look up
     * @return User if found, null otherwise
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            System.err.println("[UserDAO] getUserById error: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ – CHECK EXISTENCE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Checks whether a user with the given ID exists in the database.
     * Lightweight query — does not fetch all columns.
     */
    public boolean userExists(int userId) {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("[UserDAO] userExists error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /** Maps a ResultSet row to a User object */
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt   ("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("user_type"),
            rs.getString("joined_date")
        );
    }
}
