// app info
angular.module('quodatum.doc.apps', [ 'ui.router', 'quodatum.services' ])

.config(
    [ '$stateProvider', '$urlRouterProvider',
        function($stateProvider, $urlRouterProvider) {
          $stateProvider

          .state('app', {
            url : "/data/app",
            abstract : true,
            template : '<ui-view>App list</ui-view>',
            data : {
              entity : "app"
            }
          })

          .state('app.index', {
            url : "",
            templateUrl : '/static/doc/feats/apps/apps.xhtml',
            controller : "AppsCtrl",
            ncyBreadcrumb : {
              label : 'Apps'
            }
          })

          .state('app.item', {
            url : "/:app",
            abstract : true,
            template : '<ui-view>App detail</ui-view>',
            ncyBreadcrumb : {
              label : '{{app.name}}'
            }
          })

          .state('app.item.index', {
            url : "",
            templateUrl : '/static/doc/feats/apps/app-index.html',
            controller : "AppCtrl",
            ncyBreadcrumb : {
              label : '{{$stateParams.app}}',
              parent : 'app.index'
            }
          })

          .state('app.item.client', {
            url : "/client",
            abstract : true,
            template : '<ui-view>client</ui-view>',
            ncyBreadcrumb : {
              label : 'client',
              parent : 'app.item.index'
            }
          })

          .state('app.item.server', {
            url : "/server",
            abstract : true,
            template : '<ui-view>server</ui-view>',
            ncyBreadcrumb : {
              label : 'server',
              parent : 'app.item.index'
            }
          })

          .state('app.item.server.rest', {
            url : "/rest",
            templateUrl : '/static/doc/feats/apps/app-rest.xhtml',
            ncyBreadcrumb : {
              label : 'restXQ'
            },
            controller : "RestCtrl"
          })

               // list components used
          .state('app.item.server.component', {
            url : "/component",
            templateUrl : '/static/doc/feats/components/versions.html',
            ncyBreadcrumb : {
              label : 'Server components'
            },
            controller : "CmpCtrlX",
            data : {
              entity : "component.version"
            }
              
          })
          // list components used
          .state('app.item.client.component', {
            url : "/component",
            templateUrl : '/static/doc/feats/components/versions.html',
            ncyBreadcrumb : {
              label : 'Client components'
            },
            controller : "CmpCtrlX",
            data : {
              entity : "component.version"
            }
              
          })
          
          .state('app.item.client.template', {
            url : "/:view",
            templateUrl : '/static/doc/feats/apps/app-view.xhtml',
            ncyBreadcrumb : {
              label : 'view: {{$stateParams.view}}'
            },
            controller : "ScrollCtrl"
          })

          .state('app.item.server.xqdoc', {
            url : "/:view",
            templateUrl : '/static/doc/feats/apps/app-view.xhtml',
            ncyBreadcrumb : {
              label : 'view: {{$stateParams.view}}'
            },
            controller : "ScrollCtrl"
          })

          .state('app.item.server.wadl', {
            url : "/:view",
            templateUrl : '/static/doc/feats/apps/app-view.xhtml',
            ncyBreadcrumb : {
              label : 'view: {{$stateParams.view}}'
            },
            controller : "ScrollCtrl"
          })

        } ])

// controllers
.controller("CmpCtrlX",
    [ "$scope", "$stateParams","DiceService", function($scope, $stateParams,DiceService) {
      var app=$stateParams.app;
      function update() {
        DiceService.onelist('app', app,"component",$scope.params)
        .then(function(d) {
          $scope.apps = d;
        });
      }

      DiceService.setup($scope, update);
    } ])

    
.controller("RestCtrl",
    [ "$scope", "DiceService", function($scope, DiceService) {
      console.log("RestCtrl");

      function update() {
        /*
         * Restangular.one("data").all('app') .getList($scope.params)
         * .then(function(d){ //console.log("models..",d); $scope.apps=d; });
         */
      }
      DiceService.setup($scope, update)
    } ])

.controller("AppsCtrl",
    [ "$scope", "DiceService", function($scope, DiceService) {
       console.log("AppsCtrl2");

      function update() {
        DiceService.list('app', $scope.params).then(function(d) {
          // console.log("models..",d);
          $scope.apps = d;
        });
      }

      DiceService.setup($scope, update);
    } ])

.controller(
    "AppCtrl",
    [ "$scope", "$stateParams", "DiceService",
        function($scope, $stateParams, DiceService) {
          var app = $stateParams.app;
          console.log("AppCtrl", app);
          $scope.server = [ {
            state : "app.item.server.wadl({view:'wadl'})",
            label : "Endpoints"
          }, {
            state : "app.item.server.xqdoc({view:'xqdoc'})",
            label : "XQdoc"
          }, {
            state : "app.item.server.wadl({view:'wadl'})",
            label : "Endpoints"
          } ]
          DiceService.one('app', app).then(function(d) {
            $scope.app = d;
            // console.log(">>", d);
          });
        } ])

;