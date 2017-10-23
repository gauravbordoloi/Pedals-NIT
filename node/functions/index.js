const functions = require('firebase-functions');

//initiliazes the app
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotification = functions.database.ref("Notification")
.onWrite(event => {
    var request = event.data.val();
    var payload = {
        data:{
            username: "Gaurav Bordoloi",
            email: "gmonetix@gmail.com"
        }
    };
    
    admin.messaging().sendToDevice(request.token,payload)
    .then(function(response){
        console.log("successfully sent message :", response);
    })
    .catch(function(error){
        console.log("Error sending message", error);
    })
})
