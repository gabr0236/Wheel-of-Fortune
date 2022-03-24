# Wheel of Fortune

## Gameplay & Rules

### Recordings of the television show
Watch how the game is played:
* [English clip](https://youtu.be/j_FpIab0K_8)
* [Danish clip](https://youtu.be/79nl-BDNek0)

### Screenshots from the app

#### Game Screen
<img src="https://github.com/gabr0236/Wheel-of-Fortune/blob/master/ReadmeImg/Guess.png" width="200">

***

#### Popups
<img src="https://github.com/gabr0236/Wheel-of-Fortune/blob/master/ReadmeImg/ExtraTurn.png" width="200">

***

#### Winning Screen
<img src="https://github.com/gabr0236/Wheel-of-Fortune/blob/master/ReadmeImg/Win.png" width="200">

***

### For the Android application, the modified rules are:
1. The game is for one player.
2. When the game starts, a word is randomly chosen from predefined categories and displayed
   along with the category.
3. The word/phrase is displayed with the letters hidden and the individual words separated for a
   phrase.
4. The player “spins the wheel”. (A graphically spinning wheel is not required to be implemented
   this could simply be done by tapping a button and showing the result.)
5. The possible results of the “spinning the wheel” are: a number of points e.g. 1000 or an “extra
   turn”, “miss turn” or “bankrupt”.
6. In the event of a value being shown, a letter (consonant or vowel) is decided by the user (from
   a keyboard or otherwise). 
   If the letter is present, the user’s points total is incremented by the value shown times the number of occurrences of the letter.
   The occurrences of the letter are revealed in the word. If the letter is not present, the user loses a “life”.
7. In the event of “extra turn” being shown, the user is given an extra life.
8. In the event of “miss turn” being shown, the user loses a life without being able to choose a
   letter.
9. In the event of “bankrupt” being shown, the user loses all their points.
10. The “wheel is spun” until the game is won or lost.
11. The game is won when all the letters have been found and the user still has a life.
12. The game is lost when the user has no lives left and the word has not been found.
13. A user starts with 5 “lives”.

## Get Started
To get started, simply run the app. Sometimes a rebuild/clean/invalidate cache is required before running the app.

## License
Copyright 2021 Gabriel Rosenzweig Haugbøl

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
