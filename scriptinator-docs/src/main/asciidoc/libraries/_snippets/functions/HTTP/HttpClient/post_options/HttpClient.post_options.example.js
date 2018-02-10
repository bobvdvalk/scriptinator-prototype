// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.post({              // <2>
    url: "https://jsonplaceholder.typicode.com/posts",
    contentType: "application/json",
    body: {
        title: "This is a new entry"
    }
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "The post is returned correctly",
    101,
    response.body().asJson().id
);