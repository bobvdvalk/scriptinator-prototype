// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.get(                // <2>
    "https://jsonplaceholder.typicode.com/posts/5"
);

var post = response.body().asJson();
info(post.title);

// end::docs[]

var Assert = library("Assert");

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