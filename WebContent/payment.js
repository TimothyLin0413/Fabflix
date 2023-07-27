let sale_form = $("#sale_form");
let finalPrice = 0;

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);

    $("#priceDisplay").text("Total Price for Cart: $" + finalPrice);
}

function count_occur(resultArray, title) {
    let count = 0;
    for (let i = 0; i < resultArray.length; i++) {
        if (resultArray[i] === title) { count++; }
    }
    return count;
}

function handleCartArray(resultArray) {
    let movies = [...new Set(resultArray)];

    for (let i = 0; i < movies.length; i++) {
        let quantity = count_occur(resultArray, movies[i]);
        let price = 10;
        let totalPrice = price * quantity;
        finalPrice += totalPrice;
    }
}

function handleSaleResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    // If Checkout succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("sale_confirmation.html");
    } else {
        $("#sale_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSaleForm(formSubmitEvent) {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: sale_form.serialize(),
            success: handleSaleResult
        }
    );
}

$.ajax("api/shoppingcart", { // this is run when html is called
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a handler function
sale_form.submit(submitSaleForm);