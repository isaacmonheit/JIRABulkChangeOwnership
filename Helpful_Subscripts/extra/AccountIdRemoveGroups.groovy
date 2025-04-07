
// Input the desired email here
String email = "imonheit@copyright.com"

def url = "/rest/api/3/user/picker?query=${email}"
def response = get(url).header('Content-Type', 'application/json').asObject(Map)
def myID = response.body.users.accountId[0]

def group_locate_url = "/rest/api/3/user/groups?accountId=${myID}"
def group_locate_response = get(group_locate_url).header('Content-Type', 'application/json').asObject(List)
return group_locate_response.body
