# Vue 3 + Vite

This template should help get you started developing with Vue 3 in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

## Recommended IDE Setup

- [VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=johnsoncodehk.volar)


## the public directory
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


# TODO
- DONE configure vue-fontawesome then find/replace usages  of <icon
- DONE find/replace vuelidate usages to @vuelidate/core  and  @vuelidate/validators
- make sure vue router is configured correctly and still works
- make sure vuex is configured correctly
- correct the  slot=   attributes in templates with new vue3 syntax
- DONE vue filters may be deprecated, search for filter javascript files: dateFormatFilter


## Bootstrap Vue 4 to 5
Using the following
- b-alert
- b-button
- b-card
  - b-card-body
  - b-card-header
  - b-card-footer
  - b-card-group
- b-collapse
- b-dropdown-item
- b-dropdown-divider
- b-dropdown-item-button
- b-input
  - b-input-group-append
- b-img
- b-form
  - b-form-group
  - b-form-input
  - b-form-textarea
  - b-form-select
- b-link

- b-pagination