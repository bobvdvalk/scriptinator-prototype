// tag::docs[]
var argument = Script.argument();       // <1>

if (argument == null) {                 // <2>
    Script.error("Argument is not set.");
}

// end::docs[]
