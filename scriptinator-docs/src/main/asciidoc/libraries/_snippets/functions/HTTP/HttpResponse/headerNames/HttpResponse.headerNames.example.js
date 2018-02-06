// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/response-headers?Custom-Header=FooBar"
);

Script.info(response.headerNames());    // <3>

// end::docs[]

var Assert = Script.library("Assert");

if (response.headerNames().indexOf("Custom-Header") === -1) {
    Assert.fail("Header names contain custom header.");
}