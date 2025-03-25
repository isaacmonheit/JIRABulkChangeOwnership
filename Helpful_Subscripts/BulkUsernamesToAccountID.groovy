// BulkUsernamesToAccountID
// Created by Isaac Monheit on 25-Mar-2025
// Last updated 25-Mar-2025
// 
// Returns the associated Account IDs of the inputted string of usernames
// Run in the "Script Console" section of ScriptRunner in the 'ccc-sandbox-385' testing environment

// Result of this script found in "../UsernamesForDeletingAccounts/Users not found in AD (Account Ids).txt"

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

// List to hold the resulting account IDs
def accountIds = []

// Process each username: build the email, call the API, and collect the account ID
usernames.each { username ->
    // Construct the email address
    def email = "${username}@copyright.com"
    // Build the API URL using the email
    def url = "/rest/api/3/user/picker?query=${email}"
    
    // Make the API call; expecting a JSON response containing a list of users
    def response = get(url).header('Content-Type', 'application/json').asObject(Map)

    // Add current ID to the list
    accountIds << response.body.users.accountId[0]
    logger.info("${username} added. ID: ${response.body.users.accountId[0]}")
}

// Return the list of account IDs
return accountIds