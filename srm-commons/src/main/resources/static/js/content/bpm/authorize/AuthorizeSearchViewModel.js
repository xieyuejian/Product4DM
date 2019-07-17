Ext.define("Bpm.authorize.AuthorizeSearchViewModel", {
			extend : 'Bpm.authorize.AuthorizeViewModel',
			config : {
				data : {
					isExtend : true,
					/**
					 * @cfg {Boolean} isAudit 是否有审核右键
					 */
					isAudit : false,
					playListMode : 'normal',
					vp_addListBtn : [{
								name : 'tostop',
								text : $('btn.authorizeStop'),
								index : 4,
								iconCls : "icon-delete",
								build : power['tostop'],
								handler : 'authorizeStopHandler'
							}],
					vp_hideListBtn : ['add', 'edit', 'delete'],
					vp_listEditStateFn : [ {
						"tostop" : function(r) {
							return r.get('status') == 'TOPASS';// 审核通过状态可提前结束授权
						}
					}],
					vp_gridStore : {
						idProperty : "authorizeId",
						url : "#{dealUrl}/list?dataAuth=search",
						sort : "authorizeId",
						dir : "desc"
					}
				}
			}
		});