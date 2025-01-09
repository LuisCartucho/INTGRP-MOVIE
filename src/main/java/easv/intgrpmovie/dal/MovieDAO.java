package easv.intgrpmovie.dal;

import easv.intgrpmovie.be.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
