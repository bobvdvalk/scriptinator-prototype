// tag::docs[]
var argument = Script.argument();        // <1>

if (argument == null) {                  // <2>
    Script.warn("No argument given, using {}.");
    argument = {};
}

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Argument is {}",
    {},
    argument
);
