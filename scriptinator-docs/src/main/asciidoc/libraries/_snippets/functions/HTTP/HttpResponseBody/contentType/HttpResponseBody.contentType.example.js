// tag::docs[]
var HTTP = library("HTTP");             // <1>

var body = HTTP.get(                    // <2>
    "https://httpbin.org/response-headers?Content-Type=text%2Fplain"
).body();

info(body.contentType());               // <3>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Content type is text/plain",
    "text/plain",
    body.contentType()
);