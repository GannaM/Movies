IMPORTANT BEFORE YOU START

To build this project you need to add your own API key:
- into the string resources file as follows:
<string name="api_key”>[YOUR_API_KEY]</string>

- AND in gradle.properties file add the following line:
API_KEY="[YOUR_API_KEY]"

You can obtain your personal API key at themoviedb.org (https://developers.themoviedb.org/3/getting-started/introduction)

----------------------------------------------------------------------------

THE MOVIES APP

The app shows a list of movies from TheMovieDb.org. A user allowed to change sort oder via a setting  either by most popular or by highest-rated. Also a user can save a movie to a favorite list.

Also tapping on a movie from any other above mentioned lists transitions to a detailed screen with additional information such as: original title, movie thumbnail, synopsis, user rating and release date. Here a user can add/remove a movie to/from the favorite list.


PAGING WITH NETWORK USING PAGEKEYEDDATASOURCE

This app demonstrates how to implement Paging library with backend API (TheMovieDb API) with multiple datasource (depending on user's sort preferences).

Favorite movies are stored locally using Room Database. To retrieve favorite movies, standard datasource is used. 


ASYNCTASKLOADER TO FETCH MOVIE'S DETAILS

MovieDetailActivity shows an example how to use AsyncTaskLoader to fetch additional movie details


Libraries:

- Retrofit for api communication
- Picasso for image loading
- Android Architecture Components
- Butterknife for data binding

