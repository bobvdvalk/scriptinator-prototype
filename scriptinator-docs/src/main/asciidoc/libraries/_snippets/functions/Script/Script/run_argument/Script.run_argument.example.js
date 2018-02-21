// tag::docs[]
var jobId = Script.run(                     // <1>
    "my-first-project/a-script",
    {pi: 3.14}
);

if (jobId != null) {                        // <2>
    Script.info("Job created: " + jobId);
}

// end::docs[]
