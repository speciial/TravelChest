# TravelChest

Travel Chest is an AR application for android phones that allows you to save your 
favorite moments and travel experiences and tie them to the locations you've been
to. 

# Building the app

After cloning the project, you need to create a Firebase Project to connect the app with.
You will need a "google-services.json" in order to compile the app. The following steps 
will explain how to generate the "google-services.json" and where to put it.

1. Create a new Firebase project by going to [Firebase Console](https://console.firebase.google.com)
	1.1 Select "Add Project"
	1.2 Enter a project name like "TravelChestClone" and hit next
	1.3 Deselect google analytics and hit "Create Project" (This will take a moment)
	1.4 Once done, hit next again
2. Activate authentication
	2.1 You should now be in your project overview. On the left under "Develop" 
	select "Authentication"
	2.2 In the users tab, select "Create Authentication Method" in the middle of the screen
	2.3 Now activate the google service from the service table by clicking it. You will have 
	to supply an email address in the activation pop-up
	2.4 Then simply click "Activate" in the top right of the pop-up and click save
3. Setting up the storage
	3.1 Back in the "Develop" tab now select "Storage"
	3.2 Since there is no storage set up yet, just select the "Start Now" button in the middle 
	of the screen
	3.3 On the first pop-up page, simply click next 
	3.4 On the second, select your region (in our case "europe-west") and click next.
	Creating the storage will take a while
4. Connecting the App
	4.1 Navigate back to your project overview and click the little android icon in the middle 
	of the screen, right under the text "Add firebase to your app to get started"
	4.2 Now just add the android package name "com.speciial.travelchest" and optionally pick a nickname 
	for the app. Leave the SHA1 blank for now
	4.3 Then click register app
	4.4 Now download the "google-services.json" and paste it into your app folder of the android project
	like shown on the website
	4.5 The andriod project already contains all the dependencies, so you can skip the next two 
	steps to get back to your overview
	
That's it. Now that you have the "google-services.json" in your app folder of the android project 
you can build the app

If you encounter problems with connecting to the service, you might have an issue with your
SHA1 fingerprint. In that case, follow the instructions [here](https://stackoverflow.com/a/39144864/10235027)
