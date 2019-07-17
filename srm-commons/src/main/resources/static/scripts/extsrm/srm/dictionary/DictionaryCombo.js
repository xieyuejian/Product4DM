Ext.define("Ext.srm.dictionary.DictionaryCombo", {
	extend : "Ext.form.field.ComboBox",
	alias : "dictionaryCombo",
	xtype : "dictionaryCombo",
	triggerAction : "all",
	valueField : "itemCode",
	displayField : "itemName",
	constructor : function(A) {
		var A = A || {};
		this.store = Ext.getStore(A.groupCode + "Store");
		if (!this.store) {
			this.store = Ext.create("Ext.srm.dictionary.DictionaryStore", {
				"groupCode" : A.groupCode
			})
		}
		this.callParent([ A ])
	}
});