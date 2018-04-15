// tag::docs[]
var HTTP = library("HTTP");                     // <1>

var response = HTTP.get(                        // <2>
    "https://httpbin.org/response-headers?Custom-Header=FooBar"
);

info(response.header("Custom-Header"));         // <3>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Custom header is set",
    "FooBar",
    response.header("Custom-Header")
);