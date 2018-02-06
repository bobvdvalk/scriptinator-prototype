// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var data = {
    title: "This is a new entry"
};

var response = HTTP.post({              // <2>
    url: "https://jsonplaceholder.typicode.com/posts",
    contentType: "application/json",
    body: data
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library exists", HTTP);
Assert.equal(
    "The post is returned correctly",
    101,
    response.body().asJson().id
);