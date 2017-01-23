package ru.ibase.fbjavaex.jqgrid;

import java.util.List;
import java.util.Map;


public class JqGridData {

    /**
     * Total number of pages
     */
    private final int total;
    /**
     * The current page number
     */
    private final int page;
    /**
     * Total number of records
     */
    private final int records;
    /**
     * The actual data
     */
    private final List<Map<String, Object>> rows;

    public JqGridData(int total, int page, int records, List<Map<String, Object>> rows) {
        this.total = total;
        this.page = page;
        this.records = records;
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getRecords() {
        return records;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

}
