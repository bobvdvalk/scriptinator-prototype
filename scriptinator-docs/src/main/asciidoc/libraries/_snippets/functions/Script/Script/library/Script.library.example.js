// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get(                // <2>
    "https://jsonplaceholder.typicode.com/posts/5"
);

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library is loaded", HTTP);

Assert.equal(
    "Status is OK",
    200,
    response.code()
);
