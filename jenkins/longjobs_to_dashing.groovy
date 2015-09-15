import groovy.json.JsonBuilder
import org.apache.commons.httpclient.*
import org.apache.commons.httpclient.methods.*
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import hudson.plugins.*


// constants
String WIDGET_JOPS_WAITED_IN_QUEUE 	= "/queuejobs"
String DEFAULT_DASHING_URL      	= "http://dashing:3030/widgets/"
String DEFAULT_DASHING_TOKEN  		= "YOUR_AUTH_TOKEN"
String DEFAULT_WAITED_JOBS_IN_MINS  = "1"   // mostly 15 minutes

// data for debug, which need to be set in jenkins as parameters

dashing_url		= build.buildVariableResolver.resolve("dashingServer") ?: DEFAULT_DASHING_URL
dashing_token	= build.buildVariableResolver.resolve("dashingToken") ?: DEFAULT_DASHING_TOKEN

waitJobsInMinutes = build.buildVariableResolver.resolve("waitJobsInMinutes") ?: DEFAULT_WAITED_JOBS_IN_MINS
waitJobsInSeconds = Integer.parseInt(waitJobsInMinutes) * 60


//
// Check the long waited in queue jobs 
//

println "\n\n========= Checking for long waiting jobs in queue : =============="
println "* who are longer than $waitJobsInSeconds seconds in the queue\n\n"

def queues = Hudson.instance.queue

// println "Queue contains ${queues.items.length} items"

// get the queue list from jenkins instance , [name: waitingminutes]

def longQueues = [:]

for(queue in queues.items) {
    println(">> Check ${queue.task.name}")
	println("current time ${System.currentTimeMillis()} , and waited time: ${queue.getInQueueSince()}")
	durationInSeconds = (System.currentTimeMillis() - queue.getInQueueSince())/1000.0
	println "duration time is $durationInSeconds seconds"

	if (durationInSeconds > waitJobsInSeconds) {
		longQueues[queue.task.name] = Math.round(durationInSeconds/60)
	}
	println("\n")	
}

println (">>>>> Long duration in queues:" + longQueues)

// construct dashing widget request

data = [
  auth_token:  dashing_token,
  title: "Waited in queue (demo)",
  items: longQueues.collect { name, minutes -> 
   [
    "arrow": "icon-warning-sign",
    "color": "red",
    "value": minutes,
    label: name + " => " + minutes.toString() + " mins"
  ]}
]

sendDashing(dashing_url + WIDGET_JOPS_WAITED_IN_QUEUE, data)

// method send_to_dashing
def sendDashing(String dashing_url, Map data) {

	json = new JsonBuilder(data)
	def post = new PostMethod(dashing_url)
	println "\n>> send data to " + dashing_url
	try {
		post.setRequestEntity(
				new StringRequestEntity(json.toPrettyString(),
						"application/json",
						"UTF-8"))
		post.setRequestHeader("Content-Type",
				"application/json; charset=UTF-8")
		def httpclient = new HttpClient()

		int status_code = httpclient.executeMethod(post)
		println "INFO: Response status code: ${status_code}"
		if(status_code != 204){
			throw new IOException("Dashing push failure, status: ${status_code}")
		}
	} finally {
		post.releaseConnection()
	}
}

