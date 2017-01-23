/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibase.fbjavaex.jqgrid;

import org.jooq.*;
import java.util.List;
import java.util.Map;


import static ru.ibase.fbjavaex.exampledb.Tables.CUSTOMER;


/**
 *
 * @author Den
 */
public class JqGridCustomer extends JqGrid {

    /**
     * Добавление условия поиска
     *
     * @param query
     */
    private void makeSearchCondition(SelectQuery<?> query) {
        switch (this.searchOper) {
            case "eq":
                query.addConditions(CUSTOMER.NAME.eq(this.searchString));
                break;
            case "bw":
                query.addConditions(CUSTOMER.NAME.startsWith(this.searchString));
                break;
            case "cn":
                query.addConditions(CUSTOMER.NAME.contains(this.searchString));
                break;
        }
    }


    /**
     * Возвращает общее количество записей
     *
     * @return
     */
    @Override
    public int getCountRecord() {
        SelectFinalStep<?> select
                = dsl.selectCount()
                        .from(CUSTOMER);

        SelectQuery<?> query = select.getQuery();

        if (this.searchFlag) {
            makeSearchCondition(query);
        }

        return (int) query.fetch().getValue(0, 0);
    }

    /**
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getRecords() {
        SelectFinalStep<?> select
                = dsl.select()
                        .from(CUSTOMER);

        SelectQuery<?> query = select.getQuery();

        if (this.searchFlag) {
            makeSearchCondition(query);
        }

        switch (this.sOrd) {
            case "asc":
                query.addOrderBy(CUSTOMER.NAME.asc());
                break;
            case "desc":
                query.addOrderBy(CUSTOMER.NAME.desc());
                break;
        }

        if (this.limit != 0) {
            query.addLimit(this.limit);
        }
        if (this.offset != 0) {
            query.addOffset(this.offset);
        }
        
        return query.fetchMaps();
    }
}
