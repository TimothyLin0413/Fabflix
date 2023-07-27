USE moviedb; 
DROP procedure IF EXISTS add_movie;

DELIMITER $$ 
CREATE PROCEDURE add_movie (
    IN movieTitle VARCHAR(100),
    IN releaseYear INTEGER,
    IN movieDirector VARCHAR(100), 
    IN starName VARCHAR(100), 
    IN genreName VARCHAR(32),
    OUT message VARCHAR(100))
    c: BEGIN
        -- movie 
        IF ((SELECT COUNT(*) FROM movies WHERE title = movieTitle AND movie_year = releaseYear AND director = movieDirector) > 0) THEN
            SELECT "FAILURE: Movie already exists." INTO message;
            LEAVE c;
        END IF;

        SET @movieId = concat("tt",(SELECT MAX(substring(id, 3)) FROM movies) + 1);
        INSERT INTO movies(id, title, movie_year, director) VALUES (@movieId, movieTitle, releaseYear, movieDirector);
 
        -- star
        IF ((SELECT COUNT(*) FROM stars WHERE name = starName) = 0) THEN
            SET @starId = concat("nm",(SELECT MAX(substring(id, 3)) FROM stars) + 1);
            INSERT INTO stars(id, name, birthYear) VALUES (@starId, starName, null);
        END IF;

          INSERT INTO stars_in_movies(starId, movieId) VALUES ((SELECT id FROM stars WHERE name = starName LIMIT 1), @movieId);
          
          -- genre
        IF ((SELECT COUNT(*) FROM genres WHERE name = genreName) = 0) THEN
            SET @genreId = (SELECT MAX(id) FROM genres) + 1;
            INSERT INTO genres(id, name) VALUES (@genreId, genreName);
        END IF;

        INSERT INTO genres_in_movies(genreId, movieId) VALUES ((SELECT id FROM genres WHERE name = genreName), @movieId);

    SELECT concat("INSERT SUCCESS: Movie Title : ",movieTitle, ", Star : ", starName, " Genre : ", genreName) INTO message;
    
END$$

DELIMITER ;