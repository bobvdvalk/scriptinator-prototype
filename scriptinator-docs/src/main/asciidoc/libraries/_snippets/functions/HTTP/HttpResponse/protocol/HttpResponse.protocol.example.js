// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/ip"
);

info(response.protocol());              // <3>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Protocol is HTTP 1.1",
    "http/1.1",
    response.protocol()
);