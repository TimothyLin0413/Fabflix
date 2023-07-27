package Parser.java;

public class Movie {

    private String directorName;

    private String filmTitle;

    private int filmYear;

    private String genre;

    public Movie(String directorName, String filmTitle, int filmYear, String genre) {
        this.directorName = directorName;
        this.filmTitle = filmTitle;
        this.filmYear = filmYear;
        this.genre = genre;
    }

    public String getDirectorName() {
        return directorName;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public void changeFilmTitle(String newTitle) {
        filmTitle = newTitle;
    }

    public int getFilmYear() {
        return filmYear;
    }

    public void changeFilmYear(int newYear) {
        filmYear = newYear;
    }

    public String getGenre() {
        return genre;
    }

    public void changeGenre(String newGenre) {
        genre = newGenre;
    }

    public String toString() {
        return "DirName:" + getDirectorName() + ", " +
                "Title:" + getFilmTitle() + ", " +
                "Year:" + getFilmYear() + ", " +
                "Genre:" + getGenre() + ".";
    }
}