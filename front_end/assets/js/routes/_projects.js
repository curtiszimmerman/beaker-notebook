!(function(angular, app, templates) {
  app.config(['$stateProvider', '$urlRouterProvider', function setRoutes($stateProvider, $urlRouterProvider) {
    $stateProvider.state('projects', {
      abstract: true,
      views: {
        root: {
          controller: 'projectsRoot',
          template: templates.projects_root
        }
      }
    })
    .state('projects.items', {
      url: '/projects',
      views: {
        "app@projects": {
          controller: 'projects',
          template: templates.projects
        },
        "nav@projects": {
          controller: 'projectsList',
          template: templates.project_list
        },
        "recent-notebooks@projects": {
          controller: 'recentNotebooks',
          template: templates.recent_notebooks
        },
        "open-notebooks@projects": {
          controller: 'openNotebooks',
          template: templates.open_notebooks
        }
      }
    })
    .state('projects.items.search', {
      url: '/search',
      views: {
        "app@projects": {
          controller: function() {},
          template: templates.search
        }
      }
    })
    .state('projects.items.item', {
      url: '/:id',
      views: {
        "app@projects": {
          controller: 'project',
          template: templates.project
        }
      }
    })
    .state('projects.items.item.search', {
      url: '/search',
      views: {
        "app@projects": {
          controller: function () {},
          template: templates.search
        }
      }
    })
    .state('projects.items.item.notebook', {
      url: '/notebooks/:notebook_id',
      views: {
        "app@projects": {
          controller: 'notebook',
          template: templates.notebook
        },
        "nav@projects": {
          controller: 'projectsList',
          template: templates.project_list
        }
      },
      onEnter: function($window, Restangular,
                        $stateParams, $route, $routeParams) {
        $routeParams.sessionId = $stateParams.notebook_id;
        var notebookUri = $window.location.origin +
            Restangular.one('notebooks', $stateParams.notebook_id)
            .all('contents').getRestangularUrl();
        $route.current = {locals:
                          {target: {
                            uri: notebookUri,
                            type: "http", // beaker would guess anyway
                            format: "bkr", // beaker would guess anyway
                            readOnly: false // the default anyway
                          },
                           isOpen: true},
                          $$route: {
                            resolve: {}
                          }};
      }})
    .state('projects.items.item.notebook.search', {
      url: '/search',
      views: {
        "app@projects": {
          controller: function () {},
          template: templates.search
        }
      }
    });
  }]);
})(angular, window.bunsen, templates);
