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
  - [x] settings has no toolbar
  - [x] setting does not display current option
  - [x] scan button display a toast
  - [x] add floating action button

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
  - [x] add barcode scanner functionnality
  - [x] improve tablet layout
  - [ ] create a CustomView
  - [x] use a Library : scanning bar code integrated inside the screen
  - [x] improve UI  
  - [ ] Not requiered : add a fixed sized widget
  - [ ] Not requiered : add a collection widget  

##### 2.2_accessibility
  - [x] Add navigation drawer description for accessibility in the new ActionBarDrawerToggle()
  - [x] Not requiered : add content description for talkback use by sight-impaired users
  - [ ] Not requiered : implement accessibility interfaces for CustomView
 
##### 2.3_localisation
  - [x] android:layout_gravity="start" + android:layout_gravity="left" because we start under 17 API in activity_main
  - [x] allow for localisation in France and US
  - [x] add layout mirroring
  
##### 2.4_performance
  - [ ] Overdraw check
    - [ ] Debug GPU Overdraw check 	: All colors <= blue status OK
	- [ ] Hierarchy viewer			: layout look deep but no drop frame so I will let it like that. 32views in total is less that 70.SearchView seems to be a heavy component.
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
  - [x] no nice icons
  - [x] improve tab layout 
  - [ ] create a CustomView
  - [x] use a Library
  - [x] add a fixed sized widget
  - [x] add a collection widget  

##### 2.2_accessibility
  - [x] add content description for talkback use by sight-impaired users
  - [ ] implement accessibility interfaces for CustomView
 
##### 2.3_localisation
  - [x] allow for localisation in France and US and China
  
##### 2.4_performance
  - [ ] Overdraw check
    - [ ] Debug GPU Overdraw check		: All colors <= blue status OK
	- [ ] Hierarchy viewer 				: emptyListView has a red dot for execution is it because visibility=false ?
  - [ ] Leak Memory check  
	- [ ] TraceView					-> Tools > Android > Android Device Monitor	> DDMS tab > start method profiling > Sample base profiling
	- [ ] Heap Viewer				-> Tools > Android > Android Device Monitor > Android Device Monitor > Heap tab > cause GC
	- [ ] Allocation tracker		-> Tools > Android > Android Device Monitor	> DDMS tab > start allocation tracking > interact with app > stop allocation tracking > ddms371179675432774.alloc
	- [ ] Android Memory Monitor	-> Tools > Android > Memory Monitor
  - [ ] Battery check
	- [ ] Battery Historian

## Detail Statistics

| Branches  | LOC    | Hours |
| ------------- | :-----------: |:-------------: |
| 2.0_errors_and_guidance_messages|851   |  29   |
| 2.1_building_a_total_experience|13871  |34     |
| 2.2_accessibility|38|1|
| 2.3_localisation|22|8|
| 2.4_performance|0|0|




##General Statistics


| LOC    | Hours | 
| :------: | :-----: 
|13871  |43     | 

Last number of LOC :  22804 22782
Starting Code number of LOC : 8124
