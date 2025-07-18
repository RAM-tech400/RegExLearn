# RegExLearn
An android app for learning `Regular Experssions`. So you can continue learning this important useful concept whenever you want. If you want to test your RegEx patterns even you are offline for your minds, well this app does can be so helpful for you. If you want to know more about RegEx, well this app contains some tips&triks about it. Of course, it is still in begin of itself way and need more development so I am pleasure for your help. You are free to meking changes and contributes.

## Note
The some content in this project/app comes from others.

## Contributing
I need help to make richer the contents of this project, So I need your help! You can add more content to this app. Just should make attention to some notes to contributing even easily:

**Content categories:**
- Lessons
- Tips & Triks
- Translations
- Challenges
- <ins>*More in future!*</ins>

**Lessons:**
To add or change the lessons data you should edit [this file](https://github.com/RAM-tech400/RegExLearn/#). The lessons data file is a JSON file whit these content:
| Key | Value Type | Example | Description & Notes |
|-----|------------|---------|-------|
| title | String | "steps.whatIsRegex.title" | * Its value refers to an item in localization JSON file. |
| description | String | "steps.whatIsRegex.description" | * Its value refers to an item in localization JSON file. |
| content | String | "readme.md\ndocument.pdf\n\"image\".png\nmusic.mp4\nmanual.pdf" | This text used in preview section. The regex will apply on this text. |
| initialFlags | String | "g" | This defines the enable flags/modifiers in loding time of the lesson.<br><br>* It's not required!|
| flags | String | "gm" | This defines the flags/modifiers that should be anable to pass the lesson. |
| regex | String Array | [<br>"b[^eo]r",<br> "b[^oe]r"<br>] | Contains the all correct regex pattern that if user enter into regex input then cans passing the lesson. |
| initialValue | String | "^\\w+\\.pdf$" | The regex pattern that filled into regex input as default regex pattern on the lesson load time. |
| readOnly | Boolean | true | The user cannot change anything and just should study the lesson if this is true and the user can do lesson requirements if it is false. |
| answer | String Array | [<br>"document.pdf",<br>"manual.pdf"<br>] | Contains the all correct matches of regex pattern that applied on content value. |

You should add your lesson data with above structure into following JSON file.
And About the localization JSON file we will read in below. It used for add translated title and description to the lessons.

**Tips & Triks:**
If you have know some tips & triks about regular experssions, so please add those by [openning an issue](https://github.com/RAM-tech400/RegExLearn/issues/new) on this repo (Set its label to "help wanted"). 

**Translations:**
This app can be localed for any languages or locations on the world! So if you know any new language or seeing wrongs about currently (Auto) translated content, well try to help please! Before all you should know:

* For adding new translation, you should make a new JSON file under the directory that point to the translation locale (e.g. `https://github.com/RAM-tech400/RegExLearn/edit/master/README.md/Contributing/Translation/jp/` for Japanease translation).
* You should just translate the content of all values of this JSON file.
* For translating the app UI contents, please translate the values of this JSON file and save the result file under the directory that point to the translation locale (e.g. `https://github.com/RAM-tech400/RegExLearn/edit/master/README.md/Contributing/Translation/jp/` for Japanease translation).

**Challenges:**
If you have a creative character side, so we will glad to your desinged challenges about regex.
For adding challenges, just [open an issue](https://github.com/RAM-tech400/RegExLearn/issues/new) on this repo (Set its label to "help wanted").

**More In Future:**
Maybe I add more content category in future and then update this section.

> The name of contributers will show in app and here.


## Sources:
- [Regex Learn](https://regexlearn.com/):
  - Lessons data (JSON files)
  - Localization & translations for lessons
