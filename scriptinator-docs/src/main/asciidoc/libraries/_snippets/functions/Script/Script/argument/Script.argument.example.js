// tag::docs[]
var argument = Script.argument();               // <1>

if (argument == null) {                         // <2>
    Script.warn("No argument given");
} else {
    Script.info("Argument given", argument);    // <3>
}

// end::docs[]
