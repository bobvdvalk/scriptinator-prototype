// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/ip"
);

Script.info(response.protocol());       // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Protocol is HTTP 1.1",
    "http/1.1",
    response.protocol()
);