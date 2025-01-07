package easv.intgrpmovie.be;

public class Movie {
    private int id;
    private String name;
    private int rating;
    private String filelink;
    private String lastView;


    public Movie(int id, String name, int rating, String filelink, String lastView) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.filelink = filelink;
        this.lastView = lastView;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFilelink() {
        return filelink;
    }

    public void setFilelink(String filelink) {
        this.filelink = filelink;
    }

    public String getLastView() {
        return lastView;
    }

    public void setLastView(String lastView) {
        this.lastView = lastView;
    }
}
