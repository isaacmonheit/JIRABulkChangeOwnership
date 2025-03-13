// UpdateAllDashboards
// Created by Isaac Monheit on 13-Mar-2025
// Last updated 13-Mar-2025
// 
// USER INPUT NECCESARY BELOW!
// - Updates the ownership of all the dashboards owned by one user (username specified below) to a new owner (username specified below)
// - Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment
// - Can change a maximum of 100 dashboards at once
// ***NOTE: Ignore the red underlines, the script still works just fine but ScriptRunner thinks there are errors***
//
// CURRENT ERRORS:
// If the "Viewers" portion of the dashboard isn't set to "My Organization" then it seems to disappear from the dashboard view and it seems like it has been deleted. I can't recover it in these cases.
// The main issue is it's not giving an error message when it does this. If there was an error message it would be easier to discern what is going on.
//

// USER INPUT NECCESARY HERE:
// Enter the copyright.com username of the current (source) owner
String currentOwnerUsername = "imonheit"
// Enter the copyright.com username of the future (destination) owner
String futureOwnerUsername = "jjmartin"


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
        return "You must specify a correct email for the old owner and new owner to be able to change a dashboard's ownership"
}


// Gathering a list of all dashboard IDs by associated account
def url3 = "/rest/api/3/dashboard/search?accountId=${oldOwnerAccountId}&overrideSharePermissions=true&maxResults=500"
def response3 = get(url3)
                    .header('Content-Type', 'application/json')
                    .asObject(Map)
def listOfDashboardsOldAccount = response3.body.values.collect { it.id } 

logger.info("t${listOfDashboardsOldAccount}")



// Updating each dashboard in the list to the new owner
if (listOfDashboardsOldAccount.size() > 0) {
    def updateDashboardOwner = put("/rest/api/3/dashboard/bulk/edit")
                .header('Content-Type', 'application/json')
                .body([
                        action: "changeOwner",
                        entityIds: listOfDashboardsOldAccount,
                        changeOwnerDetails: [
                            autofixName: true,
                            newOwner: newOwnerAccountId
                        ],
                        extendAdminPermissions: true
                ])
                .asObject(Map)

    // It isn't returning any error messages but some dashboards are getting lost...
    // If the "Viewers" portion of the dashboard isn't set to "My Organization" then it seems to get lost

    return updateDashboardOwner
}

return "No dashboards to change!"