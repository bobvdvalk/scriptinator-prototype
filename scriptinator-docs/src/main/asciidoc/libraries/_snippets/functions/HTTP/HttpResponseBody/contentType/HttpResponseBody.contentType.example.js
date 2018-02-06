// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var body = HTTP.get(                    // <2>
    "https://httpbin.org/response-headers?Server=httpbin&Content-Type=text%2Fplain"
).body();

Script.info(body.contentType());        // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Content type is text/plain",
    "text/plain",
    body.contentType()
);