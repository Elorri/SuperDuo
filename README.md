# Super Duo

This repository contains the app SuperDuo project, which is part of the Android Developer Nanodegree program.
It is subdivided into 2 apps Alexandria and FootballScores.

## Alexandria

### Functionality

This app allow to add a book using its isbn number into the user local library.
The isbn can be typed in or scanned.
Books of the library can be filtered by typing words.

### Accessibility

Currently the app support Talkback  for sight-impaired users

### Language support

Currently the app is supportting the following locales : 
- english language : en_US
- french language : fr_FR
- chinese language : zh_CN

### Libraries used

* [Stetho]('com.facebook.stetho:stetho:1.2.0) (for tests)
* [TextDrawable](https://github.com/amulyakhare/TextDrawable) (for images)
* [Glide](https://github.com/bumptech/glide) (for images)
* [Google Play Mobile Vision Api](https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader) (scan functionality for devices above api 18)
* [Zxing](https://github.com/zxing/zxing/wiki/Scanning-Via-Intent)  (scan functionality for devices below api 18)


### Others

Minimum Andoid Api requiered : 15

## Football Scores

### Functionality

This app allow to visualize the past, current, and near futures matches, with relevant informations like time, team name, and scores (when known).
The user also have the choice between 2 types of widgets
- the next match widget
- the collection widget that show all the next matches planned for the current day

### Accessibility

Currently the app support Talkback  for sight-impaired users

### Language support

Currently the app is supportting the following locales : 
- english language : en_US
- french language : fr_FR
- chinese language : zh_CN

### Libraries used

* [Stetho]('com.facebook.stetho:stetho:1.2.0) (for tests)
* [TextDrawable](https://github.com/amulyakhare/TextDrawable) (for images)
* [Glide](https://github.com/bumptech/glide) (for images)

### Others

Minimum Andoid Api requiered : 14
This app uses the [The Football data API](http://api.football-data.org/register) that need an API key to retrieve its data. 
This API key should be pasted in a gradle.properties file as followed

	FootballScoresApiToken="XXXXX"

And the gradle.properties file should be added to the /SuperDuo directory 
	
## Apk
The apk realease can be found under the respected app directories.

## Roadmap
This is the ToDo list I have followed in order to achieve this.

(../master/Superduo_Roadmap.md)
	
## License
	
		The MIT License (MIT)

	Copyright (c) 2016 ETCHEMENDY ELORRI

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
