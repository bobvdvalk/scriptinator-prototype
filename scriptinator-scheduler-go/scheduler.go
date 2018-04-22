package main

import (
	"github.com/gorhill/cronexpr"
	"log"
	"time"
)

type Schedule struct {
	Id         int64
	ProjectId  int64
	Argument   string
	ScriptName string
	CronString string
	LastRun    time.Time
}

type Scheduler struct {
	dbConfig    *DbConfig
	queueConfig *QueueConfig

	jobService *JobService
}

func NewScheduler(dbConfig *DbConfig, queueConfig *QueueConfig) *Scheduler {
	return &Scheduler{
		dbConfig:    dbConfig,
		queueConfig: queueConfig,
		jobService:  &JobService{dbConfig: dbConfig, queueConfig: queueConfig},
	}
}

// Start the scheduler.
func (scheduler *Scheduler) Run() {
	// Initial tick.
	go scheduler.tick()

	// Tick every N seconds.
	tickChannel := time.Tick(5 * time.Second)
	for {
		select {
		case <-tickChannel:
			go scheduler.tick()
		default:
			time.Sleep(time.Second)
		}
	}
}

// Run the scheduler.
func (scheduler *Scheduler) tick() {
	// Update all schedules whose next run time is not yet set.
	scheduler.setNextRunOnNewSchedules()

	// Get and run all runnable schedules.
	schedules := scheduler.findRunnableSchedules()
	log.Printf("Running %d schedule(s).\n", len(schedules))
	for _, schedule := range schedules {
		scheduler.runSchedule(schedule)
	}
}

// Set the next run on all enabled schedules where it is not null.
func (scheduler *Scheduler) setNextRunOnNewSchedules() {
	log.Printf("Updating schedules.\n")

	// Find all schedules where the next run is null.
	rows, err := scheduler.dbConfig.Db.Query("SELECT id, cron_string, last_run FROM schedule WHERE enabled = true AND next_run IS NULL")
	failOnError(err, "Could not query schedules")
	defer rows.Close()

	// Update all schedules.
	for rows.Next() {
		schedule := Schedule{}
		err := rows.Scan(&schedule.Id, &schedule.CronString, &schedule.LastRun)
		failOnError(err, "Could not get row from result set")

		log.Printf("Updating schedule %d.\n", schedule.Id)
		scheduler.updateSchedule(schedule, schedule.LastRun)
	}

	failOnError(rows.Err(), "An error occurred with the result set")
}

// Find all enabled schedules whose next run is before now.
func (scheduler *Scheduler) findRunnableSchedules() []Schedule {
	schedulesStmt, err := scheduler.dbConfig.Db.Prepare("SELECT id, project_id, argument, script_name, cron_string FROM schedule WHERE enabled = true AND next_run < ?")
	failOnError(err, "Could not prepare select schedules statement")
	defer schedulesStmt.Close()

	// Get all schedules scheduled before now.
	rows, err := schedulesStmt.Query(time.Now())
	failOnError(err, "Could not query schedules")
	defer rows.Close()

	// Convert all rows to schedules.
	var schedules []Schedule
	for rows.Next() {
		nextSchedule := Schedule{}
		err := rows.Scan(&nextSchedule.Id, &nextSchedule.ProjectId, &nextSchedule.Argument, &nextSchedule.ScriptName, &nextSchedule.CronString)
		failOnError(err, "Could not get row from result set")
		schedules = append(schedules, nextSchedule)
	}

	failOnError(rows.Err(), "An error occurred with the result set")

	return schedules
}

// Run a schedule.
// This creates a job in the database and publish the created job in the message queue.
func (scheduler *Scheduler) runSchedule(schedule Schedule) {
	// Get the id of the script to run.
	scriptIdStmt, err := scheduler.dbConfig.Db.Prepare("SELECT id FROM script WHERE project_id = ? AND name = ?")
	failOnError(err, "Could not prepare select script id statement")
	defer scriptIdStmt.Close()
	var scriptId int64
	err = scriptIdStmt.QueryRow(schedule.ProjectId, schedule.ScriptName).Scan(&scriptId)

	// Check if the script exists.
	if err != nil {
		log.Printf("Could not find script '%s' for schedule %d.\n", schedule.ScriptName, schedule.Id)

		// Set the schedule status to invalid script.
		disableStmt, err := scheduler.dbConfig.Db.Prepare("UPDATE schedule SET enabled = false, status = 2 WHERE id = ?")
		failOnError(err, "Could not prepare disable schedule statement")
		defer disableStmt.Close()
		disableStmt.Exec(schedule.Id)

		return
	}

	// Update the schedule.
	if !scheduler.updateSchedule(schedule, time.Now()) {
		return
	}

	// Create the job.
	job := Job{
		DisplayName:           schedule.ScriptName,
		ScriptId:              scriptId,
		Argument:              schedule.Argument,
		TriggeredByScheduleId: schedule.Id,
	}
	scheduler.jobService.CreateJob(&job)
	log.Printf("Created job: %s.\n", job.DisplayName)
}

// Updates the schedule next and last run, returns whether the schedule is valid.
func (scheduler *Scheduler) updateSchedule(schedule Schedule, lastRun time.Time) bool {
	cronExpr, err := cronexpr.Parse(schedule.CronString)

	if err != nil {
		log.Printf("Could not parse cron expression: '%s'.\n", schedule.CronString)

		// Set the schedule status to invalid cron.
		disableStmt, err := scheduler.dbConfig.Db.Prepare("UPDATE schedule SET enabled = false, status = 1 WHERE id = ?")
		failOnError(err, "Could not prepare disable schedule statement")
		defer disableStmt.Close()
		disableStmt.Exec(schedule.Id)

		return false
	} else {
		updateStmt, err := scheduler.dbConfig.Db.Prepare("UPDATE schedule SET last_run = ?, next_run = ? WHERE id = ?")
		failOnError(err, "Could not prepare update schedule statement")
		defer updateStmt.Close()

		// Set the schedule's last and next run.
		updateStmt.Exec(lastRun, cronExpr.Next(time.Now()), schedule.Id)

		return true
	}
}
