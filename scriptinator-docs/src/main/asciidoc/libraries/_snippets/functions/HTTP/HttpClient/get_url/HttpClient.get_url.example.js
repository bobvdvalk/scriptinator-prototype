// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get(                // <2>
    "https://jsonplaceholder.typicode.com/posts/5"
);

var post = response.body().asJson();
Script.info(post.title);

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library exists", HTTP);
Assert.equal(
    "Post 5 is retrieved",
    5,
    post.id
);
Assert.equal(
    "Status is OK",
    200,
    response.code()
);