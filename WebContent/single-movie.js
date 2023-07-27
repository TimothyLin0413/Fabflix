/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

let cart = $("#cart");

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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

function handleResult(resultData) {

    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");
    let backPage = jQuery("#movie_page")
    backPage.append("<a href=" + document.referrer + ">Back to Movie List</a>");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Movie Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" );

    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<th>" // doesn't work for some reason
        let names = resultData[i]["star_names"].split(',')
        // let star_id_arr = resultData[i]["star_ids"].split(',')
        let j = 0
        while (names.length > j){
            //rowHTML += stars[0]
            rowHTML += '<a href="single-star.html?id=' + names[j] + '">'
                + names[j] +     // display star_name for the link text
                '</a>';
            rowHTML += '. ';
            j += 1;
        }
        //rowHTML += "<th>";

        rowHTML += "<th>";
        let genres = resultData[i]["genres"].split(',')
        let j2 = 0
        while (genres.length > j2){
            //rowHTML += stars[0]
            rowHTML += '<a href="movie_list.html?id=browse-genre-' + genres[j2] + '">'
                + genres[j2] +     // display genres for the link text
                '</a>';
            rowHTML += '. ';
            j2 += 1;
        }
        //rowHTML += "<th>";

        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);


    }
}

function handleCartArray(resultArray) {
    let item_list = $("#item_list");
    let res = "Purchased";
    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}


function handleCartInfo(cartEvent) { // this is run when submit button happens
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/single-movie?id=" + movieId, {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}


// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

cart.submit(handleCartInfo);