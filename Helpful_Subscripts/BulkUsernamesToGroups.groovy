// BulkUsernamesToGroups
// Created by Isaac Monheit on 2-Apr-2025
// Last updated 2-Apr-2025
// 
// Returns the associated Group IDs of the inputted string of usernames
// Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment

// Newline-separated list of usernames
def usernamesStr = """
dzuretti
echiappini
bshaver
cruggiero
"""

// Split the string into a list of usernames (ignoring any empty lines)
def usernames = usernamesStr.readLines().findAll { it?.trim() }

// List to hold the resulting group IDs
def groupIds = []

// Process each username: build the email, call the API, and collect the account ID
usernames.each { username ->
    // Construct the email address
    def email = "${username}@copyright.com"
    // Build the API URL using the email
    def url = "/rest/api/3/user/picker?query=${email}"
    
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
                def groups_names = group_locate_response.body.name
                def groups_Ids = group_locate_response.body.groupId
                groupIds << groups_Ids
                logger.info("Groups: ${groups_names}")
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

return groupIds