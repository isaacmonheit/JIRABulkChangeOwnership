// UpdateAllFilters
// Created by Isaac Monheit on 10-Mar-2025
// Last updated 13-Mar-2025
// 
// USER INPUT NECCESARY BELOW!
// - Updates the ownership of all the filters owned by one user (username specified below) to a new owner (username specified below)
// - Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment
// - Can change a maximum of 500 filters at once
// ***NOTE: Ignore the red underlines, the script still works just fine but ScriptRunner thinks there are errors***
//

// USER INPUT NECCESARY HERE:
// Enter the copyright.com username of the current (source) owner
String currentOwnerUsername = ""
// Enter the copyright.com username of the future (destination) owner
String futureOwnerUsername = ""


// Convert from username to email
String oldOwnerEmail = currentOwnerUsername + "@copyright.com"
String newOwnerEmail = futureOwnerUsername + "@copyright.com"


// Getting old owner account Id from email
def url = "/rest/api/3/user/picker?query=${oldOwnerEmail}"
def response = get(url).header('Content-Type', 'application/json').asObject(Map)
def oldOwnerAccountId = response.body.users.accountId[0]

// Getting new owner account Id from email
def url2 = "/rest/api/3/user/picker?query=${newOwnerEmail}"
def response2 = get(url2).header('Content-Type', 'application/json').asObject(Map)
def newOwnerAccountId = response2.body.users.accountId[0]

// Return early if either the old owner or new owner don't exist in Jira
if (oldOwnerAccountId == null || newOwnerAccountId == null) {
        return "You must specify a correct email for the old owner and new owner to be able to change a filter's ownership"
}


// Gathering a list of all filter IDs by associated account
def url3 = "/rest/api/3/filter/search?accountId=${oldOwnerAccountId}&overrideSharePermissions=true&maxResults=500"
def response3 = get(url3)
                    .header('Content-Type', 'application/json')
                    .asObject(Map)
def listOfFiltersOldAccount = response3.body.values.collect { it.id } 



// Updating each filter in the list to the new owner
if (listOfFiltersOldAccount.size() > 0) {
    def successfulTransfers = 0
    def failedTransfers = []

    listOfFiltersOldAccount.each { id ->
        def updateFilterOwner = put("/rest/api/3/filter/${id}/owner")
                .header('Content-Type', 'application/json')
                .body([
                        accountId: newOwnerAccountId
                ])
                .asString()

        if (updateFilterOwner.status >= 200 && updateFilterOwner.status < 300) { // If the transfer works
            successfulTransfers++
            logger.info("The filter with the Id: ${id} was successfully transferred to ${newOwnerEmail}.")
        } else { // If the transfer doesn't work
            failedTransfers << id
            logger.error("Failed to update filter with the Id: ${id}.")
        }
    }

    def totalFilters = listOfFiltersOldAccount.size()
    def resultMessage = "${successfulTransfers} of ${totalFilters} filters have been transferred to ${newOwnerEmail} successfully."

    if (failedTransfers) {
        resultMessage += "Failed filter IDs: ${failedTransfers.join(', ')}"
    }

    return resultMessage
}

return "No filters to change!"