# Drops
Drops is an Android app created to be used by the students of the TU/e. The purpose is to socially connect students to each other through a campus-based platform to share ideas, updates and thoughts.

The app provides an easy and efficient system to quickly broadcast announcements throughout the TU/e campus. The main purpose of Drops is for students to leave notes (known as drops) for each other based on their current location. The notes will be visible for 24 hours on the campus map, where one can see and interact with the drops in their vicinity. The users can also make their own groups to exclusively share drops with, and they can post anonymously. The app utilizes a community moderation system, in which users can report drops or comments for moderator accounts to filter through and delete, providing a safe and inclusive environment for the users of the app.

Drops gathers many subjects of conversation in a safe and efficient way, to make a unified social hub for TU/e students.

#### The Map and the Dashboard
The app's main functionality is centered around an interactable map, in which the user can view and interact with drops around them. Along with that, the nearby notes within a 50-meter radius can be viewed and interacted with in a list view from the dashboard.

#### The Drops
A drop is a note that a user can leave on their current location (exclusive to the TU/e campus!), in which they can share their thoughts and ideas. The drop includes a title and text, and it includes the ability to post to a group and to post anonymously. Once created, a drop can be upvoted and downvoted, it can be reported, and it can be deleted by the creator or by a moderator. Furthermore, users can leave comments on drops, and those comments can also be reported and deleted by moderators.

#### The Groups
A group is a community of people connected in some way. Any user can make a group and add other users to it. They can then make drops visible exclusively to that group. Users can choose to leave groups if they would like.

#### Moderation
A big part of the app's success is heavily dependent on the usage of proper moderation methods to keep the app a safe space for everyone. Users can report drops or comments they find inappropriate, and the moderators can then view and delete them.

## Code structure
This section will explain a few more of the functional components of our app, for your better understanding while navigating through. Below, we will further explain the specifics of the API and GPS.

We are using a maps SDK for the app, which requires an API key. In order to use the Maps SDK, follow [this guide](https://developers.google.com/maps/documentation/android-sdk/get-api-key) to get an API key. Then, add the following line (without the []) to the local.properties file:
```
MAPS_API_KEY=[YOUR API KEY]
```

This is to ensure the correct running and functionality of the app.

### API functionality
The classes relevant to the API functionality can all be found in the 'utils' folder. These classes can mostly be divided up into two categories: API interfaces and their corresponding classes. The API interfaces define the calls that can be made to the server that are relevant to a certain object, while their corresponding classes can store the objects that the server returns, they also contain the functions that actually make the API calls. There is also the RetrofitClient class, which handles all Retrofit functionality, this includes getting Retrofit instances, adding Interceptors, and making Callback functions. The SessionManager class exists to store the data that is returned from the API calls, this is done to avoid having to make certain API calls again, which would require the user to wait more often. Lastly, there is also the AuthInterceptor class, which intercepts the outgoing HTTP requests and adds an authentication token to the header.

#### Making an API call
To show an example of how an API call is made, take the getAccountById method in the AccountAPI interface:
```
  @GET("accounts/{id}")
  Call<Account> getAccountById(@Path("id") int id);
```
Here, ```@GET("accounts/{id}")``` specifies the endpoint for this API call (i.e. the base URL with suffix 'accounts/{id}'). '{id}' is in curly brackets to signify a variable, this variable has to be passed into the API call as can be seen in ```@Path("id") int id```. Lastly, you can see that the call will return an Account with ```Call<Account>```.

The next step in making an API call is found in the Account.getAccountById() method. This method starts off with creating an instance of the API interface using the RetrofitClient class.
```
  AccountApi api = RetrofitClient.getInstance(context).create(AccountApi.class);
```
The code then gets an instance of the relevant call into a variable
```
  Call<Account> getCall = api.getAccountById(user_id);
```
Lastly, the call to the server gets made with the following method
```
  getCall.enqueue(RetrofitClient.getCallback(context, "Failed to get account", successBehaviour, () -> {}));
```
Here we get a Callback from the Retrofit client class. This callback specifies a few things, firstly it specifies the error message displayed to the user in case the call fails (in this case "Failed to get account"). Then it also specifies the 'successBehaviour', this is the behavior that is run if the call to the server was successful and we got an object back. The last parameter is the 'endBehaviour', which specifies the behavior of what the callback should do if it finishes, this can either be after a successful call, but also after an unsuccessful call.

### GPS functionality
GPS helper methods are contained in the GPSHandler class, this separates their implementation from the individual usages. For a class to get the user's location it first creates its own instance of GPSHandler, and also an instance of the built-in LocationManager. Then it can use the relevant helper method to check for location permissions and get the user's location, for example:
```
  if (gpsHandler.checkPermission(getContext(), getActivity()){
    Location userLocation = locationManager.getLastKnownLocation(GPSHandler.locationProvider);
  }
```
In the case of the Map Screen, rather than a single location request, continuous updates of the user's location are needed. For this the class is made into a listener for the locationManager's updates:
```
  if (gpsHandler.gpsHandler.checkPermission(getContext(), getActivity()){
    locationManager.requestLocationUpdates(GPSHandler.locationProvider, 0, 0, this);
   }
  ...
  public void onLocationChanged(Location location){...}
  public void onLocationChanged(List<Location> locations){...}
```
The location update may come as a single location, in which case handling it is simple, or it may come in a batch of multiple locations. In the latter case the GPSHandler method fliterLocation is used to convert the batch into a single location.

The majority of the GPSHandler methods are to help with checking when the database needs to be queried for drops. On the Map Screen, whenever the user moves the map a listener is triggered which uses the checkBoundary helper method to detect whether the user's map view is close to the borders of the currently stored query area. If the view is close enough to the borders, the queryFromView helper is used to construct a new query area based on the current map view, this query area is used in an API call to the database.

Another method of query area generation is available in GPSHandler, which is queryByRange. This is used by the Dashboard to request drops close to the user's current location. The queryByRange method accepts an origin coordinate and a range in meters, it then constructs a square query area that is centered on the origin and has side length of twice the range. This is again used in an API call to the database.

### User interface
The user interface is divided into 4 main activities: the splash (the intro activity where the user can choose between sign up or log in options), the sign up, the log in and the bottom navigation activity. The last one is the main activity because it is the parent of all the fragments that make up the app functionality. The default destination of the navigation is the map fragment as Drops is an app based on the user location. Here, the map can be explored to find drop icons, which contain the messages left by other users in your vicinity. There is also a floating action button which opens the create drop dialog. The user can customize their drop and send it. It shall appear on the map at the user's current location.

From the map fragment, the user can navigate to the dashboard fragment where the available drops are listed in a feed for easier perusing. The user can also navigate to the settings fragment to access account settings. 

Clicking on either the drop marker on the map, or the compact view on the dashboard, opens a full drop fragment with the full text of the message, the time of the post, buttons such as upvote/downvote and report, and the comment section. In the dashboard there is also a section for group buttons, such that the user can choose to see either a group's drop feed or the public one. From here, the user can also open the create new group fragment.

The app heavily relies on the use of the RecyclerView component, to correctly populate the section with the relevant content from the database. In order to do so, the RecyclerViews are attached to a custom ViewHolder class, as well as an Adapter class. 

## Testing
The non-GUI classes in the 'utils' folder are tested using integration tests. This has a consequence that most classes do not have a corresponding test class. This is because those classes contain mostly API calls, which are tested with the integration tests. The callbacks that the API calls use are properly tested, this can either be a separate function or the RetrofitClient.getCallback() method, which does have unit tests.

## User scenario
In this user scenario, we will take you through some of the main functionalities of the app. We have included instructions on how to make an account, how to create groups and drops, how to explore drops in the dashboard, how to change account settings and how to navigate through moderator functionality. Along the user scenario you might notice a lot of other functionality. We encourage you to go through the user scenario intuitively and try out the app in any way you want, the user scenario is simply there to guide you through how we believe a user would navigate through the app normally.

Before going through the user scenario, please go to the website https://appdevdatabase.onrender.com/ and wait until the message "{"message":"API IS WORKING"}" appears on your screen. This is to activate the server.

### Making an account
From the splash screen, you will have to navigate to the Sign up screen by pressing the 'Sign up' button. There fill in your email, account name and password to create an account. You can also choose whether to make a moderator account or not (For this user test, you should make a moderator account to be able to access full functionality of the app). Then, you will be automatically logged in and navigated to the map screen. Please allow location services for the app to function properly.

### Creating a group
Navigate to the dashboard from the taskbar at the bottom left. In the dashboard, you can view nearby drops, you can view your groups and create new groups. By default, every account is part of the public group. To create a new group, press on the plus button at the bottom right, fill in the group name and add the members you want in the group. Once you press the create button, the group will be created and it should be visible at the bottom of the dashboard. Once you are in a group, you can also leave it by pressing it on the dashboard and pressing the 'Leave Group' button on the top right.

### Creating drops on the map
From the map tab, press the plus button on the bottom right to create a new drop. From there you can fill in a title and text, you can toggle anonymous on and off, and can choose the group you are posting in. On creation, the drop will be viewable on your position on the map. You and other accounts can then also press 

### Exploring drops in the dashboard
Once you have made a drop, you will be able to view it in the dashboard. In the dashboard you can also see all other nearby drops, and you can switch between the groups you are in to view their nearby drops. 

### Changing account settings
In the settings tab (accessed by pressing the cog on the bottom right) the user has the option to toggle moderator mode on and off (if they are a moderator account), to change their password, to log out and to delete their account. The moderator toggle unlocks moderator view for the rest of the app, which will be explained later on in the user test.

### Moderator mode
From a moderator account, moderator functionality is unlocked by toggling moderator view on from the settings tab. We suggest first reporting some drops and comments to be able to see them in moderator view. Drops and comments can be reported by pressing the red flag on any view. Once moderator mode is on, naviagate to the dashboard to view reported drops and comments. There, you are able to delete them if you'd like, by pressing the trashcan button.
