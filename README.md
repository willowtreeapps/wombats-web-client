# Wombats Web Client
This repo is a part of the Wombats Suite, documented at [Wombat Documentation](https://github.com/willowtreeapps/wombats-documentation). 

## Contributing? Issues? Features? Curiosity?
[Learn how to contribute here.](https://github.com/willowtreeapps/wombats-web-client/blob/develop/CONTRIBUTING.md)

## Our Stack
Our web client is written with the following tools:
* [ClojureScript (cljs)](https://clojurescript.org/)
* [Reagent](https://github.com/reagent-project/reagent)
* [Re-frame](https://github.com/Day8/re-frame) 
* [LESS](http://lesscss.org/#)
* [HTML5 Canvas](https://www.w3schools.com/html/html5_canvas.asp)

## Development Mode
### Run application:

```
lein run-dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## URLs
* `dev.api.wombats.io` points to the Development database 
* `qa.api.wombats.io` points to the QA database - this is the base API URL for our `qa` branch
* `api.wombats.io` points to the Production database - this is the base API URL for our `master` branch

## Production Build

The following commands compile Wombats for the deployed environments:

```
lein deploy-dev
lein deploy-qa
lein deploy-prod
```

## Spritesheet

If you want to recreate the spritesheet, you can do so using the free program [TexturePacker](https://www.codeandweb.com/texturepacker). You can add images to [/resources/spritesheet](/resources/spritesheet), and regenerate the spritesheet using [/resources/spritesheet.tps](/resources/spritesheet.tps).
