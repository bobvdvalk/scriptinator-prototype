// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/status/502"
);

Script.info(response.message());           // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Status code is 502",
    "BAD GATEWAY",
    response.message()
);