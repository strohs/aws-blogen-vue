import axios from '../../axios-auth'
import { handleAxiosError } from '../../common/errorHandlers.js'
import constants from '../../common/constants.js'

const blogenRestApiModule = {
  state: {
    // categories holds a list of the current blogen categories, they are used across multiple components
    categories: [
      { name: '', categoryUrl: '' }
    ],
    // holds the current page of posts being viewed by the user
    posts: [{
      id: '',
      text: '',
      title: '',
      created: '',
      imageUrl: '',
      user: {},
      category: {},
      postUrl: '',
      threadId: '',
      postId: '',
      children: []
    }],
    // pageInfo is returned by the API on each page request
    pageInfo: {
      totalElements: 0, // total posts available using the current filter criteria
      totalPages: 0, // total pages available using the current filter criteria
      pageNumber: 0, // the page number of data that was requested (0 based indices)
      pageSize: 0 // the maximum number of parent posts that will be displayed on a page
    },
    // avatar file names used within this application
    avatars: []
  },
  getters: {
    getCategories: state => {
      return state.categories
    },
    getAvatarUrlByFileName: () => (fileName) => {
      return constants.DEFAULT_AVATAR_URL + '/' + fileName
    },
    getPostById: (state) => (id) => {
      for (const post of state.posts) {
        if (post.id === id) {
          return post
        } else {
          // search child posts
          const child = post.children.find(c => c.id === id)
          if (child) {
            return child
          }
        }
      }
    },
    getPostIndicesById: (state) => (id) => {
      // find a post by id in this.posts as well as in this.posts.children
      let indices = { ti: -1, ci: -1 }
      for (let ti = 0; ti < state.posts.length; ti++) {
        if (state.posts[ti].id === id) {
          indices.ti = ti
          break
        } else {
          for (let ci = 0; ci < state.posts[ti].children.length; ci++) {
            if (state.posts[ti].children[ci].id === id) {
              indices.ti = ti
              indices.ci = ci
              break
            }
          }
        }
      }
      return indices
    },
    getPostIndicesByIds: (state) => (threadId, postId) => {
      // find the indices of a post within the local store using threadId and postId
      let indices = { ti: -1, ci: -1 }
      indices.ti = state.posts.findIndex(post => post.threadId === threadId)
      if (indices.ti >= 0) {
        // we found a thread matching our threadId, look for a child post matching the postId
        indices.ci = state.posts[indices.ti].children.findIndex(post => post.postId === postId)
      }
      return indices
    }
  },
  mutations: {
    'SET_CATEGORIES' (state, categoriesArr) {
      state.categories = categoriesArr
    },
    'ADD_CATEGORY' (state, category) {
      state.categories.push(category)
    },
    'UPDATE_CATEGORY' (state, updatedCategory) {
      // find existing category name and update it with the new category
      const index = state.categories.findIndex(c => c.name === updatedCategory.name)
      state.categories.splice(index, 1, updatedCategory)
    },
    'SET_POSTS' (state, postsArr) {
      state.posts = postsArr
    },
    'PREPEND_POST' (state, post) {
      state.posts.splice(0, 0, post)
    },
    'ADD_CHILD_POST' (state, { ti, newPost }) {
      // add a child post to a thread, ti is the thread index
      state.posts[ti].children.push(newPost)
    },
    'UPDATE_POST' (state, { ti, ci, newPost }) {
      // ti is index of the thread to update and ci is index of child post to update
      console.log(`ti:${ti} ci:${ci} postData`, newPost)
      if (ci >= 0) {
        state.posts[ti].children.splice(ci, 1, newPost)
      } else {
        state.posts.splice(ti, 1, newPost)
      }
    },
    'DELETE_POST' (state, { ti, ci }) {
      // console.log(`DELETE_POST pi:${ti} ci:${ci}`)
      if (ci >= 0) {
        // deleting a child post
        state.posts[ti].children.splice(ci, 1)
      } else {
        // deleting entire thread
        state.posts.splice(ti, 1)
      }
    },
    'SET_PAGE_INFO' (state, pageInfo) {
      state.pageInfo = pageInfo
    },
    'SET_CURRENT_PAGE_NUM' (state, pageNum) {
      state.pageInfo.pageNumber = pageNum
    },
    'SET_AVATARS' (state, avatarsArr) {
      state.avatars = avatarsArr
    }
  },
  actions: {

    updatePostData: ({ commit, getters }, { id, newPost }) => {
      let updateData = getters.getPostIndicesById(id)
      updateData.newPost = newPost // { ti, ci, newPost }
      commit('UPDATE_POST', updateData)
    },
    fetchAndStoreCategories: ({ commit }, { pageNum = 0, pageLimit = 20 }) => {
      return axios.get('/api/v1/categories', {
        params: {
          page: pageNum,
          limit: pageLimit
        }
      })
        .then(res => {
          console.log(`fetchAndStoreCategories response:`, res.data);
          commit('SET_CATEGORIES', res.data.categories);
          return res.data.categories;
        })
        .catch(error => {
          handleAxiosError(error);
        })
    },
    fetchCategories: (context, { pageNum = 0, pageLimit = 20 }) => {
      return axios.get('/api/v1/categories', {
        params: {
          page: pageNum,
          limit: pageLimit
        }
      })
        .then(res => {
          console.log(`fetchCategories response:`, res.data);
          return res.data;
        })
        .catch(error => {
          handleAxiosError(error)
        })
    },
    createCategory: ({ commit }, categoryObj) => {
      console.log('create category with object:', categoryObj)
      return axios.post('/api/v1/categories', categoryObj)
        .then(res => {
          console.log('createCategory response:', res.data)
          commit('ADD_CATEGORY', res.data)
          return res.data
        })
        .catch(error => {
          handleAxiosError(error)
          throw error
        })
    },
    updateCategory: ({ commit }, categoryObj) => {
      console.log('update category with object:', categoryObj)
      delete categoryObj.categoryUrl
      return axios.put(`/api/v1/categories/${categoryObj.oldName}`, categoryObj)
        .then(res => {
          console.log('updateCategory response:', res.data)
          commit('UPDATE_CATEGORY', res.data)
          return res.data
        })
        .catch(error => {
          handleAxiosError(error)
          throw error
        })
    },
    fetchPosts: ({ commit }, { pageNum, pageLimit, categoryName }) => {
      axios.get('/api/v1/posts', {
        params: {
          page: pageNum,
          limit: pageLimit,
          category: categoryName
        }
      })
        .then(res => {
          console.log('fetchPosts response:', res.data);
          commit('SET_POSTS', res.data.posts);
          commit('SET_PAGE_INFO', res.data.pageInfo);
        })
        .catch(error => {
          handleAxiosError(error)
        })
    },
    createPost: (context, { id, post }) => {
      // if id is passed, then we are creating a child post, if id is undefined then we are creating a new thread
      const url = (id) ? `/api/v1/posts/${id}` : '/api/v1/posts'
      axios.post(url, post)
        .then(res => {
          console.log(`createPost url:${url} response:`, res.data)
          if (!id) {
            // creating a new thread
            context.commit('PREPEND_POST', res.data)
          } else {
            // replying to a thread, so we need to find the current thread index within the store
            // find the new post's index using threadId and postId so we can insert it into the store
            let indices = context.getters.getPostIndicesByIds(res.data.threadId, res.data.postId)
            indices.newPost = res.data
            context.commit('ADD_CHILD_POST', indices)
          }
        })
        .catch(error => {
          handleAxiosError(error)
        })
    },
    updatePost: (context, { id, post }) => {
      // id and post are mandatory parameters
      const url = `/api/v1/posts/${id}`
      axios.put(url, post)
        .then(res => {
          console.log(`updatePost url:${url} response:`, res.data)
          context.dispatch('updatePostData', { id: id, newPost: res.data })
        })
        .catch(error => {
          handleAxiosError(error)
        })
    },
    deletePost: (context, id) => {
      const url = `/api/v1/posts/${id}`
      axios.delete(url)
        .then(res => {
          console.log(`deletePost url:${url} response:`, res.data)
          const keysArr = id.split('_')
          const threadId = keysArr[0]
          const postId = keysArr[3]
          const indices = context.getters.getPostIndicesByIds(threadId, postId)
          context.commit('DELETE_POST', indices)
        })
        .catch(error => {
          handleAxiosError(error)
        })
    },
    fetchPostsByUser: (context, { id, pageNum, pageLimit, categoryName }) => {
      const url = `/api/v1/users/${id}/posts`
      return axios.get(url, {
        params: {
          page: pageNum,
          limit: pageLimit,
          category: categoryName
        }
      })
        .then(res => {
          console.log(`get posts for user:${id} response:`, res)
          context.commit('SET_POSTS', res.data.posts)
          context.commit('SET_PAGE_INFO', res.data.pageInfo)
          return res.data
        })
        .catch(error => {
          handleAxiosError(error)
          throw error
        })
    },
    fetchAndStoreAvatarFileNames: ({ commit }) => {
      return axios.get('/api/v1/avatars')
        .then(res => {
          console.log('fetch avatar file name response:', res);
          commit('SET_AVATARS', res.data.avatars);
          return res.data.avatars;
        })
        .catch(error => {
          handleAxiosError(error);
          throw (error);
        })
    },

    /**
     * registers a newly signed up user into the "User" group within cognito.
     * This group gives them permission to use the REST Api
     * @param context
     * @param userId - the users cognito id, called the 'username' by cognito
     * @returns {AxiosInstance}
     */
    registerNewUser: (context, userId) => {
      const url = `/api/v1/auth/register/${userId}`;
      return axios.get(url)
          .then(res => {
            console.log('register new user', userId, res.status);
            return res;
          })
          .catch(error => {
            throw (error);
          })
    }
  }
};

export default blogenRestApiModule
