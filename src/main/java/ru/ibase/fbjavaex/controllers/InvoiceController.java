package ru.ibase.fbjavaex.controllers;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.beans.PropertyEditorSupport;

import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import ru.ibase.fbjavaex.jqgrid.JqGridInvoice;
import ru.ibase.fbjavaex.jqgrid.JqGridInvoiceLine;

import ru.ibase.fbjavaex.managers.InvoiceManager;

import ru.ibase.fbjavaex.jqgrid.JqGridData;

/**
 * Контроллер счёт-фактур
 *
 * @author Simonov Denis
 */
@Controller
public class InvoiceController {

    @Autowired(required = true)
    private JqGridInvoice invoiceGrid;

    @Autowired(required = true)
    private JqGridInvoiceLine invoiceLineGrid;

    @Autowired(required = true)
    private InvoiceManager invoiceManager;

    /**
     * 
     * @param binder 
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)   {
        binder.registerCustomEditor(Timestamp.class,
                new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    if ((value == null) || (value.isEmpty())) {
                        setValue(null);
                    } else {
                        Date parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(value);
                        setValue(new Timestamp(parsedDate.getTime()));
                    }
                } catch (ParseException e) {
                    throw new java.lang.IllegalArgumentException(value);
                }
            }
        });
    }

    @RequestMapping(value = "/invoice/", method = RequestMethod.GET)
    public String index(ModelMap map) {

        return "invoice";
    }

    @RequestMapping(value = "/invoice/getdata",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public JqGridData getData(
            @RequestParam(value = "rows", required = false, defaultValue = "20") int rows,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sidx", required = false, defaultValue = "") String sIdx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sOrd,
            @RequestParam(value = "_search", required = false, defaultValue = "false") Boolean search,
            @RequestParam(value = "searchField", required = false, defaultValue = "") String searchField,
            @RequestParam(value = "searchString", required = false, defaultValue = "") String searchString,
            @RequestParam(value = "searchOper", required = false, defaultValue = "") String searchOper,
            @RequestParam(value = "filters", required = false, defaultValue = "") String filters) {

        if (search) {
            invoiceGrid.setSearchCondition(searchField, searchString, searchOper);
        }
        invoiceGrid.setLimit(rows);
        invoiceGrid.setPageNo(page);

        invoiceGrid.setOrderBy(sIdx, sOrd);

        return invoiceGrid.getJqGridData();
    }

    @RequestMapping(value = "/invoice/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Map<String, Object> addInvoice(
            @RequestParam(value = "CUSTOMER_ID", required = true, defaultValue = "0") Integer customerId,
            @RequestParam(value = "INVOICE_DATE", required = false, defaultValue = "") Timestamp invoiceDate) {
        Map<String, Object> map = new HashMap<>();
        try {
            invoiceManager.create(customerId, invoiceDate);
            map.put("success", true);
        } catch (Exception ex) {
            map.put("error", ex.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/invoice/edit",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Map<String, Object> editInvoice(
            @RequestParam(value = "INVOICE_ID", required = true, defaultValue = "0") Integer invoiceId,
            @RequestParam(value = "CUSTOMER_ID", required = true, defaultValue = "0") Integer customerId,
            @RequestParam(value = "INVOICE_DATE", required = false, defaultValue = "") Timestamp invoiceDate) {
        Map<String, Object> map = new HashMap<>();
        try {
            invoiceManager.edit(invoiceId, customerId, invoiceDate);
            map.put("success", true);
        } catch (Exception ex) {
            map.put("error", ex.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/invoice/pay",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Map<String, Object> payInvoice(
            @RequestParam(value = "INVOICE_ID", required = true, defaultValue = "0") Integer invoiceId) {
        Map<String, Object> map = new HashMap<>();
        try {
            invoiceManager.pay(invoiceId);
            map.put("success", true);
        } catch (Exception ex) {
            map.put("error", ex.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/invoice/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Map<String, Object> deleteInvoice(
            @RequestParam(value = "INVOICE_ID", required = true, defaultValue = "0") Integer invoiceId) {
        Map<String, Object> map = new HashMap<>();
        try {
            invoiceManager.delete(invoiceId);
            map.put("success", true);
        } catch (Exception ex) {
            map.put("error", ex.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/invoice/getdetaildata",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public JqGridData getDetailData(
            @RequestParam(value = "INVOICE_ID", required = true) int invoice_id) {

        invoiceLineGrid.setInvoiceId(invoice_id);

        return invoiceLineGrid.getJqGridData();

    }

    @RequestMapping(value = "/invoice/createdetail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Map<String, Object> addInvoiceLine(
            @RequestParam(value = "INVOICE_ID", required = true, defaultValue = "0") Integer invoiceId,
            @RequestParam(value = "CUSTOMER_ID", required = true, defaultValue = "0") Integer customerId,
            @RequestParam(value = "QUNATITY", required = true, defaultValue = "0") Integer quantity) {
        Map<String, Object> map = new HashMap<>();
        try {
            invoiceManager.addInvoiceLine(invoiceId, customerId, quantity);
            map.put("success", true);
        } catch (Exception ex) {
            map.put("error", ex.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/invoice/editdetail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Map<String, Object> editInvoiceLine(
            @RequestParam(value = "INVOICE_LINE_ID", required = true, defaultValue = "0") Integer invoiceLineId,
            @RequestParam(value = "QUANTITY", required = true, defaultValue = "0") Integer quantity) {
        Map<String, Object> map = new HashMap<>();
        try {
            invoiceManager.editInvoiceLine(invoiceLineId, quantity);
            map.put("success", true);
        } catch (Exception ex) {
            map.put("error", ex.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/invoice/deletedetail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Map<String, Object> deleteInvoiceLine(
            @RequestParam(value = "INVOICE_LINE_ID", required = true, defaultValue = "0") Integer invoiceLineId) {
        Map<String, Object> map = new HashMap<>();
        try {
            invoiceManager.deleteInvoiceLine(invoiceLineId);
            map.put("success", true);
        } catch (Exception ex) {
            map.put("error", ex.getMessage());
        }
        return map;
    }
}
