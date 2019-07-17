/**
 * 物料组模糊查询下拉框
 */
Ext.define("Ext.srm.form.MaterialGroupComboGrid", {
    extend: "Ext.srm.form.SrmComboGrid",
    alias: "widget.materialGroupComboGrid",
    xtype: "materialGroupComboGrid",
    alternateClassName: ["Ext.srm.form.MaterialGroupComboGrid"],
    store: Ext.create("Ext.ux.data.JsonStore", {
        url: path_masterdata + "/md/materialgroup/list",
        fields: ['materialGroupCode', 'materialGroupName'],
        pageSize: 10,
        autoLoad: false
    }),
    pageSize: 10,
    queryParam: "filter_LIKE_materialGroupCode_OR_LIKE_materialGroupName",
    displayFields: [{
        Qheader: "物料组编码",
        header: $("materialGroup.code"),
        dataIndex: 'materialGroupCode',
        width: 120
    }, {
        Qheader: "物料组名称",
        header: $("materialGroup.name"),
        dataIndex: 'materialGroupName',
        width: 150
    }],
    valueField: "materialGroupCode",
    displayField: "materialGroupCode",
    /**
     * 创建弹出窗口类名
     */
    selectWinClassName: "Md.material.MaterialGroupSelectWin",
    /**
     * 字段映射配置
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  }
     */
    fieldMapping: {
        "model.materialGroupCode": "materialGroupCode",
        "model.materialGroupName": "materialGroupName"
    }
});