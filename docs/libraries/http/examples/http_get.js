var HTTP = Script.library("HTTP");

/**
 * The bacon ipsum api generates an array of
 * meatier lorem ipsum paragraphs.
 */
var response = HTTP.get({
    url: "https://baconipsum.com/api/?type=meat-and-filler"
});

var contents = response.body().asJson();

contents.forEach(function (paragraph) {
    Script.info(paragraph);
});