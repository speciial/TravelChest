# TravelChest

Travel Chest is an AR application for android phones that allows you to save your 
favorite moments and travel experiences and tie them to the locations you've been
to. 

# UI Testing

Here are some results of your accessibility testing: [Document](https://docs.google.com/document/d/1pu9inYxYFm0sXsWOSOMoreVID97PmCe5oqQ8OSW4948/edit?usp=sharing)

# Building the app

After cloning the project, you need to create a Firebase Project to connect the app with.
You will need a "google-services.json" in order to compile the app. The following steps 
will explain how to generate the "google-services.json" and where to put it.

1. Create a new Firebase project by going to [Firebase Console](https://console.firebase.google.com)
	- Select "Add Project"
	- Enter a project name like "TravelChestClone" and hit next
	- Deselect google analytics and hit "Create Project" (This will take a moment)
	- Once done, hit next again
2. Activate authentication
	- You should now be in your project overview. On the left under "Develop" select "Authentication"
	- In the users tab, select "Create Authentication Method" in the middle of the screen
	- Now activate the google service from the service table by clicking it. You will have to supply an email address in the activation pop-up
	- Then simply click "Activate" in the top right of the pop-up and click save
3. Setting up the storage
	- Back in the "Develop" tab now select "Storage"
	- Since there is no storage set up yet, just select the "Start Now" button in the middle of the screen
	- On the first pop-up page, simply click next 
	- On the second, select your region (in our case "europe-west") and click next. Creating the storage will take a while
4. Connecting the App
	- Navigate back to your project overview and click the little android icon in the middle of the screen, right under the text "Add firebase to your app to get started"
	- Now just add the android package name "com.speciial.travelchest" and optionally pick a nickname for the app. Leave the SHA1 blank for now
	- Then click register app
	- Now download the "google-services.json" and paste it into your app folder of the android project like shown on the website
	- The andriod project already contains all the dependencies, so you can skip the next two steps to get back to your overview
	
That's it. Now that you have the "google-services.json" in your app folder of the android project 
you can build the app

If you encounter problems with connecting to the service, you might have an issue with your
SHA1 fingerprint. In that case, follow the instructions [here](https://stackoverflow.com/a/39144864/10235027)
