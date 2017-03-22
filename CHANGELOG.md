CHANGELOG
=========

## Develop
**Enhancements**
* Added transitions between rounds
    [C.P. Dehli][/dehli] [#27](https://github.com/willowtreeapps/wombats-web-client/issues/27)

**Bug Fixes**
* None

## QA (3.21.2017)
**Enhancements**
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

**Bug Fixes**
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

## Master
**Enhancements**
* None

**Bug Fixes**
* Fixed Flickering of Canvas
    [CP Dehli](https://github.com/dehli) No issue created.

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
