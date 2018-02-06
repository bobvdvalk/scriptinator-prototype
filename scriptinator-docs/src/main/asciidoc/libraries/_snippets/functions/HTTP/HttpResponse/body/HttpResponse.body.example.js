// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.post(               // <2>
    "https://httpbin.org/post"
);

var body = response.body();             // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Url is https://httpbin.org/post",
    "https://httpbin.org/post",
    body.asJson().url
);