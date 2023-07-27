package Parser.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

public class XmlParser {

    List<Movie> listMovies = new ArrayList<>();
    List<Actor> listActors = new ArrayList<>();
    List<ActorMovie> listActorMovie = new ArrayList<>();
    List<String> genres = new ArrayList<>();

    Document main;
    Document actors;
    Document casts;

    public void runExample() {

        // parse the xml file and get the dom object (done)
        parseXmlFile();

        // get each employee element and create a Employee object
        parseMainDocument();

        parseActorsDocument();

        parseCastsDocument();

        // iterate through the list and print the data
//        printData();

        connectDB();
    }

    private String getGenres(String str) {
        if (str == null) { return "Miscellaneous"; }
        if (str.contains("dram") || str.contains("draam")) { return "Drama"; }
        else if (str.contains("rom")) { return "Romance"; }
        else if (str.contains("mus") || str.contains("muus")) { return "Music"; }
        else if (str.contains("myst")) { return "Mystery"; }
        else if (str.contains("advt")) { return "Adventure"; }
        else if (str.contains("doc") || str.contains("nat")) { return "Documentary"; }
        else if (str.contains("com")) { return "Comedy"; }
        else if (str.contains("act") || str.contains("axtn")) { return "Action"; }
        else if (str.contains("wes")) { return "Western"; }
        else if (str.contains("fan")) { return "Fantasy"; }
        else if (str.contains("sc") || str.contains("fi")) { return "Sci-Fi"; }
        else if (str.contains("hor")) { return "Horror"; }
        else if (str.contains("bio")) { return "Biography"; }
        else if (str.contains("his")) { return "History"; }
        else if (str.contains("cri")) { return "Crime"; }
        else if (str.contains("ad")) { return "Adult"; }
        else if (str.contains("fam")) { return "Family"; }
        else { return "Miscellaneous"; }
    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            main = documentBuilder.parse("mains243.xml");
            actors = documentBuilder.parse("actors63.xml");
            casts = documentBuilder.parse("casts124.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseMainDocument() {
        Element documentElement = main.getDocumentElement();

        NodeList dirFilmList = documentElement.getElementsByTagName("directorfilms");
        if (dirFilmList != null) {
            for (int i = 0; i < dirFilmList.getLength(); i++) {
                Element element = (Element) dirFilmList.item(i);
                NodeList dir = element.getElementsByTagName("director");

                String director = "";
                if (dir != null) {
                    Element direct = (Element) dir.item(0);
                    director = getTextValue(direct, "dirname");
                }

                NodeList films = element.getElementsByTagName("film");

                if (films != null) {
                    for (int j = 0; j < films.getLength(); j++) {
                        Element film = (Element) films.item(j);
                        Movie movie = new Movie(director, "", 0, "");
                        parseFilm(film, movie);
                        listMovies.add(movie);
                    }
                }
            }
        }
    }

    private void parseActorsDocument() {
        Element documentElement = actors.getDocumentElement();

        NodeList actors = documentElement.getElementsByTagName("actor");
        if (actors != null) {
            for (int i = 0; i < actors.getLength(); i++) {
                Element actor = (Element) actors.item(i);
                listActors.add(parseActor(actor));
            }
        }
    }

    private void parseCastsDocument() {
        Element documentElement = casts.getDocumentElement();

        NodeList films = documentElement.getElementsByTagName("filmc");
        if (films != null) {
            for (int i = 0; i < films.getLength(); i++) {
                Element actor = (Element) films.item(i);

                NodeList roles = actor.getElementsByTagName("m");

                if (roles != null) {
                    for (int j = 0; j < roles.getLength(); j++) {
                        Element role = (Element) roles.item(j);
                        listActorMovie.add(parseAM(role));
                    }
                }
            }
        }
    }

    private void parseFilm(Element element, Movie movie) {
        String title = getTextValue(element, "t");
        int year = getIntValue(element, "year");
        String genre = getTextValue(element, "cat");
        if (genre != null) {
            genre = genre.toLowerCase();
        }
        genre = getGenres(genre);


        movie.changeFilmTitle(title);
        movie.changeFilmYear(year);
        movie.changeGenre(genre);
    }

    private Actor parseActor(Element element) {
        String name = getTextValue(element, "stagename");
        int birthYear = getIntValue(element, "dob");

        return new Actor(name, birthYear);
    }

    private ActorMovie parseAM(Element element) {
        String actorName = getTextValue(element, "a");
        String title = getTextValue(element, "t");

        return new ActorMovie(actorName, title);
    }

    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0).getFirstChild();
            if (node == null) {
                textVal = null;
            } else {
                textVal = node.getNodeValue();
                if (textVal != null) {
                    textVal = textVal.strip();
                }
            }
        }
        return textVal;
    }

    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        try {
            return parseInt(getTextValue(ele, tagName));
        } catch (Exception e) {
            return 0;
        }
    }

    private void printData() {

        System.out.println("Total parsed " + listMovies.size() + " employees");

        for (Movie movie : listMovies) {
            System.out.println("\t" + movie.toString());
        }
    }

    public void connectDB() {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "foodASMR123:)");
            statement = connection.createStatement();

            // for movies
            String updateQuery = "INSERT INTO genres(name) VALUES ('Miscellaneous')";
            statement.executeUpdate(updateQuery);

            String searchQuery = "SELECT * FROM movies ORDER BY id DESC LIMIT 1";
            ResultSet rs = statement.executeQuery(searchQuery);

            int idCount = 0;
            while (rs.next()) {
                String id = rs.getString("id");
                idCount = parseInt(id.substring(2));
            }

            List<String> genreList = new ArrayList<>();
            searchQuery = "SELECT * FROM genres";
            rs = statement.executeQuery(searchQuery);

            while (rs.next()) {
                genreList.add(rs.getString("name"));
            }

            HashMap<String, Integer> titles = new HashMap<String, Integer>();
            searchQuery = "SELECT title FROM movies";
            rs = statement.executeQuery(searchQuery);

            while (rs.next()) {
                titles.put(rs.getString("title"), 1);
            }

            String insertMovie = "INSERT INTO movies(id, title, movie_year, director) VALUES (?,?,?,?)";
            String insertGenre = "INSERT INTO genres_in_movies(genreId, movieId) VALUES (?,?)";
            PreparedStatement movieStatements = connection.prepareStatement(insertMovie);
            PreparedStatement genreStatements = connection.prepareStatement(insertGenre);
            for (Movie movie : listMovies) {
                if (checkNullMovie(movie)) {
                    String filmTitle = replaceSingleQuotes(movie.getFilmTitle());

                    if (!titles.containsKey(filmTitle)) {
                        idCount++;
                        String movieId = "tt" + idCount;
                        int genreId = genreList.indexOf(movie.getGenre()) + 1;
                        movieStatements.setString(1, movieId);
                        movieStatements.setString(2, filmTitle);
                        movieStatements.setInt(3, movie.getFilmYear());
                        movieStatements.setString(4, movie.getDirectorName());
                        movieStatements.addBatch();

                        genreStatements.setInt(1, genreId);
                        genreStatements.setString(2, movieId);
                        genreStatements.addBatch();
                    }
                } else { System.out.println(movie.toString()); }
            }

            int[] movieResults = movieStatements.executeBatch();
            System.out.println("Movies Inserted: " + movieResults.length);

            int[] genreResults = genreStatements.executeBatch();
            System.out.println("Genres Inserted: " + genreResults.length);

            System.out.println("Movies are done");

            // for actors
            searchQuery = "SELECT * FROM stars ORDER BY id DESC LIMIT 1";
            rs = statement.executeQuery(searchQuery);
            while (rs.next()) {
                String id = rs.getString("id");
                idCount = parseInt(id.substring(2));
            }

            HashMap<String, Integer> stars = new HashMap<String, Integer>();
            searchQuery = "SELECT * FROM stars";
            rs = statement.executeQuery(searchQuery);

            while (rs.next()) {
                stars.put(rs.getString("name"), rs.getInt("birthYear"));
            }

            String insertStar = "INSERT INTO stars(id, name, birthYear) VALUES (?,?,?)";
            String insertName = "INSERT INTO stars(id, name) VALUES (?,?)";
            PreparedStatement starStatements = connection.prepareStatement(insertStar);
            PreparedStatement nameStatements = connection.prepareStatement(insertName);
            for (Actor actor : listActors) {
                if (actor.getName() != null) {
                    String actorName = replaceSingleQuotes(actor.getName());

                    if (!stars.containsKey(actorName) || stars.get(actorName) != actor.getBirthYear()) {
                        idCount++;
                        String starId = "nm" + idCount;
                        if (actor.getBirthYear() != 0) {
                            starStatements.setString(1, starId);
                            starStatements.setString(2, actorName);
                            starStatements.setInt(3, actor.getBirthYear());
                            starStatements.addBatch();
                        } else {
                            nameStatements.setString(1, starId);
                            nameStatements.setString(2, actorName);
                            nameStatements.addBatch();
                        }
                    }
                } else {
                    System.out.println(actor.toString());
                }
            }

            int[] starResults = starStatements.executeBatch();
            System.out.println("Stars Inserted: " + starResults.length);

            int[] nameResults = nameStatements.executeBatch();
            System.out.println("Names Inserted: " + nameResults.length);

            System.out.println("Actors are done");

            // for actor in movie (check to see if actors exist, if not add them to stars)
            HashMap<String, String> starMovie = new HashMap<String, String>();
            searchQuery = "SELECT * FROM stars_in_movies";
            rs = statement.executeQuery(searchQuery);

            while (rs.next()) {
                starMovie.put(rs.getString("starId"), rs.getString("movieId"));
            }

            HashMap<String, String> starInfo = new HashMap<String, String>();
            searchQuery = "SELECT * FROM stars";
            rs = statement.executeQuery(searchQuery);

            while (rs.next()) {
                starInfo.put(rs.getString("name"), rs.getString("id"));
            }

            HashMap<String, String> movieInfo = new HashMap<String, String>();
            searchQuery = "SELECT * FROM movies";
            rs = statement.executeQuery(searchQuery);

            while (rs.next()) {
                movieInfo.put(rs.getString("title"), rs.getString("id"));
            }

            String insertAM = "INSERT INTO stars_in_movies(starId, movieId) VALUES (?,?)";
            PreparedStatement amStatements = connection.prepareStatement(insertAM);
            for (ActorMovie am : listActorMovie) {
                if (am.getMovieTitle() != null && am.getActorName() != null) {
                    String starId = starInfo.get(am.getActorName());
                    String movieId = movieInfo.get(am.getMovieTitle());
                    if (starId != null && movieId != null && starMovie.get(starId) != movieId) {
                        amStatements.setString(1, starId);
                        amStatements.setString(2, movieId);
                        amStatements.addBatch();
                    }
                } else { System.out.println(am.toString()); }
            }

            int[] amResults = amStatements.executeBatch();
            System.out.println("AM Inserted: " + amResults.length);

            rs.close();

            System.out.println("Everything is done");

        } catch (Exception excep) {
            excep.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se) {}
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public Boolean checkNullMovie(Movie movie) {
        if (movie.getFilmYear() == 0 || movie.getFilmTitle() == null || movie.getDirectorName() == null) {
            return false;
        }
        return true;
    }

    public String replaceSingleQuotes(String str) {
        String fString = "";
        int count = 0;
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\'') {
                if (i == 0 || str.charAt(i-1) != '\\') {
                    count++;
                    index.add(i);
                }
            }
        }

        int j = 0;
        int start = 0;
        while (j < count) {
            if (index.get(j) == 0) {
                fString += "\\";
            } else { fString += str.substring(start, index.get(j)) + "\\"; }
            start = index.get(j);
            j++;
        }
        fString += str.substring(start);

//        System.out.println(fString);
        return fString;
    }

    public String replaceDoubleQuotes(String str) {
        String fString = "";
        int count = 0;
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\"') {
                if (i == 0 || str.charAt(i-1) != '\\') {
                    count++;
                    index.add(i);
                }
            }
        }

        int j = 0;
        int start = 0;
        while (j < count) {
            if (index.get(j) == 0) {
                fString += "\\";
            } else { fString += str.substring(start, index.get(j)) + "\\"; }
            start = index.get(j);
            j++;
        }
        fString += str.substring(start);

        return fString;
    }

    public static void main(String[] args) {
        // create an instance
        XmlParser domParser = new XmlParser();

        // call run example
        domParser.runExample();
    }

}