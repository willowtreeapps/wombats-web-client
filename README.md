# Wombats Web Client
This repo is a part of the Wombats Suite, documented at [Wombat Documentation](https://github.com/willowtreeapps/wombats-documentation). 

## Contributing? Issues? Features? Curiousity?
[Learn how to contribute here.](https://github.com/willowtreeapps/wombats-documentation/blob/master/CONTRIBUTING.md)

## Development Mode
### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Run LESS styles:

For hot dev:
```
lein less auto
```

To compile once:
 ```
 lein less once
 ```
 
## Production Build

To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```

## Spritesheet

If you want to recreate the spritesheet, you can do so using the free program [TexturePacker](https://www.codeandweb.com/texturepacker). You can add images to [/resources/spritesheet](/resources/spritesheet), and regenerate the spritesheet using [/resources/spritesheet.tps](/resources/spritesheet.tps).
