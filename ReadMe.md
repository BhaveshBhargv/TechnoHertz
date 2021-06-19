# A Java Spring-boot app for registering users.

##Appication Information

In this application, user can register themselves by providing required data.
After registration user will get email notification on the email which they have used to register.
The email includes username and system generated password through which user can log in and use other feature,
like get details, update details or delete details.

While registering, user have to pass some basic information about themselves, and an ID file (PDF/JPEG image).
Which they see, update and delete after successful registration.

In this application, there is also security feature, meaning user has to authenticate themselves before accessing any URI. User can also loging as admin using admin credentials.

##Application Dev info

###ADMIN CREDENTIALS:
**username: admin**

**password: admin**

###APIS:
**To authenticate:** http://localhost:8080/auth **Request type:** Post

**To register:** http://localhost:8080/user/register **Request type:** Post

**To get user details:** http://localhost:8080/user/{userEmail} **Request type:** Get

**To get user file uploaded:** http://localhost:8080/user/{userEmail}/file **Request type:** Get

**To update user:** http://localhost:8080/user/{userEmail} **Request type:** Post

**To delete user:** http://localhost:8080/user/register **Request type:** Delete
  

###Database: 
Mysql is used to store data.

For table, once dev provide url for mysql datasource, application will create **user** table on its own (if not already created).

