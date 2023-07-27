package Parser.java;


public class ActorMovie {

    private final String actorName;

    private final String movieTitle;

    public ActorMovie(String actorName, String movieTitle) {
        this.actorName = actorName;
        this.movieTitle = movieTitle;
    }

    public String getActorName() {
        return actorName;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String toString() {
        return "Actor Name: " + getActorName() + ", " +
                "Movie Title: " + getMovieTitle() + ".";
    }
}