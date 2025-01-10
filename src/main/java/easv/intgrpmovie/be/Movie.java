package easv.intgrpmovie.be;

public class Movie {
    private int ID;
    private String name;
    private double rating;
    private String fileLink;
    private String lastView;

    public Movie(int ID, String name, double rating, String fileLink, String lastView) {
        this.ID = ID;
        this.name = name;
        this.rating = rating;
        this.fileLink = fileLink;
        this.lastView = lastView;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public String getLastView() {
        return lastView;
    }

    public void setLastView(String lastView) {
        this.lastView = lastView;
    }

    @Override
    public String toString() {return name + ", " + rating + ", " + lastView;}
}
