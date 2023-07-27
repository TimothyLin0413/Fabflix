/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */
let sort = 'DESrating';
let tie = 'DES';
let sortnum = '10';
let count = 0;

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleStarResult(resultData) {
    console.log("handleStarResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    $("#movie_table_body").empty()
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(sortnum, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]["mov_title"] + '">'
            + resultData[i]["mov_title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["mov_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["mov_dir"] + "</th>";
        rowHTML += "<th>"
        //+ resultData[i]["mov_genres"] + "</th>";
        let genres = resultData[i]["mov_genres"].split(',')
        if (genres.length == 1){
            rowHTML += '<a href="movie_list.html?id=browse-genre-' + genres[0] + '">'
                + genres[0] +     // display genrefor the link text
                '</a>';
        }else if (genres.length == 2){
            rowHTML += '<a href="movie_list.html?id=browse-genre-' + genres[0] + '">'
                + genres[0] +     // display star_name for the link text
                '</a>';
            rowHTML += ", ";
            rowHTML += '<a href="movie_list.html?id=browse-genre-' + genres[1] + '">'
                + genres[1] +     // display star_name for the link text
                '</a>';
        }else if(genres.length >= 3){
            rowHTML += '<a href="movie_list.html?id=browse-genre-' + genres[0] + '">'
                + genres[0] +     // display star_name for the link text
                '</a>';
            rowHTML += ", ";
            rowHTML += '<a href="movie_list.html?id=browse-genre-' + genres[1] + '">'
                + genres[1] +     // display star_name for the link text
                '</a>';
            rowHTML += ', ';
            rowHTML += '<a href="movie_list.html?id=browse-genre-' + genres[2] + '">'
                + genres[2] +     // display star_name for the link text
                '</a>';
        }
        rowHTML += "</th>";

        rowHTML += "<th>"
        //+ resultData[i]["star_names"] + "</th>";
        // Add a link to single-star.html with id passed with GET url parameter
        //'<a href="single-movie.html?id=' + resultData[i]["mov_id"] + '">'
        //+ resultData[i]["mov_title"] +     // display star_name for the link text
        //'</a>' +
        let stars = resultData[i]["star_names"].split(',') // for some reason this prints out id (check json info)
        // let id_arr = resultData[i]["star_ids"].split(',')
        if (stars.length == 1){
            //rowHTML += stars[0]
            rowHTML += '<a href="single-star.html?id=' + stars[0] + '">'
                + stars[0] +     // display star_name for the link text
                '</a>';
        }else if (stars.length == 2){
            rowHTML += '<a href="single-star.html?id=' + stars[0] + '">'
                + stars[0] +     // display star_name for the link text
                '</a>';
            rowHTML += ", ";
            rowHTML += '<a href="single-star.html?id=' + stars[1] + '">'
                + stars[1] +     // display star_name for the link text
                '</a>';
            //rowHTML += stars[0] + ", " +  stars[1];
        }else if(stars.length >= 3){
            rowHTML += '<a href="single-star.html?id=' + stars[0] + '">'
                + stars[0] +     // display star_name for the link text
                '</a>';
            rowHTML += ", ";
            rowHTML += '<a href="single-star.html?id=' + stars[1] + '">'
                + stars[1] +     // display star_name for the link text
                '</a>';
            rowHTML += ', ';
            rowHTML += '<a href="single-star.html?id=' + stars[2] + '">'
                + stars[2] +     // display star_name for the link text
                '</a>';
        }
        //rowHTML += "<th>"

        rowHTML += "<th>" + resultData[i]["mov_rat"] + "</th>";

        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]["mov_title"] + '">'
            + "Add to Cart" +     //
            '</a>' +
            "</th>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}



/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let searchId = getParameterByName('id');
let searchTitle = getParameterByName('title')
if (searchTitle === "") { searchTitle = " "; }

let searchYear = getParameterByName('year')
if (searchYear === "") { searchYear = " "; }

let searchDirector = getParameterByName('director')
if (searchDirector === "") { searchDirector = " "; }

let searchStar = getParameterByName('star')
if (searchStar === "") { searchStar = " "; }


// Makes the HTTP GET request and registers on success callback function handleStarResult

document.getElementById('sortSelector').addEventListener('change', sortOption)
document.getElementById('limitSelector').addEventListener('change', sortNum)
document.getElementById('tieBreaker').addEventListener('change', sortTie)
document.querySelector('.Next').addEventListener('click', countNext)
document.querySelector('.Previous').addEventListener('click', countPrevious)



function countPrevious(){
    if (count != 0){
        count -= 1
    }
    check_search()

 }

 function countNext(){
     count += 1
     check_search()
 }

 function sortOption(){
     sort = document.getElementById('sortSelector').value
     check_search()
 }

 function sortNum(){
     sortnum = document.getElementById('limitSelector').value
     check_search()
 }

function sortTie(){
    tie = document.getElementById('tieBreaker').value
    check_search()
}

function loadpage(){
    let dataString = {"sorting": sort + tie, "limit": sortnum, "offset": count, "jump": "no"}
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?id="+ searchId, // Setting request url, which is mapped by StarsServlet in Stars.java
        data: dataString,
        success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function check_search() {
    if (searchId === null) {
        // Makes the HTTP GET request and registers on success callback function handleStarResult
        let dataString = {"sorting": sort, "limit": sortnum, "offset": count, "jump": "no"}
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/movielist?id=" + searchTitle + "-" + searchYear + "-" + searchDirector + "-" + searchStar + "-", // Setting request url, which is mapped by
            data: dataString,
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    } else {
        loadpage()
    }
}

check_search();