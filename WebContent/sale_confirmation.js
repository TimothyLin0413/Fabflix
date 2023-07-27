let finalPrice = 0;

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    // show cart information
    handlePurchaseArray(resultDataJson["previousItems"], resultDataJson["sales"]);

    $("#priceNum").text("Total Price for Cart: $" + finalPrice);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handlePurchaseArray(titleArray, salesArray) {
    let purchaseBody = jQuery("#confirm_table_body");

    for (let i = 0; i < salesArray.length; i++) {
        finalPrice += 10;
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + salesArray[i] + "</th>";
        rowHTML += "<th>" + titleArray[i] + "</th>";
        rowHTML += "<th>" + 1 + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        purchaseBody.append(rowHTML);
    }

}

$.ajax("api/saleconfirmation", { // change this to confirmation page
    method: "GET",
    success: handleSessionData
});