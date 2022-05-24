# Vue 3 + Vite
This directory contains the vue.js frontend code. It uses [Vite](https://vitejs.dev/) as the javascript bundler.

Node.js v14.17.5 was used during development


## Running the Dev Server 

- to start the Vite development server (defaults to port 3000)

> npm run dev


## Building for production
- to build and bundle the frontend resources for production
> npm run build

the bundled assets will be placed in the "dist" directory. To serve these assets from Spring Boot you must copy all files and sub-directories under "dist"
to Spring Boot's [resources/public directory](../backend/src/main/resources/public)




## Vite Notes
This template should help get you started developing with Vue 3 in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

### Recommended IDE Setup

- [VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=johnsoncodehk.volar)

### the public directory

if you have assets that are:

- Never referenced in source code (e.g. robots.txt)
- Must retain the exact same file name (without hashing)
- ...or you simply don't want to have to import an asset first just to get its URL

Then you can place the asset in a special public directory under your project root.
Assets in this directory will be served at root path `/` during dev, and copied to the root of the dist directory as-is.

Note that:

- You should always reference public assets using root absolute path - for example, `public/icon.png` should be
  referenced in source code as `/icon.png`.
- Assets in public cannot be imported from JavaScript.

