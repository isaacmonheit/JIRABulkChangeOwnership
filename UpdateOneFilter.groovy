// UpdateOneFilter
// Created by Isaac Monheit on 10-Mar-2025
// Last updated 10-Mar-2025
// 
// Updates the ownership of one filter (ID specified below) to a new owner (email specified below)
// Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment


// Enter the Id of the filter to update here
def filterId = '18562'

// Enter the email of the new desired owner
String newOwnerEmail = "imonheit@copyright.com"



def url = "/rest/api/3/user/picker?query=${newOwnerEmail}"
def response = get(url).header('Content-Type', 'application/json').asObject(Map)

def newOwnerAccountId = response.body.users.accountId[0]

if (filterId.size() == 0 && newOwnerAccountId.size() == 0 && newOwnerAccountId.size() == 0) {
        return "You must specify a filter id and the accountId for the new user to be able to change a filter or dashboards ownership"
}

if (filterId.size() > 0) {
    def updateFilterOwner = put("/rest/api/3/filter/${filterId}/owner")
            .header('Content-Type', 'application/json')
            .body([
                    accountId: newOwnerAccountId
            ])
            .asString()

    assert updateFilterOwner.status >= 200 && updateFilterOwner.status < 300

    logger.info("The filter with the Id: ${filterId} was updated to be owned by the user with the accountId of ${newOwnerAccountId}.")
}