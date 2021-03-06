// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.request({           // <2>
    method: "POST",
    url: "http://jsonplaceholder.typicode.com/comments",
    contentType: "application/json",
    body: {
        name: "id labore ex et quam laborum",
        text: "laudantium enim quasi est quidem magnam voluptate ipsam eos"
    }
});

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Status is 201",
    201,
    response.code()
);