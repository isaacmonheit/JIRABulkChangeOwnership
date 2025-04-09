// Remove the "it-ops" group membership for a specified user

// Input the desired email here
String email = "hujjainkar@copyright.com"

// Lookup the user to obtain the accountId
def url = "/rest/api/3/user/picker?query=${email}"
def response = get(url).header('Content-Type', 'application/json').asObject(Map)
def myID = response.body.users.accountId[0]

// (Optional) Get the groups the user is a member of (if needed for logging)
// def group_locate_url = "/rest/api/3/user/groups?accountId=${myID}"
// def group_locate_response = get(group_locate_url).header('Content-Type', 'application/json').asObject(List)
// def groups = group_locate_response.body
// logger.info("Current groups: ${groups}")

// Remove the "it-ops" group membership
def groupToRemove = "it-ops"
// URL-encode the group name
def encodedGroup = URLEncoder.encode(groupToRemove, "UTF-8")
def deleteUrl = "/rest/api/3/group/user?groupname=${encodedGroup}&accountId=${myID}"
def deleteResponse = delete(deleteUrl).header('Content-Type', 'application/json')

// Log the response code from the delete call
logger.info("Delete response code: ${deleteResponse.status}")

return "Completed deletion of group '${groupToRemove}' for user ${email}"