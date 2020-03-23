/*
此处是一个示例,展示如何基于 apiadapter 来重写特定的 APICloud 引擎
或模块的方法.
*/

/*
当 frame, window, adapter 级别同时实现此函数时,
加载优先级为: frame > window > adapter > 内置默认实现

@payload:
  @moduleName 模块名.
  @method     方法名.
  @isSync     是否是同步方法.
  @params     调用模块方法时的参数.
  @frameDom   frame 所在的 window dom对象.
  @winDom     window 所在页面的 window dom 对象.
  @apiDom     adapter 所在页面的 window dom 对象.
  @cbId       调用模块方法时,传递的回调函数的唯一标识.
  @callback   用于异步返回值的回调函数.(cbId, ret, err, del)=>{}
                @cbId   调用模块方法时,传递的回调函数的唯一标识.
                @ret    模块返回值.
                @err    错误信息.
                @del    调用后,是否删除此 cbId 对应的回调函数.
                        删除后,下一次基于同一 cbId调用callback,
                        将无法正确回传返回值.

@return: 不作处理.如果不想处理某个模块方法, 应该显式返回字符串 'TO_NEXT_API_adapter',
          以便往上传播调用.
*/
function apiadapter(payload) {
  var moduleName = payload.moduleName,
      method = payload.method,
      isSync = payload.isSync,
      params = payload.params,
      cbId = payload.cbId,
      callback = payload.callback,
      frameDom = payload.frameDom,
      winDom = payload.winDom,
      apiDom = payload.apiDom;


  if (moduleName === 'db') {
    if (method === 'openDatabase') {
      /* 直接返回即可.等到具体有操作时,再新建或打开即可. */
      callback(cbId, { status: true }, null, true);

      return;
    }

    if (method === 'executeSql') {
      var name = params.name,
          sql = params.sql;

      var db = openDatabase(name, '1.0', 'APICloud App Database', 2 * 1024 * 1024);
      db.transaction(function (ctx) {
        ctx.executeSql(sql, [], function (ctx, result) {
          callback(cbId, { status: true }, null, true);
        }, function (ctx, error) {
          callback(cbId, { status: false }, { msg: error && error.message }, true);
        });
      });

      return;
    }

    if (method === 'selectSql') {
      var _name = params.name,
          _sql = params.sql;

      var _db = openDatabase(_name, '1.0', 'APICloud App Database', 2 * 1024 * 1024);

      _db.transaction(function (ctx) {
        ctx.executeSql(_sql, [], function (ctx, result) {
          var len = result.rows.length;
          var data = [];

          for (var i = 0; i < len; i++) {
            data.push(result.rows.item(i));
          }

          callback(cbId, { status: true, data: data }, null, true);
        }, function (ctx, error) {
          callback(cbId, { status: false }, { msg: error && error.message });
        });
      });

      return;
    }
  }

  /* 默认不作处理. */
  return 'TO_NEXT_API_ADAPTER';
}
