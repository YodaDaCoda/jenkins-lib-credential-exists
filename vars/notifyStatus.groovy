#!groovy

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

String getMessage() {
	println('begin getMessage()')

	String ret = "${status}: ${currentBuild.fullDisplayName}"

	println(ret)
	println("${env.CHANGE_ID}")
	println("${env.BRANCH_NAME}")
	println(env.CHANGE_ID)
	println(env.BRANCH_NAME)
	println(env)

	if ("${env.CHANGE_ID}" != null) {
		ret = "${ret} (<${env.CHANGE_URL}|${env.CHANGE_ID}> by ${env.CHANGE_AUTHOR_DISPLAY_NAME})"
		println('change_id not null')
		println(ret)
	} else if ("${env.BRANCH_NAME}" != null) {
		println('branch_name not null')
		println(ret)
		ret = "${ret} (branch: ${env.BRANCH_NAME})"
	}

	println(ret)

	ret = "${ret} after ${currentBuild.durationString} (<${RUN_DISPLAY_URL}|Open>)"

	println(ret)

	println('end getMessage()')

	return ret
}

void call(String status) {
	println('notifyStatus')
	String color = getStatusColor(status)
	println(color)
	String message = getMessage()
	println(message)
	slackSend (
		channel : '#jenkins-ci',
		color   : color,
		message : message
	)
}
