Ext.define('Ext.comm.FileDownLoad', { 
    extend:'Ext.ux.Window',
    alias: "fileDownLoad",
    constructor: function(cfg) {
        var win = this;
        cfg = cfg || {};
        cfg = Ext.apply({
            renderTo: cfg.moduleId        
        }, cfg);

        var grid = this.grid = win.createGrid(cfg);
        
        var cfg = Ext.apply({
            title: $('attachment'),
            layout: 'border',
            width: 500,
            height: 400,
            items: [grid],
            renderTo:cfg.moduleId,
            constrain:true,
            listeners: {
                'hide': function() {
                    win.close();
                }
            }
        }, cfg);
        
        this.callParent([cfg]);
    },
    urlParams:{
    	"Authorization": window.localStorage.getItem("token"),
		"clientId": "pc" 
    },
    createGrid: function(cfg){
    	var me = this;
        var store = new Ext.data.JsonStore({
            proxy: {
                type: "ajax",
                url: path_srm + '/fs/file/getall?fileGroupId=' + cfg.uploadFileGroupId,
                actionMethods: {
                    read: "POST"
                },
                reader: {
                    rootProperty: "rows",
                    type: "json"
                }
            },
            autoLoad: true,
            pageSize: 0,
            fileds: Ext.data.Record.create([{
                name: "fileId"
            },
            {
                name: "fileCode"
            },
            {
                name: "fileName"
            },
            {
                name: "state",
                type: "int"
            },
            {
                name: "note"
            },
            {
                name: "input_element"
            }]),
            pruneModifiedRecords: true
        });
        
        var grid = new Ext.ux.grid.GridPanel({
            border: true,
            ui: 'small-grid',
            style: {
                padding: '0px 0px 0px 0px',
                border: '1px'
            },
            sm: {
                singleSelect: true
            },
            pageSize: 0,
            viewConfig: {
            	forceFit: true,
            	autoScroll: true
            },
            sm: {singleSelect: true },
            region: "center",
            border: true,
            store:store,
            cm: {
                defaultSortable: false,
                columns: [
                    {
                        header: $('file.fileName'),
                        width: 250,
                        dataIndex: "fileName",
                        sortable: true,
                        menuDisabled: true
                    },{
                        header : $('label.ClickToDownload'),
                        dataIndex: "fileCode",
                        width:160,
                        renderer:function (value, metaData, record) { 
                        	var url = me.buildUrl(path_srm + '/fs/file/download?' + "fileCode=" + record.get('fileCode')) + "\'\"";
                            var html = '[<a style="cursor:pointer;color:blue" onclick="document.location=\''
                                + url +
                                + ' title="'
                                + $("label.ClickToDownload")
                                + '">'
                                + $("label.ClickToDownload") + "</a>]";
                            return html;
                        }
                    }
                ]
            }
        });
        return grid;
    },
    /**
     * 构建参数url
     * @param {} url
     * @param {} params
     * @return {}
     */
    buildUrl : function(url) {
        var me= this,query = '',urlParams = me.urlParams; 
        if(urlParams){ 
	        Ext.Object.each(urlParams, function(key, val) {
	            query += (query ? '&' : '') + encodeURIComponent(key) + '=' + encodeURIComponent(val);
	        }); 
        }
        if (query) {
            url += (url.indexOf('?') > 0 ? '&' : '?') + query;
        }  
        return url;
    }
});
