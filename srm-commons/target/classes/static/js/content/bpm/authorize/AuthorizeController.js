/**
 * @class {Re.InsideProjectController}
 * @extend {Ext.ux.app.ViewController} 项目工单控制层
 */
Ext.define('Bpm.authorize.AuthorizeController', {
	extend : "Ext.srm.app.ViewController",
	alias : "controller.authorizeController",
	/**
	 * @method formManagerUserTrigger
	 * @param {Ext.field.Field}
	 *            field 当前field对象 表单供应商点击触发事件
	 */
	formManagerUserTrigger : function(field) {
		if (field.readOnly) { // 如果是只读，则不让下拉，直接返回
			return;
		} else {
			this.showUserSelectWin();
		}
	},

	/**
	 * @method showUserSelectWin 被授权人选择窗体显示
	 * @return {Object} 项目负责人选择窗体
	 */
	showUserSelectWin : function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var vm = this.getView();
		var selectWin = new Ext.srm.window.UserSelectWin({
					singleSelect : true,
					baseParams:{filter_NE_roles_roleType:"V"},
					renderTo : vp.editWin.id,
					select : function(g, r) {
						var form = vp.editWin.formPanel.getForm();
						form.findField('model.authorizeToPersonName')
								.setValue(r.get('userName'));
						form.findField('model.authorizeToPersonId').setValue(r
								.get('userId'));
						selectWin.close();
					}
				});
		selectWin.show();
	},

	/**
	 * @method formManagerUserClear 被授权人清空
	 */
	formManagerUserClear : function() {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.authorizeToPersonId').reset();
		form.findField('model.authorizeToPersonName').reset();
	},

	/**
	 * @method getBillTypeName 单据类型赋值
	 */
	getBillTypeName : function(combo, record, index) {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var editWin = vp.editWin;
		var form = editWin.formPanel.getForm();
		var billTypeNames = [];
		Ext.each(record, function(value, index, array) {
					billTypeNames = billTypeNames + ','
							+ value.data.itemName;
				});
		if (billTypeNames.length != 0) {
			billTypeNames = billTypeNames.substring(1, billTypeNames.length);
			form.findField("model.billTypes").setValue(billTypeNames);
		}
		// console.log(billTypeNames);
		// console.log(this.formPanel);
	},
	setFormValueAfter : function() {
		var me = this;
		var viewModel = me.getViewModel();
		var editWin = viewModel.getEditWin();
		var form = editWin.formPanel.form;
		var billId = form.findField("model.billId").getValue().replace(/\s+/g,
				"");
		form.findField("model.billIds").select(billId.split(","));
	},
	rendererStatus : function(value) {
		switch (value) {

			case 'CANCEL' :
				return $('authorize.stateStop');
			case 'TOCONFIRM' :
				return "<font color='#ff8800'>" + $('state.confirm')
						+ "</font>";
			case 'TONOPASS' :
				return "<font color='#ee4444'>" + $('state.nopass') + "</font>";
			case 'TOPASS' :
				return $('dict.isvalid');//生效
			default :
				return '新建';
		}
	},
	authorizeStopHandler : function() {
		var me = this;
		var viewModel = me.getViewModel();
		var grid = viewModel.getVp().grid;
		var selected = grid.getSelectionModel().getSelection();
		var dealUrl = viewModel.get('dealUrl');

		if (0 == selected.length) {
			Q.tips($('priceInquiry.select.msg'), 'E');
		} else {
			Q.confirmMsg($('message.authorizeStop.confirm'), {
				renderTo : me.getView().id,
				ok : function(btnId,msg,obj) {
					Ext.Ajax.request({
						url : path_srm
								+ '/sys/authorize/authorizestop',
						params : {
							id : selected[0].get('authorizeId'),
							message:msg
						},
						success : function(response) {
							var json = Ext.decode(response.responseText);
							if (false === json.success) {
								Q.tips($('authorizeStop.failure'), 'E');
								return;
							}

							Q.tips($('authorizeStop.success'));
							grid.getStore().reload();
						},
						failure : function() {
							grid.getStore().reload();
						}
					});
				}
			});
		}
	},
	vpAfterRender: function() {
		return 'view';
	},
	/**
	 * @method submitBefore
	 * @param {Ext.form.Panel}
	 *            formPanel 当前主单 提交之前事件
	 */
	submitBefore : function(formPanel) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		// 校验开始日期和结束日期
		var startDate = form.findField("model.effectiveTime").getValue();
		var endDate = form.findField("model.expiryTime").getValue();
		if (Ext.Date.format(startDate, 'Y-m-d') > Ext.Date.format(endDate,
				'Y-m-d')) {
			Q.tips($("effectiveTimeGtExpiryTime"), "E");// 生效时间不能小于失效时间
			return false;
		}

		return true;
	},
	textRenderer : function(v) {
		if (Ext.isEmpty(v)) {
			return '';
		}
		return "<span title='" + v + "'>" + v + "</span>";
	}
});