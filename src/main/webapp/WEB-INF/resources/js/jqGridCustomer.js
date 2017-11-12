var JqGridCustomer = (function ($) {

    return function (options) {
        var jqGridCustomer = {
            dbGrid: null,
            // опции
            options: $.extend({
                baseAddress: null,
                showEditorPanel: true
            }, options),
            // возвращает модель
            getColModel: function () {
                return [
                    {
                        label: 'Id', 
                        name: 'CUSTOMER_ID', // field name
                        key: true, 
                        hidden: true           
                    },
                    {
                        label: 'Name', 
                        name: 'NAME', 
                        width: 240, 
                        sortable: true, 
                        editable: true,
                        edittype: "text", // input field type in the editor
                        search: true, 
                        searchoptions: {
                            // allowed search operators
                            sopt: ['eq', 'bw', 'cn'] 
                        },
                        // // size and maximum length for the input field
                        editoptions: {size: 30, maxlength: 60}, 
                        editrules: {required: true}            
                    },
                    {
                        label: 'Address',
                        name: 'ADDRESS',
                        width: 300,
                        sortable: false, // prohibit sorting
                        editable: true, 
                        search: false, // prohibit search
                        edittype: "textarea", // memo field
                        editoptions: {maxlength: 250, cols: 30, rows: 4}
                    },
                    {
                        label: 'Zip Code',
                        name: 'ZIPCODE',
                        width: 30,
                        sortable: false,
                        editable: true,
                        search: false,
                        edittype: "text",
                        editoptions: {size: 30, maxlength: 10}
                    },
                    {
                        label: 'Phone',
                        name: 'PHONE',
                        width: 80,
                        sortable: false,
                        editable: true,
                        search: false,
                        edittype: "text",
                        editoptions: {size: 30, maxlength: 14}
                    }
                ];
            },
            // grid initialization
            initGrid: function () {
                // url to retrieve data
                var url = jqGridCustomer.options.baseAddress + '/customer/getdata';
                jqGridCustomer.dbGrid = $("#jqGridCustomer").jqGrid({
                    url: url,
                    datatype: "json", // data format
                    mtype: "GET", // request type
                    colModel: jqGridCustomer.getColModel(),
                    rowNum: 500, // number of rows displayed
                    loadonce: false, // load only once
                    sortname: 'NAME', // Sorting by NAME by default
                    sortorder: "asc", 
                    width: window.innerWidth - 80, 
                    height: 500, 
                    viewrecords: true, // display the number of records
                    guiStyle: "bootstrap",
                    iconSet: "fontAwesome",
                    caption: "Customers", 
                    // navigation item
                    pager: 'jqPagerCustomer'
                });
            },
            // опции редактирования
            getEditOptions: function () {
                return {
                    url: jqGridCustomer.options.baseAddress + '/customer/edit',
                    reloadAfterSubmit: true,
                    closeOnEscape: true,
                    closeAfterEdit: true,
                    drag: true,
                    width: 400,
                    afterSubmit: jqGridCustomer.afterSubmit,
                    editData: {
                        // дополнительно к значениям из формы передаём ключевое поле
                        CUSTOMER_ID: function () {
                            // получаем текущую строку
                            var selectedRow = jqGridCustomer.dbGrid.getGridParam("selrow");
                            // получаем значение интересуещего нас поля
                            var value = jqGridCustomer.dbGrid.getCell(selectedRow, 'CUSTOMER_ID');
                            return value;
                        }
                    }
                };
            },
            // опции добавления
            getAddOptions: function () {
                return {
                    url: jqGridCustomer.options.baseAddress + '/customer/create',
                    reloadAfterSubmit: true,
                    closeOnEscape: true,
                    closeAfterAdd: true,
                    drag: true,
                    width: 400,
                    afterSubmit: jqGridCustomer.afterSubmit
                };
            },
            // опции удаления
            getDeleteOptions: function () {
                return {
                    url: jqGridCustomer.options.baseAddress + '/customer/delete',
                    reloadAfterSubmit: true,
                    closeOnEscape: true,
                    closeAfterDelete: true,
                    drag: true,
                    msg: "Удалить выделенного заказчика?",
                    afterSubmit: jqGridCustomer.afterSubmit,
                    delData: {
                        // передаём ключевое поле
                        CUSTOMER_ID: function () {
                            var selectedRow = jqGridCustomer.dbGrid.getGridParam("selrow");
                            var value = jqGridCustomer.dbGrid.getCell(selectedRow, 'CUSTOMER_ID');
                            return value;
                        }
                    }
                };
            },
            // инициализация панели навигации вместе с диалогами редактирования
            initPagerWithEditors: function () {
                jqGridCustomer.dbGrid.jqGrid('navGrid', '#jqPagerCustomer',
                        {
                            // buttons
                            search: true, 
                            add: true, 
                            edit: true, 
                            del: true, 
                            view: true, 
                            refresh: true, 
                            // подписи кнопок
                            searchtext: "Search",
                            addtext: "Add",
                            edittext: "Edit",
                            deltext: "Delete",
                            viewtext: "View",
                            viewtitle: "Selected record",
                            refreshtext: "Refresh"
                        },
                        jqGridCustomer.getEditOptions(),
                        jqGridCustomer.getAddOptions(),
                        jqGridCustomer.getDeleteOptions()
                        );
            },
            // инициализация панели навигации вместе без диалогов редактирования
            initPagerWithoutEditors: function () {
                jqGridCustomer.dbGrid.jqGrid('navGrid', '#jqPagerCustomer',
                        {
                            // кнопки
                            search: true, 
                            add: false,
                            edit: false, 
                            del: false, 
                            view: false,
                            refresh: true, 
                            // подписи кнопок
                            searchtext: "Search",
                            viewtext: "View",
                            viewtitle: "Selected record",
                            refreshtext: "Refresh"
                        }
                );
            },
            // инициализация панели навигации
            initPager: function () {
                if (jqGridCustomer.options.showEditorPanel) {
                    jqGridCustomer.initPagerWithEditors();
                } else {
                    jqGridCustomer.initPagerWithoutEditors();
                }
            },
            // инициализация
            init: function () {
                jqGridCustomer.initGrid();
                jqGridCustomer.initPager();
            },
            // обработчик результатов обработки форм (операций)
            afterSubmit: function (response, postdata) {
                var responseData = response.responseJSON;
                // проверяем результат на наличие сообщений об ошибках
                if (responseData.hasOwnProperty("error")) {
                    if (responseData.error.length) {
                        return [false, responseData.error];
                    }
                } else {
                    // если не была возвращена ошибка обновляем грид
                    $(this).jqGrid(
                            'setGridParam',
                            {
                                datatype: 'json'
                            }
                    ).trigger('reloadGrid');
                }
                return [true, "", 0];
            }
        };
        jqGridCustomer.init();
        return jqGridCustomer;
    };
})(jQuery);

