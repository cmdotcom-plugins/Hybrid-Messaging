### Hybrid Messaging SDK
*Version: 1.2.1*

Find the complete documentation at: </br>
[http://www.cmtelecom.com/products/messaging/hybrid/sdk-documentation][1]



### Import the SDK:

##### Android Studio Project
Use gradle to import the latest version of the SDK

    compile 'com.cm.hybridmessagingsdk:hybridmessagingsdk:1.2.1'
    
When having conflicts with Google Play Services exclude the play services from the .aar 

    
    compile (com.cm.hybridmessagingsdk:hybridmessagingsdk:1.2.1@aar) {
        exclude group: 'com.android.support', module:'support-v4'
    }    
    

##### Eclipse project
Download the .jar file from [http://www.cmtelecom.com/products/messaging/hybrid/sdk-documentation][1]

[1]: http://www.cmtelecom.com/products/messaging/hybrid/sdk-documentation

### example

See the example project for more information on how to implement the SDK.
By using your credentials gained from CM you can test the flow of using the HybridMessagingSDK for logging in.
