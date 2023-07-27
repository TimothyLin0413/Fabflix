let cart = $("#cart");
let finalPrice = 0;

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);

    $("#priceNum").text("Total Price for Cart: $" + finalPrice);
}

function count_occur(resultArray, title) {
    let count = 0;
    for (let i = 0; i < resultArray.length; i++) {
        if (resultArray[i] === title) { count++; }
    }
    return count;
}

function getRandomInt(num) { // should give 1 to num
    return Math.floor(Math.random() * num) + 1;
}
/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    let cartBody = jQuery("#cart_table_body");
    let movies = [...new Set(resultArray)];


    for (let i = 0; i < movies.length; i++) {
        let quantity = count_occur(resultArray, movies[i]);
        let price = 10; //getRandomInt(10);
        let totalPrice = price * quantity;
        finalPrice += totalPrice;
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + movies[i] + "</th>";
        rowHTML += "<th>" + price + "</th>";
        rowHTML += "<th>" + quantity + "</th>";
        rowHTML += "<th>" + totalPrice + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        cartBody.append(rowHTML);
    }

}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) { // this is run when submit button happens
    // console.single_instance_log1("submit cart form");
    // /**
    //  * When users click the submit button, the browser will not direct
    //  * users to the url defined in HTML form. Instead, it will call this
    //  * event handler when the event is triggered.
    //  */
    cartEvent.preventDefault();

    $.ajax("api/shoppingcart", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => { // change this
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

$.ajax("api/shoppingcart", { // this is run when html is called
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
cart.submit(handleCartInfo); // make this a quantity button (not doing anything right now)
