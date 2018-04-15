// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.patch(              // <2>
    "http://jsonplaceholder.typicode.com/users/2"
);

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "The request was accepted",
    200,
    response.code()
);
