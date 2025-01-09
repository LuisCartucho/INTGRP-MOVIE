package easv.intgrpmovie.dal;

import easv.intgrpmovie.be.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    private DBConnection conn = new DBConnection();

    // Method to retrieve movies from the database
    public List<Movie> getMovie() {
        List<Movie> movies = new ArrayList<>();
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT * FROM Movie";
            PreparedStatement stmnt = c.prepareStatement(sql);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("name");
                int rating = rs.getInt("rating");
                String fileLink = rs.getString("fileLink");
                String lastView = rs.getString("lastView");
                Movie movie = new Movie(id, name, rating, fileLink, lastView);
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movies;
    }

    public int insertMovie(String title, int rating, String fileLink, Date lastView) throws SQLException {
        String insertMovieQuery = "INSERT INTO Movie (name, rating, fileLink, lastView) VALUES (?, ?, ?, ?)";
        try (Connection c = conn.getConnection();
             PreparedStatement stmnt = c.prepareStatement(insertMovieQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmnt.setString(1, title);
            stmnt.setInt(2, rating);
            stmnt.setString(3, fileLink);
            stmnt.setDate(4, lastView);
            stmnt.executeUpdate();
            ResultSet generatedKeys = stmnt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);  // Return the generated movieId
            }
        }
        return -1;  // In case movie was not inserted
    }

    public void updateLastView(int movieId, Date lastView) throws SQLException {
        String updateLastViewQuery = "UPDATE Movie SET lastView = ? WHERE id = ?";
        try (Connection c = conn.getConnection();
             PreparedStatement stmnt = c.prepareStatement(updateLastViewQuery)) {
            stmnt.setDate(1, lastView);
            stmnt.setInt(2, movieId);
            stmnt.executeUpdate();
        }
    }
}
