// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.head(               // <2>
    "http://placehold.it/600/92c952"
);

var contentLength = response.header("Content-Length");
info(contentLength);                    // <3>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "A length was present",
    "1754",
    contentLength
);
