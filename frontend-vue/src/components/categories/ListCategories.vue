// form for creating new categories
<template>
  <div>
    <div class="container-fluid">
      <header id="categoryHeader" class="row py-2 bg-success text-white">
        <div class="col">
          <h2>
            <font-awesome-icon
              class="mx-2"
              icon="folder"
              scale="2"
            ></font-awesome-icon>
            Categories
          </h2>
        </div>
      </header>
    </div>

    <!-- New Category Button -->
    <div class="container">
      <div class="row mt-4">
        <div class="col">
          <app-new-category @submit="createCategory"></app-new-category>
        </div>
      </div>

      <div class="row my-4">
        <div class="col-6">
          <b-card no-body border-variant="success">
            <b-card-header>
              <h3>Categories</h3>
            </b-card-header>
            <b-card-body>
              <!-- category status message -->
              <div class="row justify-content-center pt-2">
                <app-status-alert
                  v-bind="status"
                  v-model="showStatus"
                ></app-status-alert>
              </div>

              <table class="table table-bordered table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col">Category Name</th>
                    <th scope="col"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="cat in categories" :key="cat.name">
                    <td>{{ cat.name }}</td>
                    <td>
                      <transition appear name="fade" mode="out-in">
                        <app-edit-category
                          v-bind="cat"
                          @submit="editCategory"
                        ></app-edit-category>
                      </transition>
                    </td>
                  </tr>
                </tbody>
              </table>
            </b-card-body>

            <b-card-footer>
              <!-- PAGINATION -->
              <b-pagination
                v-model="currentNavPage"
                :total-rows="pageInfo.totalElements"
                :per-page="pageInfo.pageSize"
              >
              </b-pagination>
            </b-card-footer>
          </b-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NewCategory from "./NewCategory.vue";
import StatusAlert from "../common/StatusAlert.vue";
import EditCategory from "./EditCategory.vue";
import logger from "../../configs/logger";

export default {
  name: "ListCategories",
  components: {
    appNewCategory: NewCategory,
    appStatusAlert: StatusAlert,
    appEditCategory: EditCategory,
  },
  data() {
    return {
      category: "",
      status: {
        code: 200,
        message: "",
      },
      showStatus: false,
      currentNavPage: 1,

      // a page of categories to display in the table
      categories: [],

      // pageInfo holds the category 'page details' object returned from the API
      pageInfo: {
        pageNumber: 0,
        totalElements: 0,
        totalPages: 0,
        pageSize: 4,
      },
    };
  },
  computed: {},
  watch: {
    currentNavPage(newPageNum) {
      logger.debug("fetch category page", newPageNum);
      this.fetchPage(newPageNum);
    },
  },
  created() {
    this.fetchCategories(0, this.pageInfo.pageSize);
  },
  methods: {
    async fetchCategories(pageNum, pageLimit) {
      logger.debug(
        `fetch categories with pageNum:${pageNum} and limit:${pageLimit}`
      );
      await this.$store
        .dispatch("fetchCategories", { pageNum: pageNum, pageLimit: pageLimit })
        .then((data) => {
          this.categories = data.categories;
          this.pageInfo = data.pageInfo;
        });
    },
    fetchPage(page) {
      // api pages are 0-based, need to subtract 1 from table page
      this.fetchCategories(page - 1, this.pageInfo.pageSize);
    },
    createCategory(newCategory) {
      this.$store
        .dispatch("createCategory", newCategory)
        .then(() => {
          this.status.code = 200;
          this.status.message = `Category: ${newCategory.name} created`;
          // display the first page of the table
          this.currentNavPage = 1;
          this.fetchPage(1);
        })
        .catch((error) => {
          this.status.code = error.response.status;
          // TODO possibly move global error check into VUEX
          this.status.message = error.response.data.globalError[0].message;
        });
      this.displayStatusAlert();
    },
    displayStatusAlert() {
      // display the status code from CRUD request
      this.status.show = true;
    },
    dismissStatusAlert() {
      this.status.show = false;
    },
    editCategory(cat) {
      logger.debug("edit category name:", cat.name);
      this.$store
        .dispatch("updateCategory", cat)
        .then(() => {
          const idx = this.categories.findIndex((c) => c.name === cat.name);
          this.categories.splice(idx, 1, cat);
        })
        .catch((error) => {
          this.status.code = error.response.status;
          // TODO possible global error handling
          this.status.message = error.response.data.globalError[0].message;
          this.displayStatusAlert();
        });
    },
  },
};
</script>

<style>
.fade-enter {
  opacity: 0;
}

.fade-enter-active {
  transition: opacity 1s;
}

.fade-leave {
}

.fade-leave-active {
  transition: opacity 1s;
  opacity: 0;
}

.slide-enter-active {
  animation: slide-in 500ms ease-out forwards;
}

.slide-leave-active {
  animation: slide-out 500ms ease-out forwards;
}

@keyframes slide-in {
  from {
    transform: translateX(50px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slide-out {
  from {
    transform: translateX(0px);
    opacity: 1;
  }
  to {
    transform: translateX(50px);
    opacity: 0;
  }
}
</style>
