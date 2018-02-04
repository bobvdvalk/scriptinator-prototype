// tag::docs[]
var HTTP = Script.library("HTTP");

var client = HTTP.client();                     // <1>


var post = client.get(                          // <2>
    "https://jsonplaceholder.typicode.com/posts/4"
).body().asJson();                              // <3>

Script.info(post);                              // <4>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Response is OK",
    response.code(),
    200
);