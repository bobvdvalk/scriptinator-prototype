package main

type Job struct {
	Id                    int64
	DisplayName           string
	Status                int
	QueuedTime            int64
	ScriptId              int64
	Argument              string
	TriggeredByScheduleId int64
}
