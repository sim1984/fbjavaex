var JqGridCustomer = (function ($) {

    return function (options) {
        var jqGridCustomer = {
            dbGrid: null,
            // опции
            options: $.extend({
                baseAddress: null,
                showEditorPanel: true
            }, options),
            _initGrid: function () {
                // url для получения данных
                var url = jqGridCustomer.options.baseAddress + '/customer/getdata';
                jqGridCustomer.dbGrid = $("#jqGridCustomer").jqGrid({
                    url: url,
                    datatype: "json", // формат получения данных 
                    mtype: "GET", // тип http запроса
                    // описание модели
                    colModel: [
                        {
                            label: 'Id', // подпись
                            name: 'CUSTOMER_ID', // имя поля
                            key: true, // признак ключевого поля
                            hidden: true          // скрыт 
                        },
                        {
                            label: 'Name',
                            name: 'NAME',
                            width: 250, // ширина
                            sortable: true, // разрешена сортировка
                            editable: true, // разрешено редактирование
                            edittype: "text", // тип поля в редакторе
                            search: true, // разрешён поиск
                            searchoptions: {
                                sopt: ['eq', 'bw', 'cn'] // разрешённые операторы поиска
                            },
                            editoptions: {size: 30, maxlength: 60}, // размер и максимальная длина для поля ввода
                            editrules: {required: true}             // говорит о том что поле обязательное
                        },
                        {
                            label: 'Address',
                            name: 'ADDRESS',
                            width: 300,
                            sortable: false, // запрещаем сортировку
                            editable: true, // редактируемое
                            search: false, // запрещаем поиск
                            edittype: "textarea",
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
                    ],
                    rowNum: 500, // число отображаемых строк
                    loadonce: false, // загрузка только один раз
                    sortname: 'NAME', // сортировка по умолчанию по столбцу NAME
                    sortorder: "asc", // порядок сортировки
                    width: window.innerWidth - 40, // ширина грида
                    height: 500, // высота грида
                    viewrecords: true, // отображать количество записей
                    caption: "Customers", // подпись к гриду
                    // элемент для отображения навигации
                    pager: 'jqPagerCustomer'
                });
            },
            _initPager: function () {
                if (jqGridCustomer.options.showEditorPanel) {
                    jqGridCustomer.dbGrid.jqGrid('navGrid', '#jqPagerCustomer',
                            {
                                search: true, // поиск
                                add: true, // добавление
                                edit: true, // редактирование
                                del: true, // удаление
                                view: true, // просмотр записи
                                refresh: true, // обновление
                                // подписи кнопок
                                searchtext: "Поиск",
                                addtext: "Добавить",
                                edittext: "Изменить",
                                deltext: "Удалить",
                                viewtext: "Смотреть",
                                viewtitle: "Выбранная запись",
                                refreshtext: "Обновить"
                            },
                            // опции редактирования
                            {
                                url: jqGridCustomer.options.baseAddress + '/customer/edit',
                                reloadAfterSubmit: true,
                                closeOnEscape: true,
                                closeAfterEdit: true,
                                drag: true,
                                width: 400,
                                afterSubmit: jqGridCustomer.afterSubmit,
                                editData: {
                                    CUSTOMER_ID: function () {
                                        var selectedRow = jqGridCustomer.dbGrid.getGridParam("selrow");
                                        var value = jqGridCustomer.dbGrid.getCell(selectedRow, 'CUSTOMER_ID');
                                        return value;
                                    }
                                }                                
                            },
                            // опции добавления
                            {
                                url: jqGridCustomer.options.baseAddress + '/customer/create',
                                reloadAfterSubmit: true,
                                closeOnEscape: true,
                                closeAfterAdd: true,
                                drag: true,
                                width: 400,
                                afterSubmit: jqGridCustomer.afterSubmit                                
                            },
                            // опции удаления
                            {
                                url: jqGridCustomer.options.baseAddress + '/customer/delete',
                                reloadAfterSubmit: true,
                                closeOnEscape: true,
                                closeAfterDelete: true,
                                drag: true,
                                msg: "Удалить выделенного заказчика?",
                                afterSubmit: jqGridCustomer.afterSubmit,
                                delData: {
                                    CUSTOMER_ID: function () {
                                        var selectedRow = jqGridCustomer.dbGrid.getGridParam("selrow");
                                        var value = jqGridCustomer.dbGrid.getCell(selectedRow, 'CUSTOMER_ID');
                                        return value;
                                    }
                                }
                            }
                    );
                } else {
                    jqGridCustomer.dbGrid.jqGrid('navGrid', '#jqPagerCustomer',
                            {
                                search: true, // поиск
                                add: false, // добавление
                                edit: false, // редактирование
                                del: false, // удаление
                                view: false, // просмотр записи
                                refresh: true, // обновление

                                searchtext: "Поиск",
                                viewtext: "Смотреть",
                                viewtitle: "Выбранная запись",
                                refreshtext: "Обновить"
                            }
                    );
                }
            },
            init: function () {
                jqGridCustomer._initGrid();
                jqGridCustomer._initPager();
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
                    // обновление грида
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

