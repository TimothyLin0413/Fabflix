let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
let lastSuggestion;
let prevQuery = "";
function handleGenreResult(resultData) {
    console.log("handleStarResult: populating movie table from resultData");
    console.log(resultData);

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    $("#genre_table_body").empty()
    let starTableBodyElement = jQuery("#genre_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(50, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += '<a href="movie_list.html?id=browse-genre-' + resultData[i]["g"] + '">'
            + resultData[i]["g"] +     // display star_name for the link text
            '</a>';
        rowHTML += ". ";
        starTableBodyElement.append(rowHTML);
    }
}


function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    //console.single_instance_log1(query)

    // TODO: if you want to check past query results first, you can do it here
    if (prevQuery.includes(query)){
        console.log("query similar to previous query; pull suggestions from front-end cache")
        doneCallback({suggestions: lastSuggestion});
        return;
    }

    console.log("sending AJAX request to backend server")
    prevQuery = query;
    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/auto-complete?query=" + (query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup error")
            console.log(errorData)
        }
    })
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    lastSuggestion = jsonData;

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"])
    window.location.replace("single-movie.html?id=" + suggestion["value"])
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("normal search with query: " + query);
    //alert("enter pressed");
    //alert(query);
    window.location.replace("movie_list.html?title=" + query + "&year=&director=&star=")
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button


jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genre", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});


