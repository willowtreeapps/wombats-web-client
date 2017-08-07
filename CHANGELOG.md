CHANGELOG
=========

## Develop
**Enhancements**
* Mobile styles added
    [Eli Bosley](/elibosley) *No Issue*

**Bug Fixes**
* Fixed issue with simualator pause button not working after window change [Eli Bosley][/eli] [#359](https://github.com/willowtreeapps/wombats-web-client/issues/359)

## QA
**Enhancements**
* None

**Bug Fixes**
* None

## Master
**Enhancements**
* None

**Bug Fixes**
* None

## 1.0.0-alpha2 (8.2.2017)
**Enhancements**
* Create Game Modal
    [Emily Seibert][/emily] [#283](https://github.com/willowtreeapps/wombats-web-client/issues/283) [#93](https://github.com/willowtreeapps/wombats-web-client/issues/93)
* Added transitions between rounds
    [C.P. Dehli][/dehli] [#27](https://github.com/willowtreeapps/wombats-web-client/issues/27)
* Updated fog and smoke assets in spritesheet
    [C.P. Dehli][/dehli] [#211](https://github.com/willowtreeapps/wombats-web-client/issues/211)
* Added Table Component, Used on Config Panel
    [Emily Seibert][/emily] [#282](https://github.com/willowtreeapps/wombats-web-client/issues/282)
* Updated the welcome page to prompt for access token
    [Emily Seibert][/emily] [#278](https://github.com/willowtreeapps/wombats-web-client/issues/278)
* Refactored Countdown Timer to include days for improved readability
    [Emily Seibert][/emily] [#201](https://github.com/willowtreeapps/wombats-web-client/issues/201)
* Created a general Select Component ready for any form
    [Emily Seibert][/emily] [#200](https://github.com/willowtreeapps/wombats-web-client/issues/200)
* Added linter and fixed code with suggestions from kibit & bikeshed
    [Emily Seibert][/emily] [#205](https://github.com/willowtreeapps/wombats-web-client/issues/205)
* Access Key Redirect
    [Matt O'Connell][/oconn] API Issue #325[https://github.com/willowtreeapps/wombats-api/issues/325]
* Hide Delete Button for Wombats
    [C.P. Dehli][/dehli] [#303](https://github.com/willowtreeapps/wombats-web-client/issues/303)
* Create Access Keys in Config
    [Emily Seibert][/emily] [#287](https://github.com/willowtreeapps/wombats-web-client/issues/287)
* Output from simulator is displayed through Ace
    [Eli Bosley][/eli] [#189](https://github.com/willowtreeapps/wombats-web-client/issues/189)
* Date picker now shows the current time when opened
    [Eli Bosley][/eli] [#333](https://github.com/willowtreeapps/wombats-web-client/issues/333)
* User repositories are selectable through a dropdown.
    [Eli Bosley][/eli] *No Issue*
* Simulator redesigned based on updated diagrams
    [Eli Bosley][/eli] [#289](https://github.com/willowtreeapps/wombats-web-client/issues/289)
* Pagination displayed on the games list.
    [Eli Bosley][/eli], [C.P. Dehli][/dehli] [#285](https://github.com/willowtreeapps/wombats-web-client/issues/285)
* Added ability to delete games as an administrator
    [Eli Bosley](/elibosley) #[284](https://github.com/willowtreeapps/wombats-web-client/issues/284)

**Bug Fixes**
* Fixed simulator code editing
    [C.P. Dehli][/dehli] [#311](https://github.com/willowtreeapps/wombats-web-client/issues/311)
* Fixed issue where you couldn't join private games
    [C.P. Dehli][/dehli] [#310](https://github.com/willowtreeapps/wombats-web-client/issues/310)
* Fixed bug where query parameters weren't propogating
    [C.P. Dehli][/dehli] [#293](https://github.com/willowtreeapps/wombats-web-client/issues/293)
* Join Modal dropdown z index updated to be drawn over disabled color overlay
	[Emily Seibert][/emily] [#275](https://github.com/willowtreeapps/wombats-web-client/issues/275)
* Fixed simulator (large frame processing)
    [Matt O'Connell][/oconn] API Issue #[326](https://github.com/willowtreeapps/wombats-api/issues/326)
* Fixed game & game_play pages to reflect API changes
    [C.P. Dehli][/dehli] #[344](https://github.com/willowtreeapps/wombats-api/pull/344)
* Fix countdown timer between rounds (regression from Game State Refactor)
    [C.P. Dehli][/dehli]
* Fix sorting order for storing players in db
    [C.P. Dehli][/dehli] #[307](https://github.com/willowtreeapps/wombats-web-client/issues/307)
* Fixed Flickering of Canvas
    [CP Dehli](https://github.com/dehli) No issue created.
* Fixed navigation to games page twice removing query params
    [Eli Bosley](/elibosley) #[350](https://github.com/willowtreeapps/wombats-web-client/issues/350)
* Fixed issue with minified Ace editor function calls
    [Eli Bosley](/elibosley) #[350](https://github.com/willowtreeapps/wombats-web-client/issues/350)
* CSS Prefixing performed automatically using autoprefixer - this is to help improve browser compatibility
    [Eli Bosley][/eli] *No Issue*
* Fixed issue with chat box in Safari
    [Eli Bosley](/elibosley) *No Issue*
* Page indicator always displayed correctly below games
    [Eli Bosley](/elibosley) #[339](https://github.com/willowtreeapps/wombats-web-client/issues/339)

## 1.0.0-alpha1 (3.10.2017)
**Enhancements**
* Basic gameplay set up
* Add, edit, and delete a wombat.
* Join a Game
* Watch a game
* Chat during a game
* Playing simulator

**Bug Fixes**
* None

[/dehli]: https://github.com/dehli
[/emily]: https://github.com/emilyseibert
[/oconn]: https://github.com/oconn
[/eli]: https://github.com/elibosley
