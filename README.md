### Hybrid Messaging SDK
*By: CMTelecom.com*</br>*Version: 1.2.1*

**Find the complete documentation at:** </br>
[http://www.cmtelecom.com/products/messaging/hybrid/sdk-documentation][1]

Import the SDK:

#### Android Studio Project
Use gradle to import the latest version of the SDK

    compile 'com.cm.hybridmessagingsdk:hybridmessagingsdk:1.2.1'
    
When having conflicts with Google Play Services exclude the play services from the .aar 

    
    compile (com.cm.hybridmessagingsdk:hybridmessagingsdk:1.2.1@aar) {
        exclude group: 'com.android.support', module:'support-v4'
    }    
    

#### Eclipse project
Download the .jar file from [http://www.cmtelecom.com/products/messaging/hybrid/sdk-documentation][1]

[1]: http://www.cmtelecom.com/products/messaging/hybrid/sdk-documentation
