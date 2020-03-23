/*
 * APICloud JavaScript Library
 * Copyright (c) 2014 apicloud.com
 */
(function(window){
    var u = {};

    var db;
    var dbName = 'missfresh_demo';
    var dbPath = 'fs://missfresh_demo.db';
    var tableName = 'shoppingCart';

    // 引入db模块
    u.init = function(){
        if (db) {
            return;
        }
        db = api.require('db');
    };

    // 创建数据库及相关的数据表结构
    u.open = function() {
        u.init();
        db.openDatabase({
            name: dbName,
            path: dbPath
        }, function (ret) {
          db.executeSql({
              name: dbName,
              sql: 'CREATE TABLE  IF  NOT EXISTS ' + tableName + '(wareId VARCHAR(32), wareCount INT)'
          });
        });
    }

    // 查询操作，查询table中全部数据，或按商品ID查询
    u.select = function(wareId_,callback) {
        u.init();
        var sql = 'SELECT * FROM ' + tableName;
        if (typeof wareId_ === 'function') {
          callback = wareId_
          wareId_ = undefined
        }
        if (wareId_) {
            sql = 'SELECT * FROM ' + tableName + ' WHERE wareId=\"' + wareId_ + '\"';
        }
        db.selectSql({
            name: dbName,
            sql: sql
        },callback);
    }

    // 更新操作，更新购物车中指定ID的商品数量
    u.update = function(wareId_, wareCount_, callback) {
        u.init();
        db.executeSql({
            name: dbName,
            sql: 'UPDATE ' + tableName + ' SET wareCount=' + wareCount_ + ' WHERE wareId=\"' + wareId_ + '\"'
        }, callback);
    }

    // 插入操作，将商品ID和数量添加到购物车中
    u.insert = function(wareId_, wareCount_, callback) {
        u.init();
        db.executeSql({
            name: dbName,
            sql: 'INSERT INTO ' + tableName + '(wareId,wareCount) VALUES(\"' + wareId_ + '\",' + wareCount_ + ')'
        }, callback);
    }

/*end*/

    window.$db = u;

})(window);
