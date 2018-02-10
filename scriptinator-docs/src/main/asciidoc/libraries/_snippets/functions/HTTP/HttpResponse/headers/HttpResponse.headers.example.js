// tag::docs[]
var HTTP = Script.library("HTTP");              // <1>

var response = HTTP.get(                        // <2>
    "https://httpbin.org/uuid"
);

Script.info(response.headers().Date);           // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNullOrEmpty(
    "Date header is set",
    response.headers().Date[0]
);