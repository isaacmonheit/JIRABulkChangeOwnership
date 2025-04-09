// DeleteUsersFromAllGroups.groovy
// Created by Isaac Monheit on 7-Apr-2025
// Last updated 9-Apr-2025
// 
// Removes membership of all groups from all the users the inputted string of usernames
// Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment

// Newline-separated list of usernames
def usernamesStr = """
opsgenie_alert
reportportal"""

// Split the string into a list of usernames (ignoring any empty lines)
def usernames = usernamesStr.readLines().findAll { it?.trim() }

// Process each username: call the API, and remove the user from each group
usernames.each { username ->
    // Build the API URL using the username
    username = username + "@copyright.com"
    def url = "/rest/api/3/user/picker?query=${username}"
    
    try {
        // Make the API call; expecting a JSON response containing a list of users
        def response = get(url).header('Content-Type', 'application/json').asObject(Map)
        // Safely extract the first accountId from the response, if available
        def current_id = response?.body?.users?.accountId ? response.body.users.accountId[0] : null
        
        if (current_id) {
            logger.info("${username} added. ID: ${current_id}")
            // Build the URL to fetch groups for this account ID
            def group_locate_url = "/rest/api/3/user/groups?accountId=${current_id}"
            
            try {
                def group_locate_response = get(group_locate_url).header('Content-Type', 'application/json').asObject(List)
                // Iterate over each group object in the response and remove the user from the group
                group_locate_response.body.each { group ->
                    def groupId = group.groupId
                    def groupName = group.name
                    def deleteUrl = "/rest/api/3/group/user?groupId=${groupId}&accountId=${current_id}"
                    try {
                        def deleteResponse = delete(deleteUrl).header('Content-Type', 'application/json')
                        logger.info("Delete response: ${deleteResponse}")
                        logger.info("Removed user ${username} from group: ${groupName}")
                    } catch(Exception delEx) {
                        logger.error("Error removing user ${username} from group: ${groupName}: ${delEx.message}")
                    }
                }
            } catch(Exception groupEx) {
                logger.error("Error fetching groups for ${username} (ID: ${current_id}): ${groupEx.message}")
            }
        } else {
            logger.warn("No account ID found for username: ${username}")
        }
    } catch(Exception ex) {
        logger.error("Error fetching account ID for ${username}: ${ex.message}")
    }
}

return "Completed removal of users from groups"