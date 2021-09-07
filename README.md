Clearscore Tech Test
====================

A simple implementation of a donut widget for displaying credit report info.

Provides a reusable Donut widget and a simple activity that uses Retrofit to connect to a JSON
API to retrieve the credit report.

Displays an error message and prompt for retry in the case of failure.

Some considerations when building this:
* Must support i18n (tested here with some badly Google-translated French)
* Should be performant and reusable - would need refactoring to decouple it from the credit report model
* Easy fit into any UI, by working as a standard View
* Keep the app itself simple, using well-tested models and libraries

Libraries
---------
* Retrofit - the standard go-to choice for consuming REST APIs - easy to use and very well used and tested
* Robolectric - a pain to use sometimes, but less so than most options for testing UI components
* Mockito - another very standard choice, this time for unit testing. Well supported.

Future features
-------------
Some things to consider for future improvements/changes
* In the existing widget, some of the calculations for drawing could be pulled out of onDraw
* Would like to implement the Donut widget as a Compose component
* Separate the widget further from the credit report to make it more reusable
* Add some animation