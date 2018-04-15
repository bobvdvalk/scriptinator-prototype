// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/status/404"
);

info(response.code());                  // <3>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Status code is 404",
    404,
    response.code()
);