// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.delete(             // <2>
    "https://jsonplaceholder.typicode.com/posts/4"
);

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "The request was accepted",
    200,
    response.code()
);
