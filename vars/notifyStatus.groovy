#!groovy

import hudson.Util;

String getStatusColor(String status) {
	if (status == null) {
		return 'good'
	}
	if (status == 'STARTED') {
		return getStatusColor(currentBuild.previousBuild?.result)
	}
	if (status == 'SUCCESS') {
		return 'good'
	}
	if (status == 'UNSTABLE') {
		return 'warning'
	}
	if (status == 'FAILURE') {
		return 'danger'
	}
	// this should never happen - return a blue color because that's different to any other status color
	echo "Unknown build status ${status}"
	return '#0000FF'
}

String getMessage(status) {
	String ret = "${status}: ${currentBuild.fullDisplayName}"

	String buildDurationString = Util.getTimeSpanString(currentBuild.duration)

	if (env.CHANGE_ID != null) {
		ret = "${ret} (<${env.CHANGE_URL}|#${env.CHANGE_ID}>"
		if (env.CHANGE_AUTHOR_DISPLAY_NAME != null) {
			ret = "${ret} by ${env.CHANGE_AUTHOR_DISPLAY_NAME})"
		} else if (env.CHANGE_AUTHOR != null) {
			ret = "${ret} by ${env.CHANGE_AUTHOR})"
		} else if (env.CHANGE_AUTHOR_EMAIL != null) {
			ret = "${ret} by ${env.CHANGE_AUTHOR_EMAIL})"
		} else {
			ret = "${ret} by unknown)"
		}
	} else if (env.BRANCH_NAME != null) {
		ret = "${ret} (branch: ${env.BRANCH_NAME})"
	}

	if (status != 'STARTED') {
		ret = "${ret} after ${buildDurationString}"
	}

	ret = "${ret} (<${RUN_DISPLAY_URL}|Open>)"

	return ret
}

void attachFile(slackResponse) {
	String sfdxReportFile = 'build/reports/sfdx-report-short.json'

	if (!fileExists(sfdxReportFile)) {
		println("Unable to find SFDX deploy report file (${sfdxReportFile})")
		return
	}

	def report = readYaml(file: sfdxReportFile)

	def numberComponentErrors    = report['numberComponentErrors']
	def numberComponentsDeployed = report['numberComponentsDeployed']
	def numberComponentsTotal    = report['numberComponentsTotal']
	def numberTestErrors         = report['numberTestErrors']
	def numberTestsCompleted     = report['numberTestsCompleted']
	def numberTestsTotal         = report['numberTestsTotal']

	String message = "SFDX Build Report"
	message = "${message}\nComponents: ${numberComponentsDeployed}/${numberComponentsTotal} (errors: ${numberComponentErrors})"
	message = "${message}\nTests: ${numberTestsCompleted}/${numberTestsTotal} (errors: ${numberTestErrors})"

	slackSend(
		channel : slackResponse.threadId,
		message : message
	)

	slackUploadFile(
		channel: slackResponse.threadId,
		filePath: sfdxReportFile
	)
}

void sendMessage(status, channel, color, message) {
	def slackResponse = slackSend(
		channel : channel,
		color   : color,
		message : message
	)
	if (status != 'STARTED' && status != 'SUCCESS') {
		attachFile(slackResponse)
	}
}

void call(String status) {
	String color = getStatusColor(status)
	String message = getMessage(status)
	sendMessage(status, '#jenkins-ci', color, message)
}
