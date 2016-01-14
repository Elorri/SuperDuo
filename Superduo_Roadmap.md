#SuperDuo Roadmap

Note each use case will have its git branch. ex:1.0_database_beehouses_check

## Alexandria

### Release 1
This the actual version of the app. The user can search for a book using the ISBN number but can't using a word search.
##### 1.0_errors_and_guidance_messages
  - [x] refactor and get to know existing code, add TODOs
  - [x] app crash when adding a book without internet connection
  - [x] app crash when rotating "add a book"
  - [x] app crash when adding an unexisting isbn
  - [x] app crash when adding a book without authors
  - [x] clicling back button when on first screen => crash
  - [x] no guidance message when searching for a book and db is empty with or without network
  - [x] no guidance message when searching for a book and book not found in the db, although db is not empty
  - [x] handle cases when data returned from the serveur is null ex:no string, no authors, etc either display a white space, or don't add this in your db
  
##### 1.1_building_a_total_experience
  - [ ] button "rechercher" se transforme en button "OK" when rotating the screen "list of book"
  - [ ] button "scan" se transforme en button "OK" when rotating the screen "add a book"
  - [ ] settings has no toolbar
  - [ ] setting does not display current option
  - [ ] scan button display a toast

### Release 2 
The user can search for book without internet connection and the app does not crashes or when adding a book.

##### 2.0_errors_and_guidance_messages
  - [x] UI object that can be empty and need user message to comfort him.
   - [x] fragment_list_book.xml : bookList ListView 
   - [x] fragment_add_a_book.xml : noBookFound TextView 
  - [x] Make the corresponding list of tables
   - [x] BookEntry : AuthorEntry : CategoryEntry
  - [x] Make the corresponding list of serveur url request
   - [x] https://www.googleapis.com/books/v1/volumes?q=9780743477116

##### 2.1_building_a_total_experience
  - [ ] put tablet layout in its own directory
  - [ ] create a CustomView
  - [ ] use a Library : scanning bar code integrated inside the screen
  - [ ] improve UI  
  - [ ] Not requiered : add a fixed sized widget
  - [ ] Not requiered : add a collection widget  

##### 2.2_accessibility
  - [x] Add navigation drawer description for accessibility in the new ActionBarDrawerToggle()
  - [ ] Not requiered : add content description for talkback use by sight-impaired users
  - [ ] Not requiered : implement accessibility interfaces for CustomView
 
##### 2.3_localisation
  - [ ] android:layout_gravity="start" + android:layout_gravity="left" because we start under 17 API in activity_main
  - [ ] allow for localisation in France and US
  - [ ] add layout mirroring
  
##### 2.4_performance
  - [ ] Overdraw check
    - [ ] Debug GPU Overdraw check
	- [ ] Hierarchy viewer
  - [ ] Leak Memory check  
	- [ ] TraceView	
	- [ ] Heap Viewer
	- [ ] Allocation tracker
  - [ ] Battery check
	- [ ] Battery Historian


##Football Scores

### Release 1
This the actual version of the app.
##### 1.0_errors_and_guidance_messages
  - [x] no guidance when we first launch the app with no internet an d there is no data in the db
  - [x] fetch seems to be done for each day

##### 1.1_building_a_total_experience
  - [ ] if no matchs planned, mock data is displayed but no info to the user, telling him it's mock data

### Release 2 
The user can see a message that there is no data shown because no internet connection have been found.

##### 2.0_errors_and_guidance_messages
  - [x] refactor and get to know existing code, add TODOs
  - [ ] use recyvler view instead of listView
  - [ ] use retrofit
  - [x] use SyncAdapter and catch crashes and prevent him from making redundant fetch call
  - [x] UI object that can be empty and need user message to comfort him.
   - [x]  fragment_scores.xml : scores_list ListView
  - [x] Make the corresponding list of tables
   - [x] SCORES_TABLE
  - [x] Make the corresponding list of serveur url request
   - [x] http://api.football-data.org/docs/latest/index.html
   - [x] http://api.football-data.org/alpha/fixtures?timeframe=n2
   - [x] http://api.football-data.org/alpha/fixtures?timeframe=p2
  - [x] api http errors handlings : http://api.football-data.org/docs/latest/index.html#_http_error_codes_returned


##### 2.1_building_a_total_experience
  - [ ] today screen scroll doesn't match parent
  - [ ] no nice icons
  - [ ] improve tab layout implementation
  - [ ] create a CustomView
  - [ ] use a Library
  - [ ] add a fixed sized widget
  - [ ] add a collection widget  

##### 2.2_accessibility
  - [ ] add content description for talkback use by sight-impaired users
  - [ ] implement accessibility interfaces for CustomView
 
##### 2.3_localisation
  - [ ] allow for localisation in France and US and China
  
##### 2.4_performance
  - [ ] Overdraw check
    - [ ] Debug GPU Overdraw check
	- [ ] Hierarchy viewer
  - [ ] Leak Memory check  
	- [ ] TraceView	
	- [ ] Heap Viewer
	- [ ] Allocation tracker
  - [ ] Battery check
	- [ ] Battery Historian

## Detail Statistics

| Branches  | LOC    | Hours |
| ------------- | :-----------: |:-------------: |
| 2.0_errors_and_guidance_messages|739 |  23   |
| 2.1_building_a_total_experience|0|0|
| 2.2_accessibility|0|0|
| 2.3_localisation|0|0|
| 2.4_performance|0|0|




##General Statistics


| LOC    | Hours | Days | 
| :------: | :-----: |:-----: |
| 739  |  23  |  |

Last number of LOC : 8783
Starting Code number of LOC : 8124
