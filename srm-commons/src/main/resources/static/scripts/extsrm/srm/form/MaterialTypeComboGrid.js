/**
 * 物料类型模糊查询下拉框
 */
Ext.define("Ext.srm.form.MaterialTypeComboGrid", {
    extend: "Ext.srm.form.SrmComboGrid",
    alias: "widget.materialTypeComboGrid",
    xtype: "materialTypeComboGrid",
    alternateClassName: ["Ext.srm.form.MaterialTypeComboGrid"],
    store: Ext.create("Ext.ux.data.JsonStore", {
        url: path_masterdata + "/md/materialtype/list",
        fields: ['materialTypeCode', 'materialTypeName'],
        pageSize: 10,
        autoLoad: false
    }),
    pageSize: 10,
    queryParam: "filter_LIKE_materialTypeCode_OR_LIKE_materialTypeName",
    displayFields: [{
        Qheader: "物料类型编码",
        header: $("materialType.code"),
        dataIndex: 'materialTypeCode',
        width: 120
    }, {
        Qheader: "物料类型名称",
        header: $("materialType.name"),
        dataIndex: 'materialTypeName',
        width: 150
    }],
    valueField: "materialTypeCode",
    displayField: "materialTypeCode",
    /**
     * 创建弹出窗口类名
     */
    selectWinClassName: "",
    
    /**
     * 字段映射配置
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  }
     */
    fieldMapping: {
        "model.materialTypeCode": "materialTypeCode",
        "model.materialTypeName": "materialTypeName"
    }
});