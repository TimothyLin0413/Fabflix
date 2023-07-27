## CS 122B Project 

This project shows how frontend and backend are separated by implementing a movie list page, a star page, and a single movie page with movie list.

### What we do:

### Preparation done before the demo:
1. ssh onto AWS instance.
2. on AWS instance, drop the MySQL database `moviedb`.
3. removed other git repo from the AWS instance and start with a freshly cloned repo.
4. prepared movie-data.sql and store it under `/home/ubuntu/`.
5. made sure MySQL and Tomcat are running on AWS instance.

### Commit check:
1. Clone git repo to AWS instance: `git clone https://github.com/uci-jherold2-teaching/cs122b-fall-team-18.git`. 
2. cd` into repo and run `git log`, show our latest commit.

### Build a MySQL database and populate data on AWS MySQL:
1. show all databases, should not have `moviedb` now:
mysql -u root -p -e "show databases;"
2. build `moviedb`:
mysql -u root -p < createtable.sql
3. populate the data:
mysql -u root -p --database=moviedb < /home/ubuntu/movie-data.sql
4. show the data counts:
mysql -u root -p -e "use moviedb;select count(*) from stars;select count(*) from movies;"

### Deploy your web application on AWS instance:
1. inside repo, where the pom.xml file locates, we build the war file:
mvn package
2. show tomcat web apps, it should NOT have the war file yet:
ls -lah /var/lib/tomcat9/webapps/
3. copy newly built war file:
cp ./target/*.war /var/lib/tomcat9/webapps/
4. show tomcat web apps, it should now have the new war file:
ls -lah /var/lib/tomcat9/webapps/

### Show website in the web browser:
1. Refresh the tomcat manager page. You should see a new project (just deployed): project 1.
2. Click the project link, which goes to your website's landing page (could be the Movie List Page).
3. Navigate to Movie List Page. Scroll up and down to show all 20 movies if needed.
4. On the Movie List Page, click on a movie title hyperlink. Should jump to Single Movie Page. Show all information on the Single Movie Page.
5. On the Single Movie Page, click on a star name hyperlink. Should jump to Single Star Page. Show all information on Single Star Page.
6. On the Single Star Page, return to Movie List Page, without using browser history. You can do this by clicking a button or link.
7. Repeat steps 4 - 6: this time click a star name (to Single Star Page) then click a movie played by that star (to Single Movie Page), and return to Movie List Page.
Done.
Here is an example demo video: `https://youtu.be/DKNoMArZEj0` 

### Brief Explanation
- `MovieListServlet.java` is a Java servlet that talks to the database and get the movie list. It returns a list of movies in the JSON format.
- `SingleMovieServlet.java` is a Java servlet that talks to the database and get information about one Movie.
- `SingleStarServlet.java` is a Java servlet that talks to the database and get information about one Star and all the movie this Star performed. It returns a list of Movies in the JSON format.
- `[other]Servlet.java` is a Java servlet that talks to the database and get the desire result. It returns a list of the desire result in the JSON format.

- `movie_list.js` is the main Javascript file that initiates an HTTP GET request to the `MovieListServlet`. After the response is returned, `movie_list.js` populates the table using the data it gets.
- `index.html` is the main HTML file that imports jQuery, Bootstrap, and `movie_list.js`. It also contains the initial skeleton for the table.

- `[other].js` is the Javascript file that initiates an HTTP GET request to the `[Other]Servlet`. After the response is returned, `[other].js` populates the table using the data it gets.
- `[other].html` is the HTML file that imports jQuery, Bootstrap, and `[Other].js`. It also contains the initial skeleton for the movies table.


Project 2 distribution
- Nick    - 70% backend (src including Login class, User class, Session class, Shopping Cart class)
          - 30% on frontend (implemented payment, sale, shopping cart page)
- Timothy - 30% backend (src including MovieList class, Index class)
          - 70% on frontend (implemented index page, movie list page, single-movie, single-star page)


Project 2 demo Video: `https://youtu.be/a6UJUsjf0ug` 


Project 3 distribution
- Nick    - Added reCAPTCHA, encrypted password and tested it locally, imported large XML data files into Fabflix database
          - implemented parsing optimization strategies 
- Timothy - Added HTTPS, made sure all query used PreparedStatement instead of string format, 
          - encrypted password on AWS, implemented dashboard pages using stored procedure function

Deployment:
1. Commit check:
- git clone your git repo to AWS instance: `git clone <repo url>`. This step is to make sure you have a clean repo.
cd into your repo and run `git log`, show your latest commit.
2. Show database and data on AWS MySQL:
- show your data counts:
mysql -u mytestuser -p -e "use moviedb;select count(*) from stars;select count(*) from movies;"
Should not contain the data from XML yet.
- show your encrypted passwords: mysql -u mytestuser -p -e "use moviedb;select * from customers;"
- show you do not have movies that will be entered:  mysql -u mytestuser -p -e "use moviedb;select * from movies where title like 'movie%';"
3. Deploy your web application on AWS instance:
- inside your repo, where the pom.xml file locates, build the war file:
mvn package
- show tomcat web apps, it should NOT have the war file yet:
ls -lah /var/lib/tomcat9/webapps/
- copy your newly built war file:
cp ./target/*.war /var/lib/tomcat9/webapps/
- show tomcat web apps, it should now have the new war file:
ls -lah /var/lib/tomcat9/webapps/
4. Show your XML parsing on your terminal (ssh to AWS).
- Move your XML data files to a location that your XML parser will need.
- Build and execute your parser.
- An inconsistency report should be output to console or a file, and will be checked later.
The entire parsing time cannot exceed 15 minutes on AWS machine.
5. Show your website in your web browser:
u- se HTTPS to go to your website. Manually change it to HTTP with port 8080, it should be redirected to HTTPS correctly.
- Login with a customer credential.
First without using reCAPTCHA, an error message should be displayed.
Then use reCAPTCHA and login, it should succeed.
Show that there is no access to employee dashboard with customer's account.
- Login with the employee credentials email "classta@email.edu" and password "classta".
Go to employee dashboard.
Skim through the metadata of your database.
- Go to add a star:
enter star name "Star1" and birth year "2021", add it, a confirmation message should be displayed with the generated star ID.
- Go to add a movie:
enter movie name "Movie1", director "Director1", year 2021, genre "Genre1" (new), star "Star2" (new), add it, a confirmation message should be displayed with the generated Movie ID, Genre ID, and the star ID.
enter movie name "Movie2", director "Director1", year 2021, genre "Genre1" (existing), star "Star1" (existing), add it, a confirmation message should display with the generated Movie ID, and the existing star ID, Genre ID, that was found.
enter movie name "Movie1", director "Director1", year 2021, genre "Genre2" (new), star "Star2" (existing), add it, an error message should be displayed due to duplicated movie.
6. After XML parsing is finished, examine the data you entered:
- Browse movie by genre "Genre1". Two movies ("Movie1" and "Movie2") should show up. Click each of the movies to go to Single Movie Page, show star and genre information.
- Search movie by title "Movie", same two movies should show up.
- Choose any two movies from the following and display the Single Movie Page of each (those are from the XML file):
Mission Impossible
Forrest Gump
The Godfather
Star Wars
The Silence of the Lambs
The Matrix
7. Show your inconsistency report from XML parser.  
Done.

### Queries that used Prepared Statements:
- DashboardServlet, GenreServlet, InsertStarServlet, LoginServlet, MetaData, MovieListServlet, PaymentServlet, SingleMovieServlet, SingleStarServlet

### Optimization Strategies
1. HashMap - We used HashMap to store the parse file in order to check for duplicates in the xml data such that we don't have to parse or load those again which saves time. 
2. Insert Batch - Instead of insert one statement and load it we used batch insert which runs all the insert statements for each table in one query such that we don't have to connect to sql database every time we find a new movie/star/genre just one time is enough.
3. Without These 2 Optimization Strategies, our originally parsing took about 12-13 minutes to parse and load, after we implemented the strategies it decreases to 2 minutes. 

- Inconsistency data: 
- DirName:Bunuel, Title:El grand cavalcodos, Year:0, Genre:Miscellaneous.
  DirName:Humberstone, Title:null, Year:1936, Genre:Mystery.
  DirName:Dassin, Title:null, Year:1947, Genre:Action.
  DirName:W.Staudte, Title:Catherine of Russia, Year:0, Genre:Miscellaneous.
  DirName:W.Jackson, Title:null, Year:1949, Genre:Miscellaneous.
  DirName:Alan~Smithee, Title:Morgan Stewart's Coming Home, Year:0, Genre:Miscellaneous.
  DirName:Alan~Smithee, Title:Raging Angels, Year:0, Genre:Miscellaneous.
  DirName:Pollock, Title:null, Year:1999, Genre:Miscellaneous.
  DirName:G.Reinhardt, Title:null, Year:1954, Genre:Miscellaneous.
  DirName:Haedrich, Title:Stop Train 349, Year:0, Genre:Miscellaneous.
  DirName:J.G.Avildsen, Title:Karate Kid, Year:0, Genre:Miscellaneous.
  DirName:Scorsese, Title:The Eternal City, Year:0, Genre:Drama.
  DirName:Wenders, Title:Buena Vista Social Club, Year:0, Genre:Music.
  DirName:D.Lynch, Title:Mulholland Drive, Year:0, Genre:Miscellaneous.
  DirName:Egoyan, Title:Sarabande, Year:0, Genre:Miscellaneous.
  DirName:null, Title:Bachelor Party, Year:1984, Genre:Comedy.
  DirName:null, Title:Rent-a-Cop, Year:1986, Genre:Miscellaneous.
  DirName:Silberling, Title:null, Year:1988, Genre:Miscellaneous.
  DirName:Sturridge, Title:Fairy Tale: A True Story, Year:0, Genre:Fantasy.
  DirName:Dowling, Title:null, Year:0, Genre:Miscellaneous.
  DirName:diCillo, Title:Jerry and Tom, Year:0, Genre:Miscellaneous.
  DirName:UnYear95, Title:The Shanghai Triad, Year:0, Genre:Miscellaneous.
  DirName:Kiarostami, Title:Taste of Cherry, Year:0, Genre:Drama.
  DirName:M.Judge, Title:All is Routine, Year:0, Genre:Miscellaneous.
  DirName:null, Title:Show Me Love, Year:1999, Genre:Drama.
  DirName:null, Title:Together, Year:2001, Genre:Drama.
  DirName:null, Title:In the Bedroom, Year:2001, Genre:Drama.
  DirName:null, Title:Never Never, Year:2002, Genre:Drama.
  DirName:null, Title:Monster's Ball, Year:2002, Genre:Miscellaneous.
  Actor Name: null, Movie Title: La femme infidele.
  Actor Name: null, Movie Title: With a Friend Like Harry.
  Actor Name: null, Movie Title: Vertigo.


Project 3 demo Video: `https://youtu.be/yof9Tteugz4`

Project 4 Distribution
- Nick - Handled developing Android App for Fabflix, demo Android in the local emulator
- Timothy - Implemented full-text Search and Autocomplete, demo Fabflix website through AWS

Deployment

1. Commit check:
- git clone git repo to AWS instance: `git clone <repo url>`. This step is to make sure you have a clean repo.
cd into your repo and run `git log`, show your latest commit.
fail to show commit check will result in an instant 0 of project.
2. Deploy your web application on AWS instance:
- inside your repo, where the pom.xml file locates, build the war file:
mvn clean package
- show tomcat web apps, it should NOT have the war file yet:
ls -lah /home/ubuntu/tomcat/webapps
- copy your newly built war file:
cp ./target/*.war /home/ubuntu/tomcat/webapps
- show tomcat web apps, it should now have the new war file:
ls -lah /home/ubuntu/tomcat/webapps
3. Show your website in your web browser:
- open browser Devs Tools Console Panel Links to an external site..
- login as a customer into the Main Page, which has access to the main search bar.
- enter "lo" in the main search bar, hit Enter key directly, a normal full-text search should perform and jump to Movie-List Page, the autocomplete should not be triggered since only two characters are provided.
- enter "s lov" in the main search bar,  a dropdown suggestion list should show your autocomplete suggestions, use up/down arrow keys to navigate to the third suggestion, hit Enter key, the corresponding Single Movie Page should be redirected.
- enter "s lov" in the main search bar,  a dropdown suggestion list should show your autocomplete suggestions, use your mouse to click the last suggestion, the corresponding Single Movie Page should be redirected.
- quickly enter "love" in the main search bar (this is to test your autocomplete delay), only one query should be sent to the backend, then delete "e" from the main search bar, then reenter "e".
4. Show your Android in the local emulator:
- show `git status` and `git log` on your local report to ensure the same version is used.
- build and deploy the APK on your local emulator (version: Android version 30, emulator Pixel 3a).
5. go to the login page
- enter email/account "a@email.com" with a wrong password. click login, an error message should display.
- enter the correct password "a2" for the same email/account. click login, it should succeed and redirect to Main Page.
- in Main Page, enter "s lov" in the main search bar, hit Enter key, a Movie-List Page should display.
- on the Movie List Page, navigate to the second page, select the first movie and jump to the Single Movie Page.

Project 4 demo Video: `https://youtu.be/fvlv09fhlII`

# Project 5 General
  - #### Team#: 18

  - #### Names: Timothy Lin and Nicholas Huynh

  - #### Project 5 Video Demo Link: https://youtu.be/Jud7Ia235rU

  - #### Instruction of deployment:
  - Demo on local machine:
    Show your AWS EC2 page which shows your account and IP of the instances that are running.
    On your local machine, run 2 JMeter test plans as below, each for 5 minutes:
    Single-instance version, test-case#3 which is using HTTPS, 10 threads in JMeter;
    Scaled-instance version, test-case#2 which is using HTTP, 10 threads in JMeter, using the AWS load balancer.
    You can kill the JMeter for each test plan after 5 minutes limit.
    On your local machine, run the `log_processing.*` script against the sets of log files generated from the above 3 test plans; show the calculated TS/TJ time for each test plan.

  - #### Collaborations and Work Distribution:
  - Timothy: Task2,3-> Master/Slave & load balancing, Task4 -> log processing scropt & TS/TJ Report 
  - Nicholas: Task1-> Connection Pooling, Task4 -> log processing script & log files


# Connection Pooling
  - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
  - The file that contains code for JDBC Connection Pooling is WebContent/META-INF/context.xml.

  - #### Explain how Connection Pooling is utilized in the Fabflix code.
  - Connection Pooling is utilized by allowing idle connections to the database to be used when needed instead
  of creating a new connection to the database. It allows for other threads to use already established connections,
  reducing the runtime of the code.

  - #### Explain how Connection Pooling works with two backend SQL.
  - Connection Pooling works by first checking the pool of connections to see the current state of each connection.
  They will either be active or idle. If there are any idle connections, they will be selected to be used when a
  connection is requested. If there are no idle connections, a new connection will be created and added to the pool.


# Master/Slave
  - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
  - For write/update, InsertMovieServlet, InsertStarServlet, and SaleConfirmationServlet are directed to master SQL database
  - Everything else in src folder, when read, it'll rout to either Master or Slave SQL database
  - #### How read/write requests were routed to Master/Slave SQL?
  - All requests that need to use a datasource here the SQL database are connected to one of the two sources
mentioned in the context.xml file (moviedb or moviedb-master). The port 3306 is the port number for MySQL database. For Read
request, the Servlet connects to "moviedb:3306" which can rout to either master or slave database. On the other hand, for
write request, the Servlet connects to "moviedb-master:3306" which only correspondence to master database given the master's ip address 
is used to connect instead of "localhost". 


# JMeter TS/TJ Time Logs
  - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
  - To use the 'log_processing.java' script, you just need to input the correct path of the log file. At line 48: String log = {"""}; change the string to the log file location.After doing that, 
  running the main method will produce the different pieces of data you need. 

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                                                                                                                                                                                                                                 |
|------------------------------------------------|------------------------------|----------------------------|------------------------------------|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 92                         | 90                                 |           89           | This one should have the fastest time for average query time, <br/>average search servlet time, and average JDBC time because it is running on 1 thread<br/>compared to the other cases where they are running multiple threads that increase load and slow down the server. |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                 | ??                     | Compare to case 1, this one should have a longer runtime for all categories because<br/>it is increase the load of the server causing a slower q time.                                                                                                                       |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                 | ??                     | This one should have a similar result as case 2 because the only difference is <br/> changing from http to https which will not cause any increase in terms of load so <br/>the q time of all categories should be similar to case 2.                                        |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                 | ??                     | This one should have a slightly increase in average query time and search servlet time because it does have the connection pooling meaning no limit in timeout, <br/>and the servlet have to connect repeatedly instead of using ununsed connection.                         |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                                                                                                                                                                                                                                                                                                                                                    |
|------------------------------------------------|------------------------------|----------------------------|------------------------------------|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 82                         | 79                                 | 79                       | This one should have the fastest time for average query time, <br/>average search servlet time, and average JDBC time because it is running on 1 thread<br/>compared to the other cases where they are running multiple threads that increase load and slow down the server.                                                                                                                    |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                 | ??                       | Compare to case 1, this one should have a longer runtime for all categories because<br/>it is increase the load of the server causing a slower q time.<br/> but it should have a faster all around time than singe-instance case 2 because <br/> there is more backends running so teh average time is being distributed to not just 1 backend causing the overall improvement <br/> in all time. |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                 | ??                       | This one should have a slightly increase in average query time and search servlet time because it does have the connection pooling meaning no limit in timeout, <br/>and the servlet have to connect repeatedly instead of using ununsed connection.                                                                                                                                                                                                                                                                                                                                                                                                   |