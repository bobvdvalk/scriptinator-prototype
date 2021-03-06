// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.post(               // <2>
    "https://httpbin.org/anything"
);

// end::docs[]

var Assert = library("Assert");

var body = response.body().asJson();

Assert.equal(
    "No body was sent",
    "",
    body.data
);

Assert.equal(
    "Content-Length header was sent",
    "0",
    body.headers['Content-Length']
);