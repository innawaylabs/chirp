**Chirp**

**Chirp** is a mobile-only pure-proximity based Android app platform for people to interact with people around them. If you have a question to ask about the neighborhood you are in, or about a new place you are visiting, or at an event, this platform is a great way to break the ice, to get connected and stay connected.

*Required User Stories:*
* [ ] Login with Facebook - 3h
* [ ] Virtual room: Send broadcast message to server
  * [ ] Client UI - 1h
  * [ ] Server to receive the message - 2h
  * [ ] Comms to Server - 3h
* [ ] Virtual room: Poll messages from server
  * [ ] Client UI - 3h
  * [ ] Server's REST API for polling - 2h
  * [ ] Polling from Server (with count and max_id/since_id params) - 2h 
* Interest based filtering
  * [ ] Client UI for setting filters (all in one group) - 4h
  * [ ] Apply filters while polling the server - 1h

*Optional User Stories:*
* [ ] Message ID based rooms for active discussion of interested parties
* [ ] Private conversation with user from the profile page or from one of the rooms
* [ ] Option to enable/disable location tracking in the app
* [ ] Notifications on active app
* [ ] Notifications on a backgrounded/closed app
* [ ] Enable/Disable pop-up notifications
* [ ] Login with Twitter
* [ ] Sign up, email activation and Sign in

*Bonus User Stories:*
* [ ] Talk with at least one IoT machine (like turn on a thermostat/coffee machine saying "hi")
* [ ] Oauth 2.0 based login with GitHub/Google+/LinkedIn/Amazon/AOL/Box/Dropbox/Foursquare/Instagram/PayPal/Stripe/Yammer/Yandex/Zendesk
* [ ] Guest login (???) or Go Anonymous option

*Notes:*
* [ ] Minimum characters-limit on broadcast messages with smart checking.
* [ ] Prevent expletives, afap


Wireframes: <a href="https://popapp.in/w/projects/56d7933afc8bc907550fc94c/mockups">here</a>.

*Walkthrough of all user stories:*

![Video Walkthrough](demo.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).