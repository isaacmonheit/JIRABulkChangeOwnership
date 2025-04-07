// DeleteUsersFromAllGroups.groovy
// Created by Isaac Monheit on 7-Apr-2025
// Last updated 7-Apr-2025
// 
// Removes membership of all groups from     all the users the inputted string of usernames
// Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment

// Newline-separated list of usernames
def usernamesStr = """dmikulin
dzuretti
echiappini
bshaver
cruggiero
bhotwani
chan
cloring
ecolleran
evalkevich
echin
ddarichev
ewilson
egadde
eheilman
ekalashnikova
ddeshpande
dwilson
esemaniuk
ebatchelder
dvasapolli
eyardimci
esinkinson
bramkumar
cforcey
bbarrett
clapaz
ccody
cpawelczyk
bnicholson
bcrowley
bsrivastava
bhpatel
crazin
clopez
ckarunakaran
cminer
bdinnocenzo
cmartineau
jbillington
jkennedy
jmccarthy
kzamore
japaro
jpsavage
jreynolds
jbrandi
jpalve
kjadhav
jvellucci
hghadge
gscott
gheck
ggumagay
glima
ismola
gwill
gcuevas
ipianov
gghare
hsilina
fgracely
gsekar
iconnor
hujjainkar
group_pe_dev
gchaplick
grychko
inedoviesov
izhyzhrii
grao
gchasan
llawson
kmeyer
lbarrett
lrigordaeva
kelias
lbarsalou
kkumar
lpetroff
lphillips
lcleveland
kcoates
kshete
kbrusewicz
lbarron
legaldepartment
kmitchell
aoksenych
amercado
ameryman
aspencer
abidwai
asayre
alanke
apollock
avaidya
aflood_
arakitsky
ayatsau
ahaurylau
agrocholewska
alsmith
agultlingen
bsuarnaba
atanguay
asahu
akulkarni
atsyrybko
agladka
abhandari
anaseem
arajguru
amanekar
fake_addr
ashinde
atayade
aerickson
bafeltowicz
ikulyk
ipukhau
jdenicola
jrodgers
imroz
jdupont
jrichardson
imulani
noreply
jperumal
jcabaup
jpovey
jwozniak
jhayes
rpandeti
ppatil
pkoshe
terminated_pmartinez
pkulkarni
ptaylor
oboncheva
pflaherty
nngwa
olibouban
odekhtyar
oivonin
opsgenie_alert
mmahabaleshwarkar
pbarhate
nbaiduk
nchaudhari
okulkarni
skhapre
snanaware
rswitzer
rrenahan
rgilbuela
salison
smcnamara
rkulkarni
sborade
sphadtare
rbhore
pbettencourt
praj
ralves
rpeters
rjones
saphale
rspecht
saher
rgopinath
rmckinney
rlpublisherfocusteam
rjoseph
sgupta
samhayman
rpueschel
schaudhary
spotdar
pganvir
ppimpalkar
riyer
rsampath
ppatankar
pjoseph
pbaangad
rpliashkou
psanklecha
pheck
plad
pbhujbal
pshapiro
rbuhlmann
pmcgee
ptrivedi
reportportal
rkherkar
mlawate
marketplace_team
motoole
mchaudhari
mcooper
mtajale
lxu
mciambella
mbhandarkar
lalberione
bobyrne
csullivan
cpendleton
ccosgrove
cnegrotti
dbednarek
dgavali
ctargett
clawton
dwamae
dlohe
dbriggs
dpierson
dmurphy
mjablonska
mupadhyay
mleffler
mskyda
mnorell
mgolan
mjoyce
mkulch
msankaran
nsmith
ybhatt
ysiachko
ybharadwaj
sberman
srisbud
sdhamal
sjawaji
sfessenden
snolan
shjadhav
svenkatanarayana
stheyagarajan
slasinoviy
patil
sgautam
spanda
stanislav_kurilin
snagarajarao
ssharma
ssubramanian
sbelore
ssamiappan
ssarnaik
spatwardhan
ssankpal
snadkarni
wdoeleman
vtsupryk
vjanugade
vsims
voze
vgaikwad
vtatarinov
varunachalam
vrumao
vjain
wcohn
vgalande
vshutava
wkmetz
tkulkarni
tmckinney
uzburzhynski
sbosworth
tlabella
thounsome
sshipe
snelson
spathak
tdonovan
snadage
tbongiorno
smasilamani
ssahoo
tprisby
sghadage
snama
sbaker"""

// Split the string into a list of usernames (ignoring any empty lines)
def usernames = usernamesStr.readLines().findAll { it?.trim() }

// Process each username: build the email, call the API, and remove the user from each group
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
                // Iterate over each group object in the response and remove the user from the group
                group_locate_response.body.each { group ->
                    def groupName = group.name
                    def deleteUrl = "/rest/api/3/group/user?groupname=${groupName}&accountId=${current_id}"
                    try {
                        delete(deleteUrl).header('Content-Type', 'application/json')
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