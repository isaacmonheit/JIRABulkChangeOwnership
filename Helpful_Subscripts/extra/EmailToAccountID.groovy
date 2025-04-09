// EmailToAccountID
// Created by Isaac Monheit on 10-Mar-2025
// Last updated 10-Mar-2025
// 
// Returns the associated Account ID of the inputted email
// Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment

// Input the desired email here
String email = "imonheit@copyright.com"

def url = "/rest/api/3/user/picker?query=${email}"
def response = get(url).header('Content-Type', 'application/json').asObject(Map)
return response.body.users.accountId[0]