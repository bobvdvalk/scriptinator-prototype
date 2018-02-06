// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var body = HTTP.get(                    // <2>
    "https://baconipsum.com/api/?type=meat-and-filler&paras=1&format=text"
).body();

var text = body.asString();              // <3>

Script.info(text);                       // <4>

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNullOrEmpty(
    "Body is not empty",
    text
);