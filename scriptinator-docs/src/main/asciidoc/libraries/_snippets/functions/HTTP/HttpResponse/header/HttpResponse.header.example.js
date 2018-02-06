// tag::docs[]
var HTTP = Script.library("HTTP");              // <1>

var response = HTTP.get(                        // <2>
    "https://httpbin.org/response-headers?Custom-Header=FooBar"
);

Script.info(response.header("Custom-Header"));  // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Custom header is set",
    "FooBar",
    response.header("Custom-Header")
);