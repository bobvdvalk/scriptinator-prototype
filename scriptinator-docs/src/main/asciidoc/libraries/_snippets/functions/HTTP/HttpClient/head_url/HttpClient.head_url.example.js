// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.head(               // <2>
    "http://placehold.it/600/92c952"
);

var contentLength = response.header("Content-Length");
Script.info(contentLength);             // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library exists", HTTP);
Assert.equal(
    "A length was present",
    "1754",
    contentLength
);
