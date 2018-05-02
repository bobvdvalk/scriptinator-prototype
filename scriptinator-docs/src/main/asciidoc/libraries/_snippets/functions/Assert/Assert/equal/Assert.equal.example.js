// tag::docs[]
var Assert = library("Assert");                 // <1>
var HTTP = library("HTTP");

var response = HTTP.get(                        // <2>
    "https://httpbin.org/uuid"
);

Assert.equal(                                   // <3>
    "Status is ok",
    200,
    response.code()
);

// end::docs[]
