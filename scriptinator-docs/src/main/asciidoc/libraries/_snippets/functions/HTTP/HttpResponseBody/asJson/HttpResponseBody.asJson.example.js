// tag::docs[]
var HTTP = library("HTTP");             // <1>

var body = HTTP.get(                    // <2>
    "https://httpbin.org/get"
).body();

var object = body.asJson();             // <3>

info(object.url);                       // <4>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Url is https://httpbin.org/get",
    "https://httpbin.org/get",
    object.url
);