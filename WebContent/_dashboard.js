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

function handleMetaResult(resultData){
    console.log("handle show metadata response");
    let databaseNameElement = jQuery("#metadata_database");
    if (window.document.getElementById("metadata_database").textContent === "") {
        databaseNameElement.append("<p>Database: moviedb</p>");
    } else { return; }

    let dbTablesElement = jQuery("#db_tables");
    for (let i = 0; i < resultData.length; i++) {
        let tableHTML = "<pre>";
        // name of table
        tableHTML += resultData[i]["name"] + "";
        // get array of attributes and store into variable
        let attributes = resultData[i]["attributes"];
        console.log(attributes);
        for (let j = 0; j < attributes.length; j++) {
            // each line under table name = "    field: type"
            tableHTML += "\n    " + attributes[j]["field"] + " " + attributes[j]["type"];
        }
        tableHTML += "</pre>";
        dbTablesElement.append(tableHTML);
    }
}

function showMetaData(){
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/metadata",
        success: (resultData) => handleMetaResult(resultData)
    });
    return false
}

function handleInsertStarResult(resultData){
    console.log("handle insert star response");

    if (resultData["status"] === "success") {
        $("#star_form_message").text(resultData["message"]);
    }
    else {
        $("#star_form_message").text("Error when inserting new star");
        console.log(resultData["errorMessage"]);
    }
}

function insertStar(){
    let star_form = $("#star_form");
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/insertStar?" + star_form.serialize(),
        success: (resultData) => handleInsertStarResult(resultData)
    });
    return false
}


function handleInsertMovieResult(resultData){
    console.log("handle insert movie response");

    if (resultData["message"].startsWith("INSERT SUCCESS")) {
        $("#movie_form_message").text(resultData["message"]);
    }
    else {
        $("#movie_form_message").text(resultData["message"]);
        console.log(resultData["errorMessage"]);
    }
}

function insertMovie(){
    let movie_form = $("#movie_form");
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/insertMovie?"+ movie_form.serialize(),
        success: (resultData) => handleInsertMovieResult(resultData)
    });
    return false
}



