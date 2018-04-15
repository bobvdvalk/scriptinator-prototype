// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/anything"
);

debug(response.code());                 // <3>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Status is OK",
    200,
    response.code()
);
