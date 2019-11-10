package com.test.spring.cloud.controller;


/*通用的controller，用于被其他controller继承，因此是抽象类！！*/

import com.test.spring.cloud.common.domain.BaseDomain;
import com.test.spring.cloud.common.utils.MapperUtils;
import com.test.spring.cloud.components.datatables.DataTablesResult;
import com.test.spring.cloud.service.BaseClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 通用 Controller 必须为抽象类！！
 * <p>Title: BaseController</p>
 * <p>Description: </p>
 *
 * @author Lusifer
 * @version 1.0.0
 * @date 2018/8/12 14:00
 */
public abstract class BaseController<T extends BaseDomain, S extends BaseClientService> {

    /**
     * 注入业务逻辑层，service也需要有父级service
     */
    @Autowired
    protected S service;

    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public DataTablesResult page(HttpServletRequest request) {      /*返回的结果DataTablesResult需要另外创建*/
        String strDraw = request.getParameter("draw");
        String strStart = request.getParameter("start");
        String strLength = request.getParameter("length");

        int draw = strDraw == null ? 0 : Integer.parseInt(strDraw);
        int start = strStart == null ? 0 : Integer.parseInt(strStart);
        int length = strLength == null ? 10 : Integer.parseInt(strLength);

        String json = service.page(start, length, null);
        DataTablesResult dataTablesResult = null;
        try {
            dataTablesResult = MapperUtils.json2pojo(json, DataTablesResult.class);
            dataTablesResult.setDraw(draw);
            dataTablesResult.setRecordsTotal(dataTablesResult.getCursor().getTotal());
            dataTablesResult.setRecordsFiltered(dataTablesResult.getCursor().getTotal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataTablesResult;
    }
}
