# TravelChest

Travel Chest is an AR application for android phones that allows you to save your 
favorite moments and travel experiences and tie them to the locations you've been
to. 

# Google services

1. Create a FireBase project by going to: [Firebase Console](https://console.firebase.google.com)
2. In the develop tab on the left, navigate to "authentication" and enable google connetion in the list of services
3. Again in the develop tab, navigate to "storage" and create the default bucket
4. Get your Web Api key
5. Replace "<YOUR_API_KEY>" by your Api key in the file [google-services.json](app/google-services.json)

If have trouble connecting to the firebase service it might be because of your SHA1 fingerprint. 
Please refer to [SHA1 Fingerprint fix](https://stackoverflow.com/a/39144864/10235027)
